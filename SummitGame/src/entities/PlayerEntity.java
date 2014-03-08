package entities;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB;


import game.OGLRenderer;
import game.WorldBuilder;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.UnicodeFont;

public class PlayerEntity implements Entity {


	//Sprite variables
	private static int spritesheet;
	private static int spriteNo;
	private static final Map<String, Sprite> spriteMap = new HashMap<String, Sprite>();
	private static Sprite currentSprite;
	private String SPRITESHEET_IMAGE_LOCATION;
	private static final String SPRITESHEET_XML_LOCATION = "res/spriteNew.xml";
	boolean surprisePlayed = false;
	private static long lastTime;


	protected double x, y, width, height;		//player's position and draw box
	protected double vvel;						//player's vertical velocity
	protected double xvel;
	protected double jumpvel, fallvel;			//player's max jump and fall velocity
	protected Rectangle hitbox = new Rectangle();//player's hitbox
	protected boolean jumping, falling;			//whether the player is falling or jumping
	protected int id;							//player's identifying number
	protected int score;
	protected UnicodeFont font;

	public PlayerEntity(double x, double y, int id) {
		this.x = x;
		this.y = y;
		this.width = 0;
		this.height = 0;
		this.id = id;
		this.score = 0;
		this.xvel = 0.2f;
		
		//set up fonts
        /*
        Font awtFont = new Font("Serif", Font.BOLD,50);
        font = new UnicodeFont(awtFont, 128, false, false);
        */
		
		switch (id){
			case 1:
				SPRITESHEET_IMAGE_LOCATION = "res/Greenfinal.png";
				break;
			case 2:
				SPRITESHEET_IMAGE_LOCATION = "res/Redfinal.png";
				break;
			case 3:
				SPRITESHEET_IMAGE_LOCATION = "res/Bluefinal.png";
				break;
			case 4:
				SPRITESHEET_IMAGE_LOCATION = "res/Yellowfinal.png";
				break;
			default:
				SPRITESHEET_IMAGE_LOCATION = "res/Greenfinal.png";
				break;
		}
		
		spritesheet = WorldBuilder.glLoadLinearPNG(SPRITESHEET_IMAGE_LOCATION);
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = builder.build(new File(SPRITESHEET_XML_LOCATION));
			Element root = document.getRootElement();
			for (Object spriteObject : root.getChildren()) {
				Element spriteElement = (Element) spriteObject;
				String name = spriteElement.getAttributeValue("n");
				int xval = spriteElement.getAttribute("x").getIntValue();
				int yval = spriteElement.getAttribute("y").getIntValue();
				int wval = spriteElement.getAttribute("w").getIntValue();
				int hval = spriteElement.getAttribute("h").getIntValue();
				this.width = wval/3;
				this.height = hval/3;
				Sprite sprite = new Sprite(name, xval, yval, wval, hval);
				spriteMap.put(sprite.getName(), sprite);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			//cleanUp(true);
		} catch (IOException e) {
			e.printStackTrace();
			//cleanUp(true);
		}
		
		spriteNo = 1;
		currentSprite = spriteMap.get(spriteNo + ".png");
		lastTime = OGLRenderer.getTime();

		this.jumpvel = 2.0f;
		this.fallvel = -0.35f;
	}

	public void jump(){
		this.jumping = true;
		this.vvel = jumpvel;
	}

	public void land(){
		this.falling = false;
		this.jumping = false;
		this.vvel = -0.01f;
	}

	public boolean isFalling(){
		return this.falling;
	}

	public void fall(){
		this.falling = true;
	}

	public boolean isJumping(){
		return this.jumping;
	}

	public double getVvel(){
		return this.vvel;
	}

	public void setVvel(double newvel){
		this.vvel = newvel;
	}
	
	public void addJumpVel(double newvel){
		this.jumpvel += newvel;
	}
	
	public void addFallVel(double newvel){
		if(this.fallvel < -0.1f){
			this.fallvel += newvel;
		}
	}
	
	public void addXVel(double newvel){
		if(this.xvel < 3.5f){
			this.xvel += newvel;
		}
	}
	
	public void addPoints(int points){
		this.score += points;
	}
	
	public int getScore(){
		return this.score;
	}

	@Override
	public void draw() {
		//		Texture playTexture = WorldBuilder.loadTexture("res/stone.png");
		//		playTexture.bind();
		//		
		//		glBegin(GL_QUADS);
		//			glTexCoord2f(0, 0);
		//			glVertex2d(x, y);
		//			glTexCoord2f(0, 1);
		//			glVertex2d(x + width, y);
		//			glTexCoord2f(1, 1);
		//			glVertex2d(x + width, y + height);
		//			glTexCoord2f(1, 0);
		//			glVertex2d(x, y + height);
		//		glEnd();

		//		glPushMatrix();
		//		glBegin(GL_QUADS);
		//			glColor3d(1, 0, 0);
		//			glVertex2d(x, y);
		//			glColor3d(1, 0, 0);
		//			glVertex2d(x + width, y);
		//			glColor3d(1, 0, 0);
		//			glVertex2d(x + width, y + height);
		//			glColor3d(1, 0, 0);
		//			glVertex2d(x, y + height);
		//		glEnd();
		//		glPopMatrix();

		glBindTexture(GL_TEXTURE_RECTANGLE_ARB, spritesheet);

		int xval = currentSprite.getX();
		int yval = currentSprite.getY();
		int x2val = currentSprite.getX() + currentSprite.getWidth();
		int y2val = currentSprite.getY() + currentSprite.getHeight();

		glBegin(GL_QUADS);
			glTexCoord2f(xval, y2val);
			glVertex2d(x, y);
			glTexCoord2f(x2val, y2val);
			glVertex2d(x + width, y);
			glTexCoord2f(x2val, yval);
			glVertex2d(x + width, y + height);
			glTexCoord2f(xval, yval);
			glVertex2d(x, y + height);
		glEnd();
		glBindTexture(GL_TEXTURE_RECTANGLE_ARB, 0);
		
		//get rid of black box
        glEnable(GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
	}
	
	public void drawScore(){

		String scoreStr;
		Color c = new Color(1,0,0);
		glPushMatrix(); 
	        glTranslatef(0,0,-1f);
		        glEnable(GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				switch(this.id){
				case 1:
					scoreStr = "Player 1: " + this.score;
					font.drawString(0,0, scoreStr, c);
					font.drawString(30,100, "Some Message", c);
					break;
					
				case 2:
					scoreStr = "Player 2: " + this.score;
					font.drawString(50,0, scoreStr, c);
					break;
				}
				glDisable(GL_BLEND);
		glPopMatrix();
    
		GL11.glDisable(GL11.GL_TEXTURE_2D);

	}
	
	public int getID()
	{
		return id;
	}

	public int update(ArrayList<PlatformEntity> platforms, ArrayList<PowerupEntity> powerups, int delta) {
		double x = this.getX();
		double y = this.getY();
		double oldx = this.getX();
		double oldy = this.getY();
		
		if(this.id == 1){
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) x -= this.xvel * delta;
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) x += this.xvel * delta;

			if (Keyboard.isKeyDown(Keyboard.KEY_UP) && !this.isJumping() && !this.isFalling()) this.jump();
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !surprisePlayed) {
				surprisePlayed = true;
				WorldBuilder.playSound(new File(System.getProperty("user.dir") + "/res/media/surprise.wav"));
			}
			
		}
		if(this.id == 2){
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) x -= this.xvel * delta;
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) x += this.xvel * delta;

			if (Keyboard.isKeyDown(Keyboard.KEY_W) && !this.isJumping() && !this.isFalling()) this.jump();
			
		}
		//gravity
		double newvel = this.getVvel();
		if(this.isJumping()){
			//y -= 0.098f * delta;
			if(newvel > .1f){
				newvel *= .75;
				this.setVvel(newvel);
			}else{
				this.fall();
			}
		}
		if(this.isFalling() && newvel > fallvel){
			newvel -= .035f;
			if(newvel < fallvel){
				newvel = fallvel;
			}
			this.setVvel(newvel);
		}

		//moving downwards? set falling
		if((y+(newvel*delta)) < y){
			this.fall();
		}
		y += newvel * delta;
		this.setLocation(x, y);

		//collision detection between player and platforms
		for(PlatformEntity plat : platforms){
			if(this.intersects(plat) || 
					((oldy < plat.getY() && this.y > plat.getY()) && this.isJumping() && (this.x+this.width > plat.getX() && this.x < plat.getX()+plat.getWidth()))){
				if(this.intersectsY(plat, oldy) != y){
					y = this.intersectsY(plat, oldy);
					this.setY(y);
					if(this.intersects(plat)){
						if(this.intersectsX(plat, oldx) != x){
							x = this.intersectsX(plat, oldx);
							this.setX(x);
						}
					}
					
				}else{
					if(this.intersectsX(plat, oldx) != x){
						x = this.intersectsX(plat, oldx);
						this.setX(x);
					}
				}
			}
		}

		// keep player on the screen
		if (x < 0) x = 0;
		if ((x+this.getWidth()) > OGLRenderer.SCREEN_WIDTH) x = (OGLRenderer.SCREEN_WIDTH-this.getWidth());
		if (y < 0) y = 10;

		if ((y+this.getHeight()) > OGLRenderer.SCREEN_HEIGHT){
			return this.id;
		}
		this.setLocation(x, y);
		
		PowerupEntity toRemove = null;
		for(PowerupEntity pow : powerups){
			if(this.intersects(pow)){
				toRemove = pow;
			}
		}
		if(toRemove != null){
			toRemove.updateStats(this);
			powerups.remove(toRemove);
		}
		

		return 0;
		/*
		int delta = OGLRenderer.getDelta();

		double oldX = x;
		double oldY = y;

		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) x -= 0.35f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) x += 0.35f * delta;		 
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) y += 0.35f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) y -= 0.35f * delta;

		if(x < 0 || x > OGLRenderer.SCREEN_WIDTH) x = oldX;
		if(y < 0 || y > OGLRenderer.SCREEN_HEIGHT) y = oldY;
		 */
	}


	@Override
	public void update(){
		//Sprite update code
		long currTime = OGLRenderer.getTime();
		if(currTime - lastTime > 100){
			spriteNo++;
			if(spriteNo == 7) spriteNo = 1;
			currentSprite = spriteMap.get(spriteNo + ".png");
			lastTime = currTime;
		}
		
	}

	@Override
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public boolean intersects(Entity other) {
		hitbox.setBounds((int) x, (int) y, (int) width, (int) height);
		boolean doesIntersect = hitbox.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
		
		return doesIntersect;
	}
	
	public double intersectsX(Entity other, double oldx){
		//check if lagged or moved too fast
		if(oldx < other.getX() && this.x > other.getX()){
			return other.getX() - this.width;
		}
		if(oldx > other.getX()+other.getWidth() && this.x < other.getX()+other.getWidth()){
			return other.getX() + other.getWidth();
		}
		//check if player is inbetween other
		if(other.getX() < this.x && (other.getX()+other.getWidth()) > (this.x+this.width)){
			return this.x;
		}
		//check if player is approaching from left
		if(other.getX() < (this.x+this.width) && other.getX() > this.x){
			return (other.getX() - this.width);
		}
		//check if player is approaching from right
		if((other.getX()+other.getWidth()) > this.x){
			return (other.getX() + other.getWidth());
		}
		return this.x;
	}
	
	public double intersectsY(Entity other, double oldy){
		//check if lagged or moved too fast
		if(oldy < other.getY() && this.y > other.getY()){
			return other.getY() - this.height;
		}
		if(oldy > other.getY()+other.getHeight() && this.y < other.getY()+other.getHeight()){
			return other.getY() + other.getHeight();
		}
		//check if player is inbetween other
		if(other.getY() < this.y && (other.getY()+other.getHeight()) > (this.y+this.height)){
			return this.y;
		}
		//check if player is approaching from left
		if(other.getY() < (this.y+this.height) && other.getY() > this.y){
			return (other.getY() - this.height);
		}
		//check if player is approaching from right
		if((other.getY()+other.getHeight()) > this.y){
			this.land();
			return (other.getY() + other.getHeight());
		}
		return this.y;
	}


}
