/* Server Class */
/* The server is a JFrame application that
 * manages communication between clients */

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

	//variable that determines the maximum number of simultaneous connections
	public static final int MAX_USERS = 10;

	public static Random random = new Random();
	
	public static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	
	//declares sockets and streams for network communication
	ServerSocket serverSocket;
	Socket socket;
	DataOutputStream out;
	DataInputStream in;
	//declares a list of users that will handle communication with individual clients
	static Users user[] = new Users[MAX_USERS];
	
	//declare Swing components for Server GUI
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
		//this loop waits for a connection attempt
		while(true) {
			socket = serverSocket.accept();
			//upon receiving connection request, finds first available user id
			//and sets up an instance of user in that slot
			for (int i = 0; i < MAX_USERS; i++)
			{
				printMessage("Connection from: "+ socket.getInetAddress());
				out = new DataOutputStream(socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
				if (user[i] == null)
				{
					//generates new users base stats
					int[] stats = new int[3];
					switch(Math.abs(random.nextInt()) % 3) {
					case 0:
						stats[0] = 10;
						break;
					case 1:
						stats[1] = 10;
						break;
					default:
						stats[2] = 10;
					}
					user[i] = new Users(out, in, user, this, i, stats);
					/* Users are threads that run simultaneously with the main thread.
					 * While the main thread listens for connection requests, User threads
					 * handle communication with clients */
					Thread thread = new Thread(user[i]);
					thread.start();
					break;
				}
			}
		}
	}	
	
	//this function sets up a JFrame and necessary GUI components
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
					//interprets message as command if begins with '/'
					if (message.charAt(0) == '/')
					{
						serverCommand(message);
					}
					//else prints sends message to all users
					else if (!message.equals(""))
					{
						printMessage("Server", message);
						for (int i = 0; i < MAX_USERS; i++) {
							if (user[i] != null) {
								user[i].chatQueue.add(new String("Server: "+message));
							}
						}
					}
					//resets chat field
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
	
	//prints a message to the server log
	public void printMessage(String message)
	{
		chatPane.setText(chatPane.getText()+message+"\n");
		chatPane.setCaretPosition(chatPane.getText().length());
	}
	
	//prints a message with a name to the server log
	public void printMessage(String name, String message)
	{
		chatPane.setText(chatPane.getText()+name+": "+message+"\n");
		chatPane.setCaretPosition(chatPane.getText().length());
	}
	
	//evaluates command parameter and performs corresponding action
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
		
		//prints list of players
		if (action.equals("players"))
		{
			printMessage("Players Online: ");
			for (int i = 0; i < MAX_USERS; i++)
			{
				if (user[i] != null)
				printMessage(user[i].getPlayerID() + ". "+user[i].getName());
			}
		}
		//kicks specified player
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
		//stops server
		else if (action.equals("exit") || action.equals("stop"))
		{
			System.exit(0);
		}
		else {
			printMessage("Unknown Command: "+action);
		}
	}
}

//this class will handle server logic (ie missles)
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