package com.heavydose.game.gui;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.Cache;
import com.heavydose.game.Celeri;
import com.heavydose.shared.items.Item;
import com.heavydose.shared.items.weapons.Weapon;



public class ItemMenu extends Component {
	
	private final Vector2f DEF_POS = new Vector2f(370, 100);
	private ArrayList<ItemComponent> items = new ArrayList<ItemComponent>();
	
	private Celeri game;

	public ItemMenu(String name, Vector2f position, Celeri game) {
		super(name, position, Cache.images.get("ui_item_menu"));
		this.game = game;
	}
	
	public void addWeapon(Weapon item){
		items.add(new ItemComponent(item, new Vector2f(DEF_POS.x, DEF_POS.y), Cache.images.get("ui_item").copy()));
	}
	
	@Override
	public void update(){
		super.update();
		
		for(int i = 0; i < items.size(); i++){
			
			ItemComponent item = items.get(i);
			
			int offset = 0;
			if(i > 0) offset = 3;
			
			item.updateY(DEF_POS.y + (item.height * i) + offset);
			item.update(mousex, mousey);
			
		}
		
	}
	
	@Override
	public void mouseClicked(int button, int x, int y){
		for(int i = 0; i < items.size(); i++){
			items.get(i).mouseClicked(button, x, y);
		}
	}
	
	@Override
	public void render(Graphics g){
		super.render(g);
		for(int i = 0; i < items.size(); i++){
			items.get(i).render(g);
		}
	}
	
	private class ItemComponent extends Component {
		
		private Rectangle hitBox;
		private Rectangle mouse;

		private Item item;
		private TextField title;
		private TextField minMax;

		public ItemComponent(Weapon item, Vector2f position, Image image) {
			super(item.getName(), position, image);
			this.item = item;
			width = 270;
			height = 50;
			hitBox = new Rectangle(position.x, position.y, width, height);
			mouse = new Rectangle(0,0,1,1);
			
			title = new TextField(Cache.fonts.get("item"), this.name+"_ItemTitle", position.x + 5, position.y + 5, 200, 25);
			title.setText(item.getName());
			
			minMax = new TextField(Cache.fonts.get("item"), this.name+"_ItemMinMax", position.x + 5, position.y + 15, 50, 25);
			minMax.setText(item.getMinDamage() + " - " + item.getMaxDamage());
		}
		
		public void updateY(float y){
			position.y = y;
			title.position.y = y + 5;
			minMax.position.y = y + 15;
		}
		
		public void update(float mx, float my){
			super.update();
			
			mouse.setLocation(mx, my);
			hitBox.setBounds(position.x, position.y, width, height);
			
			if(intersect()){
				mouseIn();
			} else {
				mouseOut();
			}
			
		}
		
		public void render(Graphics g){
			super.render(g);
			
			title.render(g);
			minMax.render(g);
		}
		
		private boolean intersect(){
			return hitBox.intersects(mouse);
		}
		
		@Override
		public void mouseClicked(int button, int x, int y){
			if(hitBox.intersects(new Rectangle(x,y,0,0))){

                Cache.music.get("rock_loop").fade(500, 0.05f, false);
                Cache.music.get("boss_loop").fade(500, 0.05f, false);

				game.localHero.equipWeapon((Weapon) item);
				game.hideItemMenu();
				
			}
		}
		
		private void mouseOut(){
			image.setAlpha(1f);
		}
		
		private void mouseIn(){
			image.setAlpha(0.80f);
		}
		
	}

}
