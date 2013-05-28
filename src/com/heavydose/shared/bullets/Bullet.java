package com.heavydose.shared.bullets;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.FastTrig;

import com.heavydose.client.game.Celeri;
import com.heavydose.shared.Entity;
import com.heavydose.shared.items.weapons.Weapon;


public abstract class Bullet extends Entity {
	
	public int damage = 0;
	
	protected int decay;
	protected float speed;
	protected Weapon weapon;

	public Bullet(Weapon weapon, int damage, float direction, float speed, int decay, Image image) {
		super(weapon.getOwner() != null ? weapon.getOwner().getCenterPosition().x : 0,
				weapon.getOwner() != null ? weapon.getOwner().getCenterPosition().y : 0,
						8, 8);
		
		this.setOwnerEntity(weapon.getOwner());
		this.setImage(image, false);
		this.decay = decay;
		this.rotation = direction;
		this.weapon = weapon;
		this.speed = speed;
		this.damage = weapon.getDamageRoll();
		
		setSpeed(speed);
		
		setHitBox();
	}
	
	public Bullet(Weapon weapon, int damage, float direction, float speed, Image image) {
		this(weapon, damage, direction, speed, 5000, image);
	}
	
	public abstract Bullet newInstance(int damage, float direction, float speed);
	
	public void setSpeed(float speed){
		this.speed = speed;
		velocity.x = -(speed * (float) FastTrig.sin(Math.toRadians(rotation)));
		velocity.y = -(speed * (float) FastTrig.cos(Math.toRadians(rotation)));
	}
	
	@Override
	public void update(int delta){
		super.update(delta);

		if(decay < 0){
			kill(null);
		}else{
			decay -= delta;
		}
		
	}
	
	@Override
	public void render(Graphics g){
		super.render(g);
		if(Celeri.SHOW_ENEMY_ATTACKS){
			Color t = g.getColor();
			g.setColor(Color.green);
			g.fillRect(position.x, position.y, width, height);
			g.setColor(t);
		}
	}

	@Override
	public void onHit(Entity entity) {
		if(entity.isEnemyTo(getOwnerPlayer())){
			kill(entity);
		}
	}

	@Override
	public void onDeath(Entity killer) {
	}
	
	public String toString(){
		return this.getClass().getName() + " owner:" + this.getOwnerEntity();
	}
	
	public void setDecay(int decay){ this.decay = decay; }

}
