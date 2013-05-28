package com.heavydose.shared;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.shared.DetermineSide;
import com.heavydose.shared.Entity;




/**
 * 
 * Display State - entity information to be rendered
 * Previous State - last ticks entity information
 * Simulated State - information of entity ahead by one tick
 * 
 * @author dmiranda
 *
 */
public abstract class NetEntity extends Entity {
	
	private EntityState displayState, previousState, simulateState;
	private float smoothing = 0;
	private boolean isLocal;

	protected Vector2f serverPosition;
	
	public NetEntity(float x, float y, float w, float h){
		super(x, y, w, h);
		displayState = new EntityState(this);
		previousState =  new EntityState(this);
		simulateState =  new EntityState(this);
		serverPosition = new Vector2f(position.x,position.y);
	}
	
	/**
	 * Requires a packet object to set this ships x and y serverPosition.
	 * @param p the packet to be read
	 */
	public void updateNet(float x, float y, float xv, float yv, float r){
		
		serverPosition.x = x;
		serverPosition.y = y;
		
		/* IGNORE NETWORK
		if(!getOwnerPlayer().isLocal){
			
			smoothing = 1;
			
			previousState.setState(simulateState);
			
			//simulateState.setPosition(x, y);	// added since we don't have velocities yet
			simulateState.setVelocity(xv, yv);
			simulateState.setRotation(r);
		
			// Teleport entity to proper location, if the client and server positions are out of sync.
			
			float distance = serverPosition.distance(displayState.position);
			if(distance > 50 || distance < -50){
				simulateState.setPosition((serverPosition.x - xv), (serverPosition.y + yv));
			}
			

		}
		*/
	}
	
	public void update(int delta){
		super.update(delta);
		
		// interpolate remote entities
		/* IGNORE NETWORK
		if(!getOwnerPlayer().isLocal && DetermineSide.isClient){
			previousState.update();
			simulateState.update();
			
			// determine smoothing factor - six equals ticks per packet sent
			smoothing -= (1 / shared.abst.Const.SEND_PACKET_INTERVAL);
			if(smoothing < 0) smoothing = 0;
			
			// interpolate
			displayState.setPosition(previousState.position.x + (simulateState.position.x - previousState.position.x) * smoothing, 
					displayState.position.y = previousState.position.y + (simulateState.position.y - previousState.position.y) * smoothing);
			displayState.setRotation(previousState.rotation + (simulateState.rotation - previousState.rotation) * smoothing);
			
			// set new positions
			position.x = displayState.getPosition().x;
			position.y = displayState.getPosition().y;
			rotation = displayState.getRotation();
			
		}
		*/
		
	}
	
	public void render(Graphics g){
		super.render(g);
	}
	
	public boolean isLocal(){ return isLocal; }
	public void setLocal(boolean local){ this.isLocal = local; }
	
}
