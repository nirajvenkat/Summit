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
		
		for(double i = 0; i < OGLRenderer.SCREEN_HEIGHT; i+=100){
			Random generator = new Random();
			PlatformEntity plat = new PlatformEntity(generator.nextInt(OGLRenderer.SCREEN_WIDTH), i);
			platforms.add(plat);
		}
		
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
