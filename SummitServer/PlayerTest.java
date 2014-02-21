import static org.junit.Assert.*;

import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;


public class PlayerTest {
	
	Player tester;
	Player tester2;

	@Before
	public void testSetup() {
		try{
			tester = new Player(InetAddress.getByName("127.0.0.1"),1005,null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
	    }
		
		try{
			tester2 = new Player(InetAddress.getByName("127.0.0.1"),1005,null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
	    }
	}
	
	@Test
	public void testToString() {
		assertEquals("Player /127.0.0.1 [UDP PORT: " + 1005 + "] SEQ=" + 0, tester.toString());
	}
	
	@Test
	public void testEquals() {
		assertTrue(tester.equals(tester2));
	}

}
