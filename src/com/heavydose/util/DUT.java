package com.heavydose.util;

public class DUT {
	
	private long curTime = 0;
	private long duration = 0;

	public void start(){ curTime = System.currentTimeMillis(); }
	public long duration(){ return System.currentTimeMillis() - curTime; }
	public void accumulate(){ duration += duration(); }
	public void reset(){ duration = 0; }
	public String toString(){ return "DUT: " + duration + "ms"; }

}
