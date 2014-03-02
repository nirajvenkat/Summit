package entities;

import static org.lwjgl.opengl.GL11.*;
import game.OGLRenderer;
import game.WorldBuilder;

import java.awt.Rectangle;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;

public class PlayerEntity implements Entity {

	protected double x, y, width, height, vvel;
	protected Rectangle hitbox = new Rectangle();
	protected boolean jumping, falling;
	
	public PlayerEntity(double x, double y) {
		this.x = x;
		this.y = y;
		this.width = 10;
		this.height = 20;
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
		
		glPushMatrix();
		glBegin(GL_QUADS);
			glColor3d(1, 0, 0);
			glVertex2d(x, y);
			glColor3d(1, 0, 0);
			glVertex2d(x + width, y);
			glColor3d(1, 0, 0);
			glVertex2d(x + width, y + height);
			glColor3d(1, 0, 0);
			glVertex2d(x, y + height);
		glEnd();
		glPopMatrix();
		
		
	}

	@Override
	public void update() {
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
