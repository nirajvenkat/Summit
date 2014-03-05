import java.net.*;
import java.io.*;
import java.util.*;

public class SummitHSS
{
	public static final int TCP_PORT = 9998;
	public static final int DB_PORT = 3306;
	public static final String DB_UNAME = "root";
	public static final String DB_PASSWD = "kpcofgs";
	public static final String DB_URL = "192.168.1.12";
	public static final String DB_NAME = "Summit";
	public static final boolean DB_ERROR = true;

	public static final int MAX_HIGH_SCORES = 10;

	ServerSocket ss;
	DBConnection dbc;

	public SummitHSS()
	{
		try
		{
			ss = new ServerSocket(TCP_PORT);
			dbc = new DBConnection(DB_URL, DB_PORT, DB_UNAME, DB_PASSWD);
			dbc.connect(DB_NAME);
			dbc.detailedErrorOn(DB_ERROR);
			listen();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String args[])
	{
		SummitHSS HighScoresServer = new SummitHSS();
	}

	public void listen()
	{
		while(true)
		{
			try
			{
				ClientHandler ch = new ClientHandler(ss.accept());
				ch.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	class ClientHandler extends Thread
	{
		Socket s;
		DataInputStream in;
		DataOutputStream out;
		
		public ClientHandler(Socket s)
		{
			try
			{
				this.s = s;
				in = new DataInputStream(s.getInputStream());
				out = new DataOutputStream(s.getOutputStream());

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		public void run()
		{
			try
			{
				String raw = in.readUTF();
				String data[] = raw.split(";");
				if(data[0].equals("GET"))
				{
					String query = "SELECT name, score FROM highScores ORDER BY score ASC";
					ArrayList<String> highScores;
					highScores = dbc.query(query);
					String result = "";
					for(int i = 0; i < highScores.size(); i+=2)
					{
						String name = highScores.get(i).replace("NAME: ", "");
						String score = highScores.get(++i).replace("SCORE: ", "");
						result += name + ":" + score + ";";
					}
					out.writeUTF(result.substring(0, result.length()-1));
					
				}
				else if(data[0].equals("PUT"))
				{
					double time = Double.parseDouble(data[1]);
					
					String query = "SELECT score FROM highScores ORDER BY score DESC";
					ArrayList<String> highScores;
					highScores = dbc.query(query);
					System.out.println(highScores.size() + "");

					if(highScores.size() >= (MAX_HIGH_SCORES * 2))
					{
						if(time < Double.parseDouble(highScores.get(0).replace("SCORE: ", "")))
						{
							query = "DELETE FROM highScores WHERE score=" + highScores.get(0).replace("SCORE: ", "");
							dbc.query(query);
							out.writeInt(1);
							String name = in.readUTF();
							query = "INSERT INTO highScores (sid, name, score) VALUES (0, \"" + name + "\", " + time + ");";
							System.out.println(query);
							dbc.query(query);
						}
						else
						{
							out.writeInt(0);
						}
					}
					else
					{
						out.writeInt(1);
						String name = in.readUTF();
						query = "INSERT INTO highScores (sid, name, score) VALUES (0, \"" + name + "\", " + time + ");";
						System.out.println(query);
						dbc.query(query);
					}

				}

				in.close();
				out.close();
				s.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}


}
