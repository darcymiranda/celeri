package com.heavydose.client.game.gui;

import org.newdawn.slick.geom.Vector2f;

import com.heavydose.client.Cache;


public class Overlay extends Component {

	public Overlay(String name, Vector2f position) {
		super(name, position, 0, 0);
		setImage(Cache.images.get("ui_overlay"), true);
	}

}
