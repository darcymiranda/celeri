package com.heavydose.shared.map;

public class MapMiner {
	
	public int x,y;
	public int oldx, oldy;
	public int direction;
	public int wasteCount = 0;
	public int sticky = 0;
	
	public MapMiner(int x, int y){
		this.x = x; this.y = y;
	}
	
	public void move(){
		
		oldx = x;
		oldy = y;
		
		if(direction <= 0)
			y++;
		else if(direction == 1)
			y--;
		else if(direction == 2)
			x++;
		else if(direction >= 3)
			x--;
	}
	
	public void reverse(){
		if(direction <= 0)
			direction = 1;
		else if(direction == 1)
			direction = 0;
		else if(direction == 2)
			direction = 3;
		else if(direction >= 3)
			direction = 2;
	}
	
	public void reverseAndMove(int sticky){
		reverse();
		move();
		this.sticky = sticky;
	}
	
	public boolean isIdle(){
		return oldx == x && oldy == y;
	}
	
}
