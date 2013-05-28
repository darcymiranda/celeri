package com.heavydose.shared.items.weapons;

import java.util.ArrayList;

import com.heavydose.shared.Entity;


public class WeaponFactory {
	
	private static WeaponFactory instance;
	
	@SuppressWarnings("serial")
	private final ArrayList<Weapon> weapons = new ArrayList<Weapon>() {{
		add(new Pistol(null));
		add(new Machinegun(null));
		add(new ShotgunRanged(null));
		add(new Shotgun(null));
		add(new Railgun(null));
		//add(new Bazooka(null));
	}};
	
	public final Weapon getWeaponSkeleton(int i){
		return i >= 0 && i <= weapons.size() ? weapons.get(i) : null;
	}
	
	public final Weapon createWeapon(int type, Entity owner){
		return getWeaponSkeleton(type).newInstance(owner);
	}
	
	public final static WeaponFactory getInstance(){
		return instance == null ? instance = new WeaponFactory() : instance;
	}
	
	public final int getWeaponCount(){ return weapons.size(); }
	
	private WeaponFactory(){}

}
