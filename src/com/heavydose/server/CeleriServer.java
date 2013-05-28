package com.heavydose.server;

import java.io.IOException;



import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.heavydose.network.Network;
import com.heavydose.network.Player;
import com.heavydose.shared.DetermineSide;

public class CeleriServer extends Thread {
	
	public World world;
	public Server server;
	public Player computerPlayer;
	
	public CeleriServer() throws IOException{
		
		DetermineSide.isClient = false;
		
		server = new Server(16384, 16*1024){
			protected Connection newConnection(){
				return new PlayerConnection();
			}
		};
		
		Network.register(server);
		
		computerPlayer = new Player();
		computerPlayer.isLocal = false;
		computerPlayer.username = "Computer";
		computerPlayer.id = 0;
		
		Log.info("Generating world...");
		world = new World(this);
		
		server.addListener(new ServerIncoming(this));
		server.bind(Network.PORT);
		server.start();
		
		this.start();
		
	}
	
	public void run(){
		try {
			
			while(true){
				
				world.update();
				
				Thread.sleep(com.heavydose.shared.Const.TICK_RATE);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class PlayerConnection extends Connection{
		private Player player = new Player();
		
		public Player getPlayer(){ return player; }
		
	}
	
	public static void main(String args[]){
		try{
			new CeleriServer();
		}catch(Exception e){
			e.printStackTrace();
		}
		//Log.set(Log.LEVEL_DEBUG);
	}

}
