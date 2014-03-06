package com.heavydose.game.gui;

import org.newdawn.slick.Graphics;

import com.heavydose.shared.Entity;
import com.heavydose.shared.items.powerups.Tracker;

public class DurationBarManager {
	
	private final int BAR_HEIGHT = 4;
	
	private DurationBar[] bars = new DurationBar[10];
	private Entity owner;
	
	public DurationBarManager(Entity owner){
		this.owner = owner;
	}
	
	public void addDurationBar(Tracker tracker){
		
		for(int i = 0; i < bars.length; i++){
			if(bars[i] == null) continue;
			
			if(tracker.match(bars[i].getTracker()))
				return;
			
		}
		
		for(int i = 0; i < bars.length; i++){
			if(bars[i] != null) continue;
			
			bars[i] = new DurationBar(this, tracker, i, owner, owner.getWidth(), BAR_HEIGHT);
			break;
		}
	}
	
	public void removeDurationBar(DurationBar bar){
		bars[bar.id] = null;
	}
	
	public void update(int delta){
		for(int i = 0; i < bars.length; i++){
			
			if(bars[i] == null){
				
				// Move to the bottom
				int next = i + 1;
				if(next < bars.length){
					if(bars[next] != null){
						
						bars[i] = bars[next];
						bars[i].id = i;
						bars[i].update(delta);
						bars[next] = null;
						
					}
				}
				
			} else {
				
				bars[i].update(delta);
				
			}
		}
	}
	
	public void render(Graphics g){
		for(int i = 0; i < bars.length; i++){
			if(bars[i] == null) continue;
			bars[i].render(g);
		}
	}

}
