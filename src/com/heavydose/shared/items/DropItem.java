package com.heavydose.shared.items;

import java.util.Random;

import org.newdawn.slick.util.FastTrig;

import com.heavydose.shared.Entity;


public class DropItem extends Entity{
	
	private boolean flung;
	private int levelDropped;
	private Item item;
	
	private boolean hasPickUpDelay;
	private int pickUpDelay = 1000;

	public DropItem(float x, float y, Item item) {
		super(x, y, 24, 24);
		this.item = item;
		
		setImage(item.getDropItemImage(), false);
		
	}
	
	public void directionFling(float direction, float intensity){
		
		flung = true;
		
		Random rand = new Random();
		
		velocity.x = -(intensity * (float) FastTrig.sin(Math.toRadians(direction)));
		velocity.y = -(intensity * (float) FastTrig.cos(Math.toRadians(direction)));
		
		rotation = rand.nextFloat()*360;
		rotationSpeed = rand.nextFloat()*10;
		
		
		
	}
	
	public void randomFling(float intensity){
		
		flung = true;
		
		Random rand = new Random();
		velocity.x = rand.nextFloat()*intensity - intensity / 2;
		velocity.y = rand.nextFloat()*intensity - intensity / 2;
		
		rotation = rand.nextFloat()*360;
		rotationSpeed = rand.nextFloat()*10;
		
	}
	
	public void setPickUpDelay(int delay) {
		pickUpDelay = delay;
		hasPickUpDelay = true;
		setIgnoreEntityCollision(true);
	}
	
	public void update(int delta){
		super.update(delta);
		
		if(hasPickUpDelay){
			if(pickUpDelay < 0){
				hasPickUpDelay = false;
				setIgnoreEntityCollision(false);
			}
			pickUpDelay -= delta;
		}
		
		if(flung){
			
			if((velocity.x > 0.1f || velocity.x < -0.1f) && (velocity.y > 0.1f || velocity.y < -0.1f)){
				
				velocity.x *= 0.975f;
				velocity.y *= 0.975f;
				
				rotationSpeed *= 0.975f;
				
			}else{
				flung = false;
				velocity.scale(0);
				rotationSpeed = 0;
			}
			
		}
		
	}
	
	

	@Override
	public void onHit(Entity entity) {
	}

	@Override
	public void onDeath(Entity killer) {
	}
	
	public final int getLevel(){ return levelDropped; }
	public final Item getItem(){ return item; }
}
