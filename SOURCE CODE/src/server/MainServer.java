///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package server;///////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

public class MainServer extends Thread 
{
	private GameLogic game;
	private GUI view;
	private Listener listener;
	private boolean listening;
	
	public MainServer() throws UnknownHostException //the whole server is set here
	{
		listening = false;
		view = new GUI(InetAddress.getLocalHost().getHostAddress());
		game = new GameLogic();
		game.addObserver(view);		
		addEventListeners();
		view.draw();
	}
	
	public void addEventListeners() //the action listeners
	{
		addLoadMapButtonListener();
		addListenButtonListener();
		addKickButtonListener();
		addMuteButtonListener();
	}
	
	public void addLoadMapButtonListener() //this is how the loading of the map is handled
	{
		view.getBtnLoadMap().addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent arg0)
			{
				if (view.getLstPlayers().getModel().getSize() == 0)
				{
					game.startNewGame(view.getTxtLoadMap().getText());
				} 
				
				else//Removing this exception makes this game go beserk.
				{
					JOptionPane.showMessageDialog(view, "Cannot load a new map while there are players connected.", "Load Map", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	public void addListenButtonListener() //buttons which are used mainly for the chat
	{
		view.getBtnListen().addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent arg0)
			{
				if (view.getTxtPort().getText().matches("[0-9]+"))//sanitation of sorts for the input. Only numerics are accepted
				{
					int port = Integer.parseInt(view.getTxtPort().getText());
					
					if (!listening) 
					{
						try
						{
							ServerSocket serverSock = new ServerSocket(port);
							listening = true;
							listener = new Listener(serverSock, game);
							view.startListening();
						} 
						
						catch (IOException e) 
						{
							JOptionPane.showMessageDialog(view, e.getMessage(), "Server Socket Exception", JOptionPane.ERROR_MESSAGE);
						}
					} 
					
					else
					{
						try
						{
							listener.close();
							listening = false;
							view.stopListening();
						} 						
						catch (IOException e){}
					}
				} 
				
				else
				{
					JOptionPane.showMessageDialog(view, "Invalid port number.", "Invalid Port", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	public void addKickButtonListener() 
	{
		view.getBtnKickOut().addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				Player selectedPlayer = (Player) view.getLstPlayers().getSelectedValue();
				if (selectedPlayer != null) 
				{
					if (JOptionPane.showConfirmDialog(view, "Are you sure you want to kick out this player?", "Kick Out Player", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
					{
						selectedPlayer.kickOut();
					}
				} 
				
				else 
				{
					JOptionPane.showMessageDialog(view, "Please select a player from the list first.", "Kick Out Player", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	}

	public void addMuteButtonListener() 
	{
		view.getBtnMute().addActionListener(new ActionListener() 
		{			
			public void actionPerformed(ActionEvent arg0) 
			{
				Player selectedPlayer = (Player) view.getLstPlayers().getSelectedValue();
				if (selectedPlayer != null) 
				{
					if (!selectedPlayer.getClient().isMuted()) 
					{
						if (JOptionPane.showConfirmDialog(view, "Are you sure you want to mute " + selectedPlayer.toString() + "?", "Mute Player", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
						{
							selectedPlayer.getClient().setMuted(true);
						}
					} 
					
					else 
					{
						selectedPlayer.getClient().setMuted(false);
						JOptionPane.showMessageDialog(view, selectedPlayer.toString() + " was unmuted." , "Unmute Player", JOptionPane.INFORMATION_MESSAGE);
					}
				} 
				
				else 
				{
					JOptionPane.showMessageDialog(view, "Please select a player from the list first", "Mute Player", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	}

	public void loadGame(String mapFile) 
	{
		if (mapFile.length() > 0) 
		{
			game = new GameLogic(mapFile);
		} 
		
		else
		{
			game = new GameLogic();
		}
		game.addObserver(view);
	}

	public static void main(String[] args) 
	{
		try 
		{
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
		    {
		        if ("Nimbus".equals(info.getName())) 
		        {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} 		
		catch (Exception e){}
		
		try 
		{
			new MainServer();
		} 
		
		catch (UnknownHostException e)
		{
			System.out.println(e.toString());
		}
	}
}

class Connection extends Thread
{
	public static Vector<Connection> connections = new Vector<Connection>();
	private static int nameCount = 0;
	private GameLogic game;
	private Player player;
	private String username;
	private Socket sock;
	private BufferedReader input;
	private PrintWriter output;
	private Timer spamTimer;
	private int spamCount;
	private boolean muted;
	
	public Connection(Socket sock, GameLogic game) 
	{
		this.game = game;
		this.sock = sock;
		spamTimer = new Timer();
		
		try 
		{
			input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			output = new PrintWriter(sock.getOutputStream(), false);
			
			start();
		} 
		
		catch (IOException e){}
	}
	
	public GameLogic getGame() 
	{
		return game;
	}
	
	public void setGame(GameLogic game)
	{
		this.game = game;
	}


	public String getUsername()
	{
		return username;
	}


	public void setUsername(String username) 
	{
		boolean unique = false;
		String temp = username;
		int counter = 2;
		while (!unique) 
		{
			unique = true;
			for (Connection c : connections)
			{
				if (c.getUsername().equalsIgnoreCase(temp)) 
				{
					temp = username + counter++;
					unique = false;
					break;
				}
			}
		}
		this.username = temp;
		connections.add(this);
		sendMessage("HELLO " + this.username);
	}

	public void setUsername() 
	{
		setUsername("Player " + ++nameCount);
	}

	public Player getPlayer()
	{
		return player;
	}
	

	public void setPlayer(Player player)
	{
		this.player = player;
	}


	public Timer getSpamTimer() 
	{
		return spamTimer;
	}


	public int getSpamCount()
	{
		return spamCount;
	}


	public void setSpamCount(int spamCount) 
	{
		this.spamCount = spamCount;
	}

	public void incrementSpamCount()
	{
		spamCount++;
	}

	public void decrementSpamCount()
	{
		spamCount--;
	}


	public boolean isMuted() 
	{
		return muted;
	}

	public void setMuted(boolean muted)
	{
		this.muted = muted;
		
		if (muted)
		{
			sendMessage("MUTE");
		} 
		else 
		{
			sendMessage("UNMUTE");
		}
	}


	public void run() 
	{
		try 
		{
			while (true) 
			{
				String message = input.readLine();
				
				if (message == null) 
				{
					throw new IOException();
				}
				
				new ClientSay(message, this);
			}
		} 
		
		catch (IOException e)
		{
			if (player != null) 
			{
				player.leaveGame();
			}
			connections.remove(this);
		}
	}
	
	public void sendMessage(String message) 
	{
		output.println(message);
		output.flush();
	}

	public boolean isInGame() 
	{
		return (player != null);
	}

	public void close() throws IOException 
	{
		sock.close();
	}
}

class GUI extends JFrame implements Observer 
{
	private static final long serialVersionUID = 666;
	private JTextArea txtaMap;
	private JTextField txtLoadMap;
	private JButton btnLoadMap;
	private JLabel lblHost;
	private JTextField txtPort;
	private JButton btnListen;
	private JLabel lblListening;
	private JList lstPlayers;
	private JButton btnKickOut;
	private JButton btnMute;
	ImageIcon logo = new ImageIcon("Data/Images/logo.png");
	
	public GUI(String host) 
	{
		txtaMap = new JTextArea();
		txtLoadMap = new JTextField();
		btnLoadMap = new JButton("Load Map");
		lblHost = new JLabel(host);
		txtPort = new JTextField("51007");
		btnListen = new JButton("Start Listening");
		lblListening = new JLabel("Client listening disabled.");
		lstPlayers = new JList();
		btnKickOut = new JButton("Kick Out");
		btnMute = new JButton("Mute/Unmute");
	}
	
	public JTextArea getTxtaMap() 
	{
		return txtaMap;
	}


	public JTextField getTxtLoadMap() 
	{
		return txtLoadMap;
	}

	public JButton getBtnLoadMap() 
	{
		return btnLoadMap;
	}


	public JLabel getLblHost() 
	{
		return lblHost;
	}

	public JTextField getTxtPort() 
	{
		return txtPort;
	}


	public JButton getBtnListen()
	{
		return btnListen;
	}


	public JLabel getLblListening() 
	{
		return lblListening;
	}


	public JList getLstPlayers() 
	{
		return lstPlayers;
	}

	public JButton getBtnKickOut()
	{
		return btnKickOut;
	}

	//mute button//
	public JButton getBtnMute()
	{
		return btnMute;
	}


	public void draw() 
	{
		///the gui is a bit complicated because theres stuff I wanted to put but its not working if I remove them.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("DoD Server");
		setResizable(false);
		setLayout(new BorderLayout());
		JPanel logoPan = new JPanel();
		JLabel logoBig = new JLabel(logo);
		
		logoPan.add(logoBig);
		
		JPanel borderPanel = new JPanel(new BorderLayout());
		borderPanel.setBorder(new EmptyBorder(5, 3, 2, 3));
		
		JPanel mapPanel = new JPanel(new BorderLayout());
		mapPanel.setBorder(BorderFactory.createTitledBorder(""));
		JScrollPane mapScroll = new JScrollPane(txtaMap);
		mapScroll.setPreferredSize(new Dimension(400, 0));
		txtaMap.setEditable(false);
		txtaMap.setFont(new Font("Arial Black", Font.PLAIN, 20));
		txtaMap.setLineWrap(true);
		txtaMap.setFocusable(false);
		mapPanel.add(mapScroll, BorderLayout.CENTER);
		//logo and map loader panel
		JPanel loadMapPanel = new JPanel(new BorderLayout());
		JLabel mapLabel = new JLabel("Map: ");
		mapLabel.setFont(mapLabel.getFont().deriveFont(Font.BOLD));
		loadMapPanel.add(logoPan, BorderLayout.BEFORE_FIRST_LINE);
		loadMapPanel.add(mapLabel, BorderLayout.LINE_START);
		loadMapPanel.add(txtLoadMap, BorderLayout.CENTER);
		loadMapPanel.add(btnLoadMap, BorderLayout.LINE_END);
		mapPanel.add(loadMapPanel, BorderLayout.PAGE_START);
		borderPanel.add(mapPanel, BorderLayout.CENTER);		
		JPanel rightPanel = new JPanel(new BorderLayout());
		
		JPanel detailsPanel = new JPanel(new BorderLayout());
		detailsPanel.setBorder(BorderFactory.createTitledBorder(""));
		lblListening.setForeground(Color.RED);
		detailsPanel.add(lblListening, BorderLayout.PAGE_START);
		///connection pannel as I like to call it
		JPanel labelPanel = new JPanel(new GridLayout(3, 1));
		JLabel hostLabel = new JLabel("Host: ");
		hostLabel.setFont(hostLabel.getFont().deriveFont(Font.BOLD));
		labelPanel.add(hostLabel);
		JLabel portLabel = new JLabel("Port: ");
		portLabel.setFont(portLabel.getFont().deriveFont(Font.BOLD));
		labelPanel.add(portLabel);
		detailsPanel.add(labelPanel, BorderLayout.LINE_START);

		JPanel editablePanel = new JPanel(new GridLayout(3, 1));
		editablePanel.add(lblHost);
		editablePanel.add(txtPort);
		editablePanel.add(btnListen);
		detailsPanel.add(editablePanel, BorderLayout.CENTER);
		
		rightPanel.add(detailsPanel);
		
		JPanel playerPanel = new JPanel();
		playerPanel.setLayout(new BorderLayout());
				
		JPanel playerButtonPanel = new JPanel(new GridLayout(1,1));
		playerPanel.add(playerButtonPanel, BorderLayout.PAGE_END);
		borderPanel.add(rightPanel, BorderLayout.LINE_END);
		
		add(borderPanel);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	

	public void startListening()
	{//will initialise the listening.
		lblListening.setText("Client listening enabled.");
		lblListening.setForeground(new Color(0, 150, 0));//thats green
		btnListen.setText("Stop Listening");
		txtPort.setEnabled(false);
	}

	public void stopListening()
	{//Idle state.
		lblListening.setText("Client listening disabled.");
		lblListening.setForeground(new Color(255, 0, 0));//thats red
		btnListen.setText("Start Listening");
		txtPort.setEnabled(true);
	}

	public void update(Observable observable, Object value)
	{
		GameLogic game = (GameLogic) observable;		
		// Update the map
		txtaMap.setText(game.getMap().toString());
		
		// Update the players
		Vector<Player> players = game.getPlayers();
		lstPlayers.setListData(players);
		
		if (players.size() > 0) 
		{
			txtLoadMap.setEnabled(false);
			btnLoadMap.setEnabled(false);
		} 
		
		else 
		{
			txtLoadMap.setEnabled(true);
			btnLoadMap.setEnabled(true);
		}
	}
}

class Listener extends Thread 
{
	//privates/
	private ServerSocket serverSock;
	private GameLogic game;
	public Listener(ServerSocket serverSock, GameLogic game)
	{
		this.game = game;
		this.serverSock = serverSock;
		start();
	}

	public void run() 
	{
		listen();//runs the listener
	}
	

	public void listen() 
	{//will listen for clients
		try 
		{
			while (true) 
			{
				Socket newSock = serverSock.accept();
				new Connection(newSock, game);
			}
		}
		
		catch (IOException e){}
	}	

	public void close() throws IOException 
	{
		serverSock.close();
	}
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
