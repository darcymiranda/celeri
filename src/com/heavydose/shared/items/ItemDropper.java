package com.heavydose.shared.items;

import java.util.Random;

import com.heavydose.shared.items.powerups.PowerupFactory;
import com.heavydose.shared.items.weapons.WeaponFactory;

public class ItemDropper {
	
	private static ItemDropper instance;
	
	private Random rand = new Random();
	
	public DropItem dropWeapon(float x, float y){
		
		WeaponFactory wepFact = WeaponFactory.getInstance();
		
		int type = rand.nextInt(wepFact.getWeaponCount() - 1) + 1;	// -1 and +1 to make sure Pistols (index 0) never drop
		return new DropItem(x, y, wepFact.getWeaponSkeleton(type));
		
	}
	
	public DropItem dropPowerup(float x, float y){
		
		PowerupFactory powFact = PowerupFactory.getInstance();
		
		double p = rand.nextDouble();
		int type;
		if(p > 0.90){
			type = 1;
		} else {
			type = 0;
		}
		
		//int type = rand.nextInt(powFact.getPowerupCount());
		return new DropItem(x, y, powFact.getPowerupSkeleton(type));
		
	}
	
	private ItemDropper(){}
	
	public static ItemDropper getInstance(){
		return instance == null ?  new ItemDropper() : instance;
	}


}
