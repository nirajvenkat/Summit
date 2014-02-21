public class Constants
{
	/*Return Values*/
	public static final int ERR = 0;
	public static final int OK = 1;

	/*Network Constants*/
	public static final int TCP_PORT = 9998;
	public static final int UDP_PORT_FLOOR = 10300;
	public static final int UDP_PACKET_SIZE = 64; //In bytes! 

	/*Lobby Constants*/
	public static final int MAX_LOBBIES = 5;
	public static final int PLAYERS_PER_LOBBY = 3;

	/*Error Codes*/
	public static final int ERR_NO_LOBBIES_AVAILABLE = -1;
	public static final int ERR_NO_LOBBY_FOUND = -2;
	public static final int ERR_LOBBY_PASSWORD_EXISTS = -3;
	public static final int ERR_LOBBY_FULL = -4;
	public static final int ERR_NO_PLAYER_FOUND = -5;
}
