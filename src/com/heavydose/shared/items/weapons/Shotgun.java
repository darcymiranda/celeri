package com.heavydose.shared.items.weapons;

import com.heavydose.client.Cache;
import com.heavydose.client.game.Celeri;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.bullets.GenericBullet;


public class Shotgun extends Weapon {
	
	private int bulletShots = 12;

	public Shotgun(Entity owner) {
		super(owner, "Std Shotgun", 3, 4, 650, 0.95f, 800, 8, 200);
		canInteruptReload = true;
		soundShoot = Cache.sounds.get("shotgun");
		soundReload = Cache.sounds.get("reload_shotgun");
		dropItemImage = Cache.images.get("item_s");
		setBullet(new GenericBullet(this, 0, 0f, 0f, com.heavydose.client.Cache.images.get("b_pistol")));
		
	}
	
	@Override
	public Weapon newInstance(Entity owner){
		return new Shotgun(owner);
	}
	
	@Override
	public void refillAmmo(){
		
		if(storedAmmo < 1)
			return;
		
		curAmmo += 1;
		storedAmmo -= 1;
		reloading = !(curAmmo == maxAmmo);
		
		if(soundReload != null){
			soundReload.play();
		}
	}
	
	@Override
	public Bullet[] onShoot(){
		
		float rotation = owner.getRotation();
		
		int bulletCount = bulletShots;
		int randSpread = (int) (30 * this.spread);
		for(int i = 0; i < bulletShots; i++){
			
			int dir = (int) (rand.nextInt(randSpread) - randSpread/2 + rotation);
			
			float randBulletSpeed = (rand.nextFloat() * getBulletSpeed()) + getBulletSpeed() / 2;
			Bullet bullet = this.bullet.newInstance(getDamageRoll(), dir, randBulletSpeed);
			bullet.setPosition(owner.getCenterPosition());
			
			Celeri.entityManager.addBullet(bullet);
			
			bulletCount--;
			
		}
		
		return null;
	}

}
