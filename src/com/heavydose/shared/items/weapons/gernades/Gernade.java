package com.heavydose.shared.items.weapons.gernades;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.FastTrig;

import com.heavydose.client.Cache;
import com.heavydose.client.game.Celeri;
import com.heavydose.shared.Entity;
import com.heavydose.shared.Unit;
import com.heavydose.shared.enemies.Enemy;
import com.heavydose.util.Tools;


public class Gernade extends Entity {
	
	private int timeTillExplode;
	private int timer;
	private int damage = 55;
	private int explosionRadius = 250;
	private int speed = 450;
	
	private Vector2f target;

	public Gernade(Entity owner, int timeTillExplode) {
		super(0, 0, 16, 16);
		setOwnerEntity(owner);
		this.timeTillExplode = timeTillExplode;
		this.setHitBox();
		this.setImage(Cache.images.get("item"), false);
		setIgnoreEntityCollision(true);
	}
	
	public Gernade throwGernade(Vector2f target){
		Gernade newInstance = new Gernade(getOwnerEntity(), timeTillExplode);
		newInstance.setTarget(target);
		newInstance.setPosition(getOwnerEntity().getCenterPosition());
		return newInstance;
	}
	
	public void setTarget(Vector2f target){
		
		Vector2f ownerPosition = getOwnerEntity().getCenterPosition();
		float direction = (float) -(Math.atan2(target.x - ownerPosition.x , target.y - ownerPosition.y) * (180 / Math.PI));
		velocity.x = -(speed * (float) FastTrig.sin(Math.toRadians(direction)));
		velocity.y = (speed * (float) FastTrig.cos(Math.toRadians(direction)));
		
		
	}
	
	public void update(int delta){
		super.update(delta);
		
		if(timer > timeTillExplode && isAlive()){
			kill(null);
			return;
		} else {
			timer += delta;
		}
		
		velocity.x = Tools.lerp(velocity.x, 0, 0.05f);
		velocity.y = Tools.lerp(velocity.y, 0, 0.05f);
		
	}

	@Override
	protected void onDeath(Entity killer) {
		
		ArrayList<Entity> hitEntities = Celeri.entityManager.getEntitiesInRadius(
				this.getCenterPosition(), explosionRadius, true, getOwnerEntity());
		
		for(int i = 0; i < hitEntities.size(); i++){
			Entity entity = hitEntities.get(i);
			if(entity instanceof Enemy){
				Enemy unit = (Enemy)entity;
				
				float direction = (float) -(Math.atan2(unit.getCenterX() - this.getCenterX(),
						unit.getCenterY() - this.getCenterY()) * (180 / Math.PI));
				
				unit.takeDamage(getOwnerEntity(), damage);
				unit.knockback(direction, 64);
				unit.hitEffects(direction);
				
			}
			
		}
		
	}

	@Override
	protected void onHit(Entity attacker) {
	}

}
