package com.heavydose.client.game;

import java.io.IOException;

import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;

import com.heavydose.client.Cache;


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
