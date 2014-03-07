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
		font = font.deriveFont(Font.BOLD, 17);
		setSize(870,500);
		setLayout(new BorderLayout());
		String background_path = System.getProperty("user.dir") + "/src/game/images/instructions.jpg";
		String wsad_path = System.getProperty("user.dir") + "/src/game/images/wsad_keys.png";
		String arrow_path = System.getProperty("user.dir") + "/src/game/images/arrow_keys.png";
		setContentPane(new JLabel(new ImageIcon(background_path)));
		setLayout(new BorderLayout());
		
		JPanel instructions_panel = new JPanel(new BorderLayout());
		instructions_panel.setOpaque(false);
		instructions_panel.add(new JLabel(new ImageIcon(arrow_path)), BorderLayout.NORTH);
		instructions_panel.add(new JLabel(new ImageIcon(wsad_path)), BorderLayout.CENTER);
		JLabel instructions_label = new JLabel("<html>Try to collect as many powerups as possible for more points. The game ends when one player reaches the top and jumps off the screen. Good Luck!</html>");
		instructions_label.setFont(font);
		instructions_label.setForeground(Color.WHITE);
		instructions_panel.add(instructions_label, BorderLayout.SOUTH);
		
		
		
		add(instructions_panel, BorderLayout.NORTH);
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
