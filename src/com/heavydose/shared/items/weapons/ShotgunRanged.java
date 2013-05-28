package com.heavydose.shared.items.weapons;

import com.heavydose.client.Cache;
import com.heavydose.shared.DetermineSide;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.BShotGunRanged;


public class ShotgunRanged extends Weapon{

	public ShotgunRanged(Entity owner) {
		super(owner, "Electro", 3, 6, 200, 0.99f, 250, 20, 1500);
		
		soundShoot = Cache.sounds.get("gun");
		soundReload = Cache.sounds.get("reload");
		dropItemImage = Cache.images.get("item_e");
		setBullet(new BShotGunRanged(this, 0, 0f, 0f, com.heavydose.client.Cache.images.get("bullet")));
		
	}
	
	@Override
	public Weapon newInstance(Entity owner){
		return new ShotgunRanged(owner);
	}

}
