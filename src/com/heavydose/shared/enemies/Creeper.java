package com.heavydose.shared.enemies;

import java.util.Random;

import org.newdawn.slick.Sound;

import com.heavydose.Cache;
import com.heavydose.game.Celeri;
import com.heavydose.game.Debre;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.BEnemy;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.items.weapons.Weapon;


public class Creeper extends Enemy {

	public Creeper(float x, float y, int level) {
		super(x, y, 32, 32, level);

        score = 15;
		
		speed = 110;
		sight = 250000;
		setHealth(12);
		attackRange = 2500;
		chase = true;
		type = Entity.CREEP;
		setImage(Cache.images.get("creeper"), false);
		
		corpse = Cache.images.get("corpse");
		
		Weapon weapon = new Weapon(this, "creeper", 3, 5, 750, .99f, 200, 1000, 10);
		weapon.setBullet(new BEnemy(weapon, 0, 0, 0, 200, null));
		weapon.setSoundShoot(Cache.soundPacks.get("creeper")[11]);
		equipWeapon(weapon);
		
	}
	
	public void onHit(Entity entity) {
		super.onHit(entity);
		
		if(!entity.isEnemyTo(getOwnerPlayer()) || !(entity instanceof Bullet)) return;
		
		Random rand = new Random();
		if(rand.nextDouble() > .80){
			Sound[] sounds = Cache.soundPacks.get("creeper");
			sounds[rand.nextInt(sounds.length-1)].play();
			
		}
		
	}

	public void onDeath(Entity killer) {
		super.onDeath(killer);
		
		Random rand = new Random();
		Sound[] sounds = Cache.soundPacks.get("creeper");
		sounds[rand.nextInt(sounds.length-1)].play();
		
		if(rand.nextDouble() > .90){
			Debre debre = new Debre(position.x, position.y, 8, 8);
			debre.setImage(Cache.images.get("debre_creeper"), true);
			debre.directionFling(rotation - 180, rand.nextInt(150)+150);
			Celeri.entityManager.addEffect(debre);
		}
		
		hitEffects(rotation);

		
	}

}
