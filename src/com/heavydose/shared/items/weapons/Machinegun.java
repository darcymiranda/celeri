package com.heavydose.shared.items.weapons;

import com.heavydose.Cache;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.GenericBullet;


public class Machinegun extends Weapon{

	public Machinegun(Entity owner) {
		super(owner, "Std Machine Gun", 4, 5, 60, 0.90f, 750, 50, 700);
		soundShoot = Cache.sounds.get("gun");
		soundReload = Cache.sounds.get("reload");
		dropItemImage = Cache.images.get("item_m");
		setBullet(new GenericBullet(this, 0, 0f, 0f, Cache.images.get("b_pistol")));
		
	}
	
	@Override
	public Weapon newInstance(Entity owner){
		return new Machinegun(owner);
	}
}
