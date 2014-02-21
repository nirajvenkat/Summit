import java.io.*;
import java.net.*;

public class Player
{
	public int PORT; //UDP port
	public long SEQ_NUM; //Initial packet sequence number
	public InetAddress ip_address; //IP address of client
	public int player_id;
	public SummitServer.ClientHandler TCPConnection;
	public GameLobby lobby;
	
	public Player(InetAddress ip_address, int PORT, SummitServer.ClientHandler TCPConnection)
	{
		this.TCPConnection = TCPConnection;
		this.ip_address = ip_address;
		this.PORT = PORT;
		SEQ_NUM = 0;
		player_id = -1;
	}

	public String toString()
	{
		return("Player " + ip_address + " [UDP PORT: " + PORT + "] SEQ=" + SEQ_NUM);
	}

	public boolean equals(Object other)
	{
		try
		{
			Player cmp = (Player) other;
			if(ip_address.equals(cmp.ip_address) && cmp.PORT == PORT)
			{
				return true;
			}
		}
		catch(Exception e){}

		return false;
	}

}
