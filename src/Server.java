import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


public class Server extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final int MAX_USERS = 10;

	public static Random random = new Random();
	
	public static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	
	ServerSocket serverSocket;
	Socket socket;
	DataOutputStream out;
	DataInputStream in;
	static Users user[] = new Users[MAX_USERS];
	
	JPanel chatWindow;
	JTextPane chatPane;
	JScrollPane scrollPane;
	JTextField chatField;
	
	public Server(int port) throws Exception
	{
		//setup JFrame
		super("Server");
		initGui();
		
		//Run Server
		printMessage("Server Starting...");
		serverSocket = new ServerSocket(port);
		printMessage("Server Online");
		while(true) {
			socket = serverSocket.accept();
			for (int i = 0; i < MAX_USERS; i++)
			{
				printMessage("Connection from: "+ socket.getInetAddress());
				out = new DataOutputStream(socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
				if (user[i] == null)
				{
					Color color = new Color(random.nextInt()%100+100, random.nextInt()%100+100,random.nextInt()%100+100);
					user[i] = new Users(out, in, user, this, i, color);
					Thread thread = new Thread(user[i]);
					thread.start();
					break;
				}
			}
		}
	}	
	
	private void initGui()
	{
		this.setPreferredSize(new Dimension(640, 480));
		this.setSize(new Dimension(640, 480));
		this.setResizable(false);
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		chatWindow = new JPanel();
		chatWindow.setLayout(new BorderLayout());
		
		chatPane = new JTextPane();
		chatPane.setEditable(false);
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        
        chatPane.setCharacterAttributes(aset, false);

		scrollPane = new JScrollPane(chatPane);
		scrollPane.setAutoscrolls(true);
		chatWindow.add(scrollPane, BorderLayout.CENTER);
		
		chatField = new JTextField();
		chatField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					String message = chatField.getText();
					if (message.charAt(0) == '/')
					{
						serverCommand(message);
					}
					else {
						printMessage("Server", message);
						for (int i = 0; i < MAX_USERS; i++) {
							if (user[i] != null) {
								user[i].chatQueue.add(new String("Server: "+message));
							}
						}
					}
					chatField.setText("");
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		
		chatWindow.add(chatField, BorderLayout.SOUTH);
		
		this.add(chatWindow, BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	public static void main(String args[]) throws Exception
	{
		@SuppressWarnings("unused")
		Server server = new Server(12975);
	}
	
	public void printMessage(String message)
	{
		chatPane.setText(chatPane.getText()+message+"\n");
		chatPane.setCaretPosition(chatPane.getText().length());
	}
	
	public void printMessage(String name, String message)
	{
		chatPane.setText(chatPane.getText()+name+": "+message+"\n");
		chatPane.setCaretPosition(chatPane.getText().length());
	}
	
	public void printMessage(String name, String message, Color color)
	{
		chatPane.setText(chatPane.getText()+name+": "+message+"\n");
		chatPane.setCaretPosition(chatPane.getText().length());
	}
	
	public void serverCommand(String command)
	{
		String action, param = "";
		//get action command
		int actionEnd;
		if (command.contains(" ")) {
			actionEnd = command.indexOf(' ');
			param = command.substring(actionEnd+1, command.length());
		}
		else {
			actionEnd = command.length();
		}
		action = command.substring(1, actionEnd);
		//do action
		if (action.equals("players"))
		{
			printMessage("Players Online: ");
			for (int i = 0; i < MAX_USERS; i++)
			{
				if (user[i] != null)
				printMessage(user[i].getPlayerID() + ". "+user[i].getName());
			}
		}
		else if (action.equals("kick"))
		{
			int kickID = 0;
			boolean found = false;
			for (int i = 0; i < Server.MAX_USERS; i++)
			{
				if (user[i] != null)
				{
					if (user[i].getName().equals(param))
					{	
						kickID = i;
						found = true;
						break;
					}
				}
			}
			if (found)
			{
				printMessage(user[kickID].getName()+" has been kicked");
				for (int i = 0; i < MAX_USERS; i++) {
					if (user[i] != null) {
						user[i].chatQueue.add(new String(user[kickID].getName()+" has been kicked"));
					}
				}
				for (int i = 0; i < Server.MAX_USERS; i++)
				{
					if (user[i] != null && user[i].getPlayerID() != kickID)
					{
						user[i].removeQueue.add(user[kickID]);
					}
					user[kickID].disconnect();
					user[kickID] = null;
				}
			}
			else
			{
				printMessage("No Player: "+param);
			}
		}
		else if (action.equals("exit"))
		{
			System.exit(0);
		}
		else {
			printMessage("Unknown Command: "+action);
		}
	}
}

class Users implements Runnable {

	DataOutputStream out;
	DataInputStream in;
	Users user[] = new Users[Server.MAX_USERS];
	ArrayList<Users> userQueue = new ArrayList<Users>();
	ArrayList<Users> removeQueue = new ArrayList<Users>();
	ArrayList<String> chatQueue = new ArrayList<String>();
	ArrayList<Projectile> pAddQueue = new ArrayList<Projectile>();
	ArrayList<Projectile> pRemoveQueue = new ArrayList<Projectile>();
	Server server;
	int outputMode = 0;
	boolean newUser = false;
	
	private boolean disconnected = false;
	private String name;
	private int playerid;
	private Color color;
	private int x, y;
	private int hp, maxhp;
	
	public Users(DataOutputStream out, DataInputStream in, Users[] user, Server server, int pid, Color color)
	{
		this.out = out;
		this.in = in;
		this.user = user;
		this.server = server;
		this.playerid = pid;
		this.color = color;
		this.x = this.y = 200;
		this.hp = this.maxhp = 100;
	}
	
	public void run()
	{
		//connect and get name
		try {
			name = in.readUTF();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//add self
		userQueue.add(this);
		//get other users and init this for other users
		for(int i = 0; i < Server.MAX_USERS; i++) {
			if (user[i] != null && user[i] != this) {	
				user[i].userQueue.add(this);
				userQueue.add(user[i]);
			}
		}
		/* output codes */
		/* pa - player add */
		/* pu - player update */
		/* pr - player remove */
		/* pm - player message */
		/* dc - disconnect */
		/* ma - projectile add */
		
		//server loop
		while (true) {
			
			if (disconnected)
			{
				try {
					out.writeUTF("dc");
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			else if (removeQueue.size() > 0)
			{
				try {
					removeUser(removeQueue.get(0));
				} catch (Exception e) {
					System.out.println("Failed to Remove User");
					e.printStackTrace();
				}
			}
			
			else if (userQueue.size() > 0)
			{
				try {
					setupUser(userQueue.get(0));
				} catch (Exception e) {
					System.out.println("Failed to Setup User");
					e.printStackTrace();
				}
			}
			
			else {

				try {
					String message = in.readUTF();
					if (message.equals("pa"))
					{
						int x = in.readInt();
						int y = in.readInt();
						this.x = x;
						this.y = y;
					}
					else if (message.equals("pm"))
					{
						String s = in.readUTF();
						server.printMessage(s);
						for (int i = 0; i < Server.MAX_USERS; i++)
						{
							if (user[i] != null && user[i].playerid != getPlayerID()) {
								user[i].chatQueue.add(s);
							}
						}
					}
					else if (message.equals("ma"))
					{
						double mx = in.readDouble();
						double my = in.readDouble();
						double dir = in.readDouble();
						Server.projectiles.add(new Projectile(mx, my, dir, 3));
						for (int i = 0; i < Server.MAX_USERS; i++) {
							if (user[i] != null && user[i].playerid != getPlayerID()) {
								out.writeUTF("ma");
								out.writeDouble(mx);
								out.writeDouble(my);
								out.writeDouble(dir);
							}
						}
					}
					for (int i = 0; i < Server.MAX_USERS; i++) {
						if (user[i] != null && user[i].playerid != getPlayerID()) {
							out.writeUTF("pu");
							out.writeInt(user[i].getPlayerID());
							out.writeInt(user[i].getX());
							out.writeInt(user[i].getY());
							out.writeInt(user[i].getHP());
						}
					}
					while (chatQueue.size() > 0)
					{
						out.writeUTF("pm");
						out.writeUTF(chatQueue.get(0));
						chatQueue.remove(0);
					}
				} catch (IOException e) {
					server.printMessage(name + " has disconnected");
					for (int i = 0; i < Server.MAX_USERS; i++)
					{
						if (user[i] != null)
						{
							user[i].removeQueue.add(user[playerid]);
						}
					}
					this.user[playerid] = null;
					
					break;
				}
			}
		}
	}
	
	public void disconnect() {
		disconnected = true;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPlayerID() {
		return playerid;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
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
	
	public void setupUser(Users user) throws Exception {
		out.writeUTF("pa");
		out.writeInt(user.getPlayerID());
		out.writeInt(user.getX());
		out.writeInt(user.getY());
		out.writeInt(user.getColor().getRed());
		out.writeInt(user.getColor().getGreen());
		out.writeInt(user.getColor().getBlue());
		out.writeUTF(user.getName());
		
		userQueue.remove(user);
	}
	
	public void removeUser(Users user) throws Exception {
		
		int pid = user.getPlayerID();
		out.writeUTF("pr");
		out.writeInt(pid);
		
		removeQueue.remove(user);
		this.user[pid] = null;
		
	}
	
}

class ServerLogic implements Runnable {
	
	public void run() {
		while (true) {
			for (int i = 0; i < Server.projectiles.size(); i++) {
				Projectile p = Server.projectiles.get(i);
				p.update();
				for (int j = 0; j < Server.MAX_USERS; j++)
				{
					Users u = Server.user[j];
					if (p.getX() > u.getX()-2 && p.getX() < u.getX()+14 && 
							p.getY() > u.getY()-2 && p.getY() < u.getY()+14)
					{
						Server.user[j].pRemoveQueue.add(p);
						Server.user[j].setHP(Server.user[j].getHP()-10);
					}
						
				}
			}
		}
	}
	
}