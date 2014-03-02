package entities;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2d;
import game.WorldBuilder;

import java.awt.Rectangle;

import org.newdawn.slick.opengl.Texture;

public class PlayerEntity implements Entity {

	protected double x, y, width, height;
	protected Rectangle hitbox = new Rectangle();
	
	public PlayerEntity(double x, double y) {
		this.x = x;
		this.y = y;
		this.width = 10;
		this.height = 20;
	}
	
	@Override
	public void draw() {
		Texture playTexture = WorldBuilder.loadTexture("res/stone.png");
		playTexture.bind();
		
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0);
			glVertex2d(x, y);
			glTexCoord2f(0, 1);
			glVertex2d(x + width, y);
			glTexCoord2f(1, 1);
			glVertex2d(x + width, y + height);
			glTexCoord2f(1, 0);
			glVertex2d(x, y + height);
		glEnd();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

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
			return (other.getY() + other.getHeight());
		}
		return y;
	}

}
