package game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.*;

public class SummitMenu extends JFrame implements ActionListener, MouseListener, KeyListener
{
	//Colors for mouseover and mousedown on menu items
	public static final Color MOUSE_OVER_COLOR = Color.decode("#3399FF");
	public static final Color MOUSE_DOWN_COLOR = Color.decode("#000099");
	
	//Server address and port to connect to high scores server
	public static final String SERVER_ADDR = "98.226.145.27";
	public static final int SERVER_PORT = 9998;
	
	//Default values for video settings
	public static final int DEFAULT_SCREEN_WIDTH = 1024;
	public static final int DEFAULT_SCREEN_HEIGHT = 768;
	public static final int DEFAULT_FRAMES_PER_SECOND = 60;
	
	//Unused. Don't worry about this...
	public static final int SHAKE_DURATION = 3000;
	public static final int UPDATE_TIME = 5;
	public static final boolean DO_SHAKE = false;
	
	JButton start_game, exit_game, view_scores, view_instructions, view_settings; //Buttons for menu items
	Font menuFont; //Custom font used
	Preferences prefs; //Preferences object to hold user preferences
	boolean alt_down = false; //Unused. Don't worry about this
	Clip clip; //Sound clip to play music and menu effects
	Icon gifIcon; //Animated UFO sprite
	
	//These fields not used. Don't worry about these
	Point primaryLocation; 
    long startTime;
    Timer time;
    ActionTime timeListener = new ActionTime();
	
	public SummitMenu()
	{
		//Set window size, title, and resizable value
		setSize(700,550);
		setTitle("Summit");
		setResizable(true);
		
		//Set background image from file
		setLayout(new BorderLayout());
		String background_path = System.getProperty("user.dir") + "/res/images/menu.jpg";
		String logo_path = System.getProperty("user.dir") + "/res/images/menu_logo.jpg";
		setContentPane(new JLabel(new ImageIcon(background_path)));
	    setLayout(new FlowLayout());
	    
	    //Load preferences
	    prefs = Preferences.userRoot().node(SummitSettings.class.getName());
	    
	    try //Play Sandstorm!
	    {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(System.getProperty("user.dir") + "/res/media/sandstorm.wav").getAbsoluteFile());
	        clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    
	    //Load custom menu font from web
	    menuFont = new Font("Serif", Font.PLAIN, 50); 
	    try
	    {
	    	URL fontUrl = new URL("https://www.cs.purdue.edu/homes/scdickso/Arkitech_Medium.ttf");
	    	menuFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
	    	menuFont = menuFont.deriveFont(Font.BOLD,30);
	    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        ge.registerFont(menuFont);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	menuFont = new Font("Serif", Font.PLAIN, 50);
	    }
	    
	    try //Load animated sprits gif from web
		{
			URL gifURL = new URL("https://www.cs.purdue.edu/homes/scdickso/sprite.gif");
	        gifIcon = new ImageIcon(gifURL);
	        JLabel gifLabel = new JLabel(gifIcon);
	        gifLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        add(gifLabel, BorderLayout.NORTH);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	        
	    //Set up menu buttons, layout, and appearance
		start_game = new JButton("Play Game");
		start_game.setFont(menuFont);
		start_game.setOpaque(false);
		start_game.setContentAreaFilled(false);
		start_game.setBorderPainted(false);
		exit_game = new JButton("Exit");
		exit_game.setFont(menuFont);
		exit_game.setOpaque(false);
		exit_game.setContentAreaFilled(false);
		exit_game.setBorderPainted(false);
		view_scores = new JButton("Show Highscores");
		view_scores.setFont(menuFont);
		view_scores.setOpaque(false);
		view_scores.setContentAreaFilled(false);
		view_scores.setBorderPainted(false);
		view_instructions = new JButton("How to Play");
		view_instructions.setFont(menuFont);
		view_instructions.setOpaque(false);
		view_instructions.setContentAreaFilled(false);
		view_instructions.setBorderPainted(false);
		view_settings = new JButton("Settings");
		view_settings.setFont(menuFont);
		view_settings.setOpaque(false);
		view_settings.setContentAreaFilled(false);
		view_settings.setBorderPainted(false);
		start_game.addActionListener(this);
		start_game.addKeyListener(this);
		start_game.addMouseListener(this);
		exit_game.addActionListener(this);
		exit_game.addMouseListener(this);
		view_scores.addActionListener(this);
		view_scores.addMouseListener(this);
		view_instructions.addActionListener(this);
		view_instructions.addMouseListener(this);
		view_settings.addActionListener(this);
		view_settings.addMouseListener(this);
		JLabel logo = new JLabel(new ImageIcon(logo_path));
		JPanel midPanel = new JPanel();
		midPanel.setLayout(new GridLayout(5,1,10,10));
		midPanel.setOpaque(false);
		add(logo, BorderLayout.NORTH);
		JPanel space = new JPanel();
		space.setOpaque(false);
		space.setPreferredSize(new Dimension(500,50));
		add(space, BorderLayout.CENTER);
		midPanel.add(start_game);
		midPanel.add(view_scores);
		midPanel.add(view_instructions);
		midPanel.add(view_settings);
		midPanel.add(exit_game);
		add(midPanel, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		//Not a thing.
		if(DO_SHAKE) startShake();
	}
	
	public static void main(String args[])
	{
		SummitMenu weboshi = new SummitMenu();
	}
	

	@Override
	public void actionPerformed(ActionEvent e) //Handles click action for menu items
	{
		if(e.getSource().equals(start_game)) //User clicks "Start Game"
		{
			//Load preferences from prefs file
			int width = prefs.getInt("WIDTH", DEFAULT_SCREEN_WIDTH);
			int height = prefs.getInt("HEIGHT", DEFAULT_SCREEN_HEIGHT);
			int fps = prefs.getInt("FPS", DEFAULT_FRAMES_PER_SECOND);
			
			//Create dialog with player options
			String[] buttons = { "One Player", "Two Players (Local)" };    
			int returnValue = 0; //Initially 0 return value. Can be -1 if user closes dialog box without selecting an option.
			returnValue = JOptionPane.showOptionDialog(null, "Please Select Game Mode", "Play Game",
			        JOptionPane.PLAIN_MESSAGE, 0, gifIcon, buttons, buttons[0]);
			
			//Start game with number of players specified. OGLRenderer.start(int) takes as an argument the number of players in the game
			if(alt_down)
			{
				OGLRenderer game = new OGLRenderer(width, height, fps);
				game.start((++returnValue + 1));
			}
			else
			{
				OGLRenderer game = new OGLRenderer(width, height, fps);
				game.start((++returnValue + 1));
			}
		}
		else if(e.getSource().equals(view_instructions)) //User clicks "View instructions"
		{
			try
			{
				//Connect to high scores server
				Socket s = new Socket();
				s.connect(new InetSocketAddress(SERVER_ADDR, SERVER_PORT), 3000);
				DataInputStream in = new DataInputStream(s.getInputStream());
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				out.writeUTF("GET"); //GET high scores
				String data = in.readUTF(); //Server returns data as string
				out.close();
				in.close();
				s.close();
				SummitHighScores shs = new SummitHighScores(data, menuFont);
			}
			catch(Exception ex)
			{
				JOptionPane.showMessageDialog(null, "Error Loading High Scores. Please Try Again Later...");
			}
		}
		else if(e.getSource().equals(view_scores)) //User clicks "View High Scores"
		{
			SummitInstructions si = new SummitInstructions(menuFont);
		}
		else if(e.getSource().equals(view_settings)) //User clicks "Settings"
		{
			SummitSettings ss = new SummitSettings(menuFont);
		}
		else if(e.getSource().equals(exit_game)) //User clicks "Exit"
		{
			//Set preferences to default values
			prefs.putInt("WIDTH", DEFAULT_SCREEN_WIDTH);
			prefs.putInt("HEIGHT", DEFAULT_SCREEN_HEIGHT);
			prefs.putInt("FPS", DEFAULT_FRAMES_PER_SECOND);
			setVisible(false); //Quit program
		}
		
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		//Don't worry about this
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			alt_down = true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		//Don't worry about this
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			alt_down = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		//Play sound when user clicks on a menu item
		WorldBuilder.playSound(new File(System.getProperty("user.dir") + "/res/media/down.wav"));
		
		//Change color when user clicks on menu item
		if(e.getSource().equals(start_game))
		{
			start_game.setForeground(MOUSE_DOWN_COLOR);
		}
		else if(e.getSource().equals(exit_game))
		{
			exit_game.setForeground(MOUSE_DOWN_COLOR);
		}
		else if(e.getSource().equals(view_scores))
		{
			view_scores.setForeground(MOUSE_DOWN_COLOR);
		}
		else if(e.getSource().equals(view_instructions))
		{
			view_instructions.setForeground(MOUSE_DOWN_COLOR);
		}
		else if(e.getSource().equals(view_settings))
		{
			view_settings.setForeground(MOUSE_DOWN_COLOR);
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		//Change color back to original when user clicks on menu item
		if(e.getSource().equals(start_game))
		{
			start_game.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(exit_game))
		{
			exit_game.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(view_scores))
		{
			view_scores.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(view_instructions))
		{
			view_instructions.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(view_settings))
		{
			view_settings.setForeground(MOUSE_OVER_COLOR);
		}
	}
	
	//Unused
	public void startShake()
    {
        primaryLocation = getLocation();
        startTime = System.currentTimeMillis();
        time= new Timer(UPDATE_TIME,timeListener);
        time.start();
    }
     
	//Unused
    public void stopShake()
    {
        time.stop();
        setLocation(primaryLocation);
        setVisible(true);
        repaint();
    }
     
    //Unused
    private class ActionTime implements ActionListener
    {
         private int xOffset, yOffset;

        @Override
         public void actionPerformed(ActionEvent e)
         {
             long elapsedTime = System.currentTimeMillis() - startTime;
             Random r = new Random();
             int op = r.nextInt(5);
              
             if ( op > 0)
             {
                
                xOffset = primaryLocation.x + (r.nextInt(20));
                yOffset = primaryLocation.y + (r.nextInt(20));
                setLocation(xOffset,yOffset);
                setVisible(false);                
                repaint();
             }
             else
             {
                
                xOffset = primaryLocation.x - (r.nextInt(20));
                yOffset = primaryLocation.y - (r.nextInt(20));
                setLocation(xOffset,yOffset);
                setVisible(true);
                repaint();
             }
             
             if(elapsedTime > SHAKE_DURATION)
             {   
                stopShake();
             }
         }
    }

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		//Change color when user mouses over menu item
		WorldBuilder.playSound(new File(System.getProperty("user.dir") + "/res/media/over.wav"));
		if(e.getSource().equals(start_game))
		{
			start_game.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(exit_game))
		{
			exit_game.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(view_scores))
		{
			view_scores.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(view_instructions))
		{
			view_instructions.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(view_settings))
		{
			view_settings.setForeground(MOUSE_OVER_COLOR);
		}
		
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		//Change color back to original when user's mouse exists menu item
		if(e.getSource().equals(start_game))
		{
			start_game.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(exit_game))
		{
			exit_game.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(view_scores))
		{
			view_scores.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(view_instructions))
		{
			view_instructions.setForeground(MOUSE_OVER_COLOR);
		}
		else if(e.getSource().equals(view_settings))
		{
			view_settings.setForeground(MOUSE_OVER_COLOR);
		}
		
	}
	
	

}
