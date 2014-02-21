import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.net.InetAddress;
import java.util.*;


public class GameLobbyTest {
	
	GameLobby gl;
	int glstate;
	Player glowner;
	String glpass;
	ArrayList<Player> glplayers;
	String expected = "";
	
	@Before
	public void setUp() throws Exception {
		gl = new GameLobby(null);
		glstate = 1;
		glowner = null;
		glpass = null;
		glplayers = new ArrayList<Player>();
	}

	@After
	public void tearDown() throws Exception {
		expected = "";
	}

	@Test
	public void testGetPassword() {
		assertEquals(glpass, gl.getPassword());
	}
	
	@Test
	public void testSetPassword() {
		glpass = "testing";
		gl.setPassword(glpass);
		assertEquals(glpass, gl.getPassword());
	}
	
	@Test
	public void testGetGameState() {
		assertEquals(glstate, gl.getGameState());
	}
	
	@Test
	public void testGetNumPlayers() {
		assertEquals(0, gl.getNumPlayers());
	}
	
	@Test
	public void testToStringNoPlayers() {
		expected = "Game Lobby: '" + glpass + "' -- State: " + glstate + " -- [" + 0 + "/" + Constants.PLAYERS_PER_LOBBY + "] players\n";
		assertEquals(expected, gl.toString());
	}
	
	@Test
	public void testToStringFullPlayers() {
		//set up lobby with players
		for(int i = 0; i < Constants.PLAYERS_PER_LOBBY; i++){
			Player p = new Player(null, 1000+i, null);
			glplayers.add(p);
			gl.addPlayer(p);
			if(i == 0){
				glowner = p;
			}
		}
		
		//get lobby toString()
		expected = "Game Lobby: '" + glpass + "' -- State: " + 3 + " -- [" + Constants.PLAYERS_PER_LOBBY + "/" + Constants.PLAYERS_PER_LOBBY + "] players\n";
		for(Player p : glplayers) {
			expected += "\t";
			
			if(p.equals(glowner)){
				expected += "* ";
			}else{
				expected += "  ";
			}
			
			expected += p.toString() + "\n";
		}
		
		assertEquals(expected, gl.toString());
	}
	
	@Test
	public void testToStringOnePlayer() {
		//set up lobby with players
		for(int i = 0; i < 1; i++){
			Player p = new Player(null, 1000+i, null);
			glplayers.add(p);
			gl.addPlayer(p);
			if(i == 0){
				glowner = p;
			}
		}
		
		//get lobby toString()
		expected = "Game Lobby: '" + glpass + "' -- State: " + 2 + " -- [" + 1 + "/" + Constants.PLAYERS_PER_LOBBY + "] players\n";
		for(Player p : glplayers) {
			expected += "\t";
			
			if(p.equals(glowner)){
				expected += "* ";
			}else{
				expected += "  ";
			}
			
			expected += p.toString() + "\n";
		}
		
		assertEquals(expected, gl.toString());
	}
	
	@Test
	public void testGetPlayersEmpty() {
		assertTrue( glplayers.equals(gl.getPlayers()) );
	}
	
	@Test
	public void testGetPlayersfull() {
		//set up lobby with players
		for(int i = 0; i < Constants.PLAYERS_PER_LOBBY; i++){
			Player p = null;
			try{
				p = new Player(InetAddress.getByName("127.0.0.1"), 1000+i, null);
			}
			catch(Exception e)
			{
				e.printStackTrace();
		    }
			
			glplayers.add(p);
			gl.addPlayer(p);
			if(i == 0){
				glowner = p;
			}
		}
		ArrayList<Player> received = gl.getPlayers();
		assertTrue( received.equals(glplayers) );
	}
	
	@Test (expected = NullPointerException.class)
	public void testRemoveAllPlayers() {
		//set up lobby with players
		for(int i = 0; i < Constants.PLAYERS_PER_LOBBY; i++){
			Player p = null;
			try{
				p = new Player(InetAddress.getByName("127.0.0.1"), 1000+i, null);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			glplayers.add(p);
			gl.addPlayer(p);
			if(i == 0){
				glowner = p;
			}
		}
		//remove the players
		for(Player p: glplayers) {
			gl.removePlayer(p);
		}

		assertEquals( GameLobby.GAME_STATE_CLOSED, gl.getGameState() );
	}
	
	@Test
	public void testRemoveOnePlayer(){
		//set up lobby with players
		for(int i = 0; i < Constants.PLAYERS_PER_LOBBY; i++){
			Player p = null;
			try{
				p = new Player(InetAddress.getByName("127.0.0.1"), 1000+i, null);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			glplayers.add(p);
			gl.addPlayer(p);
			if(i == 0){
				glowner = p;
			}
		}
		//remove the players
		gl.removePlayer(glplayers.remove(0));

		assertTrue( glplayers.equals(gl.getPlayers()) );
	}
	
	@Test
	public void testRemoveOwner(){
		//set up lobby with players
				for(int i = 0; i < Constants.PLAYERS_PER_LOBBY; i++){
					Player p = null;
					try{
						p = new Player(InetAddress.getByName("127.0.0.1"), 1000+i, null);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}

					glplayers.add(p);
					gl.addPlayer(p);
					if(i == 0){
						glowner = p;
					}
				}
				//remove the players
				gl.removePlayer(glplayers.remove(0));
				glowner = glplayers.get(0);

				//get lobby toString()
				expected = "Game Lobby: '" + glpass + "' -- State: " + 2 + " -- [" + (Constants.PLAYERS_PER_LOBBY-1) + "/" + Constants.PLAYERS_PER_LOBBY + "] players\n";
				for(Player p : glplayers) {
					expected += "\t";
					
					if(p.equals(glowner)){
						expected += "* ";
					}else{
						expected += "  ";
					}
					
					expected += p.toString() + "\n";
				}
				
				assertEquals(expected, gl.toString());
	}

	@Test
	public void testAddPlayer() {
		Player p = null;
		try{
			p = new Player(InetAddress.getByName("127.0.0.1"), 1000, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		glplayers.add(p);
		gl.addPlayer(p);
		
		assertTrue(glplayers.equals(gl.getPlayers()));
	}
	
	
}
