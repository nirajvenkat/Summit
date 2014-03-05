import java.net.*;
import java.util.*;
import java.io.*;

public class SummitHSSConsole
{
	public static final String hostname = "localhost";
	public static final int port = 9998;

	public static void main(String args[])
	{
		try
		{
			Scanner stdin = new Scanner(System.in);
			Socket s = new Socket(hostname, port);
			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());

			out.writeUTF(args[0]);

			if(args[0].contains("PUT"))
			{
				int response = in.readInt();
				if(response == 1)
				{
					System.out.print("New High Score! Name: ");
					out.writeUTF(stdin.nextLine());
				}
				else
				{
					System.out.println("Sorry. Not a High Score.");
				}
			}
			else if(args[0].contains("GET"))
			{
				String response = in.readUTF();
				System.out.println(response);
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
