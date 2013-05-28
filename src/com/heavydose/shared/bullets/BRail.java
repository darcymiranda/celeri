package com.heavydose.shared.bullets;

import org.newdawn.slick.Image;

import com.heavydose.shared.Entity;
import com.heavydose.shared.items.weapons.Weapon;

public class BRail extends Bullet {

	public BRail(Weapon weapon, int damage, float direction, float speed, Image image) {
		super(weapon, damage, direction, speed, image);
		width = image.getWidth();
		height = image.getHeight();
	}
	
	@Override
	public Bullet newInstance(int damage, float direction, float speed){
		return new BRail(weapon, damage, direction, speed, getImage());
	}
	
	@Override
	public void onHit(Entity entity) {
		if(speed > 200){
			setSpeed(speed - (speed / 4));
		}
	}

}
