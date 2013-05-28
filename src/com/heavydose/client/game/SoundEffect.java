package com.heavydose.client.game;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Sound;

public class SoundEffect {
	
	private ArrayList<Sound> sounds = new ArrayList<Sound>();	
	private Random rand = new Random();
	
	public SoundEffect(Sound sound){
		sounds.add(sound);
	}
	
	public SoundEffect(Sound[] sounds){
		for(int i = 0; i < sounds.length; i++){
			this.sounds.add(sounds[i]);
		}
	}
	
	public void play(){
		play(1, 1);
	}
	
	public void play(float volume, float pitch){
		int index = rand.nextInt(sounds.size());
		sounds.get(index).play(volume, pitch);
	}
}
