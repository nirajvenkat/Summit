package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SummitHighScores extends JFrame implements ActionListener
{
	public static final Color foregroundColor = Color.decode("#FFCC00");
	JButton close;
	JFrame frame;
	
	public SummitHighScores(String data, Font font)
	{
		frame = this;
		setLayout(new BorderLayout());
		String background_path = System.getProperty("user.dir") + "/res/images/highscores.png";
		setContentPane(new JLabel(new ImageIcon(background_path)));
		setLayout(new FlowLayout());
		String scores[] = data.split(";");
		font = font.deriveFont(Font.BOLD,15);
		setSize(500,250);
		setTitle("Summit Wall of Champions");
		JLabel display_name = new JLabel("<html>");
		display_name.setForeground(foregroundColor);
		JLabel display_score = new JLabel("<html>");
		display_score.setForeground(foregroundColor);
		int num = 1;
		for(String score : scores)
		{
			String[] entry = score.split(":");			
			display_name.setText(display_name.getText() + (num++) + ". " + entry[0] + "<br/>");
			display_score.setText(display_score.getText() + " " + entry[1] + "<br/>");
		}
		while(num <= 10)
		{
			display_name.setText(display_name.getText() + (num++) + ".<br/>");
			display_score.setText(display_score.getText() + "<br/>");
		}
		display_name.setText(display_name.getText() + "</html>");
		display_name.setFont(font);
		display_score.setText(display_score.getText() + "</html>");
		display_score.setFont(font);
		JPanel scorePanel = new JPanel(new GridLayout(1,2,20,20));
		scorePanel.setOpaque(false);
		scorePanel.add(display_name);
		scorePanel.add(display_score);
		add(scorePanel, BorderLayout.NORTH);
		close = new JButton("Close");
		close.setFont(font);
		close.setPreferredSize(new Dimension(200,50));
		close.addActionListener(this);
		add(close, BorderLayout.SOUTH);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// TODO Auto-generated method stub
		if(e.getSource().equals(close))
		{
			setVisible(false);
			dispose();
		}
		
	}
	
}
