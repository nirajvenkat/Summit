package game;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class OGLRendererTest {

	OGLRenderer tr; //test renderer

	private final ByteArrayOutputStream outLine = new ByteArrayOutputStream();
	
	@Before
	public void setUp() throws Exception {
		tr = new OGLRenderer();
	    System.setOut(new PrintStream(outLine));
	}

	@After
	public void cleanUpStreams() {
	    System.setOut(null);
	}
	
	@Test
	public void testSetDisplayWindowed() {
		tr.setDisplayMode(1234, 600, false);
		DisplayMode temp = new DisplayMode(1234, 600);
		assertThat(temp, is(Display.getDisplayMode()));
	}

	@Test
	public void testSetBadDisplay() {
		int width = 500;
		int height = 19;
		boolean fullscreen = true;
		tr.setDisplayMode(width, height, fullscreen);
		assertEquals("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen+"\r\n", outLine.toString());
	}
	
	@Test
	public void testSetDisplayFullscreen() {
		tr.setDisplayMode(800, 600, true);
		assertEquals("800 x 600 x 32 @60Hz", Display.getDisplayMode().toString());
	}
	
	@Test
	public void testSetDisplayAfterStart() {
		//Tester should verify that window does not change modes
		tr.setDisplayMode(320, 200, false);
		tr.start(2);
		tr.setDisplayMode(800, 600, true);
		assertFalse(Display.isCreated());
	}
	
	@Test
	public void testMultipleStarts() {
		tr.setDisplayMode(320, 200, false);
		tr.start(2);
		OGLRenderer twin = new OGLRenderer();
		twin.setDisplayMode(400, 600, false);
		twin.start(2);
	}
	
	@Test
	public void testStart() {
		tr.setDisplayMode(320, 200, false);
		tr.start(2);
	}

}
