package com.heavydose.util;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.heavydose.shared.Entity;


public class Tools {
	
	public static Vector2f lerp(Vector2f a, Vector2f b, float r){
		float ax = a.x, ay = a.y;
		float bx = b.x, by = b.y;
		return new Vector2f(ax + (bx - ax) * r, ay + (by - ay) * r);
	}
	
	public static float lerp(float a, float b, float r){
		return a + (b - a) * r;
	}
	
	public static Vector2f clamp(Vector2f i, float low, float high){
		return new Vector2f(java.lang.Math.max (java.lang.Math.min (i.x, high), low),
							java.lang.Math.max (java.lang.Math.min (i.y, high), low));
	}
	
	public static double clamp (double i, double low, double high) {
		return java.lang.Math.max (java.lang.Math.min (i, high), low);
	}
	
	
	public static float clamp (float i, float low, float high) {
		return java.lang.Math.max (java.lang.Math.min (i, high), low);
	}
	
	
	public static int clamp (int i, int low, int high) {
		return java.lang.Math.max (java.lang.Math.min (i, high), low);
	}
	
	
	public static long clamp (long i, long low, long high) {
		return java.lang.Math.max (java.lang.Math.min (i, high), low);
	}
	
	public static float distanceToEntity(Entity s, Entity t){
		Vector2f source = s.getPosition();
		Vector2f target = t.getPosition();
		return source.distanceSquared(target);
	}
	
	public static float getRotationToFaceTarget(Entity source, Entity target){
		float r = (float) (Math.atan2(target.getCenterX() - source.getCenterX(), target.getCenterY() - source.getCenterY()) * (-180 / Math.PI));
		if(r < 0) r += 360;
		if(r > 360) r -= 360;
		return r;
	}
	
	
	
	public static Vector2f calcMinTranslationDistance(Rectangle rect1, Rectangle rect2){
		
		float difference;
		float minTranslateDistance;
		short axis;
		short side;
		Vector2f translatedDifference = new Vector2f(0,0);
		
		// Left
		difference = (rect1.getX() + rect1.getWidth()) - (rect2.getX());
		minTranslateDistance = difference;
		axis = 0;
		side = -1;
		
		// Right
		difference = (rect2.getX() + rect2.getWidth()) - rect1.getX();
		if(difference < minTranslateDistance){
			minTranslateDistance = difference;
			axis = 0;
			side = 1;
		}
		
		// Down
		difference = (rect1.getY() + rect1.getHeight()) - rect2.getY();
		if(difference < minTranslateDistance){
			minTranslateDistance = difference;
			axis = 1;
			side = -1;
		}
		
		// Up
		difference = (rect2.getY() + rect2.getHeight()) - rect1.getY();
		if(difference < minTranslateDistance){
			minTranslateDistance = difference;
			axis = 1;
			side = 1;
		}
		
		// Y
		if(axis == 1)	
			translatedDifference.y = side * minTranslateDistance;
		// X
		else
			translatedDifference.x = side * minTranslateDistance;
		
		return translatedDifference;
	}
}
