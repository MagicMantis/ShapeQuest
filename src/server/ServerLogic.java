package server;
/* ServerLogic Class */
/* This class handles game logic that should be handled by the server
 * to prevent exploitation, such as projectiles. */

import client.Projectile;

class ServerLogic implements Runnable {
	
	public void run() {
		while (true) {
			for (int i = 0; i < Server.projectiles.size(); i++) {
				Projectile p = Server.projectiles.get(i);
				p.update();
				for (int j = 0; j < Server.MAX_USERS; j++)
				{
					Users u = Server.user[j];
					if (u != null)
					{
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
}
