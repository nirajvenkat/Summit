import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class TestClient extends Canvas implements ActionListener, KeyListener
{
  //Networking
  Socket s;
  DataInputStream in;
  InputStream line_in;
  DataOutputStream out;
  DatagramSocket SOCK;
  byte[] recv, send;
  int PORT;
  long SEQ;
  
  //GUI
  JFrame frame;
  JButton create_lobby, join_lobby, exit;
  JLabel status;
  
  //Session
  String lobby_name;
  UDPNetworkThread n_thread;
  TCPNetworkThread t_thread;
  ArrayList<Player> players;
  int player_id;
  String code;
  
  public TestClient()
  {
    try
    {
      s = new Socket("98.226.145.27", 9998);
      line_in = s.getInputStream();
      in = new DataInputStream(line_in);
      out = new DataOutputStream(s.getOutputStream());
      recv = new byte[128];
      send = new byte[128];
      t_thread = new TCPNetworkThread();
      t_thread.start();
      
      players = new ArrayList<Player>();
      setSize(500,300);
      addKeyListener(this);
      setBackground(Color.WHITE);
      frame = new JFrame("Test Client");
      frame.setLayout(new FlowLayout());
      frame.setSize(500,500);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      JPanel controlPanel = new JPanel();
      create_lobby = new JButton("Create Lobby");
      create_lobby.addActionListener(this);
      join_lobby = new JButton("Join Lobby");
      join_lobby.addActionListener(this);
      exit = new JButton("Exit");
      exit.addActionListener(this);
      status = new JLabel("Connected To Summit Server");
      status.setForeground(Color.GREEN);
      controlPanel.add(create_lobby);
      controlPanel.add(join_lobby);
      controlPanel.add(exit);
      controlPanel.add(status);
      controlPanel.setBackground(Color.BLACK);
      frame.add(controlPanel);
      frame.add(this);
      frame.setVisible(true);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void keyReleased(KeyEvent evt){}
  public void keyPressed(KeyEvent evt)
  {
    switch(evt.getKeyCode())
    {
      case KeyEvent.VK_UP:
         players.get(0).y-=7;
        break;
      case KeyEvent.VK_DOWN:
         players.get(0).y+=7;
        break;
      case KeyEvent.VK_LEFT:
         players.get(0).x-=7;
        break;
      case KeyEvent.VK_RIGHT:
         players.get(0).x+=7;
        break;
    }
    
    String data = "ACTION_MOVE;" + player_id + ";" +  players.get(0).x + ";" +  players.get(0).y + ";" + SEQ;
    n_thread.broadcast(data);
    repaint();
  }
  public void keyTyped(KeyEvent evt){}
  
  public static void main(String args[])
  {
    TestClient tc = new TestClient();
  }
  
  public void paint(Graphics g)
  {
    for(Player player : players)
    {
      if(player.player_id == player_id)
      {
        g.setColor(Color.GREEN);
      }
      else
      {
        g.setColor(Color.RED);
      }
      g.fillOval(player.x,player.y,50,50);
    }
  }
  
  public void actionPerformed(ActionEvent evt)
  {
    try
    {
      Object src = evt.getSource();
      if(src.equals(create_lobby))
      {
        lobby_name = JOptionPane.showInputDialog("Enter Lobby Password");
        String request = "CMD_CREATE_LOBBY;" + lobby_name;
        code = "CMD_CREATE_LOBBY";
        int response;
        out.writeUTF(request);
        out.flush();
        /*if((response = in.readInt()) == 1)
        {
          String[] data = in.readUTF().split(";");
          PORT = Integer.parseInt(data[0]);
          player_id = Integer.parseInt(data[1]);
          players.add(new Player(player_id));
          SOCK = new DatagramSocket(PORT);
          SEQ = Long.parseLong(data[2]);
          //UDPNetworkThread.running = true;
          n_thread = new UDPNetworkThread();
          n_thread.start();
        }
        else
        {
          System.err.println("Could not create new lobby. Error code " + response);
        }*/
      }
      else if(src.equals(join_lobby))
      {
        lobby_name = JOptionPane.showInputDialog("Enter Lobby Password");
        String request = "CMD_JOIN_LOBBY;" + lobby_name;
        code = "CMD_JOIN_LOBBY";
        int response;
        out.writeUTF(request);
        out.flush();
        /*if((response = in.readInt()) == 1)
        {
          String[] data = in.readUTF().split(";");
          PORT = Integer.parseInt(data[0]);
          player_id = Integer.parseInt(data[1]);
          players.add(new Player(player_id));
          SOCK = new DatagramSocket(PORT);
          SEQ = Long.parseLong(data[2]);
          //running = true;
          n_thread = new UDPNetworkThread();
          n_thread.start();
        }
        else
        {
          System.err.println("Could not join lobby. Error code " + response);
        }*/
      }
      else if(src.equals(exit))
      {
        code = "CMD_DISCONNECT";
        out.writeUTF("CMD_DISCONNECT");
        out.flush();
        /*int response;
        if((response = in.readInt()) == 1)
        {
          in.readUTF();
          out.close();
          in.close();
          s.close();
          System.exit(0);
        }
        else
        {
          System.err.println("Error disconnecting from Summit Server...");
        }*/
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
  
  class Player
  {
    int player_id;
    int x, y;
    
    public Player(int player_id)
    {
      this.player_id = player_id;
      x = 0;
      y = 0;
    }
    
    
  }
  
  class TCPNetworkThread extends Thread
  {
    //boolean running;
    
    public void run()
    {
      String data[];
      int response;
      while(true)
      {
        /*try
        {
          data = in.readUTF().split(";");
          if(data[0].equals("LOBBY_NEW_PLAYER"))
          {
            status.setText(lobby_name + ": [" + data[1] + "/" + data[2] + "] players.");
            players.add(new Player(player_id));
          }
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }*/
        try
        {
          if((response = in.readInt()) == 1)
          {
            String line = in.readUTF();
            data = line.split(";");
            
            System.out.println(line);
            
            if(code.equals("CMD_JOIN_LOBBY"))
            {
              code = "";
              PORT = Integer.parseInt(data[0]);
              player_id = Integer.parseInt(data[1]);
              players.add(new Player(player_id));
              SOCK = new DatagramSocket(PORT);
              SEQ = Long.parseLong(data[2]);
              //running = true;
              n_thread = new UDPNetworkThread();
              n_thread.start();
            }
            else if(code.equals("CMD_CREATE_LOBBY"))
            {
              code = "";
              PORT = Integer.parseInt(data[0]);
              player_id = Integer.parseInt(data[1]);
              players.add(new Player(player_id));
              SOCK = new DatagramSocket(PORT);
              SEQ = Long.parseLong(data[2]);
              //UDPNetworkThread.running = true;
              n_thread = new UDPNetworkThread();
              n_thread.start();
            }
            else if(code.equals("CMD_DISCONNECT"))
            {
              code = "";
              in.readUTF();
              out.close();
              in.close();
              s.close();
              System.exit(0);
            }
            else
            {
              if(data[0].equals("LOBBY_NEW_PLAYER"))
              {
                status.setText(lobby_name + ": [" + data[1] + "/" + data[2] + "] players.");
                players.add(new Player(player_id));
              }
            }
          }
          else
          {
            System.err.println("An error occured. Error code " + response);
          }
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
        
      }
    }
  }
  
  class UDPNetworkThread extends Thread
  {
    //boolean running;
    public void run()
    {
      while(true)
      {
        try
        {
          DatagramPacket receivePacket = new DatagramPacket(recv, recv.length);
          SOCK.receive(receivePacket);
          String tmp = new String(receivePacket.getData());
          System.out.println("Received: " + tmp);
          tmp = tmp.trim();
          String[] data = tmp.split(";");
          
          int IN_SEQ = Integer.parseInt(data[data.length-1]);
          if(true) //IN_SEQ > SEQ
          {
            SEQ = IN_SEQ;
            if(data[0].equals("ACTION_MOVE"))
            {
              int player_id = Integer.parseInt(data[1]);
              int move_x = Integer.parseInt(data[2]);
              int move_y = Integer.parseInt(data[3]);
              
              for(Player player : players)
              {
                if(player.player_id == player_id)
                {
                  player.x = move_x;
                  player.y = move_y;
                  break;
                }
              }
              
              repaint();
            }
          }
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
    }
    
    public void broadcast(String data)
    {
      try
      {
        System.out.println("Sending: " + data);
        send = data.getBytes();
        InetAddress inet_address = InetAddress.getByName("98.226.145.27");
        DatagramPacket sendPacket = new DatagramPacket(send, send.length, inet_address, PORT);
        SOCK.send(sendPacket);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}