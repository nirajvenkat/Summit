package entities;

import java.awt.Rectangle;
import java.util.Random;

import org.newdawn.slick.opengl.Texture;

import game.WorldBuilder;
import static org.lwjgl.opengl.GL11.*;

public class PowerupEntity implements Entity {

	protected double x, y, width, height;
	protected Rectangle hitbox = new Rectangle();
	int type;
	protected int points;
	protected final int numTypes = 2;
	
	public PowerupEntity(double x, double y){
		this.x = x;
		this.y = y;
		this.width = 5;
		this.height = 10;
		Random generator = new Random();
		this.type = generator.nextInt(numTypes);
		switch(this.type){
		case 0:
			this.points = 3;
			break;
			
		case 1:
			this.points = 4;
			break;
		}
	}
	
	@Override
	public void draw() {
		switch(this.type){
			case 0:
				glBegin(GL_QUADS);
					glColor3d(0.7, 1, 0.2);
					glVertex2d(x, y);
					glColor3d(0.7, 1, 0.2);
					glVertex2d(x + width, y);
					glColor3d(0.7, 1, 0.2);
					glVertex2d(x + width, y + height);
					glColor3d(0.7, 1, 0.2);
					glVertex2d(x, y + height);
				glEnd();
				break;
			
			case 1:
				glBegin(GL_QUADS);
					glColor3d(0.7, 0, 0.2);
					glVertex2d(x, y);
					glColor3d(0.7, 0, 0.2);
					glVertex2d(x + width, y);
					glColor3d(0.7, 0, 0.2);
					glVertex2d(x + width, y + height);
					glColor3d(0.7, 0, 0.2);
					glVertex2d(x, y + height);
				glEnd();
				break;
			
		}
	}
	
	public void updateStats(PlayerEntity p){
		//TODO update the player's stats
		p.addPoints(this.points);
		switch(this.type){
			case 0:
				p.addJumpVel(0.15f);
				break;
				
			case 1:
				p.addFallVel(0.05f);
				break;
				
		}
	}

	@Override
	public void update() {
		//Don't do anything with update
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

}
