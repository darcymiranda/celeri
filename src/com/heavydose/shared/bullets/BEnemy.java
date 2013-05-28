package com.heavydose.shared.bullets;

import org.newdawn.slick.Image;

import com.heavydose.shared.items.weapons.Weapon;

public class BEnemy extends Bullet {
	
	public BEnemy(Weapon weapon, int damage, float direction, float speed, int decay, Image image) {
		super(weapon, damage, direction, speed, image);
		this.decay = decay;
	}

	@Override
	public Bullet newInstance(int damage, float direction, float speed) {
		return new GenericBullet(weapon, damage, direction, speed, decay, getImage());
	}

}
