package com.heavydose.shared;


import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.client.game.Celeri;
import com.heavydose.network.Player;

public abstract class Entity {
	
	public static final int ANY = 0;
	public static final int HERO = 1;
	public static final int CREEP = 10;
	public static final int TINYCREEP = 11;
	public static final int DOODAD = 50;
	public static final int BOSS = 21;
	
	public int id;

	protected int type = -1;
	protected float depth = 1;
	protected Vector2f position, lastPosition;
	protected Vector2f velocity;
	protected float rotation;
	protected float rotationSpeed;
	protected float width;
	protected float height;
	
	private Rectangle hitBox;
	private Image image;
	
	private Player ownerPlayer;
	private Entity ownerEntity;
	
	private boolean alive = true;
	private boolean ignoreEntityCollision;

	public Entity(float x, float y, float w, float h){
		position = new Vector2f(x,y);
		lastPosition = position.copy();
		velocity = new Vector2f(0,0);
		width = w;
		height = h;
	}
	
	public void setImage(Image image, boolean override){
		if(image == null) return;
		this.image = image.copy();
		if(override){
			width = image.getWidth();
			height = image.getHeight();
		}
	}
	
	public void setScale(float scale){
		setImage(image.getScaledCopy(scale), true);
	}
	
	public void update(){
		
		if(hitBox != null)
			hitBox.setLocation(position);
		
		rotation += rotationSpeed;
		
		if(image != null)
			image.rotate(rotation - image.getRotation());
		
	}
	
	public void update(int delta){
		
		lastPosition = position.copy();
		
		position.x += velocity.x * delta / 1000;
		position.y -= velocity.y * delta / 1000;
		
		if(rotation < 0) rotation += 360;
		if(rotation > 360) rotation -= 360;
		
		update();
	}
	
	public void render(Graphics g){
		if(image != null){
			image.draw(position.x, position.y);
		}/*
		else{
			Color color = g.getColor();
			g.setColor(new Color(255,0,255));
			g.fillRect(position.x, position.y, width, height);
			g.setColor(color);
		}
		*/
		if(Celeri.SHOW_HITBOXES)
			if(hitBox != null)
				g.draw(hitBox);
		
	}
	
	
	
	public void kill(Entity killer){
		if(alive) onDeath(killer);
		alive = false;
	}
	
	public void hit(Entity hitter){
		onHit(hitter);
	}
	
	public boolean isEnemyTo(Player player){
		Player op = getOwnerPlayer();
		if(player == null || op == null) return false;
		return op.computer != player.computer;
	}
	
	protected void onKill(Entity killed){};
	protected void onDeath(Entity killer){};
	protected void onHit(Entity attacker){};
	
	public Rectangle getHitBox(){ return hitBox; }
	public void setHitBox(float w, float h){ hitBox = new Rectangle(position.x, position.y, w, h); }
	public void setHitBox(){ hitBox = new Rectangle(position.x, position.y, width, height); }
	
	public Player getOwnerPlayer(){
		return (ownerEntity == null) ? ownerPlayer : ownerEntity.ownerPlayer;
	}
	
	public void setOwnerPlayer(Player owner){ this.ownerPlayer = owner; }
	public Entity getOwnerEntity(){ return ownerEntity; }
	public void setOwnerEntity(Entity entity){ this.ownerEntity = entity; }
	
	public final boolean isCollidable(){ return (hitBox != null) && alive; }
	public final boolean isAlive(){ return alive; }
	public final int getType(){ return type; }
	public final float getWidth(){ return width; }
	public final float getHeight(){ return height; }
	public final Vector2f getLastPosition(){ return lastPosition; }
	public final Image getImage(){ return image; }
	
	public Vector2f getPosition(){ return new Vector2f(position); }
	public void setPosition(Vector2f position){ this.position = new Vector2f(position); }
	public void setPositionX(float x){ position.x = x; }
	public void setPositionY(float y){ position.y = y; }
	public void setPosition(float x, float y){ position.x = x; position.y = y; }
	public void addPosition(float x, float y){ position.x += x; position.y += y; }
	public Vector2f getVelocity(){ return new Vector2f(velocity); }
	public void setVelocity(Vector2f velocity){ this.velocity = velocity; }
	public float getRotation(){ return rotation; }
	public void setRotation(float rotation){ this.rotation = rotation; }
	
	public float getCenterX(){ return position.x + (width / 2); }
	public float getCenterY(){ return position.y + (height / 2); }
	public Vector2f getCenterPosition(){ return new Vector2f(getCenterX(), getCenterY()); }
	
	public void setType(int type){ this.type = type; }
	
	public boolean getIgnoreEntityCollision(){ return ignoreEntityCollision; }
	public void setIgnoreEntityCollision(boolean b){ ignoreEntityCollision = b; }
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName() + "  Id: " + id + "  Type: " + type;
	}
}
