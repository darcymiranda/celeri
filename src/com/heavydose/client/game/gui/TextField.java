package com.heavydose.client.game.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Vector2f;

public class TextField extends Component {
	
	private String text = "";
	private UnicodeFont font;

	public TextField(UnicodeFont font, String name, float x, float y, int width, int height) {
		super(name, new Vector2f(x,y), width, height);
		this.font = font;
	}
	
	@Override
	public void update(){
		super.update();
	}

	@Override
	public void render(Graphics g){
		super.render(g);
		
		g.setFont(font);
		g.drawString(text, position.x, position.y);
		
	}
	
	public void setText(String text){ this.text = text; }
	public void setText(float n){ setText(String.valueOf(n)); }
	public void setText(int n){ setText(String.valueOf(n)); }
}
