package com.heavydose.shared.map;

import com.heavydose.shared.Entity;

public class TileOld extends Entity {

	private byte value;
	private boolean spawnable;
	
	public boolean used;
	public boolean blocked;
	public Room room;
	public int imageValue;
	
	public TileOld(float x, float y, float w, float h) {
		super(x, y, w, h);
	}
	
	public void setValue(int value){
		this.value = (byte) value;
		if(value == 1 || value == 2){
			blocked = true;
		}
		else
			blocked = false;
		
	}
	
	public byte getValue(){ return value; }
	public boolean getSpawnable(){ return spawnable; }
	public void spawnable(boolean b){ spawnable = b; }
	
	@Override
	public void onHit(Entity entity) {
	}

	@Override
	public void onDeath(Entity killer) {
	}
}
