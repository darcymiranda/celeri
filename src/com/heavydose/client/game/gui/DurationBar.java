package com.heavydose.client.game.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.shared.Entity;
import com.heavydose.shared.items.powerups.Tracker;

public class DurationBar {
	
	public int id;
	
	private float width;
	private float height;
	private float yoffset;
	
	private Tracker tracker;
	private DurationBarManager manager;
	private Vector2f position;
	private Entity owner;
	private Color color = new Color(255, 200, 0);

	public DurationBar(DurationBarManager manager, Tracker tracker, int id, Entity owner, float width, float height){
		this.manager = manager;
		this.tracker = tracker;
		this.id = id;
		this.owner = owner;
		this.width = width;
		this.height = height;
		
		position = new Vector2f(owner.getPosition().x, owner.getPosition().y + owner.getHeight() + yoffset);
	}
	
	public void update(int delta){
		
		float yoffset = (height + 2) * (id+1);
		
		position.set(owner.getPosition().x, owner.getPosition().y + owner.getHeight() + yoffset);
		
		if(tracker.interval < 1){
			manager.removeDurationBar(this);
		}
	}
	
	public void render(Graphics g){
		
		Color tempColor = g.getColor();
		g.setColor(color);
		g.fillRect(position.x, position.y, (width * (tracker.interval / tracker.total)), height);
		
		g.setColor(Color.black);
		g.drawRect(position.x, position.y, width, height);
		
		g.setColor(tempColor);

	}

	public final Tracker getTracker() { return tracker; }

}
