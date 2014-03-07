package game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.*;

public class SummitSettings extends JFrame implements ActionListener
{
	public static final String[] supportedFPS = { "15", "30", "45", "60" };
	
	Preferences prefs;
	JTextField width, height;
	JComboBox<String> fpsList;
	JButton close;
	
	public SummitSettings(Font font)
	{
		font = font.deriveFont(Font.BOLD,20);
		prefs = Preferences.userRoot().node(this.getClass().getName());
		setTitle("Summit Settings");
		setSize(300,250);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		String background_path = System.getProperty("user.dir") + "/src/game/images/settings.jpg";
		setContentPane(new JLabel(new ImageIcon(background_path)));
		setLayout(new BorderLayout());
		JPanel screenSizePanel = new JPanel(new GridLayout(2,2,5,5));
		screenSizePanel.setOpaque(false);
		width = new JTextField();
		width.setFont(font);
		width.setText(prefs.getInt("WIDTH", SummitMenu.DEFAULT_SCREEN_WIDTH) + "");
		height = new JTextField();
		height.setText(prefs.getInt("HEIGHT", SummitMenu.DEFAULT_SCREEN_HEIGHT) + "");
		height.setFont(font);
		JLabel widthLabel = new JLabel("Width: ");
		JLabel heightLabel = new JLabel("Height: ");
		heightLabel.setFont(font);
		heightLabel.setForeground(Color.WHITE);
		widthLabel.setFont(font);
		widthLabel.setForeground(Color.WHITE);
		screenSizePanel.add(widthLabel);
		screenSizePanel.add(width);
		screenSizePanel.add(heightLabel);
		screenSizePanel.add(height);
		add(screenSizePanel, BorderLayout.NORTH);
		JPanel fpsPanel = new JPanel(new GridLayout(1,2,5,5));
		fpsPanel.setOpaque(false);
		JLabel fpsLabel = new JLabel("FPS: ");
		fpsLabel.setFont(font);
		fpsLabel.setForeground(Color.WHITE);
		fpsList = new JComboBox<String>(supportedFPS);
		fpsList.setFont(font);
		int fps = prefs.getInt("FPS", 60);
		switch(fps)
		{
			case 15:
				fpsList.setSelectedIndex(0);
				break;
			case 30:
				fpsList.setSelectedIndex(1);
				break;
			case 45:
				fpsList.setSelectedIndex(2);
				break;
			case 60:
				fpsList.setSelectedIndex(3);
				break;
			default:
				fpsList.setSelectedIndex(3);
				break;
		}
		fpsPanel.add(fpsLabel);
		fpsPanel.add(fpsList);
		add(fpsPanel, BorderLayout.CENTER);
		close = new JButton("Save");
		close.addActionListener(this);
		close.setPreferredSize(new Dimension(50,50));
		close.setFont(font);
		add(close, BorderLayout.SOUTH);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// TODO Auto-generated method stub
		if(e.getSource().equals(close))
		{
			try
			{
				prefs.putInt("WIDTH", Integer.parseInt(width.getText().trim()));
				prefs.putInt("HEIGHT", Integer.parseInt(height.getText().trim()));
				prefs.putInt("FPS", Integer.parseInt(fpsList.getSelectedItem().toString()));
				setVisible(false);
				dispose();
			}
			catch(Exception ex)
			{
				JOptionPane.showMessageDialog(null, "Error. Invalid Height or Width...");
			}
			
		}
	}
	

}
