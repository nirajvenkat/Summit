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
	public static final int MAX_NAME_LENGTH = 11;
	
	Font menuFont;
	JButton close;
	JTextField name;
	Socket s;
	DataOutputStream out;
	DataInputStream in;
	boolean isHighScore = false;
	
	//public SummitVictoryScreen(ArrayList<entities.PlayerEntity> players)
	public SummitVictoryScreen(ArrayList<TestPlayer> players)
	{
		setResizable(false);
		setSize(700, 125);
		setLayout(new BorderLayout());
		String background_path = System.getProperty("user.dir") + "/src/game/images/highscores.png";
		String winner_path = System.getProperty("user.dir") + "/src/game/images/winner.png";
		setContentPane(new JLabel(new ImageIcon(background_path)));
		setLayout(new BorderLayout());
		
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
	    
	    JPanel playerPanel = new JPanel(new GridLayout(players.size(), 3));
	    playerPanel.setOpaque(false);
	    
	    
	    //entities.PlayerEntity winner = players.get(0);
	    TestPlayer winner = players.get(0);
	    
	    //for(entities.PlayerEntity player : players)
	    for(TestPlayer player : players)
	    {
	    	
	    	if(player.score > winner.score)
	    	{
	    		winner = player;
	    	}
	    	else
	    	{
	    		playerPanel.add(new JLabel(""));
	    		JLabel tmp_player = new JLabel("Player " + player.id);
	    		tmp_player.setForeground(foregroundColor);
	    		tmp_player.setFont(menuFont);
	    		playerPanel.add(tmp_player);
	    		JLabel tmp_score = new JLabel(player.score + "");
	    		tmp_score.setForeground(foregroundColor);
	    		tmp_score.setFont(menuFont);
		    	playerPanel.add(tmp_score);
	    	}
	    }
	    
	    playerPanel.add(new JLabel(new ImageIcon(winner_path)));
	    JLabel tmp_player = new JLabel("Player " + winner.id);
	    tmp_player.setForeground(foregroundColor);
		tmp_player.setFont(menuFont);
	    playerPanel.add(tmp_player);
	    JLabel tmp_score = new JLabel(winner.score + "");
	    tmp_score.setFont(menuFont);
	    tmp_score.setForeground(foregroundColor);
	    tmp_score.setText(winner.score + "");
    	playerPanel.add(tmp_score);
	    
	    add(playerPanel, BorderLayout.NORTH);
	    
	    
	    try
	    {
	    	s = new Socket();
	    	s.connect(new InetSocketAddress(SummitMenu.SERVER_ADDR, SummitMenu.SERVER_PORT), 3000);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			out.writeUTF("PUT;" + winner.score);
			int resp = in.readInt();
			if(resp == 1)
			{
				WorldBuilder.playSound(new File(System.getProperty("user.dir") + "/src/game/media/victory.wav"));
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
				JLabel promptLabel = new JLabel("Player " + winner.id + " Has a New High Score!");
				promptLabel.setFont(menuFont);
				promptLabel.setForeground(Color.WHITE);
				inputPanel.add(promptLabel, BorderLayout.NORTH);
				inputPanel.add(nameLabel, BorderLayout.WEST);
				inputPanel.add(name, BorderLayout.EAST);
				inputPanel.add(close, BorderLayout.SOUTH);
				add(inputPanel, BorderLayout.SOUTH);
			}
			else if(resp == 0)
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
	
	public static void main(String args[])
	{
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// TODO Auto-generated method stub

		if(isHighScore)
		{
			try
			{
				String puck = name.getText();
				System.out.println("Size is " + puck.length());
				if(puck != null && !puck.isEmpty())
				{
					if(puck.length() > MAX_NAME_LENGTH)
					{
						puck = puck.substring(0, MAX_NAME_LENGTH -1 );
					}
					out.writeUTF(puck);
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

			setVisible(false);
			dispose();
		
	}

}
