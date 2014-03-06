package com.heavydose.game;

import java.util.ArrayList;
import java.util.Random;

import com.heavydose.game.gui.HealthBar;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.Cache;
import com.heavydose.shared.Entity;
import com.heavydose.shared.Hero;
import com.heavydose.shared.enemies.BigSpider;
import com.heavydose.shared.enemies.Creeper;
import com.heavydose.shared.enemies.Enemy;
import com.heavydose.shared.enemies.Spitter;
import com.heavydose.shared.enemies.Strangler;
import com.heavydose.shared.enemies.WormBoss;
import com.heavydose.shared.map.TileMap;
import com.heavydose.util.Tools;


public class Level {
	
	private TileMap map;
	
	private float spawnSpeed = 500;
	private float curSpawnSpeed = 0;
	private int enemiesOnField;
	private int totalEnemies;
	private int spawnedEnemies;
	
	private Celeri game;
	
	private Vector2f heroSpawnPosition;
	
	private boolean hasBossSpawned;
	
	private int levelCount = 1;
	
	public Level(Celeri game, int enemiesOnField, int totalEnemies){
		this.enemiesOnField = enemiesOnField;
		this.totalEnemies = totalEnemies;
		this.game = game;
	}
	
	public Level(Level prevLevel, int enemiesOnField, int totalEnemies){
		this.levelCount = prevLevel.levelCount;
		this.map = prevLevel.map;
		this.game = prevLevel.game;
		this.enemiesOnField = enemiesOnField;
		this.totalEnemies = totalEnemies;
	}
	
	public void addHero(Hero hero){
		hero.setPosition(heroSpawnPosition);
		Celeri.entityManager.addEntity(hero);
	}
	
	public void load(){
		
		map = new TileMap(100, 100);
		
		heroSpawnPosition = map.generate(0, 600, 0.16, 0);
		
		Celeri.entityManager.clearAll();
		Celeri.entityManager.setMap(map);
	
	}
	
	public void update(int delta){
		
		if(curSpawnSpeed > 0){
			curSpawnSpeed -= delta;
			return;
		}
		
		int curEnemyCount = Celeri.entityManager.getEnemyCount();
		float difference = (((float)curEnemyCount/(float)enemiesOnField) * 200);
		spawnSpeed = Tools.clamp(difference, 50f, 1000f);
		
		curSpawnSpeed = spawnSpeed;
		
		Random rand = new Random();
		
		ArrayList<Vector2f> spawns = map.getSpawnPositions(game.getAllHeroes(), 750);
		
		/* debug
		System.out.println("spawn size: " + spawns.size() + "  spawned enemies < total enemies: " +
					spawnedEnemies + "/" + totalEnemies + (spawnedEnemies < totalEnemies) + "  curEnemyCount < enenmiesOnField: " +
					curEnemyCount + "/" + enemiesOnField+(curEnemyCount < enemiesOnField));
		*/
		
		if(spawns.size() > 0 &&
				spawnedEnemies < totalEnemies &&
				curEnemyCount < enemiesOnField ){
			
			Vector2f spawnPoint = spawns.get(rand.nextInt(spawns.size()));
			
			Enemy spawn = new Creeper(spawnPoint.x, spawnPoint.y, Celeri.currentLevel.getLevelCount());
			
			float chance = rand.nextFloat();

            if(levelCount > 3){
                if(chance > 0.95){
                    spawn = new Spitter(spawnPoint.x, spawnPoint.y, Celeri.currentLevel.getLevelCount());
                }
            }

            if(levelCount > 5){
                if(chance > 0.85){
                    spawn = new Strangler(spawnPoint.x, spawnPoint.y, Celeri.currentLevel.getLevelCount());
                }
            }

            if(levelCount > 7){
                if(chance > 0.84){
                    spawn = new BigSpider(spawnPoint.x, spawnPoint.y, Celeri.currentLevel.getLevelCount());
                }
            }

            if(levelCount % 2 == 0){
                spawnBoss();
            }
		
			spawn.setHitBox();
			Celeri.entityManager.addEntity(spawn);	
			
			spawnedEnemies++;
			
		}
		
	}
	
	public void spawnBoss(){

        if(!hasBossSpawned){
            game.getCamera().shake(1, 3);
            Enemy spawn = new WormBoss(heroSpawnPosition.x, heroSpawnPosition.y, Celeri.currentLevel.getLevelCount());
            spawn.setHitBox();
            spawn.setHealth(spawn.getHealth() + (levelCount * 50), spawn.getMaxHealth() + (levelCount * 50));
            Celeri.entityManager.addEntity(spawn);
            Cache.music.get("rock_loop").fade(3000, 0, false);
            Cache.music.get("boss_loop").loop(1, 0);
            Cache.music.get("boss_loop").fade(1000, 0.05f, false);
            hasBossSpawned = true;

            /**
            HealthBar bar = new HealthBar("WormBossHealth", new Vector2f(Celeri.gc.getWidth() * 0.45f, 15));
            bar.setImage(Cache.images.get("ui_healthbar_boss"), true);
            bar.setUnit(spawn);

            Celeri.hud.addComponent(bar);
                **/
        }

	}
	
	public void nextLevel(){
		levelCount++;
		spawnedEnemies = 0;
		enemiesOnField += 25;
		totalEnemies += 15;
        hasBossSpawned = false;
	}

	public final int getRemainingEnemies(){ return totalEnemies - spawnedEnemies; }
	public final int getTotalEnemyCount(){ return totalEnemies; }
	public final int getLevelCount(){ return levelCount; }
	public final TileMap getMap(){ return map; }

}
