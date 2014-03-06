package com.heavydose.shared.items.weapons;

import com.heavydose.Cache;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.BBazooka;

public class Bazooka extends Weapon{

	public Bazooka(Entity owner) {
		super(owner, "Bazooka", 20, 20, 60, 0.90f, 750, 50, 1400);
		soundShoot = Cache.sounds.get("gun");
		soundReload = Cache.sounds.get("reload");
		dropItemImage = Cache.images.get("item_m");
		
		setBullet(new BBazooka(this, 0, 0f, 0f, Cache.images.get("b_pistol")));
		
	}
	

	
	@Override
	public Weapon newInstance(Entity owner){
		return new Bazooka(owner);
	}

}
