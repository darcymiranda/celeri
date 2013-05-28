package com.heavydose.client.game;

import com.heavydose.shared.Entity;

public class Effect extends Entity {
	
	public static final int BLOOD = 50;
	public static final int CORPSE = 51;
	public static final int DEBRE = 52;
	
	public static int currentEffectId = 0;

	public Effect(float x, float y, float w, float h) {
		super(x, y, w, h);
	}

	@Override
	public void onHit(Entity entity) {
	}

	@Override
	public void onDeath(Entity killer) {
	}

}
