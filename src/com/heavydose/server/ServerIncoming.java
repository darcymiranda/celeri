package com.heavydose.server;

import java.util.Random;

import org.newdawn.slick.geom.Vector2f;



import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.heavydose.network.Player;
import com.heavydose.network.Network.Connect;
import com.heavydose.network.Network.Disconnect;
import com.heavydose.network.Network.DownloadMap;
import com.heavydose.network.Network.MoveEntity;
import com.heavydose.network.Network.Shoot;
import com.heavydose.network.Network.SpawnEntity;
import com.heavydose.server.CeleriServer.PlayerConnection;
import com.heavydose.shared.Const;
import com.heavydose.shared.Entity;
import com.heavydose.shared.Hero;
import com.heavydose.shared.Unit;

public class ServerIncoming extends Listener{
	
	CeleriServer unknown;
	
	public ServerIncoming(CeleriServer unknown){
		this.unknown = unknown;
	}

	@Override
	public void received(Connection connection, Object object){
		super.received(connection, object);
		
		PlayerConnection currentPlayerConn = (PlayerConnection)connection;
		Player currentPlayer = currentPlayerConn.getPlayer();
		
		if(object instanceof MoveEntity){
			MoveEntity moveEntity = (MoveEntity)object;
			
			currentPlayer.hero.moveUp(moveEntity.w);
			currentPlayer.hero.moveDown(moveEntity.s);
			currentPlayer.hero.moveRight(moveEntity.d);
			currentPlayer.hero.moveLeft(moveEntity.a);
			currentPlayer.hero.setPosition(new Vector2f(moveEntity.x, moveEntity.y));
			currentPlayer.hero.setVelocity(new Vector2f(moveEntity.xv, moveEntity.yv));
			currentPlayer.hero.setRotation(moveEntity.r);
		}
		
		if(object instanceof Shoot){
			Shoot shoot = (Shoot)object;
			
			Unit unit = (Unit) unknown.world.entityManager.getEntityById(shoot.owner);
			if(unit != null)
				unit.setShooting(shoot.shooting);
			
			unknown.server.sendToAllExceptTCP(currentPlayerConn.getID(), shoot);
			
		}
		
		if(object instanceof SpawnEntity){
			SpawnEntity spawnEntity = (SpawnEntity)object;
			
			switch(spawnEntity.type){
				case Entity.HERO:
					if(!currentPlayer.hero.isAlive()){
						
						Random rand = new Random();
						float x = rand.nextFloat()*1000;
						float y = rand.nextFloat()*1000;
						
						while(unknown.world.map.isPositionBlocked(x,y)){
							x = rand.nextFloat()*1000;
							y = rand.nextFloat()*1000;
						}
				
						Hero hero = new Hero(x,y);
						hero.setHitBox();
						hero.setOwnerPlayer(currentPlayer);
						currentPlayer.hero = hero;
						unknown.world.entityManager.addEntity(hero);
						
						SpawnEntity newSpawnEntity = new SpawnEntity();
						newSpawnEntity.owner = currentPlayerConn.getID();
						newSpawnEntity.id = hero.id;
						newSpawnEntity.type = Entity.HERO;
						newSpawnEntity.x = hero.getPosition().x;
						newSpawnEntity.y = hero.getPosition().y;
						unknown.server.sendToAllTCP(newSpawnEntity);
					
					}
					break;
			
			}
		}
		
		if(object instanceof Connect){
			currentPlayer.username = ((Connect)object).username;
			
			currentPlayer.id = currentPlayerConn.getID();
			
			Connect connect = new Connect();
			connect.id = currentPlayerConn.getID();
			connect.username = ((Connect)object).username;
			unknown.server.sendToAllTCP(connect);

			DownloadMap downloadMap = new DownloadMap();
			downloadMap.map = unknown.world.map.getData();
			downloadMap.width = unknown.world.map.getWidth();
			downloadMap.height = unknown.world.map.getHeight();
			unknown.server.sendToTCP(currentPlayerConn.getID(), downloadMap);
			
			// Send data of all the other clients
			PlayerConnection[] playerConnections = getPlayerConnections();
			for(int i = 0; i < playerConnections.length; i ++){
				Player player = playerConnections[i].getPlayer();
				
				if(playerConnections[i].getID() == currentPlayerConn.getID()) continue;
				
				Connect conn = new Connect();
				conn.id = playerConnections[i].getID();
				conn.username = playerConnections[i].getPlayer().username;
				unknown.server.sendToTCP(currentPlayerConn.getID(), conn);
				
				if(player.hero.isAlive()){
				
					SpawnEntity spawnEntity = new SpawnEntity();
					spawnEntity.owner = playerConnections[i].getID();
					spawnEntity.id = player.hero.id;
					spawnEntity.type = Entity.HERO;
					spawnEntity.x = player.hero.getPosition().x;
					spawnEntity.y = player.hero.getPosition().y;
					unknown.server.sendToTCP(currentPlayerConn.getID(), spawnEntity);
				
				}
			}
			
		}
		
	}
	
	private PlayerConnection[] getPlayerConnections(){
		Connection[] connections = unknown.server.getConnections();
		PlayerConnection[] playerConnections = new PlayerConnection[connections.length];
		for(int i = 0; i < connections.length; i++){
			playerConnections[i] = (PlayerConnection) connections[i];
		}
		return playerConnections;
	}
	
	@Override
	public void connected(Connection connection){
		super.connected(connection);
	}
	
	@Override
	public void disconnected(Connection connection){
		super.disconnected(connection);
		
		PlayerConnection playerConn = (PlayerConnection) connection;
		
		unknown.world.entityManager.removeEntityById(playerConn.getPlayer().hero.id);
		
		Disconnect disconnect = new Disconnect();
		disconnect.id = connection.getID();
		unknown.server.sendToAllTCP(disconnect);
	}
	
}
