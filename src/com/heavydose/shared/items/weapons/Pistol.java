package com.heavydose.shared.items.weapons;

import com.heavydose.client.Cache;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.GenericBullet;

public class Pistol extends Weapon {

	public Pistol(Entity owner) {
		super(owner, "Std Pistol", 4, 6, 250, 0.95f, 500, 12, 850);
		soundShoot = Cache.sounds.get("pistol");
		soundReload = Cache.sounds.get("reload");
		dropItemImage = Cache.images.get("item_p");
		setBullet(new GenericBullet(this, 0, 0f, 0f, com.heavydose.client.Cache.images.get("b_pistol")));
		infTotalAmmo = true;
		
	}
	
	@Override
	public Weapon newInstance(Entity owner){
		return new Pistol(owner);
	}

	
	
}
