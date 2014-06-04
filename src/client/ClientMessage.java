package client;

public class ClientMessage {

	private String message;
	private long startTime;
	
	private int x, y;
	
	public ClientMessage(String message) {
		this.message = message;
		this.startTime = System.currentTimeMillis();
		
		this.x = 15;
		this.y = Client.GAME_HEIGHT-65;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public long getTime()
	{
		return System.currentTimeMillis()-startTime;
	}
	
	public void moveUp() {
		y -= 15;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
