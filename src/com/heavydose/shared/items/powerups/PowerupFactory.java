package com.heavydose.shared.items.powerups;

import java.util.ArrayList;

import com.heavydose.shared.Entity;


public class PowerupFactory {
	
	private static PowerupFactory instance;
	
	@SuppressWarnings("serial")
	private final ArrayList<Powerup> powerups = new ArrayList<Powerup>() {{
		add(new FireRateBonus(null));
		add(new HealthPack(null));
	}};
	
	public final Powerup getPowerupSkeleton(int i){
		return i >= 0 && i < powerups.size() ? powerups.get(i) : null;
	}
	
	public final Powerup createPowerup(int type, Entity owner){
		return getPowerupSkeleton(type).newInstance(owner);
	}
	
	public final static PowerupFactory getInstance(){
		return instance == null ? new PowerupFactory() : instance;
	}
	
	public final int getPowerupCount(){ return powerups.size(); }
	
	private PowerupFactory(){}

}