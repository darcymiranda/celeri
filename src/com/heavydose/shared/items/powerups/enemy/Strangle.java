package com.heavydose.shared.items.powerups.enemy;

import com.heavydose.shared.Entity;
import com.heavydose.shared.Unit;
import com.heavydose.shared.items.powerups.Powerup;
import com.heavydose.shared.items.powerups.Tracker;

public class Strangle extends Powerup{
	
	private float modifier = 4;
	private float appliedSpeed;

	public Strangle(Entity owner) {
		super(owner, "Strangle");
		stackable = true;
		neverExpires = true;
	}

	@Override
	public void applyEffect(Unit unit) {
		appliedSpeed = unit.getSpeed() / modifier;
		unit.setSpeed(unit.getSpeed() - appliedSpeed);
	}

	@Override
	public void removeEffect(Unit unit) {
		unit.setSpeed(unit.getSpeed() + appliedSpeed);
	}

	@Override
	public Powerup newInstance(Entity owner) {
		return new Strangle(owner);
	}

	@Override
	public void refresh() {
	}

}
