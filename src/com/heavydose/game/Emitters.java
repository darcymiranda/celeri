package com.heavydose.game;

import org.newdawn.slick.particles.ConfigurableEmitter;

import com.heavydose.Cache;


public class Emitters {
	
	private static int currentBloodId = 0;
	private static ConfigurableEmitter[] blood = new ConfigurableEmitter[10];
	
	public static void init(){
		for(int i = 0; i < blood.length; i++)
			blood[i] = Cache.emitters.get("blood");
	}
	
	public static ConfigurableEmitter newBlood(){
		if(currentBloodId < blood.length - 1)
			currentBloodId++;
		else
			currentBloodId = 0;
		
		blood[currentBloodId].replay();
		return blood[currentBloodId];
	}

}
