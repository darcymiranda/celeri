package com.heavydose;

import java.awt.Font;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.OutlineEffect;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.util.ResourceLoader;

import com.heavydose.shared.map.GameMap;

public class Cache {
	
	public static Map<String, Image> images;
	public static Map<String, SpriteSheet> sheets;
	public static Map<String, Image> tiles;
	public static Map<String, ConfigurableEmitter> emitters;
	public static Map<String, UnicodeFont> fonts;
	public static Map<String, Sound> sounds;
	public static Map<String, Music> music;
	public static Map<String, Sound[]> soundPacks;
	
	public static Image[] blood;

	@SuppressWarnings("unchecked")
	public static void load(){
		images = new LinkedHashMap<String, Image>(100, .75f, true);
		sheets = new LinkedHashMap<String, SpriteSheet>(100, .75f, true);
		tiles = new LinkedHashMap<String, Image>(100, .75f, true);
		emitters = new LinkedHashMap<String, ConfigurableEmitter>(100, .75f, true);
		fonts = new LinkedHashMap<String, UnicodeFont>(100, .75f, true);
		sounds = new LinkedHashMap<String, Sound>(100, .75f, true);
		soundPacks = new LinkedHashMap<String, Sound[]>(100, .75f, true);
		music = new LinkedHashMap<String, Music>(100, .75f, true);
		
		try {
			
			loadPack("res/sound/zmb", "creeper", Sound.class);
			loadPack("res/sound/splat", "splat", Sound.class);
			loadPack("res/sound/spitter", "spitter", Sound.class);
			loadPack("res/sound/spider/atk", "spider_atk", Sound.class);
			
			sheets.put("tiles_dirt", new SpriteSheet("res/img/tiles/tile_dirt.png", GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT));
			
			final String IMAGE_WEAPONS = "res/img/items";
			File[] files = new File(IMAGE_WEAPONS).listFiles();
			for(int i = 0; i < files.length; i++){
				if(files[i].isFile()){
					String name = files[i].getName().substring(0, files[i].getName().length() - 4);
					images.put(name, new Image(IMAGE_WEAPONS + "/" + files[i].getName()));
				}
			}
			
			images.put("hero", new Image("res/img/test_hero.png"));
			images.put("b_pistol", new Image("res/img/b_pistol.png"));
			images.put("b_rail", new Image("res/img/b_rail.png"));
			images.put("bullet", new Image("res/img/test_bullet.png"));
			images.put("bullet_enemy", new Image("res/img/bullet_enemy.png"));
			images.put("creeper", new Image("res/img/test_zombie.png"));
			images.put("spider", new Image("res/img/spider.png"));
			images.put("big_spider", new Image("res/img/big_spider.png"));
			images.put("big_spider2", new Image("res/img/big_spider2.png"));
			images.put("strangler", new Image("res/img/strangler.png"));
			images.put("mutated_zombie", new Image("res/img/test_zombie.png"));
			images.put("spitter", new Image("res/img/spitter.png"));
			images.put("strangle", new Image("res/img/strangle.png"));
			images.put("worm_part", new Image("res/img/worm_part.png"));
			images.put("worm_head", new Image("res/img/worm_head.png"));
			images.put("p_blood", new Image("res/img/particles/blood.png"));
			images.put("p_default", new Image("res/img/particles/p_default.png"));
			images.put("hero_legs", new Image("res/img/test_hero_legs.png"));
			images.put("ui_healthbar", new Image("res/img/ui/healthbar2.png"));
            images.put("ui_healthbar_boss", new Image("res/img/ui/healthbar.png"));
			images.put("ui_overlay", new Image("res/img/ui/overlay.png"));
			images.put("ui_healthunit", new Image("res/img/ui/healthunit.png"));
			images.put("ui_wires", new Image("res/img/ui/wires.png"));
			images.put("ui_item_menu", new Image("res/img/ui/item_menu.png"));
			images.put("ui_item", new Image("res/img/ui/item.png"));
			images.put("ui_bar", new Image("res/img/ui/bar.png"));
			
			images.put("debre_creeper", new Image("res/img/debre/debre_creeper.png"));
			images.put("corpse", new Image("res/img/test_corpse.png"));
			
			blood = new Image[3];
			Image bloodImage = new Image("res/img/blood/blood0.png");
			images.put("blood0", bloodImage);
			blood[0] = bloodImage;
			bloodImage = new Image("res/img/blood/blood1.png");
			images.put("blood1", bloodImage);
			blood[1] = bloodImage;
			bloodImage = new Image("res/img/blood/blood2.png");
			images.put("blood2", bloodImage);
			blood[2] = bloodImage;
			
			tiles.put("dirt", new Image("res/img/test_tile.png"));
			tiles.put("dirt_wall", new Image("res/img/test_tile2.png"));
			tiles.put("room_wall", new Image("res/img/test_tile3.png"));
			tiles.put("room_door", new Image("res/img/test_tile4.png"));
			tiles.put("misc", new Image("res/img/test_tile5.png"));
			tiles.put("dirt_wall_side", new Image("res/img/wall_side.png"));
			tiles.put("wall_corner_se", new Image("res/img/wall_corner_se.png"));
			tiles.put("wall_e", new Image("res/img/wall_e.png"));
			tiles.put("wall_all", new Image("res/img/wall_all.png"));
			
			final String IMAGE_TILES = "res/img/tiles";
			files = new File(IMAGE_TILES).listFiles();
			for(int i = 0; i < files.length; i++){
				if(files[i].isFile()){
					String name = files[i].getName().substring(0, files[i].getName().length() - 4);
					tiles.put(name, new Image(IMAGE_TILES + "/" + files[i].getName()));
				}
			}
			
			emitters.put("blood", ParticleIO.loadEmitter("res/particle_data/hit_blood.xml"));
			emitters.put("death", ParticleIO.loadEmitter("res/particle_data/death_blood.xml"));
			emitters.put("strangle", ParticleIO.loadEmitter("res/particle_data/strangle.xml"));
			emitters.put("powerup", ParticleIO.loadEmitter("res/particle_data/powerup.xml"));
			emitters.put("debre_blood", ParticleIO.loadEmitter("res/particle_data/debre_blood.xml"));
			emitters.put("debre_wall", ParticleIO.loadEmitter("res/particle_data/debre_wall.xml"));
			
			final String SOUND_WEAPONS = "res/sound/weapons";
			files = new File(SOUND_WEAPONS).listFiles();
			for(int i = 0; i < files.length; i++){
				if(files[i].isFile()){
					String name = files[i].getName().substring(0, files[i].getName().length() - 4);
					sounds.put(name, new Sound(SOUND_WEAPONS + "/" + files[i].getName()));
				}
			}
			
			sounds.put("worm_hit0", new Sound("res/sound/worm_hit0.wav"));
			sounds.put("worm_hit1", new Sound("res/sound/worm_hit1.wav"));
			sounds.put("worm_spawn", new Sound("res/sound/worm.ogg"));
			sounds.put("powerup", new Sound("res/sound/powerup.wav"));
			sounds.put("pickup", new Sound("res/sound/pickup.wav"));
			
			music.put("rock_loop", new Music("res/sound/rock_loop.ogg"));
			music.put("boss_loop", new Music("res/sound/boss_loop.ogg"));
			
			/*
			sounds.put("gun", new Sound("res/sound/gun.wav"));
			sounds.put("pistol", new Sound("res/sound/pistol.wav"));
			sounds.put("shotgun", new Sound("res/sound/shotgun.wav"));
			sounds.put("shotgun_blast", new Sound("res/sound/shotgun_blast.wav"));
			sounds.put("reload", new Sound("res/sound/reload.wav"));
			sounds.put("reload_shotgun", new Sound("res/sound/reload_shotgun.wav"));
			sounds.put("spitter_shot", new Sound("res/sound/spitter_shot.wav"));
			sounds.put("rail", new Sound("res/sound/rail.wav"));
			sounds.put("rifle", new Sound("res/sound/rifle.wav"));
			*/
			
		    Font awtFont = new Font("Arial", Font.PLAIN, 26); 
		    UnicodeFont font = new UnicodeFont(awtFont, 26, false, false);
		    font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		    font.addAsciiGlyphs();
			font.loadGlyphs();
			fonts.put("default", font);
			
			awtFont = new Font("Arial", Font.PLAIN, 12);
		    font = new UnicodeFont(awtFont, 12, false, false);
		    font.getEffects().add(new ColorEffect(java.awt.Color.BLACK));
		    font.addAsciiGlyphs();
			font.loadGlyphs();
			fonts.put("item", font);
			
		    awtFont = new Font("Arial", Font.BOLD, 26); 
		    font = new UnicodeFont(awtFont, 32, false, false);
		    font.getEffects().add(new ColorEffect(java.awt.Color.YELLOW));
		    font.getEffects().add(new OutlineEffect(1, java.awt.Color.BLACK));
		    font.addAsciiGlyphs();
			font.loadGlyphs();
			font.setPaddingAdvanceX(4);
			fonts.put("counter", font);
			
		} catch (Exception se){
			se.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void loadPack(String location, String name, Class clazz) throws SlickException{
			
		File[] files = new File(location).listFiles();
		
		if(clazz.equals(Sound.class)){
		
			Sound[] s = new Sound[files.length];
			for(int i = 0; i < files.length; i++){
				if(files[i].isFile()){
					s[i] = new Sound(location + "/" + files[i].getName());
				}
			}
			
			soundPacks.put(name, s);
			
		}
	}
	
	public static Image[] getAllTiles(){
		return (Image[]) tiles.values().toArray(new Image[tiles.size()]);
	}
	
}
