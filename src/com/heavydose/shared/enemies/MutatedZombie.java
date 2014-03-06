package com.heavydose.shared.enemies;

import com.heavydose.Cache;
import com.heavydose.shared.Entity;
import com.heavydose.util.Tools;


public class MutatedZombie extends Enemy {
	
	private float chargeDistance;
	private float chargeSpeedModifier;
	private boolean isCharging;

	public MutatedZombie(float x, float y, int level) {
		super(x, y, 32, 32, level);

        score = 25;
		
		chargeDistance = 280;
		chargeSpeedModifier = 4;
		
		speed = 60;
		sight = 600;
		setHealth(3,3);
		chase = true;
		type = Entity.CREEP;
		setImage(Cache.images.get("mutated_zombie"), false);
		
	}
	
	public void update(int delta){
		super.update(delta);
		
		// increase speed when the target gets close
		if(target != null){
			float distance = Tools.distanceToEntity(this, target);
			if(distance < chargeDistance){
				if(!isCharging){
					speed *= chargeSpeedModifier;
					isCharging = true;
				}
			}else{
				if(isCharging){
					speed /= chargeSpeedModifier;
					isCharging = false;
				}
			}
		}
		
	}
	
	public void onHit(Entity entity) {
		super.onHit(entity);
		
	}

	public void onDeath(Entity killer) {
		super.onDeath(killer);
	}

}
