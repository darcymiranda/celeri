package com.heavydose.client.game.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.client.Cache;
import com.heavydose.util.Tools;



public class HealthBar extends Component {
	
	private final int XOFFSET = 53;
	private final int YOFFSET = 15;
	private final int UNITS = 20;
	
	private HealthUnit[] healthUnits;
	private int renderHealthUnits = UNITS;
	
	public HealthBar(String name, Vector2f position) {
		super(name, position, 0, 0);
		setImage(Cache.images.get("ui_healthbar"), true);
		
		healthUnits = new HealthUnit[UNITS];
		for(int i = 0; i < healthUnits.length; i++)
			healthUnits[i] = new HealthUnit(Cache.images.get("ui_healthunit"));
		
	}
	
	@Override
	public void render(Graphics g){
		super.render(g);
		
		float x = position.x + XOFFSET;
		float y = position.y + YOFFSET; 
		
		for(int i = 0; i < renderHealthUnits; i++){
			
			HealthUnit hu = healthUnits[i];
			hu.render(x, y);
			x += hu.width - 1;
			
		}
		
	}
	
	public void setAmount(float amount, float totalAmount){
		renderHealthUnits = Tools.clamp(Math.round(UNITS * (amount / totalAmount)), 0, UNITS);
	}
	
	private class HealthUnit {
		
		private Image img;
		private int width;
		private int height;
		
		public HealthUnit(Image img){
			this.img = img;
			width = img.getWidth();
			height = img.getHeight();
		}
		
		public void render(float x, float y){
			img.draw(x, y, width, height);
		}
		
	}

}
