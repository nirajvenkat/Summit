package game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import entities.PlatformEntity;
import entities.PlayerEntity;
import entities.PowerupEntity;

public class OGLRenderer {

	/** time at last frame */
	static long lastFrame;

	/** frames per second */
	int fps;
	/** last fps time */
	long lastFPS;

	/** is VSync Enabled */
	boolean vsync;
	int frames = 60;

	public static int SCREEN_WIDTH = 1024;
	public static int SCREEN_HEIGHT = 768;

	//Platform List
	private static ArrayList<PlatformEntity> platforms;
	//Powerup List
	private static ArrayList<PowerupEntity> powerups;
	//Player
	private int numPlayers;
	private ArrayList<PlayerEntity> players;
	private static int winner;

	public OGLRenderer(int width, int height, int frames)
	{
		SCREEN_WIDTH = width;
		SCREEN_HEIGHT = height;
		this.frames = frames;
	}
	
	public OGLRenderer()
	{
		super();
	}
	
	public void start(int np) {
		try {
			setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		initGL(); // init OpenGL
		getDelta(); // call once before loop to initialize lastFrame
		lastFPS = getTime(); // call before loop to initialize fps timer

		//Build world
		platforms = WorldBuilder.build();
		powerups = WorldBuilder.spawnPowerups();
		players = new ArrayList<PlayerEntity>();
		numPlayers = np;
		for(int i = 1; i <= numPlayers; i++){
			players.add(new PlayerEntity(i*SCREEN_WIDTH/(numPlayers+1),10,i));
		}
		winner = 0;

		while (!Display.isCloseRequested() && winner == 0) {
			int delta = getDelta();
			update(delta);
			renderGL();

			Display.update();
			Display.sync(frames); // cap fps to 60fps
		}
		//TODO Display winner image in Display
		//TODO check score against database and update
		System.out.print("player " + winner + " wins!");

		Display.destroy();
	}

	public void update(int delta) {
		// rotate quad
		//		rotation += 0.15f * delta;
		//TODO get two players working via networking
		for(PlayerEntity p : players){
			winner = p.update(platforms, powerups, delta);
			if(winner != 0){
				break;
			}
		}
		
		//TODO collision detection between players
		
		//		while (Keyboard.next()) {
		//		    if (Keyboard.getEventKeyState()) {
		//		        if (Keyboard.getEventKey() == Keyboard.KEY_F) {
		//		        	setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, !Display.isFullscreen());
		//		        }
		//		        else if (Keyboard.getEventKey() == Keyboard.KEY_V) {
		//		        	vsync = !vsync;
		//		        	Display.setVSyncEnabled(vsync);
		//		        }
		//		    }
		//		}

		
		updateFPS(); // update FPS Counter
	}

	/**
	 * Set the display mode to be used 
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen) {

		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width) && 
				(Display.getDisplayMode().getHeight() == height) && 
				(Display.isFullscreen() == fullscreen)) {
			return;
		}

		try {
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for (int i=0;i<modes.length;i++) {
					DisplayMode current = modes[i];

					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against the 
						// original display mode then it's probably best to go for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
								(current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width,height);
			}

			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
		}
	}

	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public static int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;

		return delta;
	}

	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}

	public void initGL() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, SCREEN_WIDTH, 0, SCREEN_HEIGHT, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL_TEXTURE_RECTANGLE_ARB);
	}


	public void renderGL() {
		// Clear The Screen And The Depth Buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// R,G,B,A Set The Color To Blue One Time Only
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		//Background texture
		Texture bgtex = WorldBuilder.loadTexture("res/puck.png");
		bgtex.bind();

		glBegin(GL_QUADS);
			glTexCoord2f(0, 1);
			glVertex2d(0, 0);
			glTexCoord2f(1, 1);
			glVertex2d(SCREEN_WIDTH, 0);
			glTexCoord2f(1, 0);
			glVertex2d(SCREEN_WIDTH, SCREEN_HEIGHT);
			glTexCoord2f(0, 0);
			glVertex2d(0, SCREEN_HEIGHT);
		glEnd();

		// draw platforms
		for(PlatformEntity plat : platforms){
			plat.draw();
		}

		//draw player
		for(PlayerEntity p : players){
			p.update();
			p.draw();
		}

		//draw powerups
		for(PowerupEntity pow : powerups){
			pow.draw();
		}
	}

	/*public static void main(String[] argv) {
		//TODO make a start screen
		//TODO connect to the High Score Database
		OGLRenderer fullscreenExample = new OGLRenderer();
		fullscreenExample.start(1);
	}*/
}