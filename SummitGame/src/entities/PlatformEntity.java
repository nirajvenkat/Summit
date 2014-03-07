package entities;

import static org.lwjgl.opengl.GL11.*;
import game.OGLRenderer;
import game.WorldBuilder;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.opengl.Texture;

public class PlatformEntity implements Entity {
	
	protected double x, y, width, height;
	protected Rectangle hitbox = new Rectangle();
	protected int type;
	private boolean up, right, left, down;
	protected final int numTypes = 2;
//	protected enum platformType {
//		FIXED, MOVING, STICKY, LAVA
//	}
	
	public PlatformEntity(double x, double y) {
		this.x = x;
		this.y = y;
		Random generator = new Random();
		if(x == 0 && y == 0){
			this.type = 0;
		}else{
			this.type = generator.nextInt(numTypes);
		}
		switch(this.type){
		case 0:
			//fixed
			//nothing to set
			this.width = generator.nextDouble()*200;
			if(this.width < 30){
				this.width = 30;
			}
			this.height = 10;
			break;
			
		case 1:
			//moving on x axis
			this.left = generator.nextBoolean();
			this.width = 70;
			this.height = 10;
			if(this.left){
				this.right = false;
			}else{
				this.right = true;
			}
			break;
			
		case 2:
			//moving on y axis
			this.up = generator.nextBoolean();
			if(this.up){
				this.down = false;
			}else{
				this.down = true;
			}
			break;
		}
	}
	
	public int getType(){
		return this.type;
	}

	@Override
	public void draw() {
		
//		Texture platTexture = WorldBuilder.loadTexture("res/stone.png");
//		platTexture.bind();
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
		
		switch(type){
		case 0:
		glBegin(GL_QUADS);
			glColor3d(0.7, 0.7, 0.7);
			glVertex2d(x, y);
			glColor3d(0.7, 0.7, 0.7);
			glVertex2d(x + width, y);
			glColor3d(0.7, 0.7, 0.7);
			glVertex2d(x + width, y + height);
			glColor3d(0.7, 0.7, 0.7);
			glVertex2d(x, y + height);
		glEnd();
			break;
			
		case 1:
			glBegin(GL_QUADS);
				glColor3d(0.7, 0.7, 0.7);
				glVertex2d(x, y);
				glColor3d(0.7, 0.7, 0.7);
				glVertex2d(x + width, y);
				glColor3d(0.7, 0.7, 0.7);
				glVertex2d(x + width, y + height);
				glColor3d(0.7, 0.7, 0.7);
				glVertex2d(x, y + height);
			glEnd();
			break;
		}
		
	}
	
	public void update(ArrayList<PlatformEntity> platforms, int delta){
		double newx = this.getX();
		double newy = this.getY();
		double oldx = this.getX();
		double oldy = this.getY();
		
		switch(this.type){
		case 0:
			//fixed
			//do nothing
			break;
			
		case 1:
			//moving on x axis
			if(this.right){
				newx += 0.15f * delta;
			}else{
				newx -= 0.15f * delta;
			}
			this.setX(newx);
			
			/*for(PlatformEntity p : platforms){
				if(this.intersects(p)){
					if(this.intersectsX(p, oldx, false) != newx){
						if(this.right && p.type == 1){
							p.right = false;
						}else{
							if(p.type == 1){
								p.right = true;
							}
						}
						newx = this.intersectsX(p, oldx, true);
						this.setX(newx);
					}
				}
			}*/
			if (newx+this.getWidth() < 0){
				this.right = true;
			}
			if ((newx > OGLRenderer.SCREEN_WIDTH)) {
				this.right = false;
			}
			
			this.setX(newx);
			break;
			
		case 2:
			//moving on y axis
			break;
		}//end switch
	}

	@Override
	public void update() {
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
	
	public double intersectsX(Entity other, double oldx, boolean edit){
		//check if lagged or moved too fast
		//from left
		if(oldx < other.getX() && this.x > other.getX()){
			if(edit){
				this.right = false;
			}
			return other.getX() - this.width;
		}
		//from right
		if(oldx > other.getX()+other.getWidth() && this.x < other.getX()+other.getWidth()){
			if(edit){
				this.right = true;
			}
			return other.getX() + other.getWidth();
		}
		//check if inbetween other
		if(other.getX() < this.x && (other.getX()+other.getWidth()) > (this.x+this.width)){
			return this.x;
		}
		//check if approaching from left
		if(other.getX() < (this.x+this.width) && other.getX() > this.x){
			if(edit){
				this.right = false;
			}
			return (other.getX() - this.width);
		}
		//check if approaching from right
		if((other.getX()+other.getWidth()) > this.x && (other.getX()+other.getWidth()) < (this.x+this.width)){
			if(edit){
				this.right = true;
			}
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
		//check if inbetween other
		if(other.getY() < this.y && (other.getY()+other.getHeight()) > (this.y+this.height)){
			return this.y;
		}
		//TODO check if approaching from above
		if(other.getY() < (this.y+this.height) && other.getY() > this.y){
			return (other.getY() - this.height);
		}
		//TODO check if approaching from left
		if((other.getY()+other.getHeight()) > this.y){
			return (other.getY() + other.getHeight());
		}
		return this.y;
	}

}
