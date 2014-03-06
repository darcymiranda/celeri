package com.heavydose.shared.enemies;

import com.heavydose.shared.items.DropItem;
import com.heavydose.shared.items.ItemDropper;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.FastTrig;

import com.heavydose.Cache;
import com.heavydose.game.Celeri;
import com.heavydose.game.gui.HealthBar;
import com.heavydose.shared.Entity;
import com.heavydose.shared.Hero;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.bullets.GenericBullet;
import com.heavydose.shared.items.weapons.Weapon;

public class WormBoss extends Enemy {
	
	private int targetRate = 5000;
	private int targetInc = 0;
	private boolean targetEnemies = true;
	
	private WormChild child;
	
	//private final float TURN_SPEED = 0.0009f;
	private final int EAT_SPEED = 250;
	private final int HERO_SPEED = 150;
	
	private int retreatCount = 3;
	private int retreatTime = 7000;
	private int retreatInterval;
	
	private Entity oldTarget;

	public WormBoss(float x, float y, int level) {
		super(x, y, 32, 32, level);

        score = 8325;
		
		//turnSpeed = TURN_SPEED;
		speed = EAT_SPEED;
		enableAutoTarget = false;
		enablePathfinding = true;
		knockbackResistance = 0;
		sight = 2500000;
		setHealth(100);
		attackRange = 2200;
		chase = true;
		type = Entity.BOSS;
		setImage(Cache.images.get("worm_head"), false);
		
		Cache.sounds.get("worm_spawn").play(0.5f,2f);
		Cache.sounds.get("worm_spawn").play(0.5f,2f);
		Cache.sounds.get("worm_spawn").play(0.5f,2f);
		
		Celeri.hud.addComponent(new HealthBar("WormBossHealth", 
				new Vector2f(Celeri.gc.getWidth() + (Celeri.gc.getWidth() / 2) - 600,
						Celeri.gc.getHeight() - 100)));
		
		//ceDebre = Cache.emitters.get("debre_wall").duplicate();
		//ceDebre.setPosition(getCenterX(), getCenterY(), false);
		//Celeri.particleSystem.addEmitter(ceDebre);
		
		addChild();
		addChild();
		
		this.setHitBox();
		
	}
	
	private void changeTargetMode(boolean target){
		if(targetEnemies != target){
			targetInc = 0;
		}
		targetEnemies = target;
		
	}
	
	@Override
	public void update(int delta){
		super.update(delta);
		
		//ceDebre.setEnabled(Celeri.currentLevel.getMap().isPositionBlocked(position));
		//ceDebre.setPosition(getCenterX(), getCenterY(), false);
		
//		((HealthBar)Celeri.hud.components.get("WormBossHealth")).setAmount(health, maxHealth);
		
		if(child != null){
		
			int childCount = 0;
			
			WormChild nextChild = child;
			while(nextChild.hasChild()){
				nextChild = nextChild.getChild();
				childCount++;
			}
			
			if(childCount > 5){
				
				if(retreatCount < 1 || retreatInterval < 1){
					changeTargetMode(false);
				}
				else{
					retreatInterval -= delta;
					changeTargetMode(true);
				}
				
			} else {
				changeTargetMode(true);
			}
		
		}
		
		
		 
		if(targetInc < 0){
			
			oldTarget = target;
			
			if(targetEnemies){
				
				target = Celeri.entityManager.getNearestEntity(this, Entity.CREEP, 1000000);
			} else {
				
				target = Celeri.entityManager.getNearestEntity(this, Entity.HERO, 1000000);
				if(oldTarget != target){
					Cache.sounds.get("worm_spawn").play(1,2);
				}
			}
			
			if(!(target instanceof Hero)){
				speed = EAT_SPEED;
			}
			else{
				speed = HERO_SPEED;
			}
			
			targetInc = targetRate;
			
		} else {
			targetInc -= delta;
		}
		
		
	}
	
	@Override
	public void render(Graphics g){
		
		if(target instanceof Hero){
			if(getImage() != null){
				getImage().draw(position.x, position.y, new Color(255, 55, 55));
			}
		}
		else{
			if(getImage() != null){
				getImage().draw(position.x, position.y);
			}
		}
		
	}
	
	@Override
	public void onDeath(Entity killer){
		super.onDeath(killer);
		
		Cache.sounds.get("worm_spawn").play(1,3);
		
		Celeri.hud.components.remove("WormBossHealth");
		
		Cache.music.get("boss_loop").fade(6000, 0, false);
		Cache.music.get("rock_loop").fade(3000, 0.05f, false);
		Cache.music.get("rock_loop").loop(1, 0.05f);
		
	}
	
	@Override
	protected void onHit(Entity entity){
		super.onHit(entity);
		
		if(entity instanceof Enemy){
		
			((Enemy) entity).hitEffects(rotation);
			entity.kill(this);
			
			addChild();
            if(getHealth() < getMaxHealth()) addHealth(10);
			
			// find new target
			targetInc = 0;
		}
		else if(entity instanceof Bullet){
			
			if(entity.isEnemyTo(this.getOwnerPlayer())){
				
				if(rand.nextInt(4) > 2){
					Cache.sounds.get("worm_hit0").play(rand.nextFloat()*2,3f);
				}else{
					Cache.sounds.get("worm_hit1").play(rand.nextFloat()*2,3f);
				}
			
				if(retreatCount > 0){
					if((health / maxHealth) < 0.55){
						if(retreatInterval < 1 && retreatCount > 1){
							retreatCount--;
							retreatInterval = retreatTime;
							Cache.sounds.get("worm_spawn").play(2.5f,1f);
						}
					}
				}
			
			}
			
		}
		else if(entity instanceof Hero){
			
			((Hero) entity).takeDamage(this, 10);
			((Hero) entity).knockback(rotation - 180, 70);
			entity.hit(this);
			
		}
		
		
	}
	
	public void addChild(){
		
		float x = 48 * (float) FastTrig.sin(Math.toRadians(rotation)) + position.x;
		float y = -(48 * (float) FastTrig.cos(Math.toRadians(rotation))) + position.y;
		
		// we have no children
		if(child == null || !child.isAlive()){
			child = new WormChild(this, x, y, this);
			Celeri.entityManager.addEntity(child);
			return;
		}
		
		WormChild nextChild = child;
		while(nextChild.hasChild()){
			nextChild = nextChild.getChild();
		}
		
		nextChild.addChild(this);
		
		
	}
	
	private class WormChild extends Enemy {
		
		private WormBoss head;
		
		private WormChild child;
		private Enemy parent;
		
		private int explodeDelay = 400;

		public WormChild(Enemy parent, float x, float y, WormBoss head) {
			super(x, y, 32, 32, 0);
			this.parent = parent;
			this.head = head;
			
			setTarget(parent);
			
			rotation = parent.getRotation();
			
			turnSpeed = 0.015f;
			speed = head.EAT_SPEED;
			enablePathfinding = false;
			enableAutoTarget = false;
			knockbackResistance = 0;
			sight = 250000;
			setHealth(55);
			attackRange = 2500;
			chase = true;
			type = Entity.BOSS;
			setImage(Cache.images.get("worm_part"), false);
			
			this.setHitBox();
		}

        @Override
        protected void onDeath(Entity killer){
            super.onDeath(killer);

            DropItem item = ItemDropper.getInstance().dropHealth(position.x, position.y);
            item.setHitBox();
            item.directionFling(this.getRotation() - 180f, rand.nextInt(75) + 75);
            Celeri.entityManager.addItem(item);

        }
		
		@Override
		protected void onHit(Entity entity){
			super.onHit(entity);
			
			if(entity instanceof Hero){
				
				((Hero) entity).takeDamage(this, 1);
				((Hero) entity).knockback(rotation - 180, 50);
				entity.hit(this);
				
			}
			
		}
		
		@Override
		public void render(Graphics g){
			
			if(head.targetEnemies == false){
				if(getImage() != null){
					getImage().draw(position.x, position.y, new Color(255, 55, 55));
				}
			}
			else{
				if(getImage() != null){
					getImage().draw(position.x, position.y);
				}
			}
			
		}
		
		@Override
		public void update(int delta){
			super.update(delta);
			
			//position = Tools.lerp(position, parent.getCenterPosition(), 0.05f);
			
			if(!parent.isAlive()){
				
				if(explodeDelay < 0){
					
					kill(null);
					
					Weapon weapon = new Weapon(this, "spitter", 1, 3, 1500, 0.98f, 300, 1000, 10);
					weapon.setBullet(new GenericBullet(weapon, 0, 0, 0, Cache.images.get("bullet_enemy")));
					int maxBullets = 8;
					for(float a = 0; (a <= 360) && maxBullets > 0; a += 45){
						
						Bullet bullet = new GenericBullet(weapon, 15, a, 300, Cache.images.get("bullet_enemy"));
						bullet.setDecay(2000);
						bullet.setPosition(this.getCenterPosition());
						
						Celeri.entityManager.addBullet(bullet);
						
						maxBullets--;
						
					}
				}
				
				explodeDelay -= delta;
				
			}
		}
		
		public void addChild(WormBoss head){
			
			float x = 48 * (float) FastTrig.sin(Math.toRadians(getRotation())) + getPosition().x;
			float y = -(48 * (float) FastTrig.cos(Math.toRadians(getRotation()))) + getPosition().y;
			
			child = new WormChild(this, x, y, head);
			Celeri.entityManager.addEntity(child);
		}
		
		public boolean hasChild(){ if(child != null) return child.isAlive(); else return false; }
		
		public WormChild getChild(){ return child; }
		
	}
}
