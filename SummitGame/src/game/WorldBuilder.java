package game;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import entities.PlatformEntity;


public class WorldBuilder {
	
	public static ArrayList<PlatformEntity> build(){
		
		ArrayList<PlatformEntity> platforms = new ArrayList<PlatformEntity>();
		double i, j;
		
		for(j = 0; j < OGLRenderer.SCREEN_WIDTH; j+=OGLRenderer.SCREEN_WIDTH/3) {
			for(i = 75; i < OGLRenderer.SCREEN_HEIGHT; i+=75){
				Random generator = new Random();
				PlatformEntity plat = new PlatformEntity(generator.nextInt(OGLRenderer.SCREEN_WIDTH), i);
				boolean intersection = false;
				if(!platforms.isEmpty()){
					PlatformEntity newp = new PlatformEntity(0,0);
					PlatformEntity oldp = new PlatformEntity(0,0);
					double newX, newWidth;
					for(PlatformEntity p : platforms){
						if(p.intersects(plat)){
							intersection = true;
							if(p.getX() < plat.getX()){
								newX = p.getX();
								newWidth = (plat.getX()+plat.getWidth())-p.getX();
							}else{
								newX = plat.getX();
								newWidth = (p.getX()+p.getWidth())-plat.getX();
							}
							newp = new PlatformEntity(newX, p.getY());
							newp.setWidth(newWidth);
							oldp = p;
						}//endif
					}//endfor
					if(intersection){
						platforms.remove(oldp);
						platforms.add(newp);
					}
				}
				if(!intersection || platforms.isEmpty()){
					platforms.add(plat);
				}//endif
			}//endfori
		}//enforj
		
		PlatformEntity floor = new PlatformEntity(0, 0);
		floor.setWidth(OGLRenderer.SCREEN_WIDTH);
		platforms.add(floor);
		
		return platforms;
	}
	
	public static Texture loadTexture(String textureFilename){
		try {
			Texture texture = TextureLoader.getTexture("PNG", new FileInputStream(textureFilename));
			return texture;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Texture loadBackground(String textureFilename){
		try {
			Texture texture = TextureLoader.getTexture("JPG", new FileInputStream(textureFilename));
			return texture;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
