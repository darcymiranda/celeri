package com.heavydose.shared.enemies;

import java.util.Random;

import org.newdawn.slick.Sound;

import com.heavydose.Cache;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.GenericBullet;
import com.heavydose.shared.items.weapons.Weapon;


public class Spider extends Enemy {
	
	private int wanderFirst = 4000;

	public Spider(float x, float y, int level) {
		super(x, y, 32, 32, level);

        score = 5;

		speed = 220;
		sight = 90000;
		setHealth(5);
		attackRange = 900;
		chase = false;
		type = Entity.TINYCREEP;
		
		setTarget(null, true, wanderFirst);
		
		setIgnoreEntityCollision(true);
		setImage(Cache.images.get("spider"), false);
		
		Weapon weapon = new Weapon(this, "creeper", 1, 1, 350, .99f, 270, 100, 10);
		weapon.setBullet(new GenericBullet(weapon, 0, 0, 0, 125, null));
		equipWeapon(weapon);
	}
	
	public void update(int delta){
		super.update(delta);
		
		if(wanderFirst < 0){
			chase = true;
		} else {
			doNonChaseAI(delta);
			wanderFirst -= delta;
		}
		
	}
	
	@Override
	protected void onAttack() {
		Random rand = new Random();
		Sound[] sounds = Cache.soundPacks.get("spider_atk");
		sounds[rand.nextInt(sounds.length-1)].play(1, 2);
	}
	
	public void onDeath(Entity killer) {
		super.onDeath(killer);
		
		Random rand = new Random();
		Sound[] sounds = Cache.soundPacks.get("splat");
		sounds[rand.nextInt(sounds.length-1)].play(1, 2);
	}

}
