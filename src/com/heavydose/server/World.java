package com.heavydose.server;

import java.util.Random;

import com.heavydose.network.Network;
import com.heavydose.shared.Const;
import com.heavydose.shared.Entity;
import com.heavydose.shared.EntityManager;
import com.heavydose.shared.Hero;
import com.heavydose.shared.Unit;
import com.heavydose.shared.enemies.Creeper;
import com.heavydose.shared.enemies.Enemy;
import com.heavydose.shared.map.TileMap;



public class World {
	
	public EntityManager entityManager;
	public TileMap map;
	
	private CeleriServer unknown;
	
	private int maxEnemyCount;
	private final int MAX_ENEMY_MULTIPLIER = 16;
	
	private int retargetRate = 1500;
	private int retargetIncrement = 0;
	
	private int enemySpawnRate = 300;
	private int enemySpawnIncrement = 0;
	
	public World(CeleriServer unknown){
		this.unknown = unknown;
		entityManager = new EntityManager();
		
		map = new TileMap(100, 100);
		//map.generate();
		entityManager.setMap(map);
		
	}
	
	public void update(){
		
		int playerCount = unknown.server.getConnections().length;
		
		// Adjust max enemy count
		maxEnemyCount = 0;//playerCount*MAX_ENEMY_MULTIPLIER;
		
		if(playerCount > 0){
			if(entityManager.getEnemyCount() <= maxEnemyCount){
				if(enemySpawnIncrement > enemySpawnRate){
					
					Random rand = new Random();
					float x = rand.nextFloat()*1000;
					float y = rand.nextFloat()*1000;
					
					while(map.isPositionBlocked(x,y)){
						x = rand.nextFloat()*1000;
						y = rand.nextFloat()*1000;
					}
					
					Enemy zombie = new Creeper(x,y,1);
					zombie.setHitBox();
					zombie.setOwnerPlayer(unknown.computerPlayer);
					entityManager.addEntity(zombie);
					//zombie.setTarget(entityManager.getNearestEntity(zombie,
					//		Const.EntityTypes.HERO, zombie.getSight()));
					//x = rand.nextFloat()*1000;
					//y = rand.nextFloat()*1000;
					//zombie.queueMove(x, y, false);
					
					enemySpawnIncrement = 0;
				}
				else
					enemySpawnIncrement += com.heavydose.shared.Const.TICK_RATE;
			}
		}
		
		Entity[] entities = entityManager.getEntities();
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			
			Entity entity = entities[i];
			
			// Re-target
			if(entity instanceof Enemy){
				Enemy enemy = (Enemy) entity;
				
				if(retargetIncrement > retargetRate){
					
					//Entity target = entityManager.getNearestEntity(enemy, Const.EntityTypes.HERO, enemy.getSight());
					//entityManager.findPath(enemy, target, 15);
					
					//enemy.setTarget(entityManager.getNearestEntity(enemy,
					//		Const.EntityTypes.HERO, enemy.getSight()));
					//Random rand = new Random();
					//float x = rand.nextFloat()*1000;
					//float y = rand.nextFloat()*1000;
					//enemy.queueMove(x, y, false);
					
					/*
					if(enemy.getTarget() != null){
						enemy.setShooting(true);
						Network.Shoot shoot = new Network.Shoot();
						shoot.owner = enemy.id;
						shoot.shooting = true;
						unknown.server.sendToAllTCP(shoot);
					}
					else{
						enemy.setShooting(false);
						Network.Shoot shoot = new Network.Shoot();
						shoot.owner = enemy.id;
						shoot.shooting = false;
						unknown.server.sendToAllTCP(shoot);
					}
					*/
					
					retargetIncrement = 0;
				} else
					retargetIncrement += com.heavydose.shared.Const.TICK_RATE;
				
			}
			
			if(entity instanceof Unit){
				Unit unit = (Unit) entity;
			
				Network.MoveEntity moveEntity = new Network.MoveEntity();
				moveEntity.id = unit.id;
				moveEntity.type = unit.getType();
				moveEntity.health = (int) unit.getHealth();
				moveEntity.x = unit.getPosition().x;
				moveEntity.y = unit.getPosition().y;
				moveEntity.xv = unit.getVelocity().x;
				moveEntity.yv = unit.getVelocity().y;
				moveEntity.r = unit.getRotation();
				
				if(entity instanceof Hero){
					Hero hero = (Hero)entity;
					moveEntity.w = hero.getW();
					moveEntity.s = hero.getS();
					moveEntity.d = hero.getD();
					moveEntity.a = hero.getA();
				}
				
				unknown.server.sendToAllTCP(moveEntity);
			
			}
		}
		
		entityManager.update(com.heavydose.shared.Const.TICK_RATE);
		
	}
}
