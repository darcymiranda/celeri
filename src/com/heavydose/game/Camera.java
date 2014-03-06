
package com.heavydose.game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.shared.Entity;
import com.heavydose.shared.map.TileMap;
import com.heavydose.util.Tools;



public class Camera {
	
   private int numTilesX;
   private int numTilesY;
   private int mapHeight;
   private int mapWidth;
   private int tileWidth;
   private int tileHeight;

   private GameContainer gc;

   private float cameraX; 
   private float cameraY;
   
   private Vector2f lastPosition;
   
   private Entity focus;
   
   private boolean shake;
   private float shakeDecay;
   
   /**
    * Create a new camera
    *
    * @param gc the GameContainer, used for getting the size of the GameCanvas
    * @param map the TiledMap used for the current scene
    */
   public Camera(GameContainer gc, TileMap map) {
      
      this.numTilesX = map.getWidth();
      this.numTilesY = map.getHeight();
      
      this.tileWidth = map.getTileWidth();
      this.tileHeight = map.getTileHeight();
      
      this.mapHeight = this.numTilesX * this.tileWidth;
      this.mapWidth = this.numTilesY * this.tileHeight;
      
      lastPosition = new Vector2f();
      
      this.gc = gc;
  
   }
   
   /**
    * "locks" the camera on the given coordinates. The camera tries to keep the location in it's center.
    *
    * @param x the real x-coordinate (in pixel) which should be centered on the screen
    * @param y the real y-coordinate (in pixel) which should be centered on the screen
    */
   public void centerOn(Vector2f position) {
	   
	  // if the object does not exist let the camera focus on the last position
	  if(position == null)
		  position = lastPosition;
	  else
		  lastPosition = position;

      //try to set the given position as center of the camera by default
      //cameraX = position.x - gc.getWidth() / 2;
      //cameraY = position.y - gc.getHeight() / 2;
	  position.x -= gc.getWidth() / 2;
	  position.y -= gc.getHeight() / 2;
      
      cameraX = Tools.lerp(cameraX, position.x, 0.15f);
      cameraY = Tools.lerp(cameraY, position.y, 0.15f);
      
      //if the camera is at the right or left edge lock it to prevent a black bar
      if(cameraX < 0) cameraX = 0;
      if(cameraX + gc.getWidth() > mapWidth) cameraX = mapWidth - gc.getWidth();
      
      //if the camera is at the top or bottom edge lock it to prevent a black bar
      if(cameraY < 0) cameraY = 0;
      if(cameraY + gc.getHeight() > mapHeight) cameraY = mapHeight - gc.getHeight();
   }
   
   float shakeX;
   float shakeY;
   
   float intensity = 1;
   int repeat = 0;
   int step = 0;
   
   public void update(int delta){
	   if(shake){
		   
		   if(shakeDecay < 0){
			   
			   if(repeat > 0){
			   
				   if(step == 0){

					   shakeDecay = 50 * intensity;
					   shakeX = cameraX - 50 * intensity;

				   }
				   else if(step == 1){

					   shakeDecay = 50 * intensity;
					   shakeX = cameraX + 50 * intensity;

				   }else{
					   centerOn(focus);
					   step = -1;
					   repeat--;
					   intensity -= 0.50f;
				   }
				   step++;
				   
				   
			   } else {
				   shake = false;
			   }
			   
			   
			   
		   }
		   
		   cameraX = Tools.lerp(cameraX, shakeX, 0.15f);
		   
		   shakeDecay -= delta;
		   
	   } else {
		   if(focus != null)
			   centerOn(focus);
	   }
	   
	  
   }
   
   public void shake(float intensity, int repeat){
	   shake = true;
	   this.intensity = intensity;
	   this.repeat = repeat;
	   step = 0;
   }
   
   /**
    * "locks" the camera on the center of the given Rectangle. The camera tries to keep the location in it's center.
    *
    * @param x the x-coordinate (in pixel) of the top-left corner of the rectangle
    * @param y the y-coordinate (in pixel) of the top-left corner of the rectangle
    * @param height the height (in pixel) of the rectangle
    * @param width the width (in pixel) of the rectangle
    */
   public void centerOn(Vector2f position, float height, float width) {
      this.centerOn(new Vector2f(position.x + width / 2, position.y + height / 2));
   }

   /**
    * "locks the camera on the center of the given Shape. The camera tries to keep the location in it's center.
    * @param shape the Shape which should be centered on the screen
    */
   public void centerOn(Shape shape) {
      this.centerOn(new Vector2f(shape.getCenterX(), shape.getCenterY()));
   }
   
   /**
    * Locks the camera on the center of the given Entity.
    * @param entity
    */
   public void centerOn(Entity entity){
	   if(entity != null)
		   this.centerOn(new Vector2f(entity.getPosition().x,
				   entity.getPosition().y));
   }
   
   public void focus(Entity entity){ focus = entity; }
   public void cancelFocus(){ focus = null; }
   
   public int[] getDrawOffsets() {
      return this.getDrawOffsests(0, 0);
   }
   
   public int[] getDrawOffsests(int offsetX, int offsetY) {
	   
       //calculate the offset to the next tile (needed by TiledMap.render())
       int tileOffsetX = (int) - (cameraX % tileWidth);
       int tileOffsetY = (int) - (cameraY % tileHeight);
       
      
       //calculate the index of the leftmost tile that is being displayed
       int tileIndexX = (int) (cameraX / tileWidth);
       int tileIndexY = (int) (cameraY / tileHeight);
       
       int[] offsets = { tileOffsetX + offsetX, 
    		   			 tileOffsetY + offsetY,
					     tileIndexX, 
					     tileIndexY,
					     (gc.getWidth()  - tileOffsetX) / tileWidth  + 1,
					     (gc.getHeight() - tileOffsetY) / tileHeight + 1 };
       
       return offsets;
       
   }
 
   public void translateGraphics() {
      gc.getGraphics().translate(-cameraX, -cameraY);
   }
      
   public void untranslateGraphics() {
      gc.getGraphics().translate(cameraX, cameraY);
   }
   
   public float getCameraX(){ return cameraX; }
   public float getCameraY(){ return cameraY; }
   
}