package com.heavydose.shared.items.weapons;

import com.heavydose.client.Cache;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.BRail;

public class Railgun extends Weapon {

	public Railgun(Entity owner) {
		super(owner, "Rail Gun", 32, 32, 600, 0.990f, 1600, 8, 800);
		soundShoot = Cache.sounds.get("rail");
		soundReload = Cache.sounds.get("reload");
		dropItemImage = Cache.images.get("item_r");
		setBullet(new BRail(this, 0, 0f, 0f, com.heavydose.client.Cache.images.get("b_rail")));
		
	}
	
	@Override
	public Weapon newInstance(Entity owner){
		return new Railgun(owner);
	}

}
