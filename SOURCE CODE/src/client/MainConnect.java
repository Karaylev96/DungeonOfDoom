///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package client;///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

public class MainConnect
{
	public static void main(String[] args) //main class
	{
		try
		{
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		    {
		        if ("Nimbus".equals(info.getName())) //if it is something else than nimbus we dun goof'd
		        {
		            UIManager.setLookAndFeel(info.getClassName());//we need to look, touch and feel stuff....
		            break;
		        }
		    }
		}

		catch (Exception e){}		//exception lal
		new MainConnect();//start a new connect();
	}

	private ConnectGUI connectView;
	public MainConnect()
	{
		connectView = new ConnectGUI("", "127.0.0.1", 51007);// the initial parmater for the exectuion of the client. They can be changed afterwards.
		askForConnection();
	}
	public void askForConnection()
	{
		connectView.btnConnect().addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String username = connectView.Usernames().getText();//here we get the username value/details
				String host = connectView.Hosting().getText();//here we get the username host value/details
				String portStr = connectView.Port().getText();//here we get the port value/details
				if (username.length() == 0)
				{
					JOptionPane.showMessageDialog(connectView, "Must enter a username.", "Incorrect Username", JOptionPane.ERROR_MESSAGE);
					return;//some guardians from crashing
				}
				if (host.length() == 0)
				{
					JOptionPane.showMessageDialog(connectView, "Must enter a host name.", "Incorrect Host", JOptionPane.ERROR_MESSAGE);
					return;//sp,e guardians from crashing
				}
				int port;
				if (portStr.matches("[0-9]+"))
				{
					port = Integer.parseInt(portStr);
				}

				else
				{
					JOptionPane.showMessageDialog(connectView, "Port must be numeric.", "Incorrect Port", JOptionPane.ERROR_MESSAGE);
					return;//help us obi-wan kenobi you are our only hope
				}

				MainClient client = new MainClient(username, host, port);//takes the given values from above default or edited.
				String error = client.connect();

				if (error == null) //if there are no error so far we can have the rest
				{
					connectView.dispose();
					GameMechanix game = new GameMechanix();
					client.modelSetting(game);
					new PlayGame(client);//play a game
				}

				else//if we have an error we will get this message
				{
					JOptionPane.showMessageDialog(connectView, error, "Connection Failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		connectView.drawCanvas();//draw everything!!!
	}
}

class MainClient extends Thread
{
	private GameMechanix mechanix;
	private String username;
	private String host;
	private int port;
	private Socket sock;
	private BufferedReader input;
	private PrintWriter output;

	public MainClient(String username, String host, int port)
	{
		this.username = username;
		this.host = host;
		this.port = port;
	}

	public BufferedReader input()
	{
		return input;//gets the input.
	}

	public PrintWriter output()
	{
		return output;//gets the output.
	}


	public String username()
	{
		return username;//return the username.
	}


	public void nameUsername(String username)
	{
		this.username = username;//uses the username.
	}


	public String host()
	{
		return host;
	}


	public void hostSetting(String host)
	{
		this.host = host;
	}

	public int port()
	{
		return port;//returns the port dang
	}

	public void portSetting(int port)
	{
		this.port = port;
	}

	public GameMechanix model()
	{
		return mechanix;
	}

	public void modelSetting(GameMechanix model)
	{
		this.mechanix = model;//deals with the settings of the model
	}

	public String connect()
	{
		try
		{
			sock = new Socket(host, port);
			input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			output = new PrintWriter(sock.getOutputStream(), false);
			sendMessage("HELLO " + username);
		}
		catch (UnknownHostException e)
		{
			return e.getMessage();
		}

		catch (IOException e)
		{
			return e.getMessage();
		}
		return null;
	}

	public void run()
	{
		try
		{
			while (true)
			{
				String message = input.readLine();//read the lines


				if (message == null)
				{
					throw new IOException();//if things goof down... throw this exception..
				}

				ServerMessage serverMssg = new ServerMessage(message, this);
				serverMssg.handle();
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Lost connection to the server.", "Lost Connection", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	public void sendMessage(String mssg)
	{
		output.println(mssg);//prints the message.
		output.flush();
	}
}

class ConnectGUI extends JFrame
{
	private static final long serialVersionUID = 111;
	private JTextField dodUsername;
	private JTextField dodHost;
	private JTextField dodPort;
	private JButton btnConnect;
	ImageIcon image1 = new ImageIcon("Data/Images/logo.png");
	ImageIcon image2 = new ImageIcon("Data/Images/nickname.png");
	ImageIcon image3 = new ImageIcon("Data/Images/host.png");
	ImageIcon image4 = new ImageIcon("Data/Images/port.png");

	public ConnectGUI(String username, String host, int port)
	{
		dodUsername = new JTextField(username);
		dodHost = new JTextField(host);
		dodPort = new JTextField(Integer.toString(port));
		btnConnect = new JButton("Connect");

		setTitle("Dungeon of Dooom - Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setResizable(false);
	}

	public JTextField Usernames()
	{
		return dodUsername;//takes the written username.
	}

	public JTextField Hosting()
	{
		return dodHost;//gets what you give to it.
	}

	public JTextField Port()
	{
		return dodPort;//gets what you give to it.
	}

	public JButton btnConnect()
	{
		return btnConnect;//returns the connect button.
	}


	public void drawCanvas()
	{
		JPanel fields = new JPanel();
		JPanel labels = new JPanel();
		JPanel container = new JPanel(new BorderLayout());
		JPanel logo = new JPanel();
		JPanel text = new JPanel();

		JLabel imgLogo = new JLabel(image1);
		JLabel imgUsername = new JLabel(image3);
		JLabel imgHost = new JLabel(image4);
		JLabel imgPort = new JLabel(image2);
		JLabel texty = new JLabel("<html><br><br>Welcome to the Dungeons of Dooom!!!<br>(three Os due to copyright reasons and stuff)<br>Georgi Karaylev Industries™<br><br><br><html>");

		labels.add(imgPort);
		labels.add(imgPort);
		labels.add(imgUsername);
		labels.add(imgHost);
		fields.add(dodUsername);
		fields.add(dodHost);
		fields.add(dodPort);
		fields.add(btnConnect);

		container.setBorder(new EmptyBorder(10, 10, 10, 10));
		container.setOpaque(false);

		labels.setLayout(new GridLayout(4, 1));
		labels.setOpaque(false);
		labels.setBorder(new EmptyBorder(10, 10, 10, 10));

		fields.setLayout(new GridLayout(4, 1));
		fields.setOpaque(false);
		fields.setPreferredSize(new Dimension(600, 150));

		logo.add(imgLogo);

		text.add(texty);

		labels.add(imgPort);
		labels.add(imgPort);
		labels.add(imgUsername);
		labels.add(imgHost);
		fields.add(dodUsername);
		fields.add(dodHost);
		fields.add(dodPort);
		fields.add(btnConnect);

		container.add(logo, BorderLayout.BEFORE_FIRST_LINE);
		container.add(labels, BorderLayout.LINE_START);
		container.add(fields, BorderLayout.CENTER);
		container.add(text, BorderLayout.AFTER_LAST_LINE);

		add(container, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}

class TurnPanel extends JPanel implements Observer
{
	private static final long serialVersionUID = 666;

	private JPanel paneContent;
	private JLabel lblTurn;
	private JButton btnEndTurn;

	private boolean turn;

	public TurnPanel()
	{
		setBorder(new EmptyBorder(0, 0, 4, 2));
		paneContent = new JPanel(new BorderLayout());
		paneContent.setBorder(BorderFactory.createLoweredBevelBorder());
		lblTurn = new JLabel();
		lblTurn.setHorizontalAlignment(JLabel.CENTER);
		lblTurn.setVerticalAlignment(JLabel.CENTER);
		paneContent.add(lblTurn, BorderLayout.CENTER);
		btnEndTurn = new JButton("End Turn");
		btnEndTurn.setFocusable(false);
		btnEndTurn.setPreferredSize(new Dimension(100, 50));
		paneContent.add(btnEndTurn, BorderLayout.PAGE_END);

		setLayout(new BorderLayout());
		add(paneContent, BorderLayout.CENTER);

		update(null, false);
	}

	public JPanel getPaneContent()
	{
		return paneContent;
	}

	public JLabel getLblTurn()
	{
		return lblTurn;
	}

	public JButton getBtnEndTurn()
	{
		return btnEndTurn;
	}

	public void chatOnly()
	{
		paneContent.setBackground(Color.GRAY);
		lblTurn.setText("Game Over!");
		btnEndTurn.setEnabled(false);
	}

	public void update(Observable observable, Object value)
	{
		turn = (Boolean) value;

		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				if (turn)
				{
					paneContent.setBackground(new Color(102, 255, 102));
					lblTurn.setText("It is your turn!");
					btnEndTurn.setEnabled(true);
				}

				else
				{
					paneContent.setBackground(new Color(255, 178, 102));
					lblTurn.setText("Wait for your turn.");
					btnEndTurn.setEnabled(false);
				}
			}
		});

	}
}

class MapPan extends JPanel implements Observer
{
	private static final long serialVersionUID = 666;

	public static final int LOOK_WIDTH = 7;
	public static final int LOOK_HEIGHT = 7;

	private JComponent[][] tiles;
	private char[][] newMap;

	public MapPan()
	{
	//the map printed onto the pane
		setLayout(new FlowLayout());
		setBackground(Color.BLACK);
		JPanel paneGrid = new JPanel(new GridLayout(LOOK_HEIGHT, LOOK_WIDTH));
		paneGrid.setPreferredSize(new Dimension(380, 380));
		paneGrid.setBackground(Color.BLACK);
		Font tileFont = new Font("Monospaced", Font.BOLD, 35);
		tiles = new JComponent[LOOK_HEIGHT][LOOK_WIDTH];
		for (int row = 0; row < LOOK_HEIGHT; row++)
		{
			for (int col = 0; col < LOOK_WIDTH; col++)
			{
				JComponent currentTile;

				if (row == LOOK_HEIGHT / 2 && col == LOOK_WIDTH / 2)
				{
					JButton btnCenter = new JButton("X");
					btnCenter.setForeground(Color.RED);
					btnCenter.setContentAreaFilled(false);
					btnCenter.setBorderPainted(false);
					btnCenter.setFocusable(false);
					btnCenter.setOpaque(false);
					btnCenter.setBackground(Color.BLACK);
					currentTile = btnCenter;
				}

				else if ((row == LOOK_HEIGHT / 2 - 1 && col == LOOK_WIDTH / 2) || (row == LOOK_HEIGHT / 2 + 1 && col == LOOK_WIDTH / 2) ||(row == LOOK_HEIGHT / 2 && col == LOOK_WIDTH / 2 - 1) || (row == LOOK_HEIGHT / 2 && col == LOOK_WIDTH / 2 + 1))
				{
					JButton btnAdjacent = new JButton("X");
					btnAdjacent.setForeground(Color.YELLOW);
					btnAdjacent.setContentAreaFilled(false);
					btnAdjacent.setBorderPainted(false);
					btnAdjacent.setFocusable(false);
					btnAdjacent.setOpaque(false);
					btnAdjacent.setBackground(Color.BLACK);
					currentTile = btnAdjacent;
				}

				else
				{//this is the unsseen part of the map
					currentTile = new JLabel("X", JLabel.CENTER);
					currentTile.setForeground(Color.WHITE);
				}

				currentTile.setFont(tileFont);
				tiles[row][col] = currentTile;
				paneGrid.add(tiles[row][col]);
			}
		}
		add(paneGrid);
	}

	public JButton getCentreButton()
	{
		return (JButton) tiles[LOOK_HEIGHT / 2][LOOK_WIDTH / 2];
	}

	public JButton getButtonN()
	{
		return (JButton) tiles[LOOK_HEIGHT / 2 - 1][LOOK_WIDTH / 2];
	}

	public JButton getButtonS()
	{
		return (JButton) tiles[LOOK_HEIGHT / 2 + 1][LOOK_WIDTH / 2];
	}

	public JButton getButtonE()
	{
		return (JButton) tiles[LOOK_HEIGHT / 2][LOOK_WIDTH / 2 + 1];
	}

	public JButton getButtonW()
	{
		return (JButton) tiles[LOOK_HEIGHT / 2][LOOK_WIDTH / 2 - 1];
	}

	public int getGridWidth()
	{
		return LOOK_WIDTH;
	}

	public int getGridHeight()
	{
		return LOOK_HEIGHT;
	}

	public void update(Observable observable, Object value)
	{
		newMap = (char[][]) value;
		///DONT TOUCH IT BARELY WORKS AS IT IS.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run() //will renew the map according to the current tile
			{
				for (int row = 0; row < LOOK_HEIGHT; row++)
				{
					for (int col = 0; col < LOOK_WIDTH; col++)
					{
						if ((row == LOOK_HEIGHT / 2 - 1 && col == LOOK_WIDTH / 2) || (row == LOOK_HEIGHT / 2 + 1 && col == LOOK_WIDTH / 2)|| (row == LOOK_HEIGHT / 2 && col == LOOK_WIDTH / 2 - 1) || (row == LOOK_HEIGHT / 2 && col == LOOK_WIDTH / 2 + 1)|| (row == LOOK_HEIGHT / 2 && col == LOOK_WIDTH / 2))
						{
							JButton currentTile = (JButton) tiles[row][col];
							currentTile.setText(Character.toString(newMap[row][col]));
							if (!(row == LOOK_HEIGHT / 2 && col == LOOK_WIDTH / 2))
							{
								if (!currentTile.getText().equals("#"))
								{
									currentTile.setForeground(Color.ORANGE);//wont see much orange in this game
								}

								else
								{
									currentTile.setForeground(Color.WHITE);
								}
							}

							else if (!currentTile.getText().equals(".") && !currentTile.getText().equals("E"))
							{
								currentTile.setForeground(Color.GREEN);
							}

							else
							{
								currentTile.setForeground(Color.WHITE);
							}
						}

						else
						{
							JLabel currentTile = (JLabel) tiles[row][col];
							currentTile.setText(Character.toString(newMap[row][col]));

							if (currentTile.getText().equals("P"))
							{
								currentTile.setForeground(Color.RED);
							}

							else
							{
								currentTile.setForeground(Color.WHITE);
							}
						}
					}
				}
			}
		});
	}
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
