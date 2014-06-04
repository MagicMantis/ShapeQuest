package client;
import java.awt.Color;
import java.util.ArrayList;


public class Player {
	
	private int x, y;
	private int rVal, bVal, gVal;
	private int hp, maxhp;
	private int attack;
	private int defense;
	private int speed;
	private int playerID;
	private Color color;
	private String name;
	
	public Player(int x, int y, int pid, int[] stats, String name)
	{
		this.x = x;
		this.y = y;
		// colorVals = [attack, defense, speed]
		this.attack = stats[0];
		this.defense = stats[1];
		this.speed = stats[2];
		setColor();
		this.playerID = pid;
		this.name = name;
		this.hp = setMaxHp();
	}
	
	private Color setColor() {
		int rVal = 155 + attack;
		int gVal = 155 + defense;
		int bVal = 155 + speed;
		if(attack > defense && attack > speed) {
			gVal -= attack;
			bVal -= attack;
		}
		else if(defense > speed) {
			rVal -= defense;
			bVal -= defense;
		}
		else { // speed is greatest stat
			rVal -= speed;
			gVal -= speed;
		}
		return this.color = new Color(rVal, bVal, gVal);
	}
	
	private int setMaxHp() {
		return this.maxhp = 100 + bVal;
	}
	
	public void move(int xmove, int ymove, ArrayList<Player> players)
	{
		/*boolean xm = true, ym = true;
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			if (p == this)
				continue;
			if (x < p.getX()-16 && x + xmove >= p.getX()-16 && y >= p.getY()-16 && y < p.getY()+16)
				xm = false;
			if (x > p.getX() && x + xmove < p.getX()+16 && y >= p.getY()-16 && y < p.getY()+16)
				xm = false;
			if (y < p.getY()-16 && y + ymove >= p.getY()-16 && x >= p.getX()-16 && x < p.getX()+16)
				ym = false;
			if (y > p.getY() && y + ymove < p.getY()+16 && x >= p.getX()-16 && x < p.getX()+16)
				ym = false;
		}*/
		//if (xm)
		x += xmove;
		//if (ym)
		y += ymove;
	}
	
	public void setPos(int xpos, int ypos)
	{
		x = xpos;
		y = ypos;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getPlayerID() {
		return playerID;
	}

	public Color getColor() {
		return color;
	}
	
	public String getName() {
		return name;
	}

	public int getHP() {
		return hp;
	}
	
	public int getMaxHP() {
		return maxhp;
	}

	public void setHP(int hp) {
		this.hp = hp;
	}
}
