package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;


public class ErrorGame {
	public ErrorGame(String message, Object object) {
		Gdx.app.log(object.toString(), message);
		int fail = 9/0;
	}

	public ErrorGame(String message) {
		ErrorGame errorGame= new ErrorGame(message, this);
	}

}
