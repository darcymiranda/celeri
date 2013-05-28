package com.heavydose.client.game.gui;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.client.game.Celeri;


public class Component {
	
	protected Vector2f position;
	protected int height;
	protected int width;
	protected Image image;
	protected String name;
	protected int mousex, mousey;
	
	public Component(String name, Vector2f position, int width, int height){
		this.name = name;
		this.position = position;
		this.width = width;
		this.height = height;
	}
	
	public Component(String name, Vector2f position, Image image){
		this.name = name;
		this.position = position;
		setImage(image, true);
	}
	
	public void setImage(Image image, boolean override){
		this.image = image;
		if(override){
			height = image.getHeight();
			width = image.getWidth();
		}
	}
	
	public void mouseClicked(int b, int x, int y){
	}
	
	public void update(){
		setMousePosition(Mouse.getX(), -(Mouse.getY() - Celeri.gc.getHeight()));
	}
	
	public void render(Graphics g){
		if(image != null)
			image.draw(position.x, position.y);
	}
	
	private void setMousePosition(int mousex, int mousey) {
		this.mousex = mousex;
		this.mousey = mousey;
	}
	
	public void setHeight(int height){ this.height = height; }
	public void setWidth(int width){ this.width = width; }
	
	public final String getName(){ return name; }

}
