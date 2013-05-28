package com.heavydose.shared.map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

public class Tile {
	
	public int type;
	public int imageIndex;
	public boolean blocked;
	public Image image;
	
	public Tile(int type, boolean blocked){
		
	}
	
	public void setImage(Image image){
		this.image = image;
	}
	
	public void setImage(SpriteSheet sheet, int index){
		if(index > sheet.getHorizontalCount()){
			Log.error("Supplied image index of " + index + " is greater than sprites in sprite sheet " + sheet.getResourceReference());
			return;
		}
		image = sheet.getSubImage(index, 0);
	}
	
	public void render(int x, int y){
		image.draw(x,y);
	}

}
