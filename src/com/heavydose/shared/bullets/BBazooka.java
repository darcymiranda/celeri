package com.heavydose.shared.bullets;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

import com.heavydose.client.game.Celeri;
import com.heavydose.shared.Entity;
import com.heavydose.shared.Unit;
import com.heavydose.shared.items.weapons.Weapon;

public class BBazooka extends Bullet {

	public BBazooka(Weapon weapon, int damage, float direction, float speed,
			Image image) {
		super(weapon, damage, direction, speed, image);
	}

	@Override
	public Bullet newInstance(int damage, float direction, float speed) {
		return new BBazooka(weapon, damage, direction, speed, getImage());
	}
	
	@Override
	public void update(int delta){
		super.update(delta);
		
		Log.debug("test");
	}
	
	@Override
	public void onHit(Entity entity){
		super.onHit(entity);
		
		ArrayList<Entity> hit = Celeri.entityManager.getEntitiesInRadius(this.getCenterPosition(), 300, true, weapon.getOwner());
		
		for(int i = 0; i < hit.size(); i++){
			if(hit.get(i) instanceof Unit){
				Unit unit = (Unit) hit.get(i);
				unit.takeDamage(weapon.getOwner(), 15);
			}
		}
		
	}
	

}
