package com.heavydose.shared.map;

import java.util.ArrayList;


public class Path {
	
	private ArrayList<Step> steps = new ArrayList<Step>();
	
	public void appendStep(int x, int y){
		steps.add(new Step(x,y));
	}
	
	public void prependStep(int x, int y){
		steps.add(0, new Step(x,y));
	}
	
	public boolean contains(int x, int y){
		return steps.contains(new Step(x,y));
	}
	
	public void removeStep(int i){
		steps.remove(i);
	}
	
	public int getSize(){ return steps.size(); }
	public Step getStep(int i){ return steps.get(i); }
	
	public class Step {
		
		public int x,y;
		
		public Step(int x, int y){
			this.x = x; this.y = y;
		}
		
		@Override
		public String toString(){
			return "x: " + x + " " + "y: " + y;
		}
	}

}
