///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package client;///////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

public class GameMechanix 
{
	//privates//har har
	private ObservableValue<String> dungeonName;
	private ObservableValue<Integer> goal;
	private ObservableValue<Boolean> turn;
	private ObservableValue<Boolean> inGame;
	private ObservableValue<String> username;	
	private ObservableValue<char[][]> map;	
	private ObservableValue<Integer> gold;
	private ObservableValue<Integer> hp;
	private ObservableValue<Boolean> sword;
	private ObservableValue<Boolean> armour;
	private ObservableValue<Boolean> lantern;
	private ObservableValue<GameSay> gameMssg;
	private ObservableArrayList<Player> players;

	public GameMechanix()
	{
		dungeonName = new ObservableValue<String>("");
		goal = new ObservableValue<Integer>(0);
		turn = new ObservableValue<Boolean>(false);
		inGame = new ObservableValue<Boolean>(true);
		username = new ObservableValue<String>("");
		map = new ObservableValue<char[][]>(new char[7][7]);
		gold = new ObservableValue<Integer>(0);
		hp = new ObservableValue<Integer>(3);
		sword = new ObservableValue<Boolean>(false);
		armour = new ObservableValue<Boolean>(false);
		lantern = new ObservableValue<Boolean>(false);
		gameMssg = new ObservableValue<GameSay>(null);
		players = new ObservableArrayList<Player>();
	}

	public ObservableValue<String> getDungeonName() 
	{
		return dungeonName;//will return the name of the dungeon
	}

	public ObservableValue<Integer> getGoal() 
	{
		return goal;//will return the goal integer
	}
	
	public ObservableValue<Boolean> getTurn()
	{
		return turn;//will tell you if its your turn
	}
	
	public ObservableValue<Boolean> getInGame() 
	{
		return inGame;//will tell you if you are ingame
	}

	public ObservableValue<String> getUsername()
	{
		return username;//will return the user given name
	}
	
	public ObservableValue<char[][]> getMap()
	{
		return map;// gets the 2d vector map
	}

	public ObservableValue<Integer> getGold()
	{
		return gold;//returns the gold value
	}

	public ObservableValue<Integer> getHp()
	{
		return hp;//returns the hp value
	}

	public ObservableValue<Boolean> getSword() 
	{
		return sword;//will give you a sword
	}
	
	public ObservableValue<Boolean> getArmour() 
	{
		return armour;//gives you armor
	}

	public ObservableValue<Boolean> getLantern()
	{
		return lantern;//returns the lantern
	}

	public ObservableValue<GameSay> getGameMessage() 
	{
		return gameMssg;//returns the game message string
	}
	
	public ObservableArrayList<Player> getPlayers() 
	{
		return players;
	}
	
	public Player getPlayer(String username)
	{
		for (Player player : players)
		{
			if (player.playername().equals(username)) 
			{
				return player;
			}
		}
		return null;
	}
	
	public void addGameMessage(String message, int type)
	{
		gameMssg.setValue(new GameSay(message, type));
	}
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
