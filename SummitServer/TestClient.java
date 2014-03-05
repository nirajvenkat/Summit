import java.net.*;
import java.util.*;
import java.io.*;

public class TestClient
{
	public static final String hostname = "204.77.0.100";
	public static final int port = 9998;
	ServerSocket ss;

	public TestClient()
	{
		try
		{
			ss = new ServerSocket(9997);
			listen();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public static void main(String args[])
	{
		TestClient tc = new TestClient();	
	}

	public void listen()
	{
			while(true)
			{
				try
				{
					new ClientThread(ss.accept());
				}
				catch(Exception e)
				{
					//
					e.printStackTrace();
				}
			}
	}

	public class ClientThread extends Thread
	{
		Socket s;
		DataInputStream in;
		DataOutputStream out;

		Socket client;
		PrintWriter client_out;
		BufferedReader client_in;

		public ClientThread(Socket client)
		{
			try
			{
				System.err.println("New Client: " + client.getInetAddress().toString());
				this.client = client;
				s = new Socket(hostname, port);
				in = new DataInputStream(s.getInputStream());
				out = new DataOutputStream(s.getOutputStream());
				client_out = new PrintWriter(client.getOutputStream());
				client_in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			}
			catch(Exception e)
			{
				//
				e.printStackTrace();
			}
			
		}

		public void run()
		{
			try
			{
				int offset = 0;
				char[] buff = new char[100];
				while((client_in.read(buff, offset++, 1) != -1));
				String line = new String(buff);
				System.err.println("\t->Sending '" + line + "'");
				out.writeUTF(line);
				int code = in.readInt();
				String response = in.readUTF();
				System.err.println("\t->Response: " + response);
				client_out.println(response);
			}
			catch(Exception e)
			{
				//
				e.printStackTrace();
			}

			try
			{
				client_out.close();
				client_in.close();
				client.close();
				out.writeUTF("CMD_DISCONNECT");
				in.readInt();
				in.close();
				out.close();
				s.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//
			}

		}
	}
}
