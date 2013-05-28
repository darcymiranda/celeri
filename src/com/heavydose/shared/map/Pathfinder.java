package com.heavydose.shared.map;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;

import com.heavydose.shared.Entity;
import com.heavydose.util.Point;

public class Pathfinder {
	
	private ArrayList<Node> open;
	private ArrayList<Node> closed;
	private Node[][] nodes;
	
	private TileMap map;
	private int width, height;
	private int tileWidth, tileHeight;
	
	public Pathfinder(TileMap map){
		
		this.map = map;
		
		width = map.getWidth();
		height = map.getHeight();
		tileWidth = map.getTileWidth();
		tileHeight = map.getTileHeight();
		
		open = new ArrayList<Node>();
		closed = new ArrayList<Node>();
		
		initialize();
		
	}
	
	public Path constructPath(Entity start, Entity target, int distance){
		
		if(start == null || target == null) return new Path();
		
		Vector2f startPos = start.getCenterPosition(),
				 targetPos = target.getCenterPosition();
		
		int startTileX = (int) startPos.x / tileWidth,
		    startTileY = (int) startPos.y / tileHeight,
		    targetTileX = (int) targetPos.x / tileWidth,
		    targetTileY = (int) targetPos.y / tileHeight;
		
		// ignore entities that are already on the same tile or out of bounds
		if(((startTileX == targetTileX) && (startTileY == targetTileY)) ||
			(targetTileX < 0 || targetTileX > width - 1 || targetTileY < 0 || targetTileY > height - 1))
			return new Path();
		
		if((startTileX < 0 || startTileX > width - 1 || startTileY < 0 || startTileY > height - 1))
			return new Path();
		
		reset(startTileX + distance, startTileY + distance);
		
		Node startNode = nodes[startTileX][startTileY],
		     targetNode = nodes[targetTileX][targetTileY];
		
		startNode.open = true;
		startNode.distanceToGoal = heuristic(startNode.position, targetNode.position);
		startNode.distanceTraveled = 0;
		
		open.add(startNode);
		
		while(open.size() > 0){
			
			Node curNode = findNearestNode();
			
			// no nodes left
			if(curNode == null || curNode.neighbors == null) {
				break;
			}
			
			// target found
			if(curNode == targetNode)
				return findFinalPath(startNode, targetNode);
			
			for(int i = 0; i < curNode.neighbors.length; i++){
				
				Node neighbor = curNode.neighbors[i];
				if(neighbor == null || neighbor.isWalkable == false)
					continue;
				
				// don't cut corners
				int sx = curNode.position.x;
				int sy = curNode.position.y;
				if(neighbor.position.x - sx != 0 && neighbor.position.y - sy != 0){
					if(map.isTileBlocked(neighbor.position.x, sy) || map.isTileBlocked(sx, neighbor.position.y)){
						continue;
					}
				}
				
				float distanceTraveled = curNode.distanceTraveled + 1;
				float heuristic = heuristic(neighbor.position, targetNode.position);
				
				// undiscovered neighbor
				if(neighbor.open == false && neighbor.closed == false){
					
					neighbor.distanceTraveled = distanceTraveled;
					neighbor.distanceToGoal = distanceTraveled + heuristic;
					neighbor.parent = curNode;
					neighbor.open = true;
					open.add(neighbor);
					
				}
				// a better choice neighbor
				else if(neighbor.open || neighbor.closed){
					
					if(neighbor.distanceTraveled > distanceTraveled){
						
						neighbor.distanceTraveled = distanceTraveled;
						neighbor.distanceToGoal = distanceTraveled + heuristic;
						neighbor.parent = curNode;
						
					}
					
				}
				
			}
			
			open.remove(curNode);
			curNode.closed = true;
			
		}
		
		System.out.println("NO PATH FOUND");
		
		// no path found
		return new Path();
			
		
	}
	
	private void initialize(){
	
		nodes = new Node[width][height];
		
		// init the nodesd
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++){
				
				Node node = new Node();
				node.position = new Point(x,y);
				node.isWalkable = !map.isTileBlocked(x, y);
				
				if(node.isWalkable){
					node.neighbors = new Node[8];
				}
				nodes[x][y] = node;
				
			}
		
		// init the node's neighbors
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++){
				
				Node node = nodes[x][y];
				if(node == null || node.isWalkable == false) continue;
				
				Point[] neighborPoints = {
					new Point(x - 1, y),
					new Point(x + 1, y),
					new Point(x, y - 1),
					new Point(x, y + 1),
					new Point(x + 1, y + 1),
					new Point(x - 1, y - 1),
					new Point(x - 1, y + 1),
					new Point(x + 1, y - 1)
				};
				
				// assign the neighbors
				for(int i = 0; i < neighborPoints.length; i++){
					
					Point p = neighborPoints[i];
					if(p.x < 0 || p.x > width - 1 || p.y < 0 || p.y > height - 1)
						continue;
					
					Node neighbor = nodes[p.x][p.y];
					if(neighbor == null || neighbor.isWalkable == false) continue;
					
					node.neighbors[i] = neighbor;
					
				}
				
			}
	
	}
	
	private Path findFinalPath(Node sNode, Node eNode){
		
		closed.add(sNode);
		
		Node pNode = eNode.parent;
		
		while(pNode != sNode){
			closed.add(pNode);
			pNode = pNode.parent;
		}
		
		Path path = new Path();
		for(int i = closed.size() - 1; i > -1; i--){
			Node step = closed.get(i);
			path.appendStep((int)step.position.x, (int)step.position.y);
		}
		
		path.removeStep(path.getSize()-1);
		
		return path;
		
	}
	
	private Node findNearestNode(){
		
		float smallestDistanceToGoal = Float.MAX_VALUE;
		Node curNode = open.get(0);
		
		for(int i = 0; i < open.size(); i++){
			
			if(open.get(i).distanceToGoal < smallestDistanceToGoal){
				
				curNode = open.get(i);
				smallestDistanceToGoal = curNode.distanceToGoal;
				
			}
			
		}
		
		return curNode;
		
	}
	
	private float heuristic(Point p1, Point p2){
		return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
	}
	
	private void reset(int xd, int yd){
		
		if(xd > width) xd = width;
		if(yd > height) yd = height;

		open.clear();
		closed.clear();
		
		int x1 = Math.max(-xd, 0);
		int y1 = Math.max(-yd, 0);
		
		for(int x = x1; x < xd; x++)
			for(int y = y1; y < yd; y++){
				
				Node node = nodes[x][y];
				if(node == null) continue;
				
				node.open = false;
				node.closed = false;
				node.distanceToGoal = Float.MAX_VALUE;
				node.distanceTraveled = Float.MAX_VALUE;
				
			}
				
	}
	
    private class Node {

        public Node parent;
        public Node[] neighbors;
        public boolean isWalkable;
        public boolean closed;
        public boolean open;
        public Point position;

        //public float heuristic;
        public float distanceToGoal;
        public float distanceTraveled;

    }

}
