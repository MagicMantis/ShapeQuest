package client;
import java.io.DataInputStream;

/* ClientInput Class */
/* This class reads input from the server and
 * updates the client */

class ClientInput implements Runnable {
	
	DataInputStream in;
	Client client;
	
	public ClientInput(DataInputStream in, Client client) {
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
					System.out.println(x + " - " + y);
				}
				else if (message.equals("mpu"))
				{
					int hp = in.readInt();
					client.game.updateMyPlayer(hp);
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