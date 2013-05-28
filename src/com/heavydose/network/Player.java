package com.heavydose.network;

import com.heavydose.shared.Hero;

public class Player {
	
	public String username = "unknown";
	public boolean isLocal;
	public boolean computer;
	
	public int id;
	
	public Hero hero;
	
	public Player(){
	}
	
	public void assignHero(Hero hero){
		this.hero = hero;
	}
	
	public String toString(){
		return "name: " + username + ", id: " + id + ", hero: " + hero + ", local?: " + isLocal;
	}

}
