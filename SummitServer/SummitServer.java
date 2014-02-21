import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;

public class SummitServer
{
	/* Summit Server Variables */
	public boolean listening = true;
	ServerSocket ss;

	/* Global Structures */
	ArrayList<GameLobby> lobbies;
	ArrayList<Integer> available_ports;

	public SummitServer()
	{
		//Setup game structures
		lobbies = new ArrayList<GameLobby>();

		available_ports = new ArrayList<Integer>();
		for(int i = 0; i < (Constants.MAX_LOBBIES * Constants.PLAYERS_PER_LOBBY) + 4; i++)
		{
			available_ports.add(Constants.UDP_PORT_FLOOR + i);
		}

		//Setup network
		try
		{
			ss = new ServerSocket(Constants.TCP_PORT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		listen();
		
	}

	public static void main(String args[])
	{
		SummitServer server = new SummitServer();
	}
	
	public void listen()
	{
		while(listening)
		{
			try
			{
				Socket s = ss.accept();
				new ClientHandler(s).start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public int getNumLobbies()
	{
		synchronized(lobbies)
		{
			return lobbies.size();
		}
	}

	public GameLobby getLobby(int index)
	{
		synchronized(lobbies)
		{
			return lobbies.get(index);
		}
	}

	private GameLobby getLobbyByPassword(String password)
	{
		for(int i = 0; i < lobbies.size(); i++)
		{
			if(lobbies.get(i).getPassword().equals(password))
			{
				return lobbies.get(i);
			}
		}

		return null;
	}

	private boolean lobbyPasswordOK(String password)
	{
		for(int i = 0; i < lobbies.size(); i++)
		{
			if(lobbies.get(i).getPassword().equalsIgnoreCase(password))
			{
				return false;
			}
		}

		return true;
	}

	public int getNextAvailablePort()
	{
		int port = -1;

		synchronized(available_ports)
		{
			port = available_ports.remove(0);
		}

		return port;
	}

	public void addAvailablePort(int port)
	{
		synchronized(available_ports)
		{
			available_ports.add(port);
		}
	}

	public int createLobby(String password, Player player)
	{
		synchronized(lobbies)
		{
			if(lobbies.size() < Constants.MAX_LOBBIES)
			{
				if(lobbyPasswordOK(password))
				{
					GameLobby new_lobby = new GameLobby(this);
					new_lobby.setPassword(password);
					new_lobby.addPlayer(player);
					lobbies.add(new_lobby);
					player.lobby = new_lobby;
					return Constants.OK;
				}
				
				return Constants.ERR_LOBBY_PASSWORD_EXISTS;
			}
			
			return Constants.ERR_NO_LOBBIES_AVAILABLE;

		}
	}

	public void deleteLobby(GameLobby lobby)
	{
		synchronized(lobbies)
		{
			lobbies.remove(lobby);
		}
	}

	public int joinLobby(String password, Player player)
	{
		synchronized(lobbies)
		{
			GameLobby found = getLobbyByPassword(password);

			if(found == null)
			{
				return Constants.ERR_NO_LOBBY_FOUND;
			}
		
			player.lobby = found;
			return found.addPlayer(player);	
		}
	}

	public int leaveLobby(int PORT)
	{
		boolean removed_player = false;

		for(int i = 0; i < getNumLobbies(); i++)
		{
			GameLobby curr_lobby = getLobby(i);
			synchronized(curr_lobby)
			{
				int j;
				for(j = 0; j < curr_lobby.players.size(); j++)
				{
					if(curr_lobby.players.get(j).PORT == PORT)
					{
						curr_lobby.removePlayer(curr_lobby.players.get(j));
						addAvailablePort(PORT);
						removed_player = true;
						break;
					}
				}

				if(removed_player)
				{
					break;
				}
			}
		}

		if(!removed_player)
		{
			return Constants.ERR_NO_PLAYER_FOUND;
		}

		return Constants.OK;
	}

	public class ClientHandler extends Thread
	{
		Socket s;
		DataInputStream in;
		DataOutputStream out;
		boolean running = true;

		public ClientHandler(Socket s)
		{
			this.s = s;

			try
			{
				in = new DataInputStream(s.getInputStream());
				out = new DataOutputStream(s.getOutputStream());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		public void sendTCP(String data)
		{
			try
			{
				try
				{
					int num = Integer.parseInt(data);
					out.writeInt(num);
					out.flush();
				}
				catch(Exception e)
				{
					out.writeUTF(data);
					out.flush();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		public void run()
		{
			/*Commands:
			  CMD_CREATE_LOBBY;password
			  CMD_JOIN_LOBBY;password
			  CMD_LEAVE_LOBBY;PORT
			  CMD_DISCONNECT
			  CMD_SHOW_SCORES
			*/

			/*Debug:
			  CMD_DEBUG_SHOW_LOBBIES
			  CMD_DEBUG_SHOW_SERVER_VARS
			*/
		while(running){
			int response = Constants.ERR;
			String command[] = {"","",""};

			try
			{
				command = in.readUTF().split(";");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			if(command[0].equals("CMD_CREATE_LOBBY"))
			{
				if(command[1] != null && !command[1].isEmpty())
				{
					InetAddress ip_address = null;

					try
					{
						ip_address = s.getInetAddress();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					Player player = new Player(ip_address, getNextAvailablePort(), this);
					response = createLobby(command[1], player);

					try
					{
						out.writeInt(response);
						out.flush();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}

					if(response < 0)
					{
						addAvailablePort(player.PORT);
						player = null;

						try
						{
							out.writeUTF("");
							out.flush();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						try
						{
							out.writeUTF(player.PORT  + ";" + player.player_id + ";" + player.SEQ_NUM);
							out.flush();

							//TODO: Send packet to lobby captain
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}

				}
			}
			else if(command[0].equals("CMD_JOIN_LOBBY"))
			{
				if(command[1] != null && !command[1].isEmpty())
				{
					InetAddress ip_address = null;
					try
					{
						ip_address = s.getInetAddress();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}

					Player player = new Player(ip_address, getNextAvailablePort(), this);
					response = joinLobby(command[1], player);

					try
					{
						out.writeInt(response);
						out.flush();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}

					if(response < 0)
					{
						addAvailablePort(player.PORT);
						player = null;

						try
						{
							out.writeUTF("");
							out.flush();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						try
						{
							out.writeUTF(player.PORT + ";" + player.player_id + ";" + player.SEQ_NUM);
							out.flush();

							//TODO: send TCP to other lobby members
							GameLobby tmp = player.lobby;
							tmp.sendTCP("1");
							tmp.sendTCP("LOBBY_NEW_PLAYER;" + tmp.getNumPlayers() + ";" + Constants.PLAYERS_PER_LOBBY + ";" + player.player_id);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			else if(command[0].equals("CMD_LEAVE_LOBBY"))
			{
				//CMD_LEAVE_LOBBY;PORT
				response = leaveLobby(Integer.parseInt(command[1]));

				try
				{
					out.writeInt(response);
					out.flush();
					out.writeUTF("");
					out.flush();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
			else if(command[0].equals("CMD_SHOW_SCORES"))
			{

			}
			else if(command[0].equals("CMD_DEBUG_SHOW_LOBBIES"))
			{
				String debug = "";
				
				for(int i = 0; i < getNumLobbies(); i++)
				{
					debug += getLobby(i).toString() + "\n";
				}

				response = Constants.OK;

				try
				{
					out.writeInt(response);
					out.flush();
					out.writeUTF(debug);
					out.flush();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
			else if(command[0].equals("CMD_DEBUG_SHOW_SERVER_VARS"))
			{
				String debug = "Summit Server running on TCP port " + Constants.TCP_PORT + "\nMAX_LOBBIES=" + Constants.MAX_LOBBIES + "\nPLAYERS_PER_LOBBY=" + Constants.PLAYERS_PER_LOBBY + "\nCurrent Lobbies: " + getNumLobbies() + "\nCurrent Available Ports: " + available_ports.size() + "\n";

				response = Constants.OK;

				try
				{
					out.writeInt(response);
					out.flush();
					out.writeUTF(debug);
					out.flush();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
			else if(command[0].equals("CMD_DISCONNECT"))
			{
				try
				{
					response = Constants.OK;
					out.writeInt(response);
					out.flush();
					out.writeUTF("");
					out.flush();
					in.close();
					out.close();
					s.close();
					running = false;
					break;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				try
				{
					out.writeInt(response);
					out.flush();
					out.writeUTF("");
					out.flush();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		}
	}
}
