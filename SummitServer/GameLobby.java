import java.util.*;
import java.net.*;
import java.util.*;

public class GameLobby
{
	public static final int GAME_STATE_FREE = 1; //No players
	public static final int GAME_STATE_OPEN = 2; //Partially full
	public static final int GAME_STATE_CLOSED = 3; //Completely full

	private SummitServer server;
	public ArrayList<Player> players;
	private ArrayList<GameThread> threads;
	private Player lobby_owner;
	private Integer game_state;
	private String password;
	private int next_player_id;

	public GameLobby(SummitServer server)
	{
		this.server = server;
		players = new ArrayList<Player>();
		threads = new ArrayList<GameThread>();
		password = null;
		lobby_owner = null;
		game_state = GAME_STATE_FREE;
		next_player_id = 1;
	}

	public String toString()
	{
		String ret;

		synchronized(players)
		{
			ret = "Game Lobby: '" + getPassword() + "' -- State: " + getGameState() + " -- [" + getNumPlayers() + "/" + Constants.PLAYERS_PER_LOBBY + "] players\n";
			for(Player p : players)
			{
				ret += "\t";

				if(p.equals(lobby_owner))
				{
					ret += "* ";
				}
				else
				{
					ret += "  ";
				}
				
				ret += p.toString() + "\n";
			}
		}

		return ret;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		if(this.password == null)
		{
			this.password = password;
		}
	}

	public int getGameState()
	{
		int ret;

		synchronized(game_state)
		{
			ret = game_state.intValue();
		}

		return ret;
	}

	private void setGameState(int game_state)
	{
		synchronized(this.game_state)
		{
			this.game_state = game_state;
		}
	}

	public synchronized int getNumPlayers()
	{
		int ret;

		synchronized(players)
		{
			ret = players.size();
		}

		return ret;
	}

	public ArrayList<Player> getPlayers()
	{
		synchronized(players)
		{
			return players;
		}
	}

	public void sendTCP(String data)
	{
		for(GameThread thread: threads)
		{
			thread.sendTCP(data);
		}
	}

	public void removePlayer(Player player)
	{
		synchronized(players)
		{
			players.remove(player);
			
			synchronized(threads)
			{
				int i;
				for(i = 0; i < threads.size(); i++)
				{
					if(threads.get(i).player.equals(player))
					{
						break;
					}
				}
				threads.get(i).running = false;
				threads.remove(i);
			}
			
			if(player.equals(lobby_owner))
			{
				if(players.size() > 1)
				{
					lobby_owner = players.get(0);
				}
				else
				{
					server.deleteLobby(this);
				}
			}

			setGameState(GAME_STATE_OPEN);

		}

	}

	public int addPlayer(Player player)
	{
		synchronized(players)
		{
			int num_players = players.size();

			if(num_players >= Constants.PLAYERS_PER_LOBBY)
			{
				return Constants.ERR_LOBBY_FULL;
			}
		
			players.add(player);
			player.player_id = next_player_id++;
			GameThread thread = new GameThread(player);

			synchronized(threads)
			{
				threads.add(thread);
				thread.start();
			}

			if(getGameState() == GAME_STATE_FREE && lobby_owner == null)
			{
				lobby_owner = player;
			}

			if(num_players == (Constants.PLAYERS_PER_LOBBY - 1))
			{
				setGameState(GAME_STATE_CLOSED);
			}
			else
			{
				setGameState(GAME_STATE_OPEN);
			}

			//sendTCP("LOBBY_NEW_PLAYER;" + players.size() + ";" + Constants.PLAYERS_PER_LOBBY + ";" + player.player_id  + ";");
		}
		return Constants.OK;
	}

	class GameThread extends Thread
	{
		Player player;
		DatagramSocket SOCK;
		byte[] recv, send;
		boolean running;

		public GameThread(Player player)
		{
			this.player = player;
			try
			{
				SOCK = new DatagramSocket(player.PORT);
				recv = new byte[Constants.UDP_PACKET_SIZE];
				send = new byte[Constants.UDP_PACKET_SIZE];
				running = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				running = false;
			}
		}

		public void sendTCP(String data)
		{
			player.TCPConnection.sendTCP(data);
		}

		public void send(String data)
		{
			try
			{
				data += "" + (++player.SEQ_NUM);
				send = data.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(send, send.length, player.ip_address, player.PORT);
				SOCK.send(sendPacket);
				System.out.println("Sent: " + data + " to " + player.ip_address + ", " + player.PORT);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		public void run()
		{
			while(running)
			{
				try
				{
					DatagramPacket receivePacket = new DatagramPacket(recv, recv.length);
					SOCK.receive(receivePacket);
					String data = new String(receivePacket.getData());
					System.out.println("Received: " + data);

					for(GameThread thread: threads)
					{
						if(!thread.player.equals(this.player))
						{
							thread.send(data);
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
		}
	}
}
