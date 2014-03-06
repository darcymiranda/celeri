package com.heavydose.shared.enemies;

import java.util.Random;

import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.Cache;
import com.heavydose.game.Celeri;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.bullets.GenericBullet;
import com.heavydose.shared.items.weapons.Weapon;
import com.heavydose.util.Tools;


public class Spitter extends Enemy {
	
	private final int EXPLODE_DAMAGE = 3;
	
	private Weapon weapon;
	
	public Spitter(float x, float y, int level) {
		super(x, y, 32, 32, level);

        score = 48;

		speed = 90;
		sight = 250000;
		attackRange = 130000;
		setHealth(35);
		type = Entity.CREEP;
		setImage(Cache.images.get("spitter"), false);
		
		weapon = new Weapon(this, "spitter", 1, 3, 1500, 0.98f, 300, 1000, 10);
		weapon.setBullet(new GenericBullet(weapon, 0, 0, 0, Cache.images.get("bullet_enemy")));
		weapon.setSoundShoot(Cache.sounds.get("spitter_shot"));
		equipWeapon(weapon);
		
	}
	
	public void update(int delta){
		super.update(delta);
		
		chase = !isShooting();
		
	}
	
	@Override
	protected void onAttack(){
		super.onAttack();
		
	}
	
	@Override
	protected void doNonChaseAI(int delta){
		
		velocity = Tools.lerp(velocity, new Vector2f(0,0), 0.01f);
		
	}
	
	public void onHit(Entity entity) {
		super.onHit(entity);
		
		if(!entity.isEnemyTo(getOwnerPlayer()) || !(entity instanceof Bullet)) return;
		
		Random rand = new Random();
		if(rand.nextDouble() > .80){
			Sound[] sounds = Cache.soundPacks.get("spitter");
			sounds[rand.nextInt(sounds.length-1)].play();
		}	
		
	}
	
	

	protected void onDeath(Entity killer) {
		super.onDeath(killer);
			
		Random rand = new Random();
		Sound[] sounds = Cache.soundPacks.get("spitter");
		sounds[rand.nextInt(sounds.length-1)].play();
			
		int maxBullets = 4;
		for(float a = 0; (a <= 360) && maxBullets > 0; a += 90){
			
			Bullet bullet = new GenericBullet(weapon, EXPLODE_DAMAGE, a, 300, Cache.images.get("bullet_enemy"));
			bullet.setDecay(1500);
			bullet.setPosition(this.getCenterPosition());
			
			Celeri.entityManager.addBullet(bullet);
			
			maxBullets--;
			
		}
	}

}
