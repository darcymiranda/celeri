package com.heavydose.shared.items.powerups;

import org.newdawn.slick.particles.ConfigurableEmitter;

import com.heavydose.Cache;
import com.heavydose.game.Celeri;
import com.heavydose.shared.Entity;
import com.heavydose.shared.Hero;
import com.heavydose.shared.Unit;
import com.heavydose.shared.items.weapons.Weapon;

public class FireRateBonus extends Powerup{
	
	private float bonus = 1.5f;
	
	private float bonusAmount[];
	private Weapon[] affectedWeapons;
	
	private ConfigurableEmitter ce;

	public FireRateBonus(Entity owner) {
		super(owner, "Fire Rate Bonus");
		
		dropItemImage = Cache.images.get("item_asb");
		
		ce = Cache.emitters.get("powerup").duplicate();
		ce.setEnabled(false);
		Celeri.particleSystem.addEmitter(ce);
		
		tracker.set(10000);
	}
	
	@Override
	public void applyEffect(Unit unit){
		
		ce.resetState();
		ce.setEnabled(true);
		
		if(unit.getType() == Entity.HERO){
			
			Hero hero = (Hero) unit;
			
			hero.addDurationBar(tracker);
			
			affectedWeapons = hero.getWeaponPocket();
			bonusAmount = new float[affectedWeapons.length];
			
		}else{
			
			affectedWeapons = new Weapon[1];
			bonusAmount = new float[1];
			affectedWeapons[0] = unit.getWeapon();
			
		}
		
		for(int i = 0; i < affectedWeapons.length; i++){
			if(affectedWeapons[i] == null) continue;
			
			bonusAmount[i] = affectedWeapons[i].fireRate / bonus;
			affectedWeapons[i].bonusFireRate -= bonusAmount[i];
			affectedWeapons[i].setInfiniteMode(true);
		}

	}
	
	@Override
	public void removeEffect(Unit unit){
		
		ce.wrapUp();
		
		for(int i = 0; i < affectedWeapons.length; i++){
			if(affectedWeapons[i] == null) continue;
			
			affectedWeapons[i].bonusFireRate += bonusAmount[i];
			
			// temp
			if(unit.getPowerups().size() == 1)
			affectedWeapons[i].setInfiniteMode(false);
		}
		
	}
	
	@Override
	public void update(int delta){
		super.update(delta);
		
		ce.setPosition(owner.getCenterX(), owner.getCenterY(), false);
		
	}

	@Override
	public Powerup newInstance(Entity owner) {
		
		return new FireRateBonus(owner);
	}

	@Override
	public void refresh() {
		tracker.interval = tracker.total;
	}

}
