package com.heavydose.shared.bullets;

import java.util.Random;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.FastTrig;

import com.heavydose.client.Cache;
import com.heavydose.client.game.Celeri;
import com.heavydose.shared.items.weapons.Weapon;
import com.heavydose.util.Tools;


public class MultiSplash extends Bullet {
	
	private int timeTillExplode;
	private int timer;

	public MultiSplash(Weapon weapon, int damage, float direction, float speed,
			Image image, int timeTillExplode) {
		super(weapon, damage, direction, speed, image);
		this.timeTillExplode = timeTillExplode;
		this.setIgnoreEntityCollision(true);
	}
	
	public void update(int delta){
		super.update(delta);
		
		if(timer > timeTillExplode && isAlive()){
			onExplode();
			timeTillExplode = (int) Tools.lerp(timeTillExplode, 25, 0.05f);
			timer = 0;
			return;
		} else {
			timer += delta;
		}
		
		velocity.x = Tools.lerp(velocity.x, 0, 0.05f);
		velocity.y = Tools.lerp(velocity.y, 0, 0.05f);
		
	}
	
	@Override
	public Bullet newInstance(int damage, float direction, float speed){
		return new MultiSplash(this.weapon, damage, direction, speed, this.getImage(), timeTillExplode);
	}
	
	public void onExplode(){
		
		int maxBullets = 10;
		Random rand = new Random();
		for(float a = 0; (a < 720) && maxBullets > 0; a += rand.nextInt(50)+10){
			
			Bullet bullet = new GenericBullet(weapon, damage, a, speed);
			bullet.setIgnoreEntityCollision(false);
			bullet.setDecay(500);
			bullet.setPosition(this.getCenterPosition());
			
			Celeri.entityManager.addBullet(bullet);
			
			maxBullets--;
			
		}
		
		
	}
	
	

}
