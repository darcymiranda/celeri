package com.heavydose.shared.items.powerups;

import com.heavydose.shared.Entity;

public class Tracker {
	
	public float total = 1;
	public float interval = 1;
	
	public Class<?> clazz;
	public Entity owner;
	
	public Tracker(Entity owner, Class<?> clazz, float total, float duration){
		this.total = total;
		this.interval = duration;
		this.owner = owner;
		this.clazz = clazz;
	}
	
	public Tracker(Entity owner, Class<?> clazz){
		this.owner = owner;
		this.clazz = clazz;
	}
	
	public void set(float duration){
		this.total = duration;
		this.interval = duration;
	}
	
	public void update(int delta){
		interval -= delta;
	}
	
	public boolean match(Tracker tracker){
		return this.owner.id == tracker.owner.id && this.clazz.equals(tracker.clazz);
	}
	
	public String toString(){
		return interval + "/" + total + "  Class: " + clazz.getSimpleName() + "  Owner: " + owner;
	}

}
