package com.heavydose.shared.map;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.Cache;
import com.heavydose.shared.Entity;
import com.heavydose.shared.bullets.Bullet;
import com.heavydose.util.Tools;

public class GameMap {
	
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;
	
	private int width, height;
	private Tile[][] tiles;
	
	private Random rand;
	
	public GameMap(int width, int height){
		this.width = width;
		this.height = height;
		
		rand = new Random();
		
		tiles = new Tile[width][height];
		
	}
	
	public void resetMap(){
		for(int x = 0; x < tiles.length; x++){
			for(int y = 0; y < tiles[x].length; y++){
				tiles[x][y] = new Tile(1, false);
			}
		}
	}

	public Vector2f generate(int maxRooms, int maxMiners, double minerSpawnChance, double roomSpawnChance){
		
		resetMap();
		
		int minerCount = 0;
		//int numRooms = 0;
		long iteration = 0;
		int genAttempts = 0;
		Random rand = new Random();
		ArrayList<MapMiner> miners = new ArrayList<MapMiner>();
		//ArrayList<Room> rooms = new ArrayList<Room>();
		miners.add(new MapMiner(tiles.length/2, tiles.length/2));
		
		long elapsedTime = System.currentTimeMillis();
		
		/* IGNORE NETWORK
		if(DetermineSide.isClient){
			System.err.println("Cannot generate map on the client.");
			return;
		}
		*/
		
		while(minerCount < maxMiners || iteration > 20000){
			
			if(iteration > 50000){
				if(genAttempts > 10){
					System.err.println("Completly failed to generate a map");
					return new Vector2f(0,0);
				}
				System.err.println("Too many iterations, failed to generate a map...retrying [" + genAttempts + "]");
				minerCount = 0;
				//numRooms = 0;
				iteration = 0;
				miners.clear();
				//rooms.clear();
				miners.add(new MapMiner(tiles.length/2, tiles.length/2));
				resetMap();
				genAttempts++;
			}
			
			for(int i = 0; i < miners.size(); i++){
				
				MapMiner miner = miners.get(i);
				
				if(tiles[miner.x][miner.y].type == 1)
					miner.wasteCount = 0;
			
				tiles[miner.x][miner.y].type = 0;
	
				if(miner.wasteCount < 2 && miner.sticky == 0){
					miner.direction = rand.nextInt(4);
				}
				
				miner.move();
				
				// Make doors on rooms that need doors, otherwise avoid
				/*
				if(tiles[miner.x][miner.y].room != null){
					
					Room room = tiles[miner.x][miner.y].room;
					// make sure there are doors needed and we're carving out a wall of the room
					if(room.numDoors < room.maxDoors && tiles[miner.x][miner.y].type == 2){
						if(miner.x != room.x ||
								miner.x != room.x + room.w ||
								(miner.x != room.x && miner.y != room.y) || 
								(miner.x != room.x + room.w && miner.y != room.y + room.h)){
							
							tiles[miner.x][miner.y].type = 3;
							room.numDoors++;
						}
					}
					
					miner.reverseAndMove(2);
				}
				*/
				
				// Remove any miners that try to go out of bounds but if there is only one left, just reverse the direction and stick
				if(miner.x < 3 || miner.y < 3 || miner.x > tiles.length - 3 || miner.y > tiles.length - 3){
					if(miners.size() > 1){
						miners.remove(miner);
					}else{
						miner.reverseAndMove(3);
					}
					continue;
				}
				
				// Remove any miners that are surrounded by walkable tiles
				if(tiles[miner.x + 1][miner.y    ].type == 0 &&
				   tiles[miner.x - 1][miner.y    ].type == 0 &&
				   tiles[miner.x    ][miner.y + 1].type == 0 &&
				   tiles[miner.x    ][miner.y - 1].type == 0){
					
					if(miners.size() > 1){
						miners.remove(miner);
						continue;
					}else{
						miner.wasteCount++;
					}
					
				} else {
					
					// spawn a new miner
					double newChance = rand.nextDouble();
					if(newChance < minerSpawnChance){
						miners.add(new MapMiner(miner.x, miner.y));
						minerCount++;
					}
					
					// create a room
					/*
					double boxChance = rand.nextDouble();
					int boxWidth = rand.nextInt(5)+5;
					int boxHeight = rand.nextInt(5)+5;
					if(boxChance < roomSpawnChance && numRooms < maxRooms){
						if(miner.x - boxWidth > 3 && miner.y - boxHeight > 3 && miner.x + boxWidth < tiles.length - 3 && miner.y + boxHeight < tiles.length - 3){
						
							Room room = new Room(miner.x, miner.y, boxWidth, boxHeight, 3);
							rooms.add(room);
							
							for(int x = -boxWidth; x < boxWidth; x++){
								for(int y = -boxHeight; y < boxHeight; y++){
									
									int offsetX = miner.x + x;
									int offsetY = miner.y + y;
									TileOld tile = tiles[offsetX][offsetY];
									
									if((x == -boxWidth || x == boxWidth - 1 || y == -boxHeight || y == boxHeight - 1) && tile.room == null){
										tile.setValue(2);
									}else{
										tile.type = 0;
									}
									
									tile.room = room;
									
								}
							}
							
							// take miner out of the room
							// TODO: Miner still gets stuck because it will be moved into an adjecent room...
							miner.x += boxWidth + 2;
							miner.y += boxHeight + 2;
							tiles[miner.x][miner.y].type = 0;
							tiles[miner.x][miner.y].room = null;
							
							numRooms++;
						
						}
					}
					*/
					
				}
				
				if(miner.sticky > 0)
					miner.sticky -= 1;
			}
			
			iteration++;
		}
		
		MapMiner lastMiner = miners.get(0);
		cleanUpMap();
		
		// Hero spawn area
		for(int x = lastMiner.x - 3; x < lastMiner.x + 3; x++){
			for(int y = lastMiner.y - 3; y < lastMiner.y + 3; y++){
				
				if(x < 2 || y < 2 || x > tiles.length-2 || y > tiles.length-2) continue;
				
				tiles[x][y].type = 0;
			}
		}
		
		setImages();
		
		elapsedTime = System.currentTimeMillis() - elapsedTime;
		System.out.println("Generated in: " + elapsedTime + "ms  Iterations: " + iteration + "  Created Miners:" + minerCount + "/" + maxMiners);// + "  Number of Rooms: " + numRooms + "/" + maxRooms);
		
		return new Vector2f(lastMiner.x * TILE_WIDTH, lastMiner.y * TILE_HEIGHT);
		
	}
	
	private void cleanUpMap(){
		
		for(int x = 1; x < tiles.length - 1; x++){
			for(int y = 1; y < tiles[x].length - 1; y++){
				
				if(tiles[x][y].type == 1){
					
					int c = 0;
					if(tiles[x + 1][y    ].type == 1) c++;
					if(tiles[x - 1][y    ].type == 1) c++;
					if(tiles[x    ][y + 1].type == 1) c++;
					if(tiles[x    ][y - 1].type == 1) c++;
					if(tiles[x + 1][y + 1].type == 1) c++;
					if(tiles[x - 1][y - 1].type == 1) c++;
					if(tiles[x - 1][y + 1].type == 1) c++;
					if(tiles[x + 1][y - 1].type == 1) c++;
					
					if(c < 3){
						tiles[x + 1][y    ].type = 0;
						tiles[x - 1][y    ].type = 0;
						tiles[x    ][y + 1].type = 0;
						tiles[x    ][y - 1].type = 0;
						tiles[x + 1][y + 1].type = 0;
						tiles[x - 1][y - 1].type = 0;
						tiles[x - 1][y + 1].type = 0;
						tiles[x + 1][y - 1].type = 0;
					}
					
				}
			}
		}
		
		for(int x = 1; x < tiles.length - 1; x++){
			for(int y = 1; y < tiles[x].length - 1; y++){
		
				if(tiles[x][y].type == 1){
					
					if(tiles[x + 1][y    ].type == 0  &&
					   tiles[x - 1][y    ].type == 0  &&
					   tiles[x    ][y + 1].type == 0  &&
					   tiles[x    ][y - 1].type == 0){
					
						tiles[x][y].type = 0;
					}
				}
			}
		}
	}

	private void setImages(){
		for(int x = 0; x < tiles.length; x++){
			for(int y = 0; y < tiles[x].length; y++){
				
				Tile tile = tiles[x][y];
				
				if(tile.type == 0){
					
					int count = Cache.sheets.get("tiles_dirt").getHorizontalCount();
					tile.setImage(Cache.sheets.get("tiles_dirt"), rand.nextInt(count));
				
				}
				else if(tile.type == 1){
					
					int c = 0;
					if(tiles[x + 1][y    ].type == 0 ) c++;
					if(tiles[x - 1][y    ].type == 0 ) c++;
					if(tiles[x    ][y + 1].type == 0 ) c++;
					if(tiles[x    ][y - 1].type == 0 ) c++;
					if(tiles[x + 1][y + 1].type == 0 ) c++;
					if(tiles[x - 1][y - 1].type == 0 ) c++;
					if(tiles[x - 1][y + 1].type == 0 ) c++;
					if(tiles[x + 1][y - 1].type == 0 ) c++;
					
					if( c > 0 )
						tile.setImage(Cache.tiles.get("dirt_wall_side"));
					else
						tile.setImage(Cache.tiles.get("dirt_wall"));
					
				}
			}
		}
	}

	public boolean isPositionBlocked(float x, float y){
		
		int ex = (int) x / TILE_WIDTH;
		int ey = (int) y / TILE_HEIGHT;
		
		return isTileBlocked(ex,ey);
	}
	public boolean isPositionBlocked(Vector2f position) {
		return isPositionBlocked(position.x, position.y);
	}
	public boolean isPositionBlocked(Entity entity){
		return isPositionBlocked(entity.getCenterX(), entity.getCenterY());
	}
	
	public boolean isTileBlocked(int x, int y){
		if(x < 0 || y < 0 || x > width - 1|| y > height - 1){
			return true;
		}
		return tiles[x][y].blocked;
	}
	
	public ArrayList<Vector2f> getSpawnPositions(Entity[] voids, float voidRadius){
		
		ArrayList<Vector2f> spawnPositions = new ArrayList<Vector2f>();
		
		int xw = (int) (voidRadius / TILE_WIDTH);
		int yw = (int) (voidRadius / TILE_HEIGHT);
		
		// no point in doing any calculations since the whole map is void
		if(xw > width && yw > height) return spawnPositions;
		
		boolean[][] badSpawns = new boolean[width][height];
		
		// bad spawns for tiles too close to listed voids
		for(int i = 0; i < voids.length; i++){
			if(voids[i] == null) continue;
			
			int x = (int) (voids[i].getPosition().x / TILE_WIDTH);
			int y = (int) (voids[i].getPosition().y / TILE_HEIGHT);
			
			for(int tileX = x - xw; tileX < x + xw; tileX++){
				for(int tileY = y - yw; tileY < y + yw; tileY++){
					if(tileX < 0 || tileY < 0 || tileX > width - 1 || tileY > height - 1) continue;
					
					badSpawns[tileX][tileY] = true;
				}
			}
		}
		
		// bad spawns for all the normally blocked tiles
		for(int tileY = 0; tileY < height; tileY++){
			for(int tileX = 0; tileX < width; tileX++){
				
				if(tiles[tileX][tileY].blocked){
					
					badSpawns[tileX][tileY] = true;
				}
			}
		}
		
		for(int x = 0; x < badSpawns.length; x++){
			for(int y = 0; y < badSpawns[x].length; y++){
				
				if(!badSpawns[x][y]){
					spawnPositions.add(new Vector2f(x * TILE_WIDTH, y * TILE_HEIGHT));
				}
			}
		}
		
		return spawnPositions;
		
	}

	public Tile getTileUnderEntity(Entity entity){
		return tiles[(int) (entity.getCenterX() / TILE_WIDTH)]
		            [(int) (entity.getCenterY() / TILE_HEIGHT)];
	}
	public Vector2f getTilePositionUnderEntity(Entity entity){
		return new Vector2f((int) entity.getCenterX() / TILE_WIDTH,
					     (int) entity.getCenterY() / TILE_HEIGHT);
	}

	public boolean isLineTouchingBlockedTiles(Line line){
		
		int x1 = (int) line.getX1() / TILE_WIDTH;
		int y1 = (int) line.getY1() / TILE_HEIGHT;
		int x2 = (int) line.getX2() / TILE_WIDTH;
		int y2 = (int) line.getY2() / TILE_HEIGHT;
		
		int dx = Math.abs(x1 - x2);
		int dy = Math.abs(y1 - y2);
		
		int x = x1;
		int y = y1;
		int i = 1 + dx + dy;
		int xi = (x2 > x1) ? 1: -1;
		int yi = (y2 > y1) ? 1: -1;
		int e = dx - dy;
		dx *= 2;
		dy *= 2;
		
		Rectangle hitBox = new Rectangle(0,0,TILE_WIDTH,TILE_HEIGHT);
		
		for(; i > 0; i--){
			
			if(isTileBlocked(x,y)){
				hitBox.setLocation(x * TILE_WIDTH, y * TILE_HEIGHT);
				if(hitBox.intersects(line))
					return true;
			}
			
			if(e > 0){
				x += xi;
				e -= dy;
			}else{
				y += yi;
				e += dx;
			}
			
		}
		
		return false;
		
	}

	public void checkTileCollision(Entity entity){
		
		final int DIST = 2;
		
		int ex = (int) entity.getPosition().x / TILE_WIDTH;
		int ey = (int) entity.getPosition().y / TILE_HEIGHT;
		
		if(ex < 0 || ey < 0 || ex > width || ey > height) return;
		
		Rectangle tileRect = new Rectangle(0,0,TILE_WIDTH,TILE_HEIGHT);
		Vector2f difference = entity.getPosition().sub(entity.getLastPosition());
		
		
		for(int y = ey-DIST; y < ey+DIST; y++){
			for(int x = ex-DIST; x < ex+DIST; x++){
				
				if(isTileBlocked(x,y)){
					
					tileRect.setLocation(x*TILE_WIDTH, y*TILE_HEIGHT);
					Rectangle entityRect = entity.getHitBox();
					
					boolean hasMoved = (difference.x != 0 || difference.y != 0);
					
					if(hasMoved){
						
						if(entityRect.intersects(tileRect)){
							
							if(entity instanceof Bullet){
								entity.kill(null);
								return;
							}
							
							Vector2f translatedDifference = Tools.calcMinTranslationDistance(entityRect, tileRect);
							entity.addPosition(translatedDifference.x, translatedDifference.y);
							entity.hit(entity);
							
						}
					}
				}
			}
		}
	}
}
