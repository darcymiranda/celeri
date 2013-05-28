package com.heavydose.shared;

public class MoveNode extends Entity {

	public MoveNode(float x, float y, float w, float h, Entity owner) {
		super(x, y, w, h);
		super.setOwnerEntity(owner);
	}

	@Override
	public void onHit(Entity entity) {
	}

	@Override
	public void onDeath(Entity killer) {
	}

}
