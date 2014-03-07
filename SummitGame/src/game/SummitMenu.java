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
import java.util.prefs.*;

public class SummitMenu extends JFrame implements ActionListener, MouseListener, KeyListener
{
	public static final Color MOUSE_OVER_COLOR = Color.decode("#3399FF");
	public static final Color MOUSE_DOWN_COLOR = Color.decode("#000099");
	public static final int CODE_MOUSE_DOWN = 1;
	public static final int CODE_MOUSE_OVER = 2;
	public static final String SERVER_ADDR = "98.226.145.27";
	public static final int SERVER_PORT = 9998;
	public static final int DEFAULT_SCREEN_WIDTH = 1024;
	public static final int DEFAULT_SCREEN_HEIGHT = 768;
	public static final int DEFAULT_FRAMES_PER_SECOND = 60;
	
	JButton start_game, exit_game, view_scores, view_instructions, view_settings;
	Font menuFont;
	Preferences prefs;
	boolean alt_down = false;
	
	public SummitMenu()
	{
		setSize(700,550);
		setTitle("Summit");
		setResizable(false);
		setLayout(new BorderLayout());
		String background_path = System.getProperty("user.dir") + "/src/game/images/menu.jpg";
		String logo_path = System.getProperty("user.dir") + "/src/game/images/menu_logo.jpg";
		setContentPane(new JLabel(new ImageIcon(background_path)));
	    setLayout(new FlowLayout());    
	    
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
	    
	    try
		{
			URL gifURL = new URL("https://www.cs.purdue.edu/homes/scdickso/sprite.gif");
	        Icon gifIcon = new ImageIcon(gifURL);
	        JLabel gifLabel = new JLabel(gifIcon);
	        gifLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        add(gifLabel, BorderLayout.NORTH);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	        
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
	}
	
	public static void main(String args[])
	{
		SummitMenu weboshi = new SummitMenu();
	}
	

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource().equals(start_game))
		{
			prefs = Preferences.userRoot().node(SummitSettings.class.getName());
			int width = prefs.getInt("WIDTH", DEFAULT_SCREEN_WIDTH);
			int height = prefs.getInt("HEIGHT", DEFAULT_SCREEN_HEIGHT);
			int fps = prefs.getInt("FPS", DEFAULT_FRAMES_PER_SECOND);
			
			if(alt_down)
			{
				OGLRenderer game = new OGLRenderer(width, height, fps);
				game.start(1);
			}
			else
			{
				
			}
		}
		else if(e.getSource().equals(view_scores))
		{
			try
			{
				Socket s = new Socket();
				s.connect(new InetSocketAddress(SERVER_ADDR, SERVER_PORT), 3000);
				DataInputStream in = new DataInputStream(s.getInputStream());
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				out.writeUTF("GET");
				String data = in.readUTF();
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
		else if(e.getSource().equals(view_instructions))
		{
			SummitInstructions si = new SummitInstructions(menuFont);
		}
		else if(e.getSource().equals(view_settings))
		{
			SummitSettings ss = new SummitSettings(menuFont);
		}
		else if(e.getSource().equals(exit_game))
		{
			System.exit(0);
		}
		
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			alt_down = true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		playSound(CODE_MOUSE_DOWN);
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
		// TODO Auto-generated method stub
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
	public void mouseEntered(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		playSound(CODE_MOUSE_OVER);
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
		// TODO Auto-generated method stub
		if(e.getSource().equals(start_game))
		{
			start_game.setForeground(Color.BLACK);
		}
		else if(e.getSource().equals(exit_game))
		{
			exit_game.setForeground(Color.BLACK);
		}
		else if(e.getSource().equals(view_scores))
		{
			view_scores.setForeground(Color.BLACK);
		}
		else if(e.getSource().equals(view_instructions))
		{
			view_instructions.setForeground(Color.BLACK);
		}
		else if(e.getSource().equals(view_settings))
		{
			view_settings.setForeground(Color.BLACK);
		}
		
	}
	
	public void playSound(int code) 
	{
	    try 
	    {
	    	File f = null;
	    	switch(code)
	    	{
	    	case CODE_MOUSE_OVER:
	    		f = new File(System.getProperty("user.dir") + "/src/game/media/over.wav");
	    		break;
	    	case CODE_MOUSE_DOWN:
	    		f = new File(System.getProperty("user.dir") + "/src/game/media/down.wav");
	    		break;
	    	}
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