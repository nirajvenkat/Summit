package game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class SummitInstructions extends JFrame implements ActionListener
{
	JButton close;
	
	public SummitInstructions(Font font)
	{
		setTitle("How To Play");
		setSize(600,600);
		setLayout(new BorderLayout());
		String background_path = System.getProperty("user.dir") + "/src/game/images/instructions.jpg";
		String logo_path = System.getProperty("user.dir") + "/src/game/images/instructions_logo.png";
		setContentPane(new JLabel(new ImageIcon(background_path)));
		setLayout(new BorderLayout());
		JLabel logo = new JLabel(new ImageIcon(logo_path));
		add(logo, BorderLayout.NORTH);
		close = new JButton("Close");
		close.setFont(font);
		close.setPreferredSize(new Dimension(200,50));
		close.addActionListener(this);
		add(close, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
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
