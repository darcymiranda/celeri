package com.heavydose.util;

public class DeltaTimer {
	
	public float end;
	public float current;
	
	public void Timer(float end){
		this.end = end;
	}
	
	public boolean increment(float delta){
		current+=delta;
		return ( current > end );
	}
	

}
