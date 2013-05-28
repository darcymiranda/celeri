package com.heavydose.shared;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.Log;

import com.heavydose.client.game.Celeri;
import com.heavydose.client.game.Effect;
import com.heavydose.shared.bullets.BShotGunRanged;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.enemies.Enemy;
import com.heavydose.shared.enemies.WormBoss;
import com.heavydose.shared.items.DropItem;
import com.heavydose.shared.map.Path;
import com.heavydose.shared.map.Pathfinder;
import com.heavydose.shared.map.TileMap;
import com.heavydose.util.DUT;
import com.heavydose.util.Tools;



public class EntityManager {
	
	private Entity[] entities = new Entity[1024];
	private Bullet[] bullets = new Bullet[2048];
	private DropItem[] items = new DropItem[128];
	private Effect[] effects = new Effect[256];
	
	public final Effect[] getEffects() { return effects; }
	public final Entity[] getEntities() { return entities; }
	public final Bullet[] getBullets() { return bullets; }
	public final DropItem[] getItems(){ return items; }
	
	private int enemyCount;
	
	private TileMap map;
	private Pathfinder pathfinder;
	
	// DEBUG
	public DUT debug_dut = new DUT();
	
	public void setMap(TileMap map){
		this.map = map;
		pathfinder = new Pathfinder(map);
	}
	
	public void addEffect(Effect effect){
		
		// Replace old effects
		if(Effect.currentEffectId == effects.length)
			Effect.currentEffectId = 0;
		
		if(effects[effects.length-1] != null){
			effects[Effect.currentEffectId] = effect;
			Effect.currentEffectId++;
			return;
		}

		// Freshly add effects
		for(int i = 0; i < effects.length; i++){
			if(effects[i] == null){
				effect.id = i;
				effects[i] = effect;
				Effect.currentEffectId = i;
				return;
			}
		}
		
	}
	
	public void addItem(DropItem item){
		for(int i = 0; i < items.length; i++){
			if(items[i] == null){
				item.id = i;
				items[i] = item;
				return;
			}
		}		
	}
	
	public Bullet getItemById(int id){
		for(int i = 0; i < items.length; i++){
			if(items[i] == null) continue;
			
			if(items[i].id == id)
				return bullets[i];
		}
		return null;
	}
	
	public void removeItemById(int id){
		for(int i = 0; i < items.length; i++){
			if(items[i] == null) continue;
			
			if(items[i].id == id){
				items[i] = null;
				return;
			}
		}
	}
	
	public void addBullet(Bullet bullet){
		for(int i = 0; i < bullets.length; i++){
			if(bullets[i] == null){
				bullet.id = i;
				bullets[i] = bullet;
				return;
			}
		}		
	}
	
	public Bullet getBulletById(int id){
		for(int i = 0; i < bullets.length; i++){
			if(bullets[i] == null) continue;
			
			if(bullets[i].id == id)
				return bullets[i];
		}
		return null;
	}
	
	public void removeBulletById(int id){
		for(int i = 0; i < bullets.length; i++){
			if(bullets[i] == null) continue;
			
			if(bullets[i].id == id){
				bullets[i] = null;
				return;
			}
		}
	}
	
	public ArrayList<Entity> getEntitiesInRadius(Vector2f pos, float radius, boolean inLineOfSight, Entity enemyOf){
		
		ArrayList<Entity> selected = new ArrayList<Entity>();
		
		if(inLineOfSight){
		
			for(int i = 0; i < entities.length; i++){
				if(entities[i] == null) continue;
				if(enemyOf != null && !entities[i].isEnemyTo(enemyOf.getOwnerPlayer())) continue;
				Entity entity = entities[i];
				
				float d = entity.getPosition().distance(pos);
				if( d  < radius){
					
					if(checkLineOfSight(pos, entity.getCenterPosition())){
						selected.add(entity);	
					}
				}
			}
		
		} else {
			
			for(int i = 0; i < entities.length; i++){
				if(entities[i] == null) continue;
				if(enemyOf != null && !entities[i].isEnemyTo(enemyOf.getOwnerPlayer())) continue;
				Entity entity = entities[i];
				
				float d = entity.getPosition().distance(pos);
				if( d  < radius){
					selected.add(entity);
				}
			}
			
		}
		
		return selected;
	}
	
	public ArrayList<Entity> getEntitiesInRadius(Vector2f pos, float radius, Entity enemyOf){
		return getEntitiesInRadius(pos, radius, false, enemyOf);
	}
	
	public ArrayList<Entity> getEntitiesInRadius(Vector2f pos, float radius){
		return getEntitiesInRadius(pos, radius, false, null);
	}
	
	public Entity getNearestEntity(Entity entity, final int FIND_TYPE, float maxDistance){
		
		float distance = maxDistance;
		Entity closestEntity = null;
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			if(entities[i].id == entity.id) continue;
			if(FIND_TYPE == Entity.ANY || (entities[i].getType() == FIND_TYPE && entities[i].isAlive())){
			
				float curDistance = Tools.distanceToEntity(entity, entities[i]);
				if(curDistance < distance){
					closestEntity = entities[i];
					distance = curDistance;
				}
				
			}
			
		}
		
		return closestEntity;
	}
	
	public boolean checkLineOfSight(Line line){
		return !map.isLineTouchingBlockedTiles(line);
	}
	
	public boolean checkLineOfSight(Entity source, Entity target){
		
		Vector2f sourcePos = source.getCenterPosition();
		Vector2f targetPos = target.getCenterPosition();
		
		return checkLineOfSight(sourcePos, targetPos);
	}
	
	public boolean checkLineOfSight(Vector2f source, Vector2f target){
		
		Line ray = new Line(source.x, source.y, target.x, target.y);
		return !map.isLineTouchingBlockedTiles(ray);
	}
	
	public Entity getEntityById(int id){
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			
			if(entities[i].id == id)
				return entities[i];
		}
		return null;
	}
	
	public void removeEntityById(int id){
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			
			if(entities[i].id == id){
				entities[i].kill(null);
				entities[i] = null;
				return;
			}
		}
	}
	
	public void addEntity(Entity entity){
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null){
				entity.id = i;
				entities[i] = entity;
				return;
			}
		}
	}
	
	public void addEntity(Entity entity, int id){
		if(entities[id] != null) Log.warn("added an entity to the index that wasn't null");
		entity.id = id;
		entities[id] = entity;
	}
	
	public void findPath(Enemy source, Entity target, int depth){
		
		Path path = pathfinder.constructPath(source, target, depth);
		
		source.clearMoveQueue();
		for(int i = 0; i < path.getSize(); i++){
			Path.Step step = path.getStep(i);
			source.queueMove(step.x*map.getTileWidth() + (map.getTileWidth()/4), step.y*map.getTileHeight() + (map.getTileHeight()/4));
		}
	}
	
	public void update(int delta){
		
		int countEnemies = 0;
		
		// Updates all of the bullets
		for(int i = 0; i < bullets.length; i++){
			if(bullets[i] == null) continue;
			
			Bullet bullet = bullets[i];
			
			if(!bullet.isAlive()){
				bullets[i] = null;
				continue;
			}
			
			bullet.update(delta);
			
			// reimplement for better performance
			map.checkTileCollision(bullet);
			
		}
		
		// update all of the items
		for(int i = 0; i < items.length; i++){
			if(items[i] == null) continue;
			
			DropItem item = items[i];
			
			if(!item.isAlive()){
				items[i] = null;
				continue;
			}
			
			item.update(delta);
			
			// reimplement for better performance
			map.checkTileCollision(item);
			
		}
		
		// Updates all of the effects (debris, blood, etc.)
		for(int i = 0; i < effects.length; i++){
			if(effects[i] == null) continue;
			
			Effect effect = effects[i];
			
			effect.update(delta);
			
		}
		
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			
			Entity entity = entities[i];
			
			// remove the entity if it's dead
			if(!entity.isAlive()){
				entities[i] = null;
				continue;
			}
			
			
			
			if(entity instanceof Enemy){
				
				countEnemies++;
				
				/*
				if(!(entity instanceof WormBoss)){
				
					int cam_x = (int)Celeri.cam.getCameraX();
					int cam_y = (int)Celeri.cam.getCameraY();
					int cam_width = (int)Celeri.gc.getWidth();
					int cam_height = (int)Celeri.gc.getHeight();
					
					
									
					if (entity.position.x < cam_x || entity.position.y < cam_y ||
							entity.position.x > (cam_x + cam_width ) || entity.position.y > (cam_y + cam_height)) {
						continue;
					}
				}
				*/
			}

			entity.update(delta);
			
			//debug_dut.start();
			
			// Collision
			if(entity.isCollidable()){
				
				//if(entity.type != Entity.BOSS)
				map.checkTileCollision(entity);
				
				// Bullet
				for(int j = 0; j < bullets.length; j++){
					if(bullets[j] == null) continue;
					if(bullets[j].getOwnerEntity() == entity) continue;
					
					// if a bullet already killed this entity, we should ignore it for the rest
					if(!entity.isAlive()) continue;
					
					Bullet bullet = bullets[j];
					
					if(bullet instanceof BShotGunRanged){
						BShotGunRanged bShotGunRanged = (BShotGunRanged)bullet;
						if(entity.getHitBox().intersects(bShotGunRanged.getRay())){
							bShotGunRanged.onRayHit(entity);
						}
					}
					
					if(bullet.isCollidable() && !bullet.getIgnoreEntityCollision()){
						if(entity.getHitBox().intersects(bullet.getHitBox())){
							entity.onHit(bullet);
							bullet.onHit(entity);
						}
					}
				}
				
				// Item
				// only affects hero types
				if(entity.type == Entity.HERO){
					
					Hero hero = (Hero)entity;
				
					for(int j = 0; j < items.length; j++){
						if(items[j] == null) continue;
						if(items[j].getIgnoreEntityCollision()) continue;
						
						DropItem item = items[j];
						
						Rectangle entityRect = entity.getHitBox(),
						  otherEntityRect = item.getHitBox();
				
						if(entityRect.intersects(otherEntityRect)){
							item.onHit(entity);
							hero.onPickUp(item);
							items[j] = null;
						}
					}
				}
				
				if(!entity.getIgnoreEntityCollision()){
			
					// Entity
					for(int j = 0; j < entities.length; j++){
						if(entities[j] == null) continue;
						if(entities[i].id == entities[j].id) continue;
						
						Entity otherEntity = entities[j];
						
						if(entity.type == Entity.BOSS && otherEntity instanceof Enemy)
							continue;
						
						if(otherEntity.isCollidable() && !otherEntity.getIgnoreEntityCollision()){
							
							Rectangle entityRect = entity.getHitBox(),
									  otherEntityRect = otherEntity.getHitBox();
							
							if(entityRect.intersects(otherEntityRect)){
								
								entity.onHit(otherEntity);
								otherEntity.onHit(entity);
								
								Vector2f translatedDifference = Tools.calcMinTranslationDistance(entityRect, otherEntityRect);
								entity.addPosition(translatedDifference.x, translatedDifference.y);
							}
						}
						
						
						/**
						 * Slows down enemies depending how close they're to one another
						 * Not worth the framerate loss
						if(entity instanceof Enemy && otherEntity instanceof Enemy){
							Enemy enemy = (Enemy)entity;
							Enemy otherEnemy = (Enemy)otherEntity;
							
							if(enemy.getInfrontCast().intersects(otherEnemy.getHitBox())){
								
								
								Vector2f enemyVel = enemy.getVelocity();
								
								float dist = enemy.position.distance(otherEnemy.position);
								dist /= 64;
								
								enemyVel.x *= dist;
								enemyVel.y *= dist;
								
								enemy.setVelocity(enemyVel);
								
							}
								
						}
						*/
					}
				}
			}
			
			//debug_dut.accumulate();
			
			if(entity instanceof Unit){
				Unit unit = (Unit)entity;
				
				if(unit.isShooting()){
					Bullet[] bullet = unit.shoot();
					if(bullet != null){
						for(int bul = 0; bul < bullet.length; bul++){
							addBullet(bullet[bul]);
						}
					}
				}
			}
			
			
			
			
		}
		
		enemyCount = countEnemies;
		
		//System.out.println(debug_dut.toString());
		//debug_dut.reset();
		
	}
	
	public void clearAll(){
		for(int i = 0; i < effects.length; i++){
			effects[i] = null;
		}
		for(int i = 0; i < bullets.length; i++){
			bullets[i] = null;
		}
		for(int i = 0; i < items.length; i++){
			items[i] = null;
		}
		for(int i = 0; i < entities.length; i++){
			entities[i] = null;
		}
	}

	
	public void render(Graphics g){
		for(int i = 0; i < effects.length; i++){
			if(effects[i] == null) continue;
			effects[i].render(g);
		}
		for(int i = 0; i < items.length; i++){
			if(items[i] == null) continue;
			items[i].render(g);
		}
		for(int i = 0; i < bullets.length; i++){
			if(bullets[i] == null) continue;
			bullets[i].render(g);
		}
		for(int i = entities.length-1; i >= 0 ; i--){
			if(entities[i] == null) continue;
			
			// because the hero can be dead but still in the list
			if(entities[i].isAlive())
				entities[i].render(g);
		}
	}
	
	public int getEnemyCount(){ return enemyCount; }

}
