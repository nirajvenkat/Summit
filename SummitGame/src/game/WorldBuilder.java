package game;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB;


import entities.PlatformEntity;
import entities.PowerupEntity;


public class WorldBuilder {
	
	public static ArrayList<PlatformEntity> build(){
		
		ArrayList<PlatformEntity> platforms = new ArrayList<PlatformEntity>();
		double i, j;
		
		for(j = 0; j < OGLRenderer.SCREEN_WIDTH; j+=OGLRenderer.SCREEN_WIDTH/3) {
			for(i = 75; i < OGLRenderer.SCREEN_HEIGHT; i+=75){
				Random generator = new Random();
				PlatformEntity plat = new PlatformEntity(generator.nextInt(OGLRenderer.SCREEN_WIDTH-20), i);
				boolean intersection = false;
				boolean diffIntersection = false;
				if(!platforms.isEmpty()){
					PlatformEntity newp = new PlatformEntity(0,0);
					PlatformEntity oldp = new PlatformEntity(0,0);
					double newX, newWidth;
					for(PlatformEntity p : platforms){
						if(p.intersects(plat) && (p.getType() == plat.getType())){
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
						if(p.intersects(plat) && (p.getType() != plat.getType())){
							diffIntersection = true;
						}
					}//endfor
					if(intersection){
						platforms.remove(oldp);
						platforms.add(newp);
					}
				}
				if((!intersection && !diffIntersection) ){
					platforms.add(plat);
				}//endif
			}//endfori
		}//enforj
		
		PlatformEntity floor = new PlatformEntity(0, 0);
		floor.setWidth(OGLRenderer.SCREEN_WIDTH);
		platforms.add(floor);
		
		return platforms;
	}
	
	public static ArrayList<PowerupEntity> spawnPowerups(){
		ArrayList<PowerupEntity> powerups = new ArrayList<PowerupEntity>();
		for(double i = 0; i < OGLRenderer.SCREEN_HEIGHT; i+=44){
			Random generator = new Random();
			PowerupEntity pow = new PowerupEntity(generator.nextInt(OGLRenderer.SCREEN_WIDTH), i);
			powerups.add(pow);
		}
		
		return powerups;
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
	
	/**
     * Loads a PNG texture and puts it in Gl_TEXTURE_RECTANGLE_ARB
     *
     * @param location the location of the png image file
     *
     * @return the generated texture handle or -1 if there was a loading error
     */
    public static int glLoadLinearPNG(String location) {
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_RECTANGLE_ARB, texture);
        InputStream in = null;
        try {
            in = new FileInputStream(location);
            PNGDecoder decoder = new PNGDecoder(in);
            ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.RGBA);
            buffer.flip();
            glTexParameteri(GL_TEXTURE_RECTANGLE_ARB, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_RECTANGLE_ARB, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_RECTANGLE_ARB, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA,
                    GL_UNSIGNED_BYTE, buffer);
        } catch (FileNotFoundException e) {
            System.err.println("Texture file could not be found.");
            return -1;
        } catch (IOException e) {
            System.err.print("Failed to load the texture file.");
            return -1;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        glBindTexture(GL_TEXTURE_RECTANGLE_ARB, 0);
        return texture;
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
	
	public static void playSound(File f) 
	{
	    try 
	    {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f.getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } 
	    catch(Exception ex) 
	    {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
}
