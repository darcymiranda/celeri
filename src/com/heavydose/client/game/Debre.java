package com.heavydose.client.game;

import java.util.Random;

import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.util.FastTrig;

import com.heavydose.client.Cache;

public class Debre extends Effect{
	
	private boolean flung;
	
	
	private ConfigurableEmitter ce;

	public Debre(float x, float y, float w, float h) {
		super(x, y, w, h);
		this.type = DEBRE;
		
		ce = Cache.emitters.get("debre_blood").duplicate();
		Celeri.particleSystem.addEmitter(ce);
		
	}
	
	@Override
	public void update(int delta){
		super.update(delta);
		
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
		
		ce.setPosition(getCenterX(), getCenterY(), false);
		ce.angularOffset.setValue(rotation - 180);
		ce.speed.setMin((velocity.x + velocity.y) / 10);
		ce.speed.setMax((velocity.x + velocity.y) / 10);
	}
	
	public void directionFling(float direction, float intensity){
		
		float offset = 15;
		
		flung = true;
		
		Random rand = new Random();
		
		if(rand.nextInt(1) == 1)
			offset = -15;
		
		direction += offset;
		
		
		velocity.x = -(intensity * (float) FastTrig.sin(Math.toRadians(direction)));
		velocity.y = -(intensity * (float) FastTrig.cos(Math.toRadians(direction)));
		
		rotation = rand.nextFloat()*360;
		rotationSpeed = rand.nextFloat()*5;
		
	}
	
	

}
