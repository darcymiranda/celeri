package com.heavydose.shared;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.Cache;
import com.heavydose.game.Celeri;
import com.heavydose.game.gui.TextField;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.items.DropItem;
import com.heavydose.shared.items.Item;
import com.heavydose.shared.items.powerups.Powerup;
import com.heavydose.shared.items.powerups.Tracker;
import com.heavydose.shared.items.weapons.Weapon;
import com.heavydose.shared.items.weapons.gernades.Gernade;
import com.heavydose.util.Tools;


public class Hero extends Unit {

	private boolean w,s,d,a;
	
	private float legRotation = 0;
	private Image legImage;
	
	private int flashInc = 64;
	private boolean flash;
	
	private final int MAX_GERNADE_AMMO = 3;
	private Gernade equipedGernade;
	private int gernadeAmmo = MAX_GERNADE_AMMO;
	
	private int killCount;
	
	private Weapon[] weaponPocket = new Weapon[9];
	
	//private int godModeTimer;
	//private boolean godMode;
	//private int godModeFlickerTimer;
	//private boolean godModeFlicker;

	public Hero(float x, float y) {
		super(x, y, 32, 32);
		speed = 210;
		setHealth(50,50);
		type = Entity.HERO;
		legImage = (Image) Cache.images.get("hero_legs");
		
		equipedGernade = new Gernade(this, 2000);
		
	}
	
	public void update(int delta){
		super.update(delta);
		
		if(!isAlive()) return;
		
		cycleWeapons();
		
		if(rotation > 360) rotation -= 360;
		if(rotation < 0) rotation += 360;
		
		if(w){
			velocity.y = speed;
			legRotation = Tools.lerp(legRotation, 0, 0.05f);
		}
		else if(s){
			velocity.y = -speed;
			legRotation = Tools.lerp(legRotation, 180, 0.05f);
		}
		else
			velocity.y = 0;
		
		if(d){
			velocity.x = speed;
			legRotation = Tools.lerp(legRotation, 90, 0.05f);
		}
		else if(a){
			velocity.x = -speed;
			legRotation = Tools.lerp(legRotation, 270, 0.05f);
		}
		else
			velocity.x = 0;
		
		legImage.rotate(legRotation - legImage.getRotation());
		
		if(flashInc < 0)
			flash = false;
		else
			flashInc -= delta;
		
		/*
		if(godMode){
			godModeTimer -= delta;
			if(godModeTimer > 0){
				
				if(godModeFlickerTimer > 0){
					godModeFlickerTimer -= delta;
				}else{
					godModeFlickerTimer = 32;
					godModeFlicker = !godModeFlicker;
				}
				
			}else
				godMode = false;
		}
		*/
		
	}
	
	public void flash(){
		flash = true;
		flashInc = 64;
	}
	
	public void render(Graphics g){
		super.render(g);
		
		if(flash){
			legImage.drawFlash(position.x, position.y);
			getImage().drawFlash(position.x, position.y);
		}else{
			legImage.draw(position.x, position.y);
			getImage().draw(position.x, position.y);
		}
		
		/*
		Image image = getImage();
		if(image != null){
			if(godMode){
				if(godModeFlicker){
					legImage.drawFlash(position.x, position.y);
					image.drawFlash(position.x, position.y);
				}else{
					legImage.draw(position.x, position.y);
					image.draw(position.x, position.y);
				}
			}else{
			
				legImage.draw(position.x, position.y);
				image.draw(position.x, position.y);
			}
		}
		*/
	}
	
	/*
	public void enableGodMode(int duration){
		godModeTimer = duration;
		godMode = true;
	}
	*/
	
	private void cycleWeapons(){
		
		if(getWeapon().getStoredAmmo() == 0 && getWeapon().getAmmoRemaining() == 0){
			
			boolean isSwitched = false;
			for(int i = 1; i < weaponPocket.length; i++){
				if(weaponPocket[i] == null) continue;
				
				Weapon switchTo = weaponPocket[i];
				
				if(switchTo.getStoredAmmo() > 0 ){
					equipWeapon(switchTo);
					isSwitched = true;
				}
			}
			
			// Switch to infinite pistol if no weapons have ammo
			if(!isSwitched){
				equipWeapon(weaponPocket[0]);
			}
		}
	}
	
	public void equipWeapon(Weapon weapon){
		super.equipWeapon(weapon);
		
		// Reapply powerup affects because of a new weapon now
		java.util.ArrayList<Powerup> powerups = getPowerups();
		for(int i = 0; i < powerups.size(); i++){
			Powerup powerup = powerups.get(i);
			powerup.removeEffect(this);
			powerup.applyEffect(this);
		}
		
		((TextField) Celeri.hud.components.get("equipWeaponName")).setText(weapon.getName());
	}
	     /*
	public void throwGernade(Vector2f target){
		if(gernadeAmmo < 1 && isAlive()) return;
		
		Gernade gernade = equipedGernade.throwGernade(target);
		Celeri.entityManager.addEntity(gernade);
		
		gernadeAmmo--;
		
	}
	*/
	
	public void emptyWeaponPocket(){
		for(int i = 0; i < weaponPocket.length; i++)
			weaponPocket[i] = null;
	}
	
	public boolean addWeapon(Weapon weapon){
		
		if(weapon == null) return false;
		
		for(int i = 0; i < weaponPocket.length; i++){
			Weapon wp = weaponPocket[i];
			if(wp == null){
				if(weaponPocket[0] == null || weaponPocket[1] == null) equipWeapon(weapon);
                weaponPocket[i] = weapon;
				return true;
			} else {
				if(weapon.getClass().equals(wp.getClass())){
					wp.addAmmo(weapon.getStoredAmmo());
					return true;
				}
			}
		}
		
		// weapon pocket is full
		return false;
	}
	
	@Override
	public void onKill(Entity killed){
		super.onKill(killed);
		this.addKill();
	}

	@Override
	public void onHit(Entity entity){
		super.onHit(entity);
		
		if(!entity.isEnemyTo(getOwnerPlayer())) return;
			
		if(entity instanceof Bullet){
			flash();
			takeDamage(entity.getOwnerEntity(), ((Bullet)entity).damage);
		}
	}

	@Override
	public void onDeath(Entity killer) {
		super.onDeath(killer);
		
		velocity.x = 0;
		velocity.y = 0;
		setShooting(false);
	}

	public void onPickUp(DropItem droppedItem) {
		
		gernadeAmmo++;
		
		Item item = droppedItem.getItem();
		
		if(item instanceof Weapon){
			
			if(addWeapon(((Weapon) item).newInstance(this))){
				
				Cache.sounds.get("pickup").play();
				
			} else {
				
				droppedItem = new DropItem(this.getCenterX(), this.getCenterY(), item);
				droppedItem.setHitBox();
				droppedItem.setPickUpDelay(1000);
				droppedItem.randomFling(300);
				Celeri.entityManager.addItem(droppedItem);
			}
		}
		else if(item instanceof Powerup){
			
			Powerup powerup = ((Powerup) item).newInstance(this);
				
			Cache.sounds.get("powerup").play(1,3);
			addPowerup(powerup);
		}
				
	}

	@Override
	public void onAttack() {
		
	}
	
	public void addKill(){ this.killCount += 1; }
	public void addKills(int kills){ this.killCount += kills; }
	public final int getKillCount(){ return killCount; }
	
	public void moveUp(boolean w){ this.w = w; }
	public void moveDown(boolean s){ this.s = s; }
	public void moveRight(boolean d){ this.d = d; }
	public void moveLeft(boolean a){ this.a = a; }
	
	public final boolean getW(){ return w; }
	public final boolean getS(){ return s; }
	public final boolean getD(){ return d; }
	public final boolean getA(){ return a; }

	public final Weapon[] getWeaponPocket(){ return weaponPocket; }
	public final Weapon getWeapon(int i){ return i >= 0 && i < weaponPocket.length ? weaponPocket[i] : null; }
	public final int getWeaponId(){
		for(int i = 0; i < weaponPocket.length; i++){
			if(weaponPocket[i] == null) continue;
			if(weaponPocket[i].getClass().equals(getWeapon().getClass())) return i;
		}
		return -1;
	}
	
	public void addDurationBar(Tracker tracker){
		durationBars.addDurationBar(tracker);
	}

	//public final boolean hasGodMode(){ return godMode; }

}
