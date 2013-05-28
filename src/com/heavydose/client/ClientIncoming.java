package com.heavydose.client;




import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.heavydose.client.game.Celeri;
import com.heavydose.network.Player;
import com.heavydose.network.Network.Connect;
import com.heavydose.network.Network.Disconnect;
import com.heavydose.network.Network.DownloadMap;
import com.heavydose.network.Network.MoveEntity;
import com.heavydose.network.Network.Shoot;
import com.heavydose.network.Network.SpawnEntity;
import com.heavydose.shared.Const;
import com.heavydose.shared.Entity;
import com.heavydose.shared.Hero;
import com.heavydose.shared.NetEntity;
import com.heavydose.shared.Unit;
import com.heavydose.shared.enemies.Creeper;

public class ClientIncoming extends Listener{
	
	private Celeri unknown;
	
	public ClientIncoming(Celeri unknown){
		this.unknown = unknown;
	}

	@Override
	public void received(Connection connection, Object object){
		super.received(connection, object);
			
		if(object instanceof MoveEntity){
			MoveEntity moveEntity = (MoveEntity)object;
			
			NetEntity entity = (NetEntity) unknown.entityManager.getEntityById(moveEntity.id);
			
			if(entity == null){
				
				switch(moveEntity.type){
					case Entity.CREEP:
						
						Creeper zombie = new Creeper(moveEntity.x, moveEntity.y, 1);
						zombie.setImage(Cache.images.get("zombie"), true);
						zombie.setHitBox();
						zombie.setOwnerPlayer(unknown.getComputerPlayer());
						
						unknown.entityManager.addEntity(zombie, moveEntity.id);
						entity = zombie;
						break;
				
				}
			}
			
			if(entity != null){
				
				entity.updateNet(moveEntity.x, moveEntity.y, moveEntity.xv, moveEntity.yv, moveEntity.r);
				
				if(entity instanceof Unit){
					Unit unit = (Unit) entity;
					
					unit.setHealth(moveEntity.health);
					
				}
				
				if(entity instanceof Hero){
					Hero hero = (Hero) entity;
					
					if(hero.getOwnerPlayer() != unknown.getLocalPlayer()){
					
						hero.moveUp(moveEntity.w);
						hero.moveDown(moveEntity.s);
						hero.moveRight(moveEntity.d);
						hero.moveLeft(moveEntity.a);
						
					}
					else{
						// Check and make sure local hero is sync with server.
					}
				
				}
			
			}
			
		}
		
		if(object instanceof Shoot){
			Shoot shoot = (Shoot)object;
			
			Unit unit = (Unit) unknown.entityManager.getEntityById(shoot.owner);
			if(unit != null)
				unit.setShooting(shoot.shooting);
			
		}
	
		if(object instanceof SpawnEntity){
			SpawnEntity spawnEntity = (SpawnEntity)object;
			
			switch(spawnEntity.type){
				case Entity.HERO:
					
					Player player = unknown.getPlayers().get(spawnEntity.owner);
					Hero hero = new Hero(spawnEntity.x, spawnEntity.y);
					hero.setOwnerPlayer(player);
					hero.setImage(Cache.images.get("hero"), true);
					hero.setHitBox();
					player.hero = hero;
					
					unknown.entityManager.addEntity(hero, spawnEntity.id);
					
					if(player.isLocal){
						unknown.focusCamera(hero);
					}
					break;
			}
			
		}
		
		if(object instanceof Connect){
			Connect connect = (Connect)object;
			
			if(connect.username != null){
				if(unknown.getPlayers().get(connect.id) == null){
					unknown.createPlayer(connect.id, ((Connect)object).username);
				}
				else
					Log.warn("A player connected with an id that already owns a player class.");
			}
			else{
				// send message invalid name
			}
		}
		
		if(object instanceof Disconnect){
			Disconnect disconnect = (Disconnect)object;
			
			Player player = unknown.getPlayers().get(disconnect.id);
			unknown.getPlayers().remove(disconnect.id);
			player.hero.kill(null);
			
		}
		
		if(object instanceof DownloadMap){
			DownloadMap downloadMap = (DownloadMap)object;
			//unknown.map.load(downloadMap.map, downloadMap.width, downloadMap.height);
		}
		
	}
	
	@Override
	public void connected(Connection connection){
		super.connected(connection);

	}
	
	@Override
	public void disconnected(Connection connection){
		super.disconnected(connection);
		
	}
	
}
