package com.heavydose.client.game.gui;

import java.util.HashMap;
import java.util.Iterator;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Hud {
	
	public HashMap<String, Component> components = new HashMap<String, Component>();
	private GameContainer gc;
	
	public Hud(GameContainer gc){
		this.gc = gc;
	}
	
	public void update(){
		Iterator<Component> it = components.values().iterator();
		while(it.hasNext())
			it.next().update();
	}
	
	public void render(Graphics g){
		Iterator<Component> it = components.values().iterator();
		while(it.hasNext())
			it.next().render(g);
		//Object[] c = components.values().toArray();
		//for(int i = c.length - 1; i > 0; i--){
		//	((Component)c[i]).render(g);
		//}
	}
	
	
	public void clearHud(){ components.clear(); }
	public void addComponent(Component c){ components.put(c.getName(), c); }

}
