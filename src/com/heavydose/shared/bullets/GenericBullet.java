package com.heavydose.shared.bullets;

import com.heavydose.Cache;
import org.newdawn.slick.Image;

import com.heavydose.shared.items.weapons.Weapon;

public class GenericBullet extends Bullet{

	public GenericBullet(Weapon weapon) {
		super(weapon, 0, 0, 0, Cache.images.get("bullet"));
	}
	
	public GenericBullet(Weapon weapon, int damage, float direction, float speed) {
		super(weapon, damage, direction, speed, Cache.images.get("bullet"));
	}
	
	public GenericBullet(Weapon weapon, int damage, float direction, float speed, Image image) {
		super(weapon, damage, direction, speed, image);
	}
	
	public GenericBullet(Weapon weapon, int damage, float direction, float speed, int decay, Image image) {
		super(weapon, damage, direction, speed, decay, image);
	}

	@Override
	public Bullet newInstance(int damage, float direction, float speed) {
		return new GenericBullet(weapon, damage, direction, speed, decay, getImage());
	}

}
