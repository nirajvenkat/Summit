package entities;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB;


import game.OGLRenderer;
import game.WorldBuilder;

import java.awt.Rectangle;
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

public class PlayerEntity implements Entity {

	protected double x, y, width, height, vvel;
	protected Rectangle hitbox = new Rectangle();
	protected boolean jumping, falling;
	protected int id;

	//Sprite variables
	private static int spritesheet;
	private static int spriteNo;
	private static final Map<String, Sprite> spriteMap = new HashMap<String, Sprite>();
	private static Sprite currentSprite;
	private static final String SPRITESHEET_IMAGE_LOCATION = "res/sprites.png";
	private static final String SPRITESHEET_XML_LOCATION = "res/sprites.xml";

	public PlayerEntity(double x, double y, int id) {
		this.x = x;
		this.y = y;
		this.width = 0;
		this.height = 0;
		this.id = id;

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
	}

	public void jump(){
		jumping = true;
		vvel = 2.0f;
	}

	public void land(){
		falling = false;
		jumping = false;
	}

	public boolean isFalling(){
		return falling;
	}

	public void fall(){
		falling = true;
	}

	public boolean isJumping(){
		return jumping;
	}

	public double getVvel(){
		return vvel;
	}

	public void setVvel(double newvel){
		vvel = newvel;
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
			glTexCoord2f(xval, yval);
			glVertex2d(x, y);
			glTexCoord2f(xval, y2val);
			glVertex2d(x + width, y);
			glTexCoord2f(x2val, y2val);
			glVertex2d(x + width, y + height);
			glTexCoord2f(x2val, yval);
			glVertex2d(x, y + height);
		glEnd();
		glBindTexture(GL_TEXTURE_RECTANGLE_ARB, 0);

	}

	public int update(ArrayList<PlatformEntity> platforms, int delta) {
		double x = this.getX();
		double y = this.getY();
		if(this.id == 1){
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) x -= 0.2f * delta;
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) x += 0.2f * delta;

			if (Keyboard.isKeyDown(Keyboard.KEY_UP) && !this.isJumping() && !this.isFalling()) this.jump();
		}
		if(this.id == 2){
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) x -= 0.2f * delta;
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) x += 0.2f * delta;

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
		if(this.isFalling() && newvel > -0.35f){
			newvel -= .035f;
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
			if(this.intersects(plat)){
				if(this.intersectsY(plat) != y){
					y = this.intersectsY(plat);
					this.setY(y);
					if(this.intersects(plat)){
						if(this.intersectsX(plat) != x){
							x = this.intersectsX(plat);
							this.setX(x);
						}
					}
				}else{
					if(this.intersectsX(plat) != x){
						y = this.intersectsX(plat);
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
		spriteNo++;
		if(spriteNo == 7) spriteNo = 1;
		currentSprite = spriteMap.get(spriteNo + ".png");
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
		return hitbox.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}

	@Override
	public double intersectsX(Entity other){
		//check if player is inbetween other
		if(other.getX() < x && (other.getX()+other.getWidth()) > (x+width)){
			return x;
		}
		//check if player is approaching from left
		if(other.getX() < (x+width) && other.getX() > x){
			return (other.getX() - width);
		}
		//check if player is approaching from right
		if((other.getX()+other.getWidth()) > x){
			return (other.getX() + other.getWidth());
		}
		return x;
	}

	@Override
	public double intersectsY(Entity other){
		if(other.getY() < y && (other.getY()+other.getHeight()) > (y+height)){
			return y;
		}
		if(other.getY() < (y+height) && other.getY() > y){
			return (other.getY() - height);
		}
		if((other.getY()+other.getHeight()) > y){
			this.land();
			return (other.getY() + other.getHeight());
		}
		return y;
	}


}
