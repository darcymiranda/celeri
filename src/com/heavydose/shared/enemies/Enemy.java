package com.heavydose.shared.enemies;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.util.FastTrig;

import com.heavydose.client.game.Celeri;
import com.heavydose.shared.Entity;
import com.heavydose.shared.MoveNode;
import com.heavydose.shared.Unit;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.shared.items.DropItem;
import com.heavydose.shared.items.ItemDropper;
import com.heavydose.util.Tools;

public class Enemy extends Unit {
	
	private final int DEFAULT_LOCK_TIME = 10000;
	private final int DEFAULT_SIGHT_RANGE = 150000;
	
	protected Entity target = null;
	protected boolean chase = true;
	protected boolean targetInSight;
	protected boolean paralyzed;
	protected boolean enableAutoTarget = true;
	protected boolean enablePathfinding = true;
	protected float acceleration = 1;
	protected float distanceToTarget;
	protected float rotateTo;
	protected float turnSpeed = 0.005f;
	protected int attackRange = 5000;//2500;
	
	private ArrayList<MoveNode> moves = new ArrayList<MoveNode>();
	private MoveNode pathTarget;
	
	private Line sightCast;
	
	private int wanderTimer;
	private boolean lockTarget;
	private int lockTargetTimer;
	
	private int pathfindingRate = 1000;
	private int pathfindingIncrement;
	
	private int targetingRate = 1000;
	private int targetingIncrement;
	
	private int level;

	public Enemy(float x, float y, float w, float h, int level) {
		super(x, y, w, h);
		this.level = level;
		sightCast = new Line(0,0,0,0);
		
		setOwnerPlayer(Celeri.computerPlayer);
		sight = DEFAULT_SIGHT_RANGE;
	}
	
	public void setTarget(Entity entity){
		setTarget(entity, false);
	}
	public void setTarget(Entity entity, boolean lock){
		setTarget(entity, lock, DEFAULT_LOCK_TIME);
	}
	public void setTarget(Entity entity, boolean lock, int duration){
		
		if(target != null && entity != null && target.id == entity.id) return;
		
		if(lock){
			lockTarget = true;
			lockTargetTimer = duration;
			target = entity;
			doTargetChange(target);
			return;
		}
		
		if(!lockTarget){
			target = entity;
			doTargetChange(target);
		}
		
	}
	
	public void removeTarget(){
		if(!lockTarget)
			target = null;
	}
	
	public void queueMove(float x, float y){
		MoveNode move = new MoveNode(x,y,8,8,this);
		move.setHitBox();
		moves.add(move);
		pathTarget = moves.get(0);
	}
	
	public void clearMoveQueue(){
		moves.clear();
	}
	
	public void update(int delta){
		super.update(delta);
		
		targetInSight = false;
		
		if(enableAutoTarget){
			targetingIncrement -= delta;
			if(targetingIncrement < 0){
				setTarget(Celeri.entityManager.getNearestEntity(this, Entity.HERO, sight));
				targetingIncrement = targetingRate;
			}
		}

		
		// Determine if we unlock target
		if(lockTarget){
			if(lockTargetTimer < 1){
				lockTarget = false;
				if(distanceToTarget < this.sight) 
					target = null;
			}else{
				lockTargetTimer -= delta;
			}
		}
		
		if(target != null){
			
			if(!target.isAlive()){
				removeTarget();
				return;
			}
			
			distanceToTarget = Tools.distanceToEntity(this, target);
			rotateTo = Tools.getRotationToFaceTarget(this, target);
			
			if(chase){
				goTowardsAngle(delta, rotation);
			} else {
				doNonChaseAI(delta);
			}
			
			// Line of sight
			float cx = target.getCenterX();
			float cy = target.getCenterY();
			sightCast.set(getCenterX(), getCenterY(), 
								Tools.clamp(cx, cx-sight, cx+sight),
								Tools.clamp(cy, cy-sight, cy+sight));
			
			targetInSight = Celeri.entityManager.checkLineOfSight(sightCast);
			
			// Move and attack
			if(targetInSight){
				
				rotateTo = Tools.getRotationToFaceTarget(this, target);
				setShooting( distanceToTarget < attackRange );
				doChaseAI(delta);
				
				moves.clear();
			
			} else {
				
				// Find path
				if(enablePathfinding){
					
					if(pathfindingIncrement < 0){
						
						Celeri.entityManager.findPath(this, target, 64);
						pathfindingIncrement = pathfindingRate;
						
					}
					
					// Do actual pathfinding
					if(!moves.isEmpty()){
						
						rotateTo = Tools.getRotationToFaceTarget(this, pathTarget);
						
						if(this.getHitBox().intersects(pathTarget.getHitBox())){
							pathTarget.kill(null);
							moves.remove(0);
							if(!moves.isEmpty()) pathTarget = moves.get(0);
						}
					}
					
					pathfindingIncrement -= delta;
					
				}
			}
		}
		
		// No target, start wandering
		else{
			// this is because targets that were in sight then become null would stay in sight
			targetInSight = false;
			moves.clear();
			
			if(wanderTimer < 1){
				rotateTo = resetWanderMove();
			}else{
				wanderTimer -= delta;
			}
			
			goTowardsAngle(delta, rotation);
		}
		
		// Slowly turn
		float shr = rotateTo - rotation;
		if(shr > 180)
			shr -= 360;
		else if(shr < -180)
			shr += 360;
		
		rotation += shr * turnSpeed * delta;
		
	}
	
	
	protected void doNonChaseAI(int delta){
	}
	
	protected void doChaseAI(int delta){
		
	}
	
	protected void doTargetChange(Entity newTarget){
	}
	
	protected void goTowardsAngle(int delta, float angle){
		
		//velocity.x += -((acceleration * delta / 16) * (float) FastTrig.sin(Math.toRadians(angle)));
		//velocity.y += -((acceleration * delta / 16) * (float) FastTrig.cos(Math.toRadians(angle)));
		
		float dx = (float) FastTrig.sin(Math.toRadians(angle - 180));
		float dy = (float) FastTrig.cos(Math.toRadians(angle - 180));
		
		velocity.x = (speed * dx);
		velocity.y = (speed * dy);
		
		/*
		float x = getCenterX();
		float y = getCenterY();
		infrontCast.set(x, y, x + (dx * 64) , y - (dy * 64));
		*/

		//velocity = Tools.clamp(velocity, -speed, speed);
		
	}
	

	
	private float resetWanderMove(){
		if(target != null) System.out.println(target + " HIT A WALL");
		wanderTimer = rand.nextInt(6000)+4000;
		return rand.nextFloat()*360+rotation;
	}
	
	@Override
	protected void onHit(Entity entity){
		super.onHit(entity);
		
		//XXX: UNIQUE TO SERVER
		if(entity instanceof Bullet){
			
			Bullet bullet = (Bullet) entity;
			
			// Don't care about friendly bullets
			if(!entity.isEnemyTo(getOwnerPlayer()))
				return;
			
			if(knockbackResistance > 0){
				knockback(bullet.getRotation(), Math.max(bullet.damage, 32));
			}
			
			// chase whoever shot this unit and lock target for the default duration
			if(target == null && !lockTarget)
				setTarget(bullet.getOwnerEntity(), true);
			
			
		} else {
			
			if(target == null){
				rotateTo = resetWanderMove();
			}
			
		}
		
	}
	
	@Override
	protected void onDeath(Entity killer) {
		super.onDeath(killer);
		
		moves.clear();
		
		if(killer == null)
			return;
			
		// Items
		if(rand.nextDouble() > 0.80){
			
			DropItem item = ItemDropper.getInstance().dropWeapon(this.getCenterX(), this.getCenterY());
			item.setHitBox();
			item.directionFling(killer.getRotation(), rand.nextInt(75) + 75);
			Celeri.entityManager.addItem(item);
			
			if(rand.nextDouble() > 0.80){
				
				DropItem item2 = ItemDropper.getInstance().dropPowerup(this.getCenterX(), this.getCenterY());
				item2.setHitBox();
				item2.directionFling(killer.getRotation(), rand.nextInt(75) + 75);
				Celeri.entityManager.addItem(item2);
				
			}
		}
		
	}
	
	@Override
	protected void onAttack() {
	}
	
	public void render(Graphics g){
		super.render(g);
		
		if(Celeri.SHOW_LINEOFSIGHT){
			if(targetInSight)
				g.setColor(Color.pink);
			g.draw(sightCast);
			g.setColor(Color.white);
		}
		
		//if(Celeri.SHOW_INFRONTCAST){
			//g.setColor(Color.black);
			//g.draw(infrontCast);
		//}
		
		if(Celeri.SHOW_PATHFINDING)
			for(int i = 0; i < moves.size(); i++){
				MoveNode move = moves.get(i);
				g.fillRect(move.getPosition().x, move.getPosition().y, move.getWidth(), move.getHeight());
				//Celeri.gc.getDefaultFont().drawString(move.getPosition().x, move.getPosition().y, ""+i, Color.black);
			}
		
	}
	
	public void unlockTarget(){
		lockTarget = false;
		lockTargetTimer = 0;
	}
	
	public float getSight(){ return sight; }
	public Entity getTarget(){ return target; }
	public Line getLineSightCast(){ return sightCast; }
	public boolean isIdle(){ return moves.isEmpty(); }
	public boolean inSightOfTarget(){ return targetInSight; }
	public boolean getChase(){ return chase; }
	




}
