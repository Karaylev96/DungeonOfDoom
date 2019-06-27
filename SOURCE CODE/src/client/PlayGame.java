///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package client;///////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

public class PlayGame 
{
	public static final ArrayList<String> chatCommands = new ArrayList<String>();
	private GameMechanix model;
	private gameGUI view;
	private MainClient client;
	private boolean typing;
	
	public PlayGame(MainClient client) 
	{//
		this.client = client;
		model = client.model();
		view = new gameGUI();
		typing = false;
		
		startGame();
	}
	
	public void startGame()
	{//mechanism for getting the game
		client.start();
		addObservers();
		addEventListeners();
		view.draw();
	}
	
	public void addObservers()
	{//the observer classes are added 
		model.getMap().addObserver(view.getPaneMap());
		model.getDungeonName().addObserver(view.getLblDungeonName());
		model.getGoal().addObserver(view.getLblGoal());
		model.getGold().addObserver(view.getLblGold());
		model.getHp().addObserver(view.getLblHp());
		model.getSword().addObserver(view.getLblSword());
		model.getArmour().addObserver(view.getLblArmour());
		model.getLantern().addObserver(view.getLblLantern());
		model.getGameMessage().addObserver(view.getPaneGameMessages());
		model.getTurn().addObserver(view.getPaneTurn());
		model.getPlayers().addObserver(view.getPanePlayers());
		model.getInGame().addObserver(view);
	}

	public void addEventListeners()
	{//event mechanism
		leaveButtonEvents();
		mapButtonEvents();
		chatBoxEvents();
		endTurnButtonEvents();
		chatStylingEvents();
		chatComboBoxEvents();
	}
	
	public void chatComboBoxEvents() 
	{//events for the chat mechanics
		view.getCmbChat().addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent e) 
			{
				if (((String)view.getCmbChat().getSelectedItem()).equals("Shout: "))
				{//shout mechn
					view.getTxtaChat().setText(view.getTxtaChat().getText().replaceAll("\n", ""));
				}
			}
		});
	}

	public void chatStylingEvents() //adds some buttons to the chat systems
	{
		addStylingEvent(view.getBtnBold());
		addStylingEvent(view.getBtnItalic());
		addStylingEvent(view.getBtnUnderline());
		addColorStylingEvent(view.getBtnColor());
	}
	
	public void addStylingEvent(final JButton tagButton) //the styling button html configuration
	{
		tagButton.addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent e)
			{
				view.insertChatTags("[" + tagButton.getName() + "]", "[/" + tagButton.getName() + "]");
			}//kind of a lazy way to do it but oh well look at me care
		});
	}
	
	public void addColorStylingEvent(final JButton colorButton)
	{
		colorButton.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e) 
			{//color for the text
				Color pickedColor = JColorChooser.showDialog(view, "Choose Text Color", colorButton.getBackground());
				if (pickedColor != null) 
				{
					colorButton.setBackground(pickedColor);
					view.insertChatTags("[c=#" +  Integer.toHexString(pickedColor.getRGB() & 0xFFFFFF).toUpperCase() + "]", "[/c]");
				}
			}
		});
	}

	public void leaveButtonEvents() 
	{//leave button or simply the system exit button
		view.getBtnLeave().addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
	}

	public void mapButtonEvents() 
	{
		view.getPaneMap().getCentreButton().addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e) 
			{
				client.sendMessage("PICKUP");
			}
		});
		
		view.getPaneMap().getButtonN().addActionListener(new ActionListener() 
		{
			//bellow the app is ahdling the movement and attacking.
			public void actionPerformed(ActionEvent e) 
			{//North attack/move
				if (view.getPaneMap().getButtonN().getText().equals("P"))//stands for players
				{
					client.sendMessage("ATTACK N");
				} 
				
				else 
				{
					client.sendMessage("MOVE N");
				}
			}
		});
		
		view.getPaneMap().getButtonS().addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent e)
			{//south attack/move
				if (view.getPaneMap().getButtonS().getText().equals("P")) 
				{
					client.sendMessage("ATTACK S");
				} 
				
				else
				{
					client.sendMessage("MOVE S");
				}
			}
		});
		
		view.getPaneMap().getButtonE().addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{//east attack/move
				if (view.getPaneMap().getButtonE().getText().equals("P"))
				{
					client.sendMessage("ATTACK E");
				} 
				
				else 
				{
					client.sendMessage("MOVE E");
				}
			}
		});
		
		view.getPaneMap().getButtonW().addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent e)
			{//west attack/move
				if (view.getPaneMap().getButtonW().getText().equals("P")) 
				{
					client.sendMessage("ATTACK W");
				} 
				
				else 
				{
					client.sendMessage("MOVE W");
				}
			}
		});
		

		MouseListener hover = new MouseListener() //the magic using the mouse to play the game
		{
			public void mouseClicked(MouseEvent e) {}	
			public void mouseEntered(MouseEvent e)
			{
				JButton btnHovered = (JButton) e.getSource();//if the 
				btnHovered.setContentAreaFilled(true);
			}

			public void mouseExited(MouseEvent e) 
			{
				JButton btnHovered = (JButton) e.getSource();
				btnHovered.setContentAreaFilled(false);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		};
		
		view.getPaneMap().getCentreButton().addMouseListener(hover);
		view.getPaneMap().getButtonN().addMouseListener(hover);
		view.getPaneMap().getButtonE().addMouseListener(hover);
		view.getPaneMap().getButtonS().addMouseListener(hover);
		view.getPaneMap().getButtonW().addMouseListener(hover);
	}
	
	public void chatBoxEvents() 
	{
		view.getTxtaChat().getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "doNothing");
		view.getTxtaChat().addKeyListener(new KeyListener()
		{			
			public void keyPressed(KeyEvent e) 
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER) //enter is the only key working in the game.
				{
					String message = view.getTxtaChat().getText();
					if (e.getModifiers() != KeyEvent.SHIFT_MASK) 
					{
						if (message.length() > 0) 
						{
							if (((String) view.getCmbChat().getSelectedItem()).equals("Shout: "))
							{//shouting work
								client.sendMessage("SHOUT " + message);
								view.getTxtaChat().setText("");
							} 
							
							else 
							{//tried to make things very similar to iRC.
								if (message.equals("/commands")) 
								{
									view.getTxtaChat().setText("");
									
									String commandList = "";
									for (String command : chatCommands) 
									{
										commandList += command + "\n";
									}
									JTextArea txtaCommands = new JTextArea(commandList);
									txtaCommands.setRows(chatCommands.size());
									txtaCommands.setLineWrap(true);
									txtaCommands.setEditable(false);									
									JOptionPane.showMessageDialog(view, txtaCommands, "Chat Commands", JOptionPane.PLAIN_MESSAGE);
								} 
								
								else 
								{
									ArrayList<String> lines = new ArrayList<String>();
									try 
									{
										int beginIndex = 0;
										while (beginIndex < message.length())
										{
											int endIndex = Utilities.getRowEnd(view.getTxtaChat(), beginIndex);
											lines.add(message.substring(beginIndex, endIndex));
											beginIndex = endIndex + 1;
										}
									} 
									
									catch (BadLocationException ble)
									{
										ble.printStackTrace();
									}
									
									message = "";
									for (int i = 0; i < lines.size(); i++)
									{
										String line = lines.get(i).replaceAll("\n", ""); // Remove new line characters
										message += line;
										
										if (i != lines.size() - 1)
										{
											message += "[br]";
										}
									}
									
									client.sendMessage("CHAT " + message);
									view.getTxtaChat().setText("");
								}
							}
						}
					}
					
					else
					{
						if (!view.getCmbChat().getSelectedItem().equals("Shout: "))
						{
							view.getTxtaChat().setText(message + "\n");
						}
					}
				}
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) 
			{
				if (view.getTxtaChat().getForeground() == Color.GRAY) 
				{
					view.getTxtaChat().setText("");
					view.getTxtaChat().setForeground(Color.BLACK);
				}
			}
		});
		
		view.getTxtaChat().getDocument().addDocumentListener(new DocumentListener()//document listener made for dealing with the game messages
		{
			public void changedUpdate(DocumentEvent e)
			{
				sendTypingMessage();
			}
			
			public void insertUpdate(DocumentEvent e) 
			{
				sendTypingMessage();
			}
			
			public void removeUpdate(DocumentEvent e) 
			{
				sendTypingMessage();
			}
						
			public void sendTypingMessage() 
			{//default message in the idle chat box
				if (!view.getTxtaChat().getText().equals("<Type your chat message here and press ENTER>")) 
				{
					if (view.getTxtaChat().getText().length() > 0 && !typing)
					{
						client.sendMessage("STARTTYPING");
						typing = true;
					} 
					
					else if (view.getTxtaChat().getText().length() == 0 && typing) 
					{
						client.sendMessage("ENDTYPING");
						typing = false;
					}
					
					view.getTxtaChat().setForeground(Color.BLACK);
				}
			}
		});
	}
	
	public void endTurnButtonEvents()
	{
		view.getPaneTurn().getBtnEndTurn().addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent e) 
			{//endturn button
				client.sendMessage("ENDTURN");
			}
		});
	}
}

class gameGUI extends JFrame implements Observer 
{
	//privates//lol
	private static final long serialVersionUID = 666;//numbah of the beast hell and fire are about to be released
	private MapPan paneMap;
	private ObserverLabel lblDungeonName;
	private ObserverLabel lblGoal;
	private ObserverLabel lblGold;
	private ObserverLabel lblHp;
	private JButton btnLeave;
	private JComboBox cmbChat;
	private JTextArea txtaChat;
	private JButton btnBold;
	private JButton btnItalic;
	private JButton btnUnderline;
	private JButton btnColor;
	private ObserverMain lblSword;
	private ObserverMain lblArmour;
	private ObserverMain lblLantern;
	private MessagePanel paneGameMessages;
	private TurnPanel paneTurn;
	private PlayerPanelList panePlayers;

	public gameGUI() 
	{
		getContentPane().setBackground(Color.BLACK);
		paneMap = new MapPan();
		lblDungeonName = new ObserverLabel("Map: ", "Default Map");
		lblGoal = new ObserverLabel("Goal: ", "0");
		lblGold = new ObserverLabel("Gold: ", "0");
		lblHp = new ObserverLabel("HP: ", "3");
		btnLeave = new JButton("Exit");
		cmbChat = new JComboBox();
		txtaChat = new JTextArea();
		btnBold = new JButton("B");
		btnItalic = new JButton("I");
		btnUnderline = new JButton("<html><u>U</u></html>");
		btnColor = new JButton();
		lblSword = new ObserverMain("Data/Images/", "Data/Images/", "Sword", false);//maybe I will put images for those items
		lblArmour = new ObserverMain("Data/Images/", "Data/Images/", "Armour", false);
		lblLantern = new ObserverMain("Data/Images/", "Data/Images/", "Lantern", false);
		paneGameMessages = new MessagePanel();
		paneTurn = new TurnPanel();
		panePlayers = new PlayerPanelList(txtaChat);
	}

	public JButton getBtnLeave() 
	{
		return btnLeave;//the leave button
	}
	
	public JComboBox getCmbChat()
	{
		return cmbChat;
	}
	
	public JTextArea getTxtaChat()
	{
		return txtaChat;
	}
	
	public JButton getBtnBold() 
	{
		return btnBold;//bold button
	}

	public JButton getBtnItalic()
	{
		return btnItalic;//italic button
	}

	public JButton getBtnUnderline()
	{
		return btnUnderline;//underline button
	}

	public JButton getBtnColor() 
	{
		return btnColor;//colour button
	}

	public MapPan getPaneMap() 
	{
		return paneMap;
	}
	
	public ObserverLabel getLblDungeonName() 
	{
		return lblDungeonName;
	}
	
	public ObserverLabel getLblGoal() 
	{
		return lblGoal;
	}
	
	public ObserverLabel getLblGold()
	{
		return lblGold;
	}
	
	public ObserverLabel getLblHp()
	{
		return lblHp;
	}

	public ObserverMain getLblSword()
	{
		return lblSword;
	}
	
	public ObserverMain getLblArmour()
	{
		return lblArmour;
	}

	public ObserverMain getLblLantern() 
	{
		return lblLantern;
	}

	public MessagePanel getPaneGameMessages()
	{
		return paneGameMessages;
	}

	public TurnPanel getPaneTurn() 
	{
		return paneTurn;
	}
	
	public PlayerPanelList getPanePlayers() 
	{
		return panePlayers;
	}
	

	public void draw() //drawing the user interface with a more favourable game
	{
		setTitle("Dungeon of Dooom - Game of the Year Edition");//name of the title and the game. yest this is the game of the year edition
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.BLACK);
		setLayout(new BorderLayout());		
		add(paneMap, BorderLayout.CENTER); 
		topPanel();
		addBottomPanel();		
		pack();
		if (getSize().width < 900) 
		{
			setSize(new Dimension(900, getSize().height));
		}
		setLocationRelativeTo(null);
		setVisible(true);
	}
	

	private void addBottomPanel() 
	{
		JPanel paneBottom = new JPanel(new BorderLayout());
		paneBottom.setOpaque(false);

		// Bottom left panel
		JPanel paneBottomLeftPadding = new JPanel();
		paneBottomLeftPadding.setBorder(BorderFactory.createRaisedBevelBorder());
		JPanel paneBottomLeft = new JPanel(new BorderLayout());
		JPanel paneChatInput = new JPanel(new BorderLayout());
		Dimension styleButtonSize = new Dimension(40, 30);
		JPanel paneChatStyling = new JPanel(new FlowLayout());
		
		btnBold.setFont(btnBold.getFont().deriveFont(Font.BOLD));
		btnBold.setName("b");
		btnBold.setPreferredSize(styleButtonSize);
		btnBold.setFocusable(false);
		paneChatStyling.add(btnBold);
		
		btnItalic.setFont(btnItalic.getFont().deriveFont(Font.ITALIC));
		btnItalic.setName("i");
		btnItalic.setPreferredSize(styleButtonSize);
		btnItalic.setFocusable(false);
		paneChatStyling.add(btnItalic);
		
		btnUnderline.setName("u");
		btnUnderline.setPreferredSize(styleButtonSize);
		btnUnderline.setFocusable(false);
		paneChatStyling.add(btnUnderline);
		
		btnColor.setPreferredSize(styleButtonSize);
		btnColor.setBackground(Color.BLUE);
		btnColor.setFocusable(false);
		paneChatStyling.add(btnColor);
		
		btnLeave.setPreferredSize(new Dimension(btnLeave.getPreferredSize().width, 48));
		btnLeave.setFocusable(false);
		paneChatStyling.add(btnLeave);		
		paneChatInput.add(paneChatStyling, BorderLayout.PAGE_START);
		
		// Chat typing panel
		JPanel paneChat = new JPanel(new BorderLayout());
		cmbChat.addItem("Chat: ");
		cmbChat.addItem("Shout: ");
		cmbChat.setFocusable(false);
		cmbChat.setPreferredSize(new Dimension(130, 0));
		paneChat.add(cmbChat, BorderLayout.LINE_START);
		txtaChat.setForeground(Color.GRAY);
		txtaChat.setText("<Type your chat message here and press ENTER>");
		txtaChat.setCaretPosition(0);
		txtaChat.setLineWrap(true);
		txtaChat.setWrapStyleWord(true);
		txtaChat.setRows(3);
		JScrollPane scrlChatBox = new JScrollPane(txtaChat);
		paneChat.add(scrlChatBox, BorderLayout.CENTER);
		paneChatInput.add(paneChat, BorderLayout.PAGE_END);
		
		paneBottomLeft.add(paneChatInput, BorderLayout.PAGE_END);
		
		// Game message panel
		JScrollPane scrlMessages = new JScrollPane(paneGameMessages);
		scrlMessages.setPreferredSize(new Dimension(550, 120));
		scrlMessages.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		paneBottomLeft.add(scrlMessages, BorderLayout.CENTER);
		
		paneBottomLeftPadding.add(paneBottomLeft);
		paneBottom.add(paneBottomLeftPadding, BorderLayout.LINE_START);
		
		// Bottom right panel
		JPanel paneBottomRightPadding = new JPanel();
		paneBottomRightPadding.setBorder(BorderFactory.createRaisedBevelBorder());
		JPanel paneBottomRight = new JPanel(new BorderLayout());
		
		
		// Items and turn panel
		JPanel paneItems = new JPanel(new BorderLayout());
		paneItems.setBorder(new EmptyBorder(10, 0, 0, 0));
		JPanel paneItemImages = new JPanel(new FlowLayout());
		paneItemImages.setBorder(BorderFactory.createTitledBorder(""));
		paneItemImages.add(lblSword);
		lblArmour.setBorder(new EmptyBorder(0, 10, 0, 10));
		paneItemImages.add(lblArmour);
		paneItemImages.add(lblLantern);
		paneItems.add(paneItemImages, BorderLayout.LINE_START);
		paneChatStyling.add(paneTurn);
		paneBottomRight.add(paneItems, BorderLayout.PAGE_END);
	
		add(paneBottom, BorderLayout.PAGE_END);
	}
	
	private void topPanel()
	{
		//settings for the top panel
		Font topFont = new Font("Serif", Font.BOLD, 20);
		JPanel paneTop = new JPanel(new BorderLayout());
		paneTop.setBackground(Color.darkGray);
		JPanel paneTopLeft = new JPanel(new BorderLayout());
		paneTopLeft.setOpaque(false);
		lblDungeonName.setForeground(Color.WHITE);
		lblDungeonName.setIcon(new ImageIcon("Data/Image/"));
		lblDungeonName.setFont(topFont);
		paneTopLeft.add(lblDungeonName, BorderLayout.LINE_START);
		lblGoal.setForeground(Color.RED);
		lblGoal.setIcon(new ImageIcon("Data/Image/"));
		lblGoal.setFont(topFont);
		paneTopLeft.add(lblGoal, BorderLayout.LINE_END);
		paneTop.add(paneTopLeft, BorderLayout.LINE_START);
		JPanel paneTopRight = new JPanel(new FlowLayout());
		paneTopRight.setOpaque(false);
		
		//Gold
		lblGold.setForeground(Color.YELLOW);
		lblGold.setIcon(new ImageIcon(""));
		lblGold.setFont(topFont);
		lblGold.setPreferredSize(new Dimension(130, 48));
		paneTopRight.add(lblGold);
		
		//HitPoints
		lblHp.setForeground(Color.RED);
		lblHp.setIcon(new ImageIcon("Data/Image/"));
		lblHp.setFont(topFont);
		lblHp.setPreferredSize(new Dimension(110, 48));
		paneTopRight.add(lblHp);
		
		paneTop.add(paneTopRight, BorderLayout.LINE_END);
		
		add(paneTop, BorderLayout.PAGE_START);
	}
	
	public void insertChatTags(String tagStart, String tagEnd) 
	{
		if (txtaChat.getForeground() != Color.BLACK)
		{
			txtaChat.setForeground(Color.BLACK);
			txtaChat.setText("");
		}
		
		int startIndex = txtaChat.getSelectionStart();
		int endIndex = txtaChat.getSelectionEnd() + tagStart.length();
		try 
		{
			txtaChat.getDocument().insertString(startIndex, tagStart, null);
			txtaChat.getDocument().insertString(endIndex, tagEnd, null);
			txtaChat.setCaretPosition(endIndex);
		}
		
		catch (BadLocationException e) 
		{
			e.printStackTrace();
		}
	}

	public void update(Observable observable, Object value) 
	{
		paneTurn.chatOnly();
	}
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
