package game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;


public class SummitVictoryScreen extends JFrame implements ActionListener
{
	public static final Color foregroundColor = Color.decode("#FFCC00");
	public static final int MAX_NAME_LENGTH = 11; //Max length of name. Preserves formatting in HighScores
	
	Font menuFont;
	JButton close;
	JTextField name;
	
	//Connection to high scores server
	Socket s;
	DataOutputStream out;
	DataInputStream in;
	
	boolean isHighScore = false;
	ArrayList<entities.PlayerEntity> players; //List of players
	
	public SummitVictoryScreen(ArrayList<entities.PlayerEntity> players)
	{
		this.players = players;
		setResizable(false);
		setSize(700, 125);
		
		//Load background image
		setLayout(new BorderLayout());
		String background_path = System.getProperty("user.dir") + "/res/images/highscores.png";
		String winner_path = System.getProperty("user.dir") + "/res/images/winner.png";
		setContentPane(new JLabel(new ImageIcon(background_path)));
		setLayout(new BorderLayout());
		
		//Get menu font from web
		menuFont = new Font("Serif", Font.PLAIN, 50); 
	    try
	    {
	    	URL fontUrl = new URL("https://www.cs.purdue.edu/homes/scdickso/Arkitech_Medium.ttf");
	    	menuFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
	    	menuFont = menuFont.deriveFont(Font.BOLD,25);
	    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        ge.registerFont(menuFont);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	menuFont = new Font("Serif", Font.PLAIN, 50);
	    }
	    
	    //Setup JFrame
	    JPanel playerPanel = new JPanel(new GridLayout(players.size(), 3));
	    playerPanel.setOpaque(false);
	    
	    entities.PlayerEntity winner = players.get(0);
	    entities.PlayerEntity other = players.get(0);
	    
	    	//Loop through players and choose player with highest score
		    for(entities.PlayerEntity player : players)
		    {
		    	
		    	if(player.getScore() > winner.getScore())
		    	{
		    		winner = player;
		    	}
		    	else
		    	{
		    		other = player;
		    	}
		    }
	    
		//Display user information in JLabel
	    for(entities.PlayerEntity player : players)
	    {
	    		if(player.equals(winner)) //If user is winner
	    		{
	    			playerPanel.add(new JLabel("")); //Display blank label
	    		}
	    		else //If user is not winner
	    		{
	    			playerPanel.add(new JLabel(new ImageIcon(winner_path))); //Display star
	    		}
	    		
	    		//Add text to JLabel and add to panel
	    		JLabel tmp_player = new JLabel("Player " + player.getID());
	    		tmp_player.setForeground(foregroundColor);
	    		tmp_player.setFont(menuFont);
	    		playerPanel.add(tmp_player);
	    		JLabel tmp_score = new JLabel(player.getScore() + "");
	    		tmp_score.setForeground(foregroundColor);
	    		tmp_score.setFont(menuFont);
		    	playerPanel.add(tmp_score);
	    }
	    
	    add(playerPanel, BorderLayout.NORTH);
	    
	    
	    try //Connect to high scores server and see if winning score is a high score
	    {
	    	s = new Socket();
	    	s.connect(new InetSocketAddress(SummitMenu.SERVER_ADDR, SummitMenu.SERVER_PORT), 3000);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			out.writeUTF("PUT;" + winner.getScore());
			int resp = in.readInt();
			if(resp == 1) //If 1 is returned from server, it's a high score
			{
				WorldBuilder.playSound(new File(System.getProperty("user.dir") + "/res/media/victory.wav")); //YAY VICTORY SOUND
				
				//Change JFrame and allow user to enter a name
				setSize(700, 275);
				isHighScore = true;
				name = new JTextField("", MAX_NAME_LENGTH);
				name.setFont(menuFont);
				close = new JButton("Submit");
				close.setFont(menuFont);
				close.addActionListener(this);
				close.setPreferredSize(new Dimension(50,50));
				JPanel inputPanel = new JPanel(new BorderLayout(15,5));
				inputPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
				inputPanel.setOpaque(false);
				JLabel nameLabel = new JLabel("Your Name: ");
				nameLabel.setFont(menuFont);
				nameLabel.setForeground(Color.WHITE);
				JLabel promptLabel = new JLabel("Player " + other.getID() + " Has a New High Score!");
				promptLabel.setFont(menuFont);
				promptLabel.setForeground(Color.WHITE);
				inputPanel.add(promptLabel, BorderLayout.NORTH);
				inputPanel.add(nameLabel, BorderLayout.WEST);
				inputPanel.add(name, BorderLayout.EAST);
				inputPanel.add(close, BorderLayout.SOUTH);
				add(inputPanel, BorderLayout.SOUTH);
			}
			else if(resp == 0) //0 return value means score is not a high score
			{
				isHighScore = false;
				close = new JButton("Close");
				close.setFont(menuFont);
				close.addActionListener(this);
				close.setPreferredSize(new Dimension(50,50));
				add(close, BorderLayout.SOUTH);
			}
			
	    }
	    catch(Exception e)
	    {
	    	
	    }
	    
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{

		if(isHighScore) //Save score on server on exit if it's a high score
		{
			try
			{
				String puck = name.getText();

				if(puck != null) //Check if name is null
				{
					out.writeUTF(puck); //Write out to server and close connection
					out.close();
					in.close();
					s.close();
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

			SummitVictoryScreen svs = new SummitVictoryScreen(players); //Finally, close window
		
	}

}
