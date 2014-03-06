package com.heavydose.shared.map;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.Cache;
import com.heavydose.shared.DetermineSide;
import com.heavydose.shared.Entity;
import com.heavydose.util.Point;
import com.heavydose.util.Tools;


public class TileMap {
	
	private final int TILE_WIDTH = 32, TILE_HEIGHT = 32;
	private int width, height;
	private Image[] images;
	private TileOld[][] tiles;
	private boolean load = false;
	
	private ArrayList<TileOld> enemySpawnPoints;
	
	private final int GEN_ATTEMPTS = 10;
	
	public TileMap(int x, int y){
		width = x;
		height = y;
		
		if(DetermineSide.isClient)
			this.images = Cache.getAllTiles();
		
		tiles = new TileOld[width][height];
	}
	
	public void resetMap(int debugValue){
		for(int tileX = 0; tileX < tiles.length; tileX++){
			for(int tileY = 0; tileY < tiles[tileX].length; tileY++){
				tiles[tileX][tileY] = new TileOld(tileX*TILE_WIDTH, tileY*TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
				tiles[tileX][tileY].setHitBox();
				tiles[tileX][tileY].setValue(debugValue);
				tiles[tileX][tileY].imageValue = 1;
			}
		}
	}
	
	public Vector2f generate(int maxRooms, int maxMiners, double minerSpawnChance, double roomSpawnChance){
		
		resetMap(1);
		
		enemySpawnPoints = new ArrayList<TileOld>();
		
		int minerCount = 0;
		int numRooms = 0;
		long iteration = 0;
		int genAttempts = 0;
		Random rand = new Random();
		ArrayList<Miner> miners = new ArrayList<Miner>();
		ArrayList<Room> rooms = new ArrayList<Room>();
		miners.add(new Miner(tiles.length/2, tiles.length/2));
		
		long elapsedTime = System.currentTimeMillis();
		
		/* IGNORE NETWORK
		if(DetermineSide.isClient){
			System.err.println("Cannot generate map on the client.");
			return;
		}
		*/
		
		while(minerCount < maxMiners || iteration > 20000){
			
			if(iteration > 50000){
				if(genAttempts > GEN_ATTEMPTS){
					System.err.println("Completly failed to generate a map");
					return new Vector2f(0,0);
				}
				System.err.println("Too many iterations, failed to generate a map...retrying [" + genAttempts + "]");
				minerCount = 0;
				numRooms = 0;
				iteration = 0;
				miners.clear();
				rooms.clear();
				miners.add(new Miner(tiles.length/2, tiles.length/2));
				resetMap(1);
				genAttempts++;
			}
			
			for(int i = 0; i < miners.size(); i++){
				
				Miner miner = miners.get(i);
				
				if(tiles[miner.x][miner.y].getValue() == 1)
					miner.wasteCount = 0;
			
				tiles[miner.x][miner.y].setValue(0);
	
				if(miner.wasteCount < 2 && miner.sticky == 0){
					miner.direction = rand.nextInt(4);
				}
				
				// Create spawns for creep
				if(miner.wasteCount > 6){
					tiles[miner.x][miner.y].spawnable(true);
					enemySpawnPoints.add(tiles[miner.x][miner.y]);
				}
				
				miner.move();
				
				// Make doors on rooms that need doors, otherwise avoid
				if(tiles[miner.x][miner.y].room != null){
					
					Room room = tiles[miner.x][miner.y].room;
					// make sure there are doors needed and we're carving out a wall of the room
					if(room.numDoors < room.maxDoors && tiles[miner.x][miner.y].getValue() == 2){
						if(miner.x != room.x ||
								miner.x != room.x + room.w ||
								(miner.x != room.x && miner.y != room.y) || 
								(miner.x != room.x + room.w && miner.y != room.y + room.h)){
							
							tiles[miner.x][miner.y].setValue(3);
							room.numDoors++;
						}
					}
					
					miner.reverseAndMove(2);
				}
				
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
				if(tiles[miner.x + 1][miner.y    ].getValue() == 0 &&
				   tiles[miner.x - 1][miner.y    ].getValue() == 0 &&
				   tiles[miner.x    ][miner.y + 1].getValue() == 0 &&
				   tiles[miner.x    ][miner.y - 1].getValue() == 0){
					
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
						miners.add(new Miner(miner.x, miner.y));
						minerCount++;
					}
					
					// create a room
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
										tile.setValue(0);
									}
									
									tile.room = room;
									
								}
							}
							
							// take miner out of the room
							// TODO: Miner still gets stuck because it will be moved into an adjecent room...
							miner.x += boxWidth + 2;
							miner.y += boxHeight + 2;
							tiles[miner.x][miner.y].setValue(0);
							tiles[miner.x][miner.y].room = null;
							
							numRooms++;
						
						}
					}
					
				}
				
				if(miner.sticky > 0)
					miner.sticky -= 1;
			}
			
			iteration++;
		}
		
		Miner lastMiner = miners.get(0);
		cleanUpMap();
		
		// Hero spawn area
		for(int x = lastMiner.x - 3; x < lastMiner.x + 3; x++){
			for(int y = lastMiner.y - 3; y < lastMiner.y + 3; y++){
				
				if(x < 2 || y < 2 || x > tiles.length-2 || y > tiles.length-2) continue;
				
				tiles[x][y].setValue(0);
				tiles[x][y].spawnable(false);
				
			}
		}
		
		setImageValues();
		
		elapsedTime = System.currentTimeMillis() - elapsedTime;
		System.out.println("Generated in: " + elapsedTime + "ms  Iterations: " + iteration + "  Created Miners:" + minerCount + "/" + maxMiners + "  Number of Rooms: " + numRooms + "/" + maxRooms);
		
		return new Vector2f(lastMiner.x * TILE_WIDTH, lastMiner.y * TILE_HEIGHT);
		
	}
	
	private void cleanUpMap(){
		
		for(int x = 1; x < tiles.length - 1; x++){
			for(int y = 1; y < tiles[x].length - 1; y++){
				
				if(tiles[x][y].getValue() == 1){
					
					int c = 0;
					if(tiles[x + 1][y    ].getValue() == 1 && tiles[x + 1][y    ].room == null) c++;
					if(tiles[x - 1][y    ].getValue() == 1 && tiles[x - 1][y    ].room == null) c++;
					if(tiles[x    ][y + 1].getValue() == 1 && tiles[x    ][y + 1].room == null) c++;
					if(tiles[x    ][y - 1].getValue() == 1 && tiles[x    ][y - 1].room == null) c++;
					if(tiles[x + 1][y + 1].getValue() == 1 && tiles[x + 1][y + 1].room == null) c++;
					if(tiles[x - 1][y - 1].getValue() == 1 && tiles[x - 1][y - 1].room == null) c++;
					if(tiles[x - 1][y + 1].getValue() == 1 && tiles[x - 1][y + 1].room == null) c++;
					if(tiles[x + 1][y - 1].getValue() == 1 && tiles[x + 1][y - 1].room == null) c++;
					
					if(c < 3){
						if(tiles[x + 1][y    ].room == null) tiles[x + 1][y    ].setValue(0);
						if(tiles[x - 1][y    ].room == null) tiles[x - 1][y    ].setValue(0);
						if(tiles[x    ][y + 1].room == null) tiles[x    ][y + 1].setValue(0);
						if(tiles[x    ][y - 1].room == null) tiles[x    ][y - 1].setValue(0);
						if(tiles[x + 1][y + 1].room == null) tiles[x + 1][y + 1].setValue(0);
						if(tiles[x - 1][y - 1].room == null) tiles[x - 1][y - 1].setValue(0);
						if(tiles[x - 1][y + 1].room == null) tiles[x - 1][y + 1].setValue(0);
						if(tiles[x + 1][y - 1].room == null) tiles[x + 1][y - 1].setValue(0);
					}
					
				}
			}
		}
		
		for(int x = 1; x < tiles.length - 1; x++){
			for(int y = 1; y < tiles[x].length - 1; y++){
		
				if(tiles[x][y].getValue() == 1){
					
					if(tiles[x + 1][y    ].getValue() == 0 || tiles[x + 1][y    ].room != null &&
					   tiles[x - 1][y    ].getValue() == 0 || tiles[x - 1][y    ].room != null &&
					   tiles[x    ][y + 1].getValue() == 0 || tiles[x    ][y + 1].room != null &&
					   tiles[x    ][y - 1].getValue() == 0 || tiles[x    ][y - 1].room != null){
					
						tiles[x][y].setValue(0);
					}
				}
			}
		}
	}
	
	private void setImageValues(){
		
		Random rand = new Random();
		
		for(int x = 1; x < tiles.length - 1; x++){
			
			for(int y = 1; y < tiles[x].length - 1; y++){
				
				TileOld tile = tiles[x][y];
				
				if(tile.getValue() == 0){
					tile.imageValue = rand.nextInt(3) + 9;;
				}
				else if(tile.getValue() == 1){
				
					int w = Math.max(x - 1, 0);
					int e = Math.min(x + 1, tiles.length-1);
					int n = Math.max(y - 1, 0);
					int s = Math.min(y + 1, tiles[x].length-1);
					
					int c = 0;
					if(tiles[x + 1][y    ].getValue() == 0 ) c++;
					if(tiles[x - 1][y    ].getValue() == 0 ) c++;
					if(tiles[x    ][y + 1].getValue() == 0 ) c++;
					if(tiles[x    ][y - 1].getValue() == 0 ) c++;
					if(tiles[x + 1][y + 1].getValue() == 0 ) c++;
					if(tiles[x - 1][y - 1].getValue() == 0 ) c++;
					if(tiles[x - 1][y + 1].getValue() == 0 ) c++;
					if(tiles[x + 1][y - 1].getValue() == 0 ) c++;
					
					if( c > 0 )
						tile.imageValue = 5;
					
					/*
					
					if(tiles[e][tileY].getValue() == 0)
						tile.imageValue = 9;
					
					else if(tiles[w][tileY].getValue() == 0)
						tile.imageValue = 10;
					
					else if(tiles[tileX][s].getValue() == 0)
						tile.imageValue = 11;
					
					else if(tiles[tileX][n].getValue() == 0)
						tile.imageValue = 12;
					
					else if(tiles[e][s].getValue() == 0)
						tile.imageValue = 5;
					
					else if(tiles[w][s].getValue() == 0) 
						tile.imageValue = 6;
					
					else if(tiles[e][n].getValue() == 0)
						tile.imageValue = 7;
					
					else if(tiles[w][n].getValue() == 0)
						tile.imageValue = 8;
					
					else
						tile.imageValue = 1;
						
					*/
				
				}
				else if(tile.getValue() == 2){
					tile.imageValue = 2;
				}
				else if(tile.getValue() == 3){
					tile.imageValue = 3;
				}
				
			}
		}
		
	}
	
	public void load(byte[][] map){
		load(map, width, height);
	}
	
	public void load(byte[][] data, int width, int height){
		
		this.width = width;
		this.height = height;
		
		tiles = new TileOld[width][height];
		
		for(int tileX = 0; tileX < tiles.length; tileX++){
			for(int tileY = 0; tileY < tiles[tileX].length; tileY++){
				tiles[tileX][tileY] = new TileOld(tileX*TILE_WIDTH, tileY*TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
				tiles[tileX][tileY].setHitBox();
				tiles[tileX][tileY].setValue(data[tileX][tileY]);
			}
		}
		
		load = true;
		
	}
	
	public byte[][] getData(){
		
		byte[][] data = new byte[width][height];
		
		for(int tileX = 0; tileX < tiles.length; tileX++){
			for(int tileY = 0; tileY < tiles[tileX].length; tileY++){
				data[tileX][tileY] = tiles[tileX][tileY].getValue();
			}
		}
		
		return data;
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
	
	public boolean isTileBlockedNC(int x, int y){
		return tiles[x][y].blocked;
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
	
	public TileOld getTileUnderEntity(Entity entity){
		return tiles[(int) (entity.getCenterX() / TILE_WIDTH)]
		            [(int) (entity.getCenterY() / TILE_HEIGHT)];
	}
	
	public Point getTilePositionUnderEntity(Entity entity){
		return new Point((int) entity.getCenterX() / TILE_WIDTH,
					     (int) entity.getCenterY() / TILE_HEIGHT);
	}
	
	public boolean isLineTouchingBlockedTiles(Line line){
		
		int x1 = (int) line.getX1() / TILE_WIDTH;
		int y1 = (int) line.getY1() / TILE_HEIGHT;
		int x2 = (int) line.getX2() / TILE_WIDTH;
		int y2 = (int) line.getY2() / TILE_HEIGHT;
		
		int dx = Math.abs(x1 - x2);
		int dy = Math.abs(y1 - y2);
		
		int modWidth = width + 2;
		int modHeight = height + 2;
		
		int x = x1;
		int y = y1;
		int i = 1 + dx + dy;
		int xi = (x2 > x1) ? 1: -1;
		int yi = (y2 > y1) ? 1: -1;
		int e = dx - dy;
		dx *= 2;
		dy *= 2;
		
		for(; i > 0; i--){
			
		
			if(!(x < 0 || y < 0 || x > width - 1|| y > height - 1))
				if(isTileBlockedNC(x,y))
					if(tiles[x][y].getHitBox().intersects(line))
						return true;
			
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
		
		if(ex < 0 || ey < 0|| ex > width - 1 || ey > height - 1) return;
		
		Rectangle tileRect = new Rectangle(0,0,TILE_WIDTH,TILE_HEIGHT);
		Vector2f difference = entity.getPosition().sub(entity.getLastPosition());
		
		//float xforce = 0;
		//float yforce = 0;
		
		for(int y = ey-DIST; y < ey+DIST; y++){
			for(int x = ex-DIST; x < ex+DIST; x++){
				
				if(x < 0 || y < 0|| x > width - 1 || y > height - 1) return;
				
				if(isTileBlocked(x,y)){
					
					tileRect.setLocation(x*TILE_WIDTH, y*TILE_HEIGHT);
					Rectangle entityRect = entity.getHitBox();
					
					boolean hasMoved = (difference.x != 0 || difference.y != 0);
					
					if(hasMoved){
						
						if(entityRect.intersects(tileRect)){
							
							if(entity instanceof com.heavydose.shared.bullets.Bullet){
								entity.kill(null);
								return;
							}
							
							Vector2f translatedDifference = Tools.calcMinTranslationDistance(entityRect, tileRect);
							entity.addPosition(translatedDifference.x, translatedDifference.y);
							entity.hit(tiles[x][y]);
							
						}
					}
				}
			}
		}
	}
	
	public void renderFloor(int[] offsets){
		this.renderFloor(offsets[0], offsets[1], offsets[2], offsets[3], offsets[4], offsets[5]);
	}
	
	public void renderFloor(int x, int y, int sx, int sy, int width, int height){
		
		for(int tileY = 0; tileY < height; tileY++){
			for(int tileX = 0; tileX < width; tileX++){
				
				if((sx+tileX < 0) || (sy+tileY < 0)) continue;
				if((sx+tileX >= this.width) || (sy+tileY >= this.height)) continue;
				
				// we will render this later
				if(tiles[tileX+sx][tileY+sy].blocked)
					continue;
				
				images[tiles[tileX+sx][tileY+sy].imageValue].draw(x+((tileX)*TILE_WIDTH), y+((tileY)*TILE_HEIGHT));
			
			}
		}
		
	}
	
	public void renderWalls(int[] offsets){
		this.renderWalls(offsets[0], offsets[1], offsets[2], offsets[3], offsets[4], offsets[5]);
	}
	
	public void renderWalls(int x, int y, int sx, int sy, int width, int height){
		
		for(int tileY = 0; tileY < height; tileY++){
			for(int tileX = 0; tileX < width; tileX++){
				
				if((sx+tileX < 0) || (sy+tileY < 0)) continue;
				if((sx+tileX >= this.width) || (sy+tileY >= this.height)) continue;
				
				// non collidables have already been rendered
				if(!tiles[tileX+sx][tileY+sy].blocked)
					continue;
				
				images[tiles[tileX+sx][tileY+sy].imageValue].draw(x+((tileX)*TILE_WIDTH), y+((tileY)*TILE_HEIGHT));
			}
		}
	}
	
	private class Miner {
		
		public int x,y;
		public int oldx, oldy;
		public int direction;
		public int wasteCount = 0;
		public int sticky = 0;
		
		public Miner(int x, int y){
			this.x = x; this.y = y;
		}
		
		public void move(){
			
			oldx = x;
			oldy = y;
			
			if(direction <= 0)
				y++;
			else if(direction == 1)
				y--;
			else if(direction == 2)
				x++;
			else if(direction >= 3)
				x--;
		}
		
		public void reverse(){
			if(direction <= 0)
				direction = 1;
			else if(direction == 1)
				direction = 0;
			else if(direction == 2)
				direction = 3;
			else if(direction >= 3)
				direction = 2;
		}
		
		public void reverseAndMove(int sticky){
			reverse();
			move();
			this.sticky = sticky;
		}
		
		public boolean isIdle(){
			return oldx == x && oldy == y;
		}
		
	}
	
	public final TileOld[][] getTiles(){ return tiles; }
	public void setTiles(TileOld[][] tiles){ this.tiles = tiles; }
	
	public final boolean isLoaded(){ return load; }
	
	public final int getWidth(){ return width; }
	public final int getHeight(){ return height; }
	
	public final int getTileWidth(){ return TILE_WIDTH; }
	public final int getTileHeight(){ return TILE_HEIGHT; }
	
	public final ArrayList<TileOld> getEnemySpawnPoints(){ return enemySpawnPoints; }

}
