package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bruinbeargames.ardenne.ardenne;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		Graphics.DisplayMode mode = Lwjgl3ApplicationConfiguration.getDisplayMode();

		// Full ScreenGame
		//config.setFullscreenMode(mode);

		// Windowed Version


		config.setWindowedMode(mode.width, mode.height);
		config.setWindowPosition(-1, 25);
		config.setResizable(false);
		config.useVsync(true);
		config.setDecorated(false);
//		config.setWindowIcon(Files.FileType.Internal , "effects/desktopicon.png");
		new Lwjgl3Application(new ardenne(), config);

	}
}
