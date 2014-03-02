package entities;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2d;
import game.WorldBuilder;

import java.awt.Rectangle;

import org.newdawn.slick.opengl.Texture;

public class PlatformEntity implements Entity {
	
	protected double x, y, width, height;
	protected Rectangle hitbox = new Rectangle();
	protected enum platformType {
		FIXED, MOVING, STICKY, LAVA
	}
	
	public PlatformEntity(double x, double y) {
		this.x = x;
		this.y = y;
		this.width = 200;
		this.height = 10;
	}

	@Override
	public void draw() {
		
		Texture platTexture = WorldBuilder.loadTexture("res/stone.png");
		platTexture.bind();
		
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
		//Platforms dont move... for now
		
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
	public double intersectsX(Entity other) {
		return 0;
	}
	
	@Override
	public double intersectsY(Entity other) {
		return 0;
	}

}
