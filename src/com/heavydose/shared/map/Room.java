package com.heavydose.shared.map;

public class Room{
	
	public int x, y;
	public int w, h;
	public int numDoors, maxDoors;
	
	public Room(int x, int y, int w, int h, int doors){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		maxDoors = doors;
	}
	
}
