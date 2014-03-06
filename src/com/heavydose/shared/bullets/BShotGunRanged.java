package com.heavydose.shared.bullets;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.util.FastTrig;

import com.heavydose.Cache;
import com.heavydose.game.Celeri;
import com.heavydose.shared.Entity;
import com.heavydose.shared.items.weapons.Weapon;


public class BShotGunRanged extends Bullet {
	
	private int minTravelTimer = 400;//250;
	private Polygon ray;
	
	private Bullet scatterBullets;

	public BShotGunRanged(Weapon weapon, int damage, float direction,
			float speed, Image image) {
		super(weapon, damage, direction, speed, image);
		
		float cx = getCenterX(), cy = getCenterY();
		
		scatterBullets = new GenericBullet(weapon);
		
		float x1 = -(150 * (float) FastTrig.sin(Math.toRadians(direction-15)));
		float y1 = -(150 * (float) FastTrig.cos(Math.toRadians(direction-15)));
		float x2 = -(150 * (float) FastTrig.sin(Math.toRadians(direction+15)));
		float y2 = -(150 * (float) FastTrig.cos(Math.toRadians(direction+15)));
		
		ray = new Polygon(new float[]{cx, cy,
				cx + x1,
				cy - y1,
				cx + x2,
				cy - y2
		});
		
		
	}

	public void update(int delta){
		super.update(delta);
		
		if( minTravelTimer > 0 )
			minTravelTimer -= delta;

		ray.setCenterX(getCenterX());
		ray.setCenterY(getCenterY());
		
	}
	
	public void onRayHit(Entity entity){
		
		if(!entity.isAlive())
			return;
		
		if(minTravelTimer <= 0){
			
			this.kill(null);
			
			Cache.sounds.get("shotgun_blast").play();
			
			java.util.Random rand = weapon.getWeaponRand();
			
			int maxBullets = 6;
			int splitDamage = damage / 2;
			for(float a = this.getRotation()-60; (a < this.getRotation()+60) && maxBullets > 0; a += rand.nextInt(30)+5){
				
				Bullet bullet = scatterBullets.newInstance(splitDamage, a, 550);
				bullet.setDecay(3300);
				bullet.setPosition(this.getCenterPosition());
				
				Celeri.entityManager.addBullet(bullet);
				
				maxBullets--;
				
			}
		
		}
		
	}
	
	@Override
	public Bullet newInstance(int damage, float direction, float speed){
		return new BShotGunRanged(weapon, damage, direction, speed, getImage());
	}
	
	@Override
	public void onHit(Entity entity) {
		super.onHit(entity);
	}

	@Override
	public void onDeath(Entity killer) {
		super.onDeath(killer);
	}
	
	@Override
	public void render(Graphics g){
		super.render(g);
		if(Celeri.SHOW_GENERIC_RAYS){
			org.newdawn.slick.Color c = g.getColor();
			g.setColor(org.newdawn.slick.Color.black);
			g.draw(ray);
			g.setColor(c);
			
		}
	}
	
	public Polygon getRay(){ return ray; }


}
