package com.heavydose.shared;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.util.FastTrig;

import com.esotericsoftware.minlog.Log;
import com.heavydose.client.Cache;
import com.heavydose.client.game.Celeri;
import com.heavydose.client.game.Effect;
import com.heavydose.client.game.gui.DurationBarManager;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.enemies.WormBoss;
import com.heavydose.shared.items.powerups.Powerup;
import com.heavydose.shared.items.weapons.Weapon;
import com.heavydose.util.Tools;


public abstract class Unit extends NetEntity {
	
	protected float speed = 0;
	protected float sight = 150000;
	protected float knockbackResistance = 1;	// 0 to 1
	protected float health = 15;
	protected float maxHealth = 15;
	
	protected Image corpse;
	protected DurationBarManager durationBars;
	protected Random rand;
	
	private ArrayList<Powerup> powerups = new ArrayList<Powerup>();

	private Vector2f knockbackTo;
	private float knockbackTimer;
	private boolean shooting;
	private Weapon weapon;
	
	private int godModeInc = 0;
	
	private ArrayList<Bullet> alreadyHitBullets = new ArrayList<Bullet>();

	public Unit(float x, float y, float w, float h) {
		super(x, y, w, h);
		rand = new Random();
		durationBars = new DurationBarManager(this);
	}
	
	protected abstract void onAttack();
	
	public void update(int delta){
		super.update(delta);
		
		if(knockbackTimer > 0){
			position = Tools.lerp(position, knockbackTo, 0.2f);
			knockbackTimer -= delta;
		}
		
		for(int i = 0; i < powerups.size(); i++){
			Powerup powerup = powerups.get(i);
			
			powerup.update(delta);
			
			if(powerup.isExpired()){
				
				powerup.removeEffect(this);
				powerups.remove(i);
				continue;
			}
		}
		
		durationBars.update(delta);
		
		if(godModeInc > 1){
			godModeInc -= delta;
		} else {
			if(!isAlive() && health < 1){
				kill(null);
				return;
			}
		}
		
		if(weapon != null){
			weapon.update(delta);
			weapon.aimDirection = rotation;
		}
		
	}
	
	public Bullet[] shoot(){
		if(weapon != null){
			Bullet[] bullets = (Bullet[]) weapon.action();
			if(bullets != null){
				onAttack();
			}
			return bullets;
			 
		}
		return null;
	}
	
	public void takeDamage(Entity attacker, float damage){
		if(godModeInc < 1){
			health -= damage;
			if(health < 1){
				attacker.onKill(this);
				kill(attacker);
			}
		}
	}
	
	public void addHealth(float h){
		if(health + h > maxHealth) health = maxHealth;
		else health += h;
	}
	
	public void setHealth(float f){
		setHealth(f, f);
	}
	
	public void setHealth(float health, float maxHealth){
		this.health = health;
		this.maxHealth = maxHealth;
		if(health > maxHealth){
			this.health = maxHealth;
			Log.warn(this + " set health " + health+ " to higher than max health " + maxHealth);
		}
	}
	
	public void knockback(float r, float force){
		r = r - 180;
		
		force *= knockbackResistance;
		
		knockbackTimer = force * 8;
		knockbackTo = new Vector2f( (force * (float) FastTrig.sin(Math.toRadians(r))),
									(-force * (float) FastTrig.cos(Math.toRadians(r))))
					.add(position);
	}
	
	public void addPowerup(Powerup powerup){
		
		if(powerup.isStackable()){
			
			powerup.applyEffect(this);
			powerups.add(powerup);
			
		} else {
			
			// refresh the currently applied power up that is the same as the added one
			for(int i = 0; i < powerups.size(); i++){
				Powerup p = powerups.get(i);
				if(p.getTracker().match(powerup.getTracker())){
					p.refresh();
					return;
				}
			}
			
			// since no similar powerup was found, add it
			powerup.applyEffect(this);
			powerups.add(powerup);
		}
	}
	
	public void removePowerUp(Powerup toBeRemoved){
		
		for(int i = 0; i < powerups.size(); i++){
			Powerup powerup = powerups.get(i);
			
			if(toBeRemoved.getTracker().match(powerup.getTracker())){
				
				powerup.forceExpire();
				powerup.removeEffect(this);
				powerups.remove(i);
				return;
			}
		}
	}
	
	@Override
	protected void onDeath(Entity killer){
		super.onDeath(killer);
		
		if(corpse != null && !(killer instanceof WormBoss)){
			
			Effect effectCorpse = new Effect(position.x, position.y, 64, 64);
			effectCorpse.setImage(corpse, false);
			effectCorpse.setRotation(rotation);
			effectCorpse.setType(Effect.CORPSE);
			effectCorpse.update();
			
			Celeri.entityManager.addEffect(effectCorpse);
		}
		
	}
	
	@Override
	protected void onHit(Entity entity){
		
		if(entity instanceof Bullet){
			
			// Don't care about allied bullets
			if(!entity.isEnemyTo(getOwnerPlayer()))
				return;
			
			// Ensure the same bullet never hits twice
			for(int i = 0; i < alreadyHitBullets.size(); i++){
				if(alreadyHitBullets.get(i) == entity)
					return;
			}
			
			Bullet bullet = (Bullet) entity;
			
			alreadyHitBullets.add(bullet);
			
			takeDamage(bullet.getOwnerEntity(), bullet.damage);
			
			hitEffects(bullet.getRotation());
			
		}
		
	}
		
	public void hitEffects(float direction){
		
		ConfigurableEmitter ce = Cache.emitters.get("blood").duplicate();
		ce.setPosition(getCenterX(), getCenterY());
		ce.angularOffset.setValue(direction);
		Celeri.particleSystem.addEmitter(ce);
		
		if(rand.nextDouble() > 0.50){
			
			Effect blood = new Effect(position.x, position.y, 64, 64);
			blood.setImage(Cache.blood[rand.nextInt(Cache.blood.length)], false);
			blood.setScale((float)rand.nextDouble());
			blood.setRotation(rand.nextInt(360));
			blood.setType(Effect.BLOOD);
			blood.update();
			
			Celeri.entityManager.addEffect(blood);
			
		}
		
	}
		
	@Override
	public void render(Graphics g){
		super.render(g);
		
		durationBars.render(g);
	}
	
	public final float getSpeed(){ return speed; }
	public void setSpeed(float speed){ this.speed = speed; }
	
	public void setGodeMode(int duration){ godModeInc = duration; }
	
	public final boolean isShooting(){ return shooting; }
	public void setShooting(boolean shooting){ this.shooting = shooting; }
	
	public final float getHealth(){ return health; }
	public final float getMaxHealth(){ return maxHealth; }
	public void setMaxHealth(int maxHealth){ this.maxHealth = maxHealth; }
	
	public final Weapon getWeapon(){ return weapon; }
	public void equipWeapon(Weapon weapon){ this.weapon = weapon; }
	
	public final ArrayList<Powerup> getPowerups(){ return powerups; }
	public final boolean isBeingKnockedBack(){ return knockbackTimer > 0; }
	
}
