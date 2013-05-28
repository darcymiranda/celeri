package com.heavydose.shared.items.powerups;

import com.heavydose.shared.Entity;
import com.heavydose.shared.Unit;
import com.heavydose.shared.items.Item;

public abstract class Powerup extends Item {
	
	protected boolean neverExpires;
	protected boolean stackable;
	protected Tracker tracker;

	public Powerup(Entity owner, String name) {
		super(owner, name);
		tracker = new Tracker(owner, getClass(), 0, 0);
	}
	
	public abstract void refresh();
	
	public abstract void applyEffect(Unit unit);
	
	public abstract void removeEffect(Unit unit);
	
	public abstract Powerup newInstance(Entity owner);
	
	public void update(int delta){
		tracker.interval -= delta;
	}
	
	public void forceExpire(){
		tracker.interval = 0;
	}
	
	public final Tracker getTracker(){ return tracker; }
	public final boolean isExpired(){ return neverExpires ? false : tracker.interval < 0; }
	public final boolean isStackable(){ return stackable; }

}
