///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package client;///////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Player extends Observable 
{
	public static final int ALIVE = 0;//0 is alive
	public static final int DEAD = 1;//1 is deaddddd
	
	private int status;
	private String username;
	private boolean typing;
	private boolean chat;
	public Player(int status, String username, boolean chat) 
	{
		this.status = status;//gets the status
		this.username = username;//gets the username
		this.chat = chat;//gets the chat 
	}

	public int status()
	{
		return status;//player status 
	}

	public void setStatus(int status) 
	{
		this.status = status;		
		setChanged();
		notifyObservers(this);
	}

	public String playername()
	{
		return username;
	}

	public void setUsername(String username) 
	{
		this.username = username;		
		setChanged();
		notifyObservers(this);
	}

	public boolean typing() 
	{
		return typing;//leave you in the typing zone
	}

	public void setTyping(boolean typing) 
	{
		this.typing = typing;
		setChanged();
		notifyObservers(this);
	}

	public boolean chat() 
	{
		return chat;
	}


	public void setChat(boolean chat) 
	{
		this.chat = chat;
		setChanged();
		notifyObservers(this);
	}
}

class PlayerPanel extends JPanel implements Observer 
{
	private static final long serialVersionUID = 666;//number of the beast
	private int state;
	private boolean typing;
	private String playerName;
	private JLabel lblDeadIcon;
	private JLabel lblTypingIcon;
	private JButton btnPrivateChat;
	private JTextArea chatTextField;

	public PlayerPanel(String username, int state, boolean typing, boolean chat, JTextArea txtaChat) 
	{
		this.state = state;
		this.typing = typing;
		this.chatTextField = txtaChat;
		this.playerName = username;
		setLayout(new BorderLayout());
		setOpaque(false);
		setMaximumSize(new Dimension(600, 24));
		JPanel paneIcons = new JPanel(new FlowLayout());
		paneIcons.setOpaque(false);
		lblDeadIcon = new JLabel();
		lblDeadIcon.setPreferredSize(new Dimension(16, 16));
		if (state == Player.DEAD)
		{//unused
			lblDeadIcon.setIcon(new ImageIcon("Data/Images/"));
		} 
		
		else 
		{//unused
			lblDeadIcon.setIcon(new ImageIcon("Data/Images/"));
		}
		paneIcons.add(lblDeadIcon);

		lblTypingIcon = new JLabel();
		lblTypingIcon.setPreferredSize(new Dimension(16, 16));
		if (typing)
		{//unused
			lblTypingIcon.setIcon(new ImageIcon("Data/Images/"));
		}
		paneIcons.add(lblTypingIcon);
		add(paneIcons, BorderLayout.LINE_START);
		JPanel paneRight = new JPanel(new BorderLayout());
		paneRight.setOpaque(false);
		JLabel lblUsername = new JLabel(username);
		paneRight.add(lblUsername, BorderLayout.LINE_START);
		if (chat) 
		{
			btnPrivateChat = new JButton("Whisper");
			btnPrivateChat.setIcon(new ImageIcon("resources/images/private_chat.png"));
			btnPrivateChat.setFocusable(false);
			btnPrivateChat.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0) 
				{
					chatTextField.setText("/whisper " + playerName + " ");
				}
			});
			paneRight.add(btnPrivateChat, BorderLayout.LINE_END);
		}
		
		add(paneRight, BorderLayout.CENTER);
	}
	
	public JLabel getLblDeadIcon() 
	{
		return lblDeadIcon;
	}

	public JLabel getLblTypingIcon()
	{
		return lblTypingIcon;
	}

	public JButton getBtnPrivateChat() 
	{
		return btnPrivateChat;
	}
	
	public void update(Observable observable, Object value)
	{
		Player player = (Player) value;
		state = player.status();
		typing = player.typing();

		SwingUtilities.invokeLater(new Runnable() 
		{
			
			public void run() 
			{
				// Update the dead icon
				
				if (state == Player.DEAD) 
				{//unused
					lblDeadIcon.setIcon(new ImageIcon("Data/Images/"));
				}
				
				else
				{//unused
					lblDeadIcon.setIcon(new ImageIcon("Data/Images/"));
				}
				
				// Update the typing icon
				if (typing) 
				{//unused 
					lblTypingIcon.setIcon(new ImageIcon("Data/Images/"));
				} 
				
				else 
				{
					lblTypingIcon.setIcon(null);
				}
			}
		});
	}
}

class PlayerPanelList extends JPanel implements Observer 
{//unused because It gets broken too easily and causes random crashes. Though executed seperatedly witht eh required values filled out it works like a charm but just not in game.
	private static final long serialVersionUID = 666;
	private Object[] players;
	private JTextArea chitChat;

	public PlayerPanelList(JTextArea chitChat) 
	{
		this.chitChat = chitChat;
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}
	
	public void update(Observable observable, Object value)
	{
		players = (Object[]) value;
		SwingUtilities.invokeLater(new Runnable()
		{
			
			public void run() 
			{
				removeAll();
				for (Object playaObj : players)
				{
					Player player = (Player) playaObj;
					String newUsername = player.playername();
					int newState = player.status();
					boolean newTyping = player.typing();
					boolean newChat = player.chat();					
					PlayerPanel nuPlComp = new PlayerPanel(newUsername, newState, newTyping, newChat, chitChat);
					player.addObserver(nuPlComp);
					add(nuPlComp);
				}
				revalidate();
				repaint();
			}
		});
	}
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
