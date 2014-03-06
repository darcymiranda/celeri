package com.heavydose.shared.items.powerups;

import org.newdawn.slick.particles.ConfigurableEmitter;

import com.heavydose.Cache;
import com.heavydose.shared.Entity;
import com.heavydose.shared.Unit;

public class HealthPack extends Powerup {
	
	private ConfigurableEmitter ce;

	public HealthPack(Entity owner) {
		super(owner, "Basic Health Pack");
		dropItemImage = Cache.images.get("item_health");
		
		tracker.set(0);
		
	}

	@Override
	public void refresh() {
	}

	@Override
	public void applyEffect(Unit unit) {
		unit.setHealth(unit.getMaxHealth());
	}

	@Override
	public void removeEffect(Unit unit) {
	}

	@Override
	public Powerup newInstance(Entity owner) {
		return new HealthPack(owner);
	}

}
