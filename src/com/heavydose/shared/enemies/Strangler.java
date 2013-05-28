package com.heavydose.shared.enemies;

import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.particles.ConfigurableEmitter;

import com.heavydose.client.Cache;
import com.heavydose.client.game.Celeri;
import com.heavydose.shared.Entity;
import com.heavydose.shared.Unit;
import com.heavydose.shared.bullets.BEnemy;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.items.powerups.enemy.Strangle;
import com.heavydose.shared.items.weapons.Weapon;

public class Strangler extends Enemy {
	
	private float strangleRange = 100000;
	private boolean isStrangling;
	
	private Image strangleGraphic;
	private Strangle ability;
	
	private ConfigurableEmitter ce;

	public Strangler(float x, float y, int level) {
		super(x, y, 32, 32, level);
		speed = 150;
		sight = 250000;
		setHealth(45);
		attackRange = 2500;
		type = Entity.CREEP;
		
		setImage(Cache.images.get("strangler"), false);
		
		strangleGraphic = Cache.images.get("strangle");
		strangleGraphic.setCenterOfRotation(strangleGraphic.getWidth(), 1);
		
		ability = new Strangle(this);
		
		ce = Cache.emitters.get("strangle").duplicate();
		ce.angularOffset.setValue(rotation - 180);
		ce.setEnabled(false);
		Celeri.particleSystem.addEmitter(ce);
		
		Weapon weapon = new Weapon(this, "strangler", 2, 3, 750, .99f, 200, 1000, 10);
		weapon.setBullet(new BEnemy(weapon, 0, 0, 0, 200, null));
		weapon.setSoundShoot(Cache.soundPacks.get("spider_atk")[0]);
		equipWeapon(weapon);
		
	}
	
	@Override
	protected void doTargetChange(Entity newTarget){
		if(isStrangling) stopStrangling();
	}
	
	@Override
	protected void doNonChaseAI(int delta){
		super.doNonChaseAI(delta);
		
		stopStrangling();
	}
	
	@Override
	protected void doChaseAI(int delta){
		
		if(distanceToTarget < strangleRange){
			
			if(!isStrangling){
				strangle();
			}
			
		} else {
			
			stopStrangling();
			
		}
		
	}
	
	@Override
	public void kill(Entity killer){
		super.kill(killer);
		
		stopStrangling();
		Celeri.particleSystem.removeEmitter(ce);
	}
	
	public void strangle(){
		
		if(target instanceof Unit){
			isStrangling = true;
			
			((Unit) target).addPowerup(ability);
			
			ce.angularOffset.setValue(rotation - 180);
			ce.setPosition(target.getCenterX(), target.getCenterY(), false);
			ce.setEnabled(true);

		}
		
	}
	
	public void stopStrangling(){
		if(!isStrangling) return;
		
		if(target instanceof Unit){
			isStrangling = false;
			
			((Unit) target).removePowerUp(ability);
			
			ce.setEnabled(false);
		}
		
	}
	
	@Override
	public void update(int delta){
		super.update(delta);
		
		if(!targetInSight){
			stopStrangling();
		}
		
		if(isStrangling && target != null){
			ce.setPosition(target.getCenterX(), target.getCenterY(), false);
		}
	}
	
	@Override
	public void render(Graphics g){
		super.render(g);
		
		if(isStrangling){
			
			strangleGraphic.setRotation(rotateTo);
			strangleGraphic.draw(getPosition().x, getCenterY(), strangleGraphic.getWidth(), (float) Math.sqrt(distanceToTarget));
		}
	}
	
	@Override
	public void onHit(Entity entity) {
		super.onHit(entity);
		
		if(!entity.isEnemyTo(getOwnerPlayer()) || !(entity instanceof Bullet)) return;
		
		Random rand = new Random();
		if(rand.nextDouble() > .50){
			Sound[] sounds = Cache.soundPacks.get("spider_atk");
			sounds[rand.nextInt(sounds.length-1)].play(1, 5);
		}	
		
	}
	
	@Override
	protected void onAttack() {
		Random rand = new Random();
		Sound[] sounds = Cache.soundPacks.get("spider_atk");
		sounds[rand.nextInt(sounds.length-1)].play(1, 5);
	}
	
	@Override
	public void onDeath(Entity killer) {
		super.onDeath(killer);
		
		Random rand = new Random();
		Sound[] sounds = Cache.soundPacks.get("spider_atk");
		sounds[rand.nextInt(sounds.length-1)].play(1, 5);
	}

}
