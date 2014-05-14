import java.awt.Color;
import java.util.ArrayList;


public class Player {
	
	private int x, y;
	private int hp, maxhp;
	private int playerID;
	private Color color;
	private String name;
	
	public Player(int x, int y, int pid, Color color, String name)
	{
		this.x = x;
		this.y = y;
		this.playerID = pid;
		this.color = color;
		this.name = name;
		this.setHp(this.maxhp = 100);
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

	public void setHp(int hp) {
		this.hp = hp;
	}
}
