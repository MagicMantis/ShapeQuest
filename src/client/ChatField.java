package client;
import java.awt.Color;
import java.awt.Graphics;


public class ChatField {
	
	private int x, y;
	private String text;
	
	public ChatField () {
	
		x = 15;
		y = Client.GAME_HEIGHT-45;
		text = "";
		
	}

	public String getText() {
		return text;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setText(String setText) {
		text = setText;
	}
	
	public void addChar(char c) {
		if (getText().length() < 37)
		setText(getText()+c);
	}
	
	public void back() {
		if (getText().length() > 0)
		setText(text.substring(0, getText().length()-1));
	}
	
	public void draw(Graphics g, String name, boolean chatMode)
	{
		if(!chatMode)
		{
			g.setColor(Color.GRAY);
		}
		else
		{
			g.drawRect(getX()-1, getY()-15, 250, 20);
		}
		g.drawString(name+": "+getText(), getX(), getY());
		g.setColor(Color.BLACK);
	}
}
