package com.heavydose.shared.items.weapons;

import java.util.Random;

import com.heavydose.Cache;
import org.newdawn.slick.Sound;

import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.bullets.GenericBullet;
import com.heavydose.shared.items.Item;


public class Weapon extends Item {
	
	protected Random rand;
	
	public float bulletSpeed;
	public float fireRate, bonusFireRate;
	public float spread;
	public float aimDirection;
	
	protected int minDamage;
	protected int maxDamage;
	protected float fireRateIncr;
	protected int maxAmmo;
	protected int curAmmo;
	protected int storedAmmo;
	protected float reloadTime;
	protected float reloadTimeIncr;
	
	protected boolean infTotalAmmo;
	protected boolean infAmmo;
	protected boolean canShoot;
	protected boolean reloading;
	protected boolean canInteruptReload;

	protected Bullet bullet;
	protected Sound soundShoot;
	protected Sound soundReload;

	public Weapon(Entity owner, String name, int minDamage, int maxDamage, float fireRate,
			float spread, float bulletSpeed, int maxAmmo, float reloadSpeed) {
		
		super(owner, name);
		super.type = Item.WEAPON;
		
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.fireRate = fireRate;
		this.spread = spread;
		this.bulletSpeed = bulletSpeed;
		this.maxAmmo = maxAmmo;
		this.curAmmo = maxAmmo;
		this.storedAmmo = maxAmmo * 2;
		this.reloadTime = reloadSpeed;
		
		rand = new Random();
		rand.nextFloat();
		rand.nextFloat();
		
		setBullet(new GenericBullet(this, 0, 0f, 0f, Cache.images.get("bullet")));
		
	}
	
	private boolean tickReloadSpeed(int delta){
		if(reloading){
			if(reloadTimeIncr > reloadTime){
				reloading = false;
				refillAmmo();
				reloadTimeIncr = 0;
			}else{
				reloadTimeIncr += delta;
				return false;
			}
		}
		return true;
	}
	
	private boolean tickFireRate(int delta){
		if(fireRateIncr >= fireRate + bonusFireRate){
			fireRateIncr = fireRate + bonusFireRate;
			return true;
		}else{
			fireRateIncr += delta;
			return false;
		}
	}
	
	public void update(int delta){
		
		if(curAmmo < 1 && !reloading){
			if(tickFireRate(delta)) reload();	// Delay before able to reload
			canShoot = false;
			return;
		}
		canShoot = tickReloadSpeed(delta);
		if(canShoot) canShoot = tickFireRate(delta);
		
	}
	
	public int getDamageRoll(){
		return rand.nextInt(maxDamage) + minDamage;
	}
	
	public void refillAmmo(){
		
		if(infTotalAmmo){
			curAmmo = maxAmmo;
			return;
		}
		
		if(maxAmmo < storedAmmo){
			
			int difference = maxAmmo - curAmmo;
			curAmmo = maxAmmo;
			storedAmmo -= difference;
			
		}else{
			curAmmo = storedAmmo;
			storedAmmo = 0;
		}
		
		
	}
	
	public void reload(){
		
		if(infAmmo){
			refillAmmo();
		}
		
		if(curAmmo >= maxAmmo || reloading || storedAmmo < 1) return;
		
		reloading = true;
		
		if(soundReload != null){
			soundReload.play();
		}

	}
	
	public void aim(float aimDirection){
		this.aimDirection = aimDirection;
	}
	
	protected float getEffectedAccuracy(){
		float min = spread - 1;
		float max = 1 - spread;
		return aimDirection - (rand.nextFloat() * (max - min) + min) * 100;
	}
	
	private boolean preAction(){
		
		if(!canShoot)
			return false;
		
		if(reloading && curAmmo != 0 && canInteruptReload)
			reloading = false;
		
		if(soundShoot != null){
            float pitch = 0.75f + (float)Math.random() * ((1.25f - 0.75f) + 1);
			soundShoot.play(pitch,0.75f);
		}
			
		curAmmo--;
		fireRateIncr = 0;
		
		return true;
		
	}
	
	protected Bullet[] onShoot(){
		Bullet[] bullets = new Bullet[1];
		bullets[0] = bullet.newInstance(getDamageRoll(), getEffectedAccuracy(), bulletSpeed);
		return bullets;
	}
	
	public Bullet[] action() {
		if(!preAction()) return null;
		return onShoot();
	}
	
	public Weapon newInstance(Entity owner){
		return new Weapon(getOwner(), name, minDamage, maxDamage, fireRate, spread, bulletSpeed, maxAmmo, reloadTime);
	}
	
	public String toString(){
		return this.getClass().getName() + "  Owner:" + this.getOwner();
	}
	
	public void addAmmo(int ammo){ this.storedAmmo += ammo; }
	
	public void setBullet(Bullet bullet){ this.bullet = bullet; }
	public void setSoundShoot(Sound sound){ this.soundShoot = sound; }
	public void setSoundReload(Sound sound){ this.soundReload = sound; }
	public void setInfiniteMode(boolean mode){ this.infAmmo = mode; this.infTotalAmmo = mode; }
	
	public final int getMinDamage(){ return minDamage; }
	public final int getMaxDamage(){ return maxDamage; }
	public final int getAmmoRemaining(){ return curAmmo; }
	public final int getAmmoCapacity(){ return maxAmmo; }
	public final int getStoredAmmo(){ return storedAmmo; }
	public final float getBulletSpeed(){ return bulletSpeed; }
	public final String getName(){ return name; }
	public final Random getWeaponRand(){ return rand; }
	
	public final boolean isInfiniteAmmo(){ return infTotalAmmo; }

}
