package com.heavydose.shared.items;


import org.newdawn.slick.Image;

import com.heavydose.shared.Entity;



public abstract class Item {
	
	public static int WEAPON = 0;
	public static int ITEM = 1;
	
	public int type = -1;
	
	protected Entity owner;
	protected String name;
	protected Image dropItemImage;

	public Item(Entity owner, String name){
		this.owner = owner;
		this.name = name;
	}
	
	public String toString(){
		return "Owner: " + owner.toString() + "  Name: " + name;
	}
	
	public final Entity getOwner(){ return owner; }
	public String getName(){ return name; }
	public final Image getDropItemImage(){ return dropItemImage; }
	
}
