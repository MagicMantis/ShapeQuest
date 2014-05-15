import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;


public class Client extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	
	public Socket socket;
	public DataInputStream in;
	public DataOutputStream out;
	
	private static Input input;
	
	public Game game;
	
	public Client(String address, int port, String name) throws Exception
	{
		super("Client");
		initWindow();
		if (!connectToServer(address, port))
			return;
		out.writeUTF(name);
		in.readUTF();
		game.setMyPlayer(initPlayer());
		input = new Input(in, this);
		Thread ithread = new Thread(input);
		ithread.start();
		Thread thread = new Thread(game);
		thread.start();
	}
	
	public static void main(String args[]) throws Exception
	{
		Login login = new Login();
		while(login.getSuccessful() == false)
		{
			login.update();
		}
		login.dispose();
	}
	
	private void initWindow()
	{
		this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
		this.setSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
		this.setResizable(false);
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		game = new Game(this);
		getContentPane().add(game, BorderLayout.CENTER);

		addKeyListener(game);
		addMouseListener(game);
		addMouseMotionListener(game);
		
		pack();
		
		this.setVisible(true);
	}
	
	public boolean connectToServer(String address, int port) throws Exception
	{
		game.printMessage("Connecting...");
		try {
			socket = new Socket(address, port);
		} catch (Exception e) {
			game.printMessage("Connection to Server could not be established");
			return false;
		}
		game.printMessage("Connected");
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		return true;
	}
	
	public Player initPlayer() throws Exception {
		
		int x, y, pid, atk, def, spd;
		String n;
		pid = in.readInt();
		x = in.readInt();
		y = in.readInt();
		atk = in.readInt();
		def = in.readInt();
		spd = in.readInt();
		n = in.readUTF();

		Player player = new Player(x, y, pid, new int[]{atk,def,spd}, n);
		return player;
	}
	
	public void updatePlayer(Player player) throws IOException {

		out.writeUTF("pa");
		out.writeInt(player.getX());
		out.writeInt(player.getY());
		
	}
	
	public void sendMessage(String s) throws IOException {

		out.writeUTF("pm");
		out.writeUTF(s);
		
	}
	
	public void initProjectile(Projectile p) throws IOException {
		
		out.writeUTF("ma");
		out.writeDouble(p.getX());
		out.writeDouble(p.getY());
		out.writeDouble(p.getDirection());
		
	}
	
}

class Input implements Runnable {
	
	DataInputStream in;
	Client client;
	
	public Input(DataInputStream in, Client client) {
		this.in = in;
		this.client = client;
	}
	
	public void run() {
		while(true){
			String message;
			try {
				message = in.readUTF();
				if (message.equals("pu"))
				{
					int pid = in.readInt();
					int x = in.readInt();
					int y = in.readInt();
					int hp = in.readInt();
					client.game.updatePlayer(pid, x, y, hp);
				}
				else if (message.equals("pa"))
				{
					client.game.addPlayer(client.initPlayer());
				}
				else if (message.equals("pr"))
				{
					int pid = in.readInt();
					client.game.removePlayer(pid);
				}
				else if (message.equals("pm"))
				{
					String s = in.readUTF();
					client.game.printMessage(s);
				}
				else if (message.equals("ma"))
				{
					double mx = in.readDouble();
					double my = in.readDouble();
					double mdir = in.readDouble();
					Game.projectiles.add(new Projectile(mx, my, mdir, 3));
				}
				else if (message.equals("dc"))
				{
					client.game.printMessage("Error - Lost Connection to Server");
					client.game.onDisconnect();
					client.socket = null;
				}
				else {
					System.out.println(message);
				}
			} catch (Exception e) {
				client.game.printMessage("Error - Lost Connection to Server");
				client.game.onDisconnect();
				client.socket = null;
				break;
			}
		}
	}
	
}