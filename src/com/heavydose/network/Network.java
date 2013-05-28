package com.heavydose.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	
	public static final int PORT = 19555;
	
	public static void register(EndPoint endPoint){
		
		Kryo kyro = endPoint.getKryo();
		kyro.register(Connect.class);
		kyro.register(SpawnEntity.class);
		kyro.register(MoveEntity.class);
		kyro.register(Disconnect.class);
		kyro.register(Shoot.class);
		kyro.register(byte[][].class);
		kyro.register(DownloadMap.class);
		
	}
	
	public static class Connect{
		public int id;
		public String username;
	}
	
	public static class Shoot{
		public boolean shooting;
		public int owner;
	}
	
	public static class SpawnEntity{
		public int owner;
		public int id;
		public int type;
		public float x,y,r;
	}
	
	public static class MoveEntity{
		public int id, type, health;
		public float x,y,xv,yv,r;
		public boolean w,s,d,a;
	}
	
	public static class Disconnect{
		public int id;
	}
	
	public static class DownloadMap{
		public byte[][] map;
		public int width, height;
	}
	
	public static class DeathEntity{
		public int id, killer;
	}

}
