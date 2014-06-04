package client;
import java.awt.Color;
import java.awt.Graphics;


public class HUD {
	
	private Player player;
	
	public HUD(Player player)
	{
		this.player = player;
	}
	
	public void draw(Graphics g)
	{
		if (player != null)
		{
			double ratio = (double)player.getHP()/(double)player.getMaxHP();
			g.setColor(new Color(255-(int)(255*ratio), (int)(255*ratio), 0));
			g.fillRect(5, 5, (int)(150*ratio), 20);
			g.setColor(Color.black);
			g.drawRect(5, 5, 150, 20);
			g.drawString(player.getHP()+"/"+player.getMaxHP(), 60, 20);
		}
	}

}
