package com.heavydose.game;

import com.heavydose.shared.Hero;

public class Player {
	
	public String username = "unknown";
	public boolean isLocal;
	public boolean computer;
	
	public int id;
	
	public Hero hero;
    private int score;

    public Player(){
		hero = new Hero(0,0);
		hero.setOwnerPlayer(this);
		hero.kill(null);
	}

    public void assignHero(Hero hero){
        this.hero = hero;
    }
    
    public void addScore(int score){
        this.score += score;
    }
    
    public int getScore(){
        return score;
    }

}
