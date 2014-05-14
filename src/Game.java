import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;


public class Game extends JPanel implements KeyListener, MouseListener, MouseMotionListener, Runnable {
	
	private static final long serialVersionUID = 1L;

	public static ArrayList<ClientMessage> chatLog = new ArrayList<ClientMessage>();
	ArrayList<Player> players = new ArrayList<Player>();
	ArrayList<String> chatQueue = new ArrayList<String>();
	
	public static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	
	public static ChatField chatField;

	private Client client;
	
	private BufferedImage buffer;
	private Graphics2D bufferGraphics;
	
	private String name;
	private Player myPlayer;
	
	private boolean chatMode = false;
	
	boolean w=false, a=false, s=false, d=false;
	
	public Game(Client client) {
		setSize(640, 480);
		setBackground(Color.WHITE);
		//addKeyListener(this);
		this.client = client;
		chatField = new ChatField();
	}
	
	public void run() {
		while(true)
		{
			try {
				tick();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			this.repaint();
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void tick() throws IOException
	{
		checkInput();
		updateEntities();
		updateChatLog();
		updateServer();
	}
	
	public void checkInput() {
		if (client.isFocused() == false)
			w = a = s = d = false;
		if (w)
			myPlayer.move(0, -1, players);
		if (a)
			myPlayer.move(-1, 0, players);
		if (s)
			myPlayer.move(0, 1, players);
		if (d)
			myPlayer.move(1, 0, players);
	}
	
	public void updateEntities() {
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = projectiles.get(i);
			p.update();
			if (p.getTime() > 4000)
			{
				projectiles.remove(p);
			}
		}
	}
	
	public void updateChatLog() {
		while (chatLog.size() > 20 && chatLog.get(0).getTime() > 8000)
			chatLog.remove(0);
		while (chatLog.size() > 10 && chatLog.get(0).getTime() > 18000)
			chatLog.remove(0);
	}
	
	public void updateServer() throws IOException {
		client.updatePlayer(myPlayer);
		while (chatQueue.size() > 0)
		{
			String s = chatQueue.get(0);
			client.sendMessage(s);
			chatQueue.remove(0);
		}
	}
	
	@Override
	public void paint(Graphics g)
	{	
		//buffer setup
		if (buffer == null)
		{
			buffer = (BufferedImage) createImage(getWidth(), getHeight());
		}
			
		if (bufferGraphics == null)
		{
			bufferGraphics = (Graphics2D) buffer.getGraphics();
		}
		//buffer clear
		bufferGraphics.clearRect(0, 0, getWidth(), getHeight());
		//draw logic
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			bufferGraphics.setColor(p.getColor());
			bufferGraphics.fillRect(p.getX(), p.getY(), 15, 15);
		}
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			bufferGraphics.setColor(p.getColor());
			bufferGraphics.setFont(new Font("Dialog", Font.PLAIN, 12));
			bufferGraphics.drawString(p.getName(), (int) (p.getX()+5-(2.5*p.getName().length())), p.getY()-1);
		}
		bufferGraphics.setColor(Color.BLACK);
		for (Projectile p: projectiles)
		{
			p.draw(bufferGraphics);
		}
		for (ClientMessage e: chatLog)
		{
			bufferGraphics.drawString(e.getMessage(), e.getX(), e.getY());
		}
		chatField.draw(bufferGraphics, name, chatMode);
		g.drawImage(buffer, 0, 0, null);
	}
	
	public void printMessage(String message)
	{
		for(ClientMessage e: chatLog)
		{
			e.moveUp();
		}
		chatLog.add(new ClientMessage(message));
	}

	public void setMyPlayer(Player player)
	{
		myPlayer = player;
		name = player.getName();
		addPlayer(myPlayer);
	}
	
	public void addPlayer(Player player)
	{
		players.add(player);
	}
	
	public void updatePlayer(int playerID, int x, int y, int hp)
	{
		for (Player p: players)
		{
			if (playerID == p.getPlayerID())
			p.setPos(x, y);
			p.setHp(hp);
		}
	}
	
	public void removePlayer(int playerID) {
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			if (p.getPlayerID() == playerID)
				players.remove(p);
		}
	}
	
	public void onDisconnect() {
		System.out.println("Called Disconnect");
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			if (p != myPlayer)
			{
				players.remove(p);
			}
		}
	}
	
	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		int px = myPlayer.getX()+8;
		int py = myPlayer.getY()+8;
		double angle = Math.atan2(e.getY()-py, e.getX()-px)*180/Math.PI;
		Projectile p = new Projectile(px,py,angle,4);
		projectiles.add(p);
		try {
			client.initProjectile(p);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (!chatMode)
		{
			if (e.getKeyCode() == KeyEvent.VK_W)
				w = true;
			if (e.getKeyCode() == KeyEvent.VK_A)
				a = true;
			if (e.getKeyCode() == KeyEvent.VK_S)
				s = true;
			if (e.getKeyCode() == KeyEvent.VK_D)
				d = true;
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
				chatMode = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if (!chatField.getText().isEmpty())
			{
				printMessage(myPlayer.getName()+": "+chatField.getText());
				chatQueue.add(new String(myPlayer.getName()+": "+chatField.getText()));
				chatField.setText("");
			}
			chatMode = false;
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W)
			w = false;
		if (e.getKeyCode() == KeyEvent.VK_A)
			a = false;
		if (e.getKeyCode() == KeyEvent.VK_S)
			s = false;
		if (e.getKeyCode() == KeyEvent.VK_D)
			d = false;
	}

	public void keyTyped(KeyEvent e) {
		if (chatMode)
		{
			if (e.getKeyChar() == '\b')
			{
				chatField.back();
			}
			else if (e.getKeyChar() != '\n')
			{
				chatField.addChar(e.getKeyChar());
			}
		}
	}

}
