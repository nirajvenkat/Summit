import java.net.*;
import java.util.*;
import java.io.*;

public class TestClientCMD
{
	public static final String hostname = "localhost";
	public static final int port = 9998;

	public static void main(String args[])
	{
		try
		{
			Socket s = new Socket(hostname, port);
			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());

			String line;
			boolean finished = false;
			Scanner scanner = new Scanner(System.in);
			while(!finished)
			{
				System.out.print("-->");
				line = scanner.nextLine();

				if(line.equalsIgnoreCase("EXIT"))
				{
					finished = true;
				}
				else
				{
					out.writeUTF(line);
					int code = in.readInt();
					String response = in.readUTF();
					System.out.println(code + "\n" + response);

				//	if((line.equals("CMD_JOIN_LOBBY") || line.equals("CMD_CREATE_LOBBY")) && response)
				//	{
				//	}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
