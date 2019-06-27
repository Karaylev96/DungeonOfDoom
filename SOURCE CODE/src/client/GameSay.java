///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package client;///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameSay 
{
	// Message types///
	public static final int NORMAL = 0;
	public static final int WHISPER = 1;
	public static final int WARNING = 2;
	public static final int ERROR = 3;
	public static final int DEATH = 4;	
	public static final int WIN = 5;
	public static final int HP = 6;
	public static final int GOLD = 7;
	public static final int CHAT = 8;
	public static final int SWORD = 9;	
	public static final int LANTERN = 10;
	public static final int ARMOUR = 11;
	public static final int SHOUT = 12;
	public static final int HPLOSS = 13;
	public static final int WAITING = 14;
	public static final int SUCCEED = 15;
	
	private int type;
	private String message;
	
	public GameSay(String message, int type) 
	{
		this.message = message;//will bring the string message 
		this.type = type;//with the speciific int
	}

	public GameSay(String message) 
	{
		this(message, NORMAL);//will bring the message
	}

	public int typeRt() 
	{
		return type;//will return the type
	}


	public void typeSetting(int type) 
	{
		this.type = type;
	}

	public String mssgRt() 
	{
		return message;//returns the message
	}

	public void mssgSetting(String message) 
	{
		this.message = message;//handles the sring setting
	}
}

class ServerMessage 
{
	//privates//
	private MainClient client;
	private GameMechanix model;	
	private String command;
	private String parameter;
	
	public ServerMessage(String message, MainClient client) 
	{
		this.client = client;//this is the client 
		model = client.model();//we will use model from the client class
		String[] splitMessage = message.split(" ", 2);
		command = splitMessage[0];
		if (splitMessage.length == 2) 
		{
			parameter = splitMessage[1];
		}
		
		else 
		{
			parameter = null;
		}
	}
	
	public String commandRt()
	{
		return command;//commands string is being returned
	}

	public void commandSetting(String command) 
	{
		this.command = command;//Additional settings for the command
	}

	public String parameterRt()
	{
		return parameter;//paramaters are being returned
	}

	public void setParameter(String parameter) 
	{
		this.parameter = parameter;
	}

	public void handle() 
	{
		if (command.equals("HELLO") && parameter != null) 
		{
			gameHello();//heelllloooooo 
		} 
		
		else if (command.equals("GOAL") && parameter != null) 
		{
			gameAim();
		}
		
		else if (command.equals("WIN")) 
		{
			gameWin();//e[icwin
		} 
		
		else if (command.equals("LOSE"))
		{
			gameLose();//epic fail
		}

		else if (command.equals("CHANGE"))
		{
			gameChange();//the wind of change blows straight
		}
		
		else if (command.equals("STARTTURN")) 
		{
			gameStartTurn();//start turn
		} 
		
		else if (command.equals("ENDTURN")) 
		{
			gameEndTurn();//end turn will initialise
		} 
		
		else if (command.equals("HITMOD") && parameter != null) 
		{
			gameHitMod();//hit points modifier
		} 
		
		else if (command.equals("TREASUREMOD") && parameter != null) 
		{
			gameTreasureMod();//gold count modifier
		} 
		
		else if (command.equals("MESSAGE") && parameter != null) 
		{
			gameMssg();//Massager
		}
		
		else if (command.equals("FAIL") && parameter != null) 
		{
			gameFail();//epic one
		} 
		
		else if (command.equals("LOOKREPLY"))
		{
			gameLookReply();//this was mostly used by the bot which I unintentionally broke and removed. I blame windows for this. And java for being a clunky piece of ....
		}
		
		else if (command.equals("DUNGEON") && parameter != null)
		{
			gameDng();//
		}
		
		else if (command.equals("SWORDMOD")) 
		{
			gameSword();//sword modifier
		}
		
		else if (command.equals("ARMOURMOD")) 
		{
			gameArmour();//armor modifier
		}
		
		else if (command.equals("LANTERNMOD"))
		{
			gameLanternMod();//lantern modifier. lnatern should be a synonym for increased visibility in this game. hmmmmm
		}
		
		else if (command.equals("SHOUT") && parameter != null) 
		{
			gameShout();//will handle the shouting mechanism in the chat 
		} 
		
		else if (command.equals("PLAYERS") && parameter != null) 
		{
			gamePlayerOnline();//something I couldnt get working because java cannot see sharp.
		} 
		
		else if (command.equals("STARTTYPING") && parameter != null) 
		{
			startTyping();//command and chat related thingy. I am a scientist thus thingy is an absolutely valid wordQQ
		} 
		
		else if (command.equals("ENDTYPING") && parameter != null) 
		{
			endTyping();//similar to the one above
		} 
		
		else if (command.equals("CHAT") && parameter != null) 
		{
			serverChat();//we get to chat with people while playing so rad wow
		} 
		
		else if (command.equals("WHISPERTO") && parameter != null) 
		{
			serverWhisperTo();//shhhhhh it handles the whispers to someone
		}
		
		else if (command.equals("WHISPERFROM") && parameter != null)
		{
			serverWhisperFrom();//handles the whispers from someone
		}
		
		else if (command.equals("WAITING") && parameter != null)		
		{
			serverWaiting();//handles the waiting 
		} 
		
		else if (command.equals("MUTE")) 
		{
			serverMute();//handles the muted players
		} 
		
		else if (command.equals("UNMUTE")) 
		{
			serverUnmute();//same as the above but with this we unmute the muted player
		} 
		
		else if (command.equals("ATTACKWARNING") && parameter != null)
		{
			serverAttackWarning();//handles the attack warning we receive while we are being attacked by some other player
		} 
		
		else if (command.equals("ATTACKSUCCEED") && parameter != null) 
		{
			serverAttackSucceed();//message about the success of the attack
		}
		
		else if (command.equals("DEATH") && parameter != null)
		{
			serverDeath();//death message handling
		}
	}

	//bellow im attempting to implement most of the html syntaxis into the chat system
	public String escapeHTML(String source) 
	{
		source = source.replaceAll("&", "&amp;");
		source = source.replaceAll("\"", "&quot;");
		source = source.replaceAll("<", "&lt;");
		source = source.replaceAll(">", "&gt;");
		return source;
	}
	
	public String chatFormatting(String source)
	{
		source = escapeHTML(source);
		if (source.matches(".*\\[b\\].*\\[/b\\].*"))
		{
			source = source.replaceAll("\\[b\\]", "<b>");
			source = source.replaceAll("\\[/b\\]", "</b>");
		}
		
		if (source.matches(".*\\[i\\].*\\[/i\\].*"))
		{
			source = source.replaceAll("\\[i\\]", "<i>");
			source = source.replaceAll("\\[/i\\]", "</i>");
		}
		
		if (source.matches(".*\\[u\\].*\\[/u\\].*"))
		{
			source = source.replaceAll("\\[u\\]", "<u>");
			source = source.replaceAll("\\[/u\\]", "</u>");
		}
		
		if (source.matches(".*\\[c=#[A-Za-z0-9]+\\].*\\[/c\\].*"))
		{
			source = source.replaceAll("\\[c=(#[A-Za-z0-9]+)\\]", "<font color='$1'>");
			source = source.replaceAll("\\[/c\\]", "</font>");
		}

		if (source.contains("[br]"))
		{
			source = source.replaceAll("\\[br\\]", "<br>");
		}		
		return source;
	}
	

	private void gameHello() 
	{
		model.getUsername().setValue(parameter);
		client.sendMessage("LOOK");
	}

	private void gameAim()//brings out the game aim
	{
		model.getGoal().setValue(Integer.parseInt(parameter));
	}

	private void gameWin()//game win conition is satisfied 
	{
		model.addGameMessage("You won the game! Great success!!!", GameSay.WIN);
		model.getInGame().setValue(false);
	}

	private void gameLose() //you either have died or some other player got the winning
	{
		model.addGameMessage("You lost the game. Puny mortal...", GameSay.WARNING);
		JOptionPane.showMessageDialog(null, "You lost the game! Not great....", "Lose!!!!", JOptionPane.WARNING_MESSAGE);
		model.getInGame().setValue(false);
	}
	
	private void gameChange()
	{
		client.sendMessage("LOOK");//look commad
	}
	
	private void gameStartTurn()
	{
		model.getTurn().setValue(true);//game turn intiation handling 
	}
	
	private void gameEndTurn()
	{//end turn handling
		model.addGameMessage("Your turn has ended.", GameSay.WARNING);
		model.getTurn().setValue(false);
	}
	
	private void gameHitMod() 
	{//clunky HP modifier
		int amount = Integer.parseInt(parameter);
		model.getHp().setValue(model.getHp().getValue() + amount);
		
		if (amount < 0)
		{//if you get hit this happens
			model.addGameMessage("HP has decreased by " + Math.abs(amount) + "!", GameSay.HPLOSS);
		} 
		
		else 
		{//elsewise we get hp
			model.addGameMessage("HP has increased by " + amount + ".", GameSay.HP);
		}
	}

	private void gameTreasureMod()
	{//the gold handling
		int amount = Integer.parseInt(parameter);
		model.getGold().setValue(model.getGold().getValue() + amount);		
	
		if (amount < 0) 
		{//its self explanatory. 
			model.addGameMessage("Your Gold has been decreased by " + Math.abs(amount) + "!", GameSay.GOLD);
		} 
		
		else
		{
			model.addGameMessage("Your Gold has just increased by " + amount + "!", GameSay.GOLD);
		}
	}
	
	private void gameMssg()
	{
		model.addGameMessage(parameter, GameSay.NORMAL);//MESSAAGGGGEEEESSS
	}

	private void gameFail() 
	{
		model.addGameMessage(parameter, GameSay.ERROR);///
	}
	
	private void gameLookReply()
	{
		// Set up new vision to default 'X' values
		char[][] lookReply = new char[7][7];
		for (int row = 0; row < 7; row++) 
		{
			for (int col = 0; col < 7; col++) 
			{
				lookReply[row][col] = 'X';
			}
		}
		
		try 
		{
			String rowString = client.input().readLine();
			int offSet = 1;
			if (rowString.length() == 7)
			{
				offSet = 0;
			}
			
			ArrayList<String> rows = new ArrayList<String>();
			rows.add(rowString);
			//When I wrote this, only God and I understood what I was doing
			for (int i = 1; i < rowString.length(); i++) 
			{
				rowString = client.input().readLine();
				rows.add(rowString);
			}
			
			for (int row = 0; row < rows.size(); row++)
			{
				String currentRow = rows.get(row);
				
				for (int col = 0; col < currentRow.length(); col++)
				{
					lookReply[row + offSet][col + offSet] = currentRow.charAt(col);
				}
			}			
			model.getMap().setValue(lookReply);			
			//Now, God only knows
		} 
		
		catch (IOException e) 
		{
			return;
		}
	}

	private void gameDng() 
	{
		model.getDungeonName().setValue(parameter);
	}
	
	private void gameSword() 
	{
		model.getSword().setValue(true);
		model.addGameMessage("Got a sword.", GameSay.SWORD);
	}
	
	private void gameArmour() 
	{
		model.getArmour().setValue(true);
		model.addGameMessage("Got an armour.", GameSay.ARMOUR);
	}

	private void gameLanternMod() 
	{
		model.getLantern().setValue(true);
		model.addGameMessage("Got a lantern.", GameSay.LANTERN);
	}
	
	
	private void gameShout() 
	{
		String[] shoutParams = parameter.split(" ", 2);
		model.addGameMessage(shoutParams[0] + " shouted: " + shoutParams[1], GameSay.SHOUT);
	}
	
	private void gamePlayerOnline() 
	{//handles the players online
		int count = Integer.parseInt(parameter);
		
		try {
			ArrayList<Player> players = new ArrayList<Player>();
			
			for (int i = 0; i < count; i++) 
			{
				String playerLine = client.input().readLine();
				String[] playerParameters = playerLine.split(" ", 3);				
				String username = playerParameters[0];
				int status = Integer.parseInt(playerParameters[1]);
				boolean chat = false;
				if (playerParameters[2].equals("1")) 
				{
					chat = true;
				}				
				players.add(new Player(status, username, chat));
			}			
			model.getPlayers().clear();
			model.getPlayers().addAll(players);
		} 
		
		catch(IOException e)
		{
			return;
		}
	}
	
	private void startTyping() 
	{//typing is typing
		model.getPlayer(parameter).setTyping(true);
	}
	
	private void endTyping() 
	{//I explained it
		model.getPlayer(parameter).setTyping(false);
	}
	
	private void serverChat()
	{
		//chat system parameters
		String[] chatParameters = parameter.split(" ", 2);
		String username = chatParameters[0];
		String chatMessage = chatParameters[1];
		chatMessage = chatFormatting(chatMessage);
		model.addGameMessage("<b>" + username + "</b>: " + chatMessage, GameSay.CHAT);
	}
	

	private void serverWhisperTo() 
	{
		//whisperings whitin the chat system (still very clunky but it just works, how good well...)
		String[] whisperParameters = parameter.split(" ", 2);
		String username = whisperParameters[0];
		String whisperMessage = whisperParameters[1];
		whisperMessage = chatFormatting(whisperMessage);
		model.addGameMessage("(To <b>" + username + "</b>): " + whisperMessage, GameSay.WHISPER);
	}
	
	private void serverWhisperFrom()
	{
		///the whisper reception in the chat system
		String[] whisperParameters = parameter.split(" ", 2);
		String username = whisperParameters[0];
		String whisperMessage = whisperParameters[1];
		whisperMessage = chatFormatting(whisperMessage);
		model.addGameMessage("(From <b>" + username + "</b>): " + whisperMessage, GameSay.WHISPER);
	}
	
	private void serverWaiting() 
	{
		model.addGameMessage("<b>" + escapeHTML(parameter) + "</b> is waiting for the next turn.", GameSay.WAITING);
	}
	
	private void serverMute() 
	{//if you are muted this happens
		model.addGameMessage("You are now unable to chat.", GameSay.WARNING);
	}

	private void serverUnmute() 
	{//if you are unmuted
		model.addGameMessage("You can chat now.", GameSay.NORMAL);
	}
	

	private void serverAttackWarning() 
	{//attack warning working
		model.addGameMessage(escapeHTML(parameter), GameSay.WARNING);
	}

	private void serverAttackSucceed() 
	{//attack successful things message
		model.addGameMessage("Attack against " + "<b>" + escapeHTML(parameter) + "</b> was successful.", GameSay.SUCCEED);
	}

	private void serverDeath() 
	{//death message
		model.addGameMessage("<b>" + escapeHTML(parameter) + "</b> has died.", GameSay.DEATH);
		model.getPlayer(parameter).setStatus(Player.DEAD);
	}
}

class MessagePanel extends JPanel implements Observer 
{	
	private static final long serialVersionUID = 666;//number of the beast
	
	private GameSay newMssg;
	public MessagePanel() 
	{
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBackground(Color.WHITE);
	}
	
	public void update(Observable observable, Object value)
	{
		newMssg = (GameSay) value;
		SwingUtilities.invokeLater(new Runnable()
		{
			private GameSay mssg = newMssg;
			public void run() //this is how the game messages work
			{
				JPanel paneMessage = new JPanel(new BorderLayout());
				paneMessage.setOpaque(false);
				JLabel lblTime = new JLabel(new SimpleDateFormat(" [HH:mm] ").format(Calendar.getInstance().getTime()));
				lblTime.setForeground(Color.GRAY);
				paneMessage.add(lblTime, BorderLayout.LINE_START);
				JLabel lblNewMessage = new JLabel("<html>" + mssg.mssgRt() + "<html>");
				lblNewMessage.setIcon(new ImageIcon("resources/images/message_" + mssg.typeRt() + ".png"));
				switch (mssg.typeRt())
				{
					case GameSay.WIN:
					case GameSay.HP:
					case GameSay.SUCCEED:
						break;
						
					case GameSay.WARNING:
					case GameSay.HPLOSS:
					case GameSay.DEATH:
						lblNewMessage.setForeground(Color.RED);
						break;
						
					case GameSay.WAITING:
						break;
					//default chat
					case GameSay.CHAT:
						lblNewMessage.setForeground(Color.BLUE);
						break;
//whispers are in blue and italic
					case GameSay.WHISPER:
						lblNewMessage.setForeground(Color.BLUE);
						lblNewMessage.setFont(lblNewMessage.getFont().deriveFont(Font.ITALIC));
						lblNewMessage.setBackground(new Color(230, 230, 230));
						lblNewMessage.setOpaque(true);
						break;

					case GameSay.SHOUT:
						lblNewMessage.setFont(lblNewMessage.getFont().deriveFont(Font.BOLD));
						break;
				}
				
				paneMessage.add(lblNewMessage, BorderLayout.CENTER);
				add(paneMessage);
				revalidate();
				repaint();
		        int height = (int) getPreferredSize().getHeight();
		        Rectangle rect = new Rectangle(0,height,10,10);//im way too afraid to touch it otherwise it will break down
		        scrollRectToVisible(rect);//sets visible
			}
		});
	}
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
