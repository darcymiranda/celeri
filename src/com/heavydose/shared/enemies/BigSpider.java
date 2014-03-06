package com.heavydose.shared.enemies;

import java.util.Random;

import org.newdawn.slick.Sound;

import com.heavydose.Cache;
import com.heavydose.game.Celeri;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.bullets.GenericBullet;
import com.heavydose.shared.items.weapons.Weapon;

public class BigSpider extends Enemy {
	
	private int spawnSpiders = 12;

	public BigSpider(float x, float y, int level) {
		super(x, y, 64, 64, level);

        score = 224;

		speed = 70;
		sight = 250000;
		setHealth(75);
		attackRange = 5000;
		knockbackResistance = 0.50f;
		turnSpeed = 0.0015f;
		type = Entity.CREEP;
		
		setImage(Cache.images.get("big_spider2"), false);
		
		Weapon weapon = new Weapon(this, "big_spider", 5, 10, 1200, 1, 270, 1000, 10);
		weapon.setBullet(new GenericBullet(weapon, 0, 0, 0, 125, null));
		equipWeapon(weapon);
		
	}
	
	@Override
	public void onHit(Entity entity) {
		super.onHit(entity);
		
		if(!entity.isEnemyTo(getOwnerPlayer()) || !(entity instanceof Bullet)) return;
		
		Random rand = new Random();
		if(rand.nextDouble() > .80){
			Sound[] sounds = Cache.soundPacks.get("spider_atk");
			sounds[rand.nextInt(sounds.length-1)].play(0.5f, 5);
		}	
		
	}
	
	@Override
	protected void onAttack() {
		Random rand = new Random();
		Sound[] sounds = Cache.soundPacks.get("spider_atk");
		sounds[rand.nextInt(sounds.length-1)].play(0.5f, 4);
	}
	
	public void onDeath(Entity killer) {
		super.onDeath(killer);
		
		Random rand = new Random();
		Sound[] sounds = Cache.soundPacks.get("splat");
		sounds[rand.nextInt(sounds.length-1)].play(0.5f, 4);
		
		for(int i = 0; i < spawnSpiders; i++){
			Spider spider = new Spider(this.getCenterX(), this.getCenterY(), Celeri.currentLevel.getLevelCount());
			spider.setSpeed(rand.nextFloat() * spider.getSpeed() + 100);
			spider.setHitBox();
			spider.setGodeMode(250);
			Celeri.entityManager.addEntity(spider);
		}
	}

}
