package com.heavydose.client.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ParticleSystem;


import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.minlog.Log;
import com.heavydose.client.Cache;
import com.heavydose.client.ClientIncoming;
import com.heavydose.client.game.gui.Component;
import com.heavydose.client.game.gui.HealthBar;
import com.heavydose.client.game.gui.Hud;
import com.heavydose.client.game.gui.ItemMenu;
import com.heavydose.client.game.gui.Overlay;
import com.heavydose.client.game.gui.TextField;
import com.heavydose.network.Network;
import com.heavydose.network.Player;
import com.heavydose.shared.DetermineSide;
import com.heavydose.shared.Entity;
import com.heavydose.shared.EntityManager;
import com.heavydose.shared.Hero;
import com.heavydose.shared.enemies.Creeper;
import com.heavydose.shared.enemies.Spitter;
import com.heavydose.shared.enemies.Strangler;
import com.heavydose.shared.enemies.WormBoss;
import com.heavydose.shared.items.Item;
import com.heavydose.shared.items.weapons.Weapon;
import com.heavydose.shared.items.weapons.WeaponFactory;
import com.heavydose.shared.map.TileMap;
import com.heavydose.util.Point;

public class Celeri extends BasicGame {
	
	public static int debugcount = 0;
	
	public static GameContainer gc;
	public static boolean STOP_SPAWNING = false;
	public static boolean SHOW_INFRONTCAST;
	public static boolean SHOW_PATHFINDING;
	public static boolean SHOW_LINEOFSIGHT;
	public static boolean SHOW_HITBOXES;
	public static boolean SHOW_GENERIC_RAYS;
	public static boolean SHOW_ENEMY_ATTACKS;
	
	public static EntityManager entityManager;
	public static ParticleSystem particleSystem;
	public static Hud hud;
	
	public static Level currentLevel;
	
	private Client client;
	public static Camera cam;
	
	public Hero localHero;
	public Player localPlayer;
	
	private HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	public static Player computerPlayer;
	
	private int sendPacketIncrement = 0;
	
	private static enum STATES { LOAD_GAME, PLAY_GAME, ITEM_MENU }
	private STATES currentState = STATES.LOAD_GAME;
	
	public Celeri(String title){
		super(title);
	}
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		//gc.setVSync(true);
		//gc.setAlwaysRender(true);
		gc.setMaximumLogicUpdateInterval(20);
		gc.setMinimumLogicUpdateInterval(20);
		//gc.setSmoothDeltas(true);
		
		gc.setSoundVolume(0.15f);
		
		Celeri.gc = gc;
		DetermineSide.isClient = true;
		
		hud = new Hud(gc);
		
		client = new Client(16384, 16*1024);
		client.start();
		Network.register(client);
		client.addListener(new ThreadedListener(new ClientIncoming(this)));
		
		/*
		// Read configuration file
		String host = "127.0.0.1";
		int port = Network.PORT;
		String username = "unknown";
		File conf = new File("unknown.conf");
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(conf));
			host = br.readLine();
			port = Integer.parseInt(br.readLine());
			username = br.readLine();
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		// Connect to server
		/* IGNORE NETWORK
		try {
			client.connect(15000, host, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		/* IGNORE NETWORK
		Network.Connect connect = new Network.Connect();
		connect.username = username;
		client.sendTCP(connect);
		*/
		
		
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		
		Input input = gc.getInput();
		
		switch(currentState){
			case LOAD_GAME:
				/* IGNORE NETWORK
				if(client.isConnected() && map.isLoaded())
				*/
				
				// LOAD CACHE
				Cache.load();
				Emitters.init();
				gc.setDefaultFont(Cache.fonts.get("default"));
				
				computerPlayer = new Player();
				computerPlayer.isLocal = false;
				computerPlayer.username = "computer";
				computerPlayer.id = 0;
				computerPlayer.computer = true;
				
				
				entityManager = new EntityManager();
				particleSystem = new ParticleSystem(Cache.images.get("p_default"), 5000);
				particleSystem.setRemoveCompletedEmitters(true);
				
				hud.clearHud();
				
				TextField debug = new TextField(Cache.fonts.get("default"),
						"debug",
						gc.getWidth() - 300, 50,
						50, 50);
				debug.setText("Press F1 to reset");
				hud.addComponent(debug);
				hud.addComponent(new Overlay("0overlay", new Vector2f(0,0)));
				hud.addComponent(new HealthBar("1mainHealth", new Vector2f(60, 9)));
				hud.addComponent(new Component("2wires", new Vector2f(327,13), Cache.images.get("ui_wires")));
				hud.addComponent(new TextField(Cache.fonts.get("counter"),
						"ammoCount",
						200, gc.getHeight() - 100,
						50, 50));
				hud.addComponent(new TextField(Cache.fonts.get("counter"),
						"equipWeaponName",
						100, gc.getHeight() - 135,
						50, 50));
				hud.addComponent(new TextField(Cache.fonts.get("counter"),
						"enemyCount",
						gc.getWidth() - 250, gc.getHeight() - 100,
						50, 50));
				hud.addComponent(new TextField(Cache.fonts.get("counter"),
						"levelDisplay",
						gc.getWidth() / 2 - 50, 50,
						50, 50));
		
				localPlayer = createPlayer(0, "single player");
				
				currentLevel = new Level(this, 30, 50);
				currentLevel.load();
				
				localHero = new Hero(-35, -35);
				localHero.setOwnerPlayer(localPlayer);
				localHero.setImage(Cache.images.get("hero"), true);
				localHero.setHitBox();
				localHero.addWeapon(WeaponFactory.getInstance().createWeapon(0, localHero));
				//localHero.addWeapon(WeaponFactory.getInstance().createWeapon(0, localHero));
				
				currentLevel.addHero(localHero);
				
				localPlayer.assignHero(localHero);
				focusCamera(localHero);
				
				currentState = STATES.PLAY_GAME;
				Cache.music.get("rock_loop").loop(1, 0.05f);
				break;
				
			case PLAY_GAME:
				
				if(localHero.isAlive()){
				
					((HealthBar) hud.components.get("1mainHealth")).setAmount(localHero.getHealth(), localHero.getMaxHealth());
					
					StringBuffer sb = new StringBuffer();
					sb.append(localHero.getWeapon().getAmmoRemaining());
					sb.append("/");
					sb.append(localHero.getWeapon().getAmmoCapacity());
					sb.append(" [");
					sb.append(localHero.getWeapon().isInfiniteAmmo() ? "~" : localHero.getWeapon().getStoredAmmo());
					sb.append("]");
					((TextField) hud.components.get("ammoCount")).setText(sb.toString());
					
					//localHero.setShooting(input.isKeyDown(Input.KEY_SPACE));
					
					// Rotation based on mouse
					{
						float mousex = input.getMouseX() + cam.getCameraX();
						float mousey = input.getMouseY() + cam.getCameraY();
						
						float rotation = (float) -(Math.atan2(mousex - localHero.getCenterX(), 
								mousey - localHero.getCenterY()) * (180 / Math.PI));
						localHero.setRotation(rotation);
					}
				
				}else{
					// just for now to make sure health bar is empty when dead
					((HealthBar) hud.components.get("1mainHealth")).setAmount(localHero.getHealth(), localHero.getMaxHealth());
				}
				
				// DEBUG
				//((TextField)hud.components.get("enemyCount")).setText("(" + Celeri.entityManager.getEnemyCount() + ")  " +
				//		currentLevel.getRemainingEnemies());
				((TextField)hud.components.get("enemyCount")).setText("Kills: " + localHero.getKillCount() );
				
				((TextField)hud.components.get("levelDisplay")).setText("Level: " + currentLevel.getLevelCount());
				
				// Shift to next level
				System.out.println(currentLevel.getRemainingEnemies() + " " + currentLevel.getLevelCount());
				if(currentLevel.getRemainingEnemies() < 10 * (1 + (currentLevel.getLevelCount() / 2))){
					currentLevel.nextLevel();
				}
				
				if(!STOP_SPAWNING)
					currentLevel.update(delta);
				
				entityManager.update(delta);
				particleSystem.update(delta);
				
				hud.update();
				cam.update(delta);
				
				break;
				
			case ITEM_MENU:
				
				hud.update();
				
				break;
	
		}
		
	}
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		switch(currentState){
			case LOAD_GAME:
				String msg = "loading just chill...";
				
				/* IGNORE NETWORK
				if(!client.isConnected())
					msg = "Connecting to host...";
				else if(!map.isLoaded())
					msg = "Downloading map...";
				*/
				
				g.drawString(msg, gc.getWidth()/2-(gc.getDefaultFont().getWidth(msg)/2),
								  gc.getHeight()/2-gc.getDefaultFont().getHeight(msg));
				break;
			case PLAY_GAME:
				
			{
				TileMap map = currentLevel.getMap();
				
				int[] offsets = cam.getDrawOffsets();
				
				map.renderFloor(offsets);
				
				
				cam.translateGraphics();
				
				entityManager.render(g);
				particleSystem.render();
				
				cam.untranslateGraphics();
				
				map.renderWalls(offsets);
				
				hud.render(g);
				
				break;
				
			}
				
			case ITEM_MENU:
				
			{
				
				TileMap map = currentLevel.getMap();
				
				int[] offsets = cam.getDrawOffsets();
				map.renderFloor(offsets);
				
				
				cam.translateGraphics();
				entityManager.render(g);
				particleSystem.render();
				cam.untranslateGraphics();
				
				map.renderWalls(offsets);
				
				hud.render(g);
				
			}
		}
	}
	
	public void showItemMenu(){
		
		currentState = STATES.ITEM_MENU;
		ItemMenu itemMenu = new ItemMenu("ItemMenu", new Vector2f(350, 80), this);
		
		Weapon[] itemsAquired = localHero.getWeaponPocket();
		for(int i = 0; i < itemsAquired.length; i++){
			
			Item item = itemsAquired[i];
			if(item == null) continue;
			
			if(item instanceof Weapon){
				itemMenu.addWeapon((Weapon) item);
			}
		}
		
		hud.addComponent(itemMenu);
		
	}
	
	public void resumeNextLevel(){
		
		hideItemMenu();
		localHero.emptyWeaponPocket();
		currentLevel.nextLevel();
	}
	
	public void hideItemMenu(){
		
		currentState = STATES.PLAY_GAME;
		if(hud.components.containsKey("ItemMenu")){
			hud.components.remove("ItemMenu");
		}
	}
	
	public void mousePressed(int button, int x, int y){
		if(currentState == STATES.PLAY_GAME){
			
			Player localPlayer = getLocalPlayer();
			if(localPlayer == null) return;
			
			Hero localHero = getLocalPlayer().hero;
			if(localHero.isAlive()){
			
				localHero.setShooting(true);
				
				/* IGNORE NETWORK
				Network.Shoot shoot = new Network.Shoot();
				shoot.shooting = localHero.isShooting();
				shoot.owner = localHero.id;
				client.sendTCP(shoot);
				*/
			
			}
			
		} else if(currentState == STATES.ITEM_MENU){
			hud.components.get("ItemMenu").mouseClicked(button, x, y);
		}
	}
	
	public void mouseReleased(int button, int x, int y){
		if(currentState == STATES.PLAY_GAME){
			
			Player localPlayer = getLocalPlayer();
			if(localPlayer == null) return;
			
			Hero localHero = getLocalPlayer().hero;
			if(localHero.isAlive()){
			
				localHero.setShooting(false);
				
				/* IGNORE NETWORK
				Network.Shoot shoot = new Network.Shoot();
				shoot.shooting = localHero.isShooting();
				shoot.owner = localHero.id;
				client.sendTCP(shoot);
				*/
			
			}
			
		}		
	}
	
	public void keyPressed(int key, char c){
		
		Player localPlayer = getLocalPlayer();
		if(localPlayer == null) return;
		
		Hero localHero = getLocalPlayer().hero;
		
		if(localHero != null){
			if(key == Input.KEY_W){
				localHero.moveUp(true);
			}
			if(key == Input.KEY_S){
				localHero.moveDown(true);
			}
			if(key == Input.KEY_D){
				localHero.moveRight(true);
			}
			if(key == Input.KEY_A){
				localHero.moveLeft(true);
			}
		}
	}
	
	public void keyReleased(int key, char c){
		
		if(key == Input.KEY_Z){
			STOP_SPAWNING = !STOP_SPAWNING;
		}
		
		if(key == Input.KEY_F4){
			//currentLevel.spawnBoss();
		}
		
		if(localHero != null){
			if(key == Input.KEY_W){
				localHero.moveUp(false);
			}
			if(key == Input.KEY_S){
				localHero.moveDown(false);
			}
			if(key == Input.KEY_D){
				localHero.moveRight(false);
			}
			if(key == Input.KEY_A){
				localHero.moveLeft(false);
			}
		}
		
		if(key == Input.KEY_V){
			if(currentState == STATES.PLAY_GAME){
				this.showItemMenu();
			}else if(currentState == STATES.ITEM_MENU){
				this.hideItemMenu();
			}
		}
		
		if(key == Input.KEY_T){
			SHOW_INFRONTCAST = !SHOW_INFRONTCAST;
			SHOW_PATHFINDING = !SHOW_PATHFINDING;
			SHOW_LINEOFSIGHT = !SHOW_LINEOFSIGHT;
			SHOW_GENERIC_RAYS = !SHOW_GENERIC_RAYS;
			SHOW_HITBOXES = !SHOW_HITBOXES;
			SHOW_ENEMY_ATTACKS = !SHOW_ENEMY_ATTACKS;
		}
		
		// Gernade throw
		if(key == Input.KEY_G){
			
			float mousex = Mouse.getX() + cam.getCameraX();
			float mousey = Mouse.getY() + cam.getCameraY();
			
			Vector2f target = new Vector2f(mousex, mousey);
			
			localHero.throwGernade(target);
		}
		
		if(key == Input.KEY_R){
			localHero.getWeapon().reload();
		}
		
		for(int i = 2; i < 11; i++ ){
			if(key == i){
				Weapon weapon = localHero.getWeaponPocket()[i - 2];
				if(weapon != null){
					localHero.equipWeapon(weapon);
				}
			}
		}
		
		if(key == Input.KEY_F1){
			
			entityManager.clearAll();
			currentLevel.getMap().resetMap(1);
			players.clear();
			currentState = Celeri.STATES.LOAD_GAME;
			
		}
		
		if(key == Input.KEY_F2){
			debugSpawnNearEntity(1, localHero);
		}
		
		if(key == Input.KEY_ENTER){
			if(!localHero.isAlive()){
				/* IGNORE NETWORK
				Network.SpawnEntity spawnEntity = new Network.SpawnEntity();
				spawnEntity.type = Const.EntityTypes.HERO;
				spawnEntity.owner = getLocalPlayer().id;
				client.sendTCP(spawnEntity);
				*/
				
			}
		}

	}
	
	public void spawnCreepers(){
		
		Random rand = new Random();
		
		int czombie = 0;
		int cspitter = 0;
		
		TileMap map = currentLevel.getMap();
		com.heavydose.shared.map.TileOld[][] tiles = map.getTiles();
		
		for(int x = 0; x < tiles.length; x++){
			for(int y = 0; y < tiles[x].length; y++){
				if(tiles[x][y].getSpawnable()){
					
					if(rand.nextFloat() < 0.90){
						Creeper zombie = new Creeper(x * map.getTileWidth(), y * map.getTileHeight(), currentLevel.getLevelCount());
						zombie.setHitBox();
						entityManager.addEntity(zombie);
						czombie++;
					}else{
						Spitter spitter = new Spitter(x * map.getTileWidth(), y * map.getTileHeight(), currentLevel.getLevelCount());
						spitter.setHitBox();
						entityManager.addEntity(spitter);
						cspitter++;
					}
				}
			}
		}
		
		System.out.println("Spawned " + czombie + " zombies");
		System.out.println("Spawned " + cspitter + " spitters");
		
	}
	
	public void debugSpawnAnywhere(int amount){
		
		int numSpawns = amount;
		int countSpawns = 0;
		
		Random rand = new Random();
		
		TileMap map = currentLevel.getMap();
		
		for(int x = 0; x < map.getWidth(); x++){
			for(int y = 0; y < map.getHeight(); y++){
				
				if(countSpawns > numSpawns) break;
				
				if(!map.isTileBlocked(x, y)){
					if(rand.nextDouble() > 0.95){
						
						Creeper zombie = new Creeper(x * map.getTileWidth(), y * map.getTileHeight(), currentLevel.getLevelCount());
						zombie.setHitBox();
						entityManager.addEntity(zombie);
						countSpawns++;
						
					}
				}
				
			}
		}
	}
	
	public void debugSpawnNearEntity(int amount, Entity spawnNear){
		
		TileMap map = currentLevel.getMap();
		
		Point tilePos = map.getTilePositionUnderEntity(spawnNear);
		
		Point[] possibleSpawns = new Point[14*14];
		
		int lazyCount = 0;
		for(int x = -7; x < 7; x++)
			for(int y = -7; y < 7; y++){
			
				int nx = x + tilePos.x;
				int ny = y + tilePos.y;
				
				if(!map.isTileBlocked(nx, ny)){
					possibleSpawns[lazyCount] = new Point(nx * map.getTileWidth(), ny * map.getTileHeight());
				}
				
				lazyCount++;
			
			}
		
		Random rand = new Random();
		int i = rand.nextInt(100);
		
		Point spawn = null;
		
		for(int j = 0; j < amount; j++){
			while(spawn == null){
			
				i = rand.nextInt(14*14);
				
				spawn = possibleSpawns[i];
				possibleSpawns[i] = null;
				
			}
			
			//test = new WormBoss(spawn.x, spawn.y, currentLevel.getLevelCount());
			//test.setHitBox();
			//entityManager.addEntity(test);
			
			spawn = null;
			
		}
	}
	
	public Player getLocalPlayer(){
		
		Iterator<Player> it = players.values().iterator();
		while(it.hasNext()){
			Player player = it.next();
			if(player.isLocal){
				return player;
			}
		}
		
		return null;
	}
	
	public Entity[] getAllHeroes(){
		Entity[] heroes = new Entity[players.size()];
		Iterator<Player> it = players.values().iterator();
		int i = 0;
		while(it.hasNext()){
			heroes[i] = it.next().hero;
			i++;
		}
		return heroes;
	}
	
	public Player createPlayer(int id, String name){
		
		Player player = new Player();
		
		if(players.size() == 0)
			player.isLocal = true;
		
		player.id = id;
		player.username = name.trim();
		players.put(id, player);
		
		return player;
	}

	
	public void focusCamera(Entity entity){
		if(cam == null){
			cam = new Camera(gc, currentLevel.getMap());
		}
		cam.focus(entity);
	}
	
	public HashMap<Integer, Player> getPlayers(){
		return players;
	}
	
	public Player getComputerPlayer(){ return computerPlayer; }
	public Camera getCamera(){ return cam; }
	
	public static void main(String args[]){
		
		try{
	        AppGameContainer agc = new AppGameContainer(new Celeri("Celeri"), 1280, 800, false);
	        agc.start();
		}catch(SlickException e){
			e.printStackTrace();
		}
		Log.set(Log.LEVEL_DEBUG);
	}

}
