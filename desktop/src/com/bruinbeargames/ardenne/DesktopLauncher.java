package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bruinbeargames.ardenne.ardenne;

import java.io.FileOutputStream;
import java.io.PrintStream;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		Graphics.DisplayMode mode = Lwjgl3ApplicationConfiguration.getDisplayMode();

		// Full ScreenGame
		//config.setFullscreenMode(mode);

		// Windowed Version
	//	DoRedirectConsole();


		config.setWindowedMode(mode.width, mode.height);
		config.setWindowPosition(1, 1);
		config.setResizable(false);
		config.useVsync(true);
		config.setDecorated(false);
		config.setWindowIcon(Files.FileType.Internal, "effects/desktopicon.png");
		new Lwjgl3Application(new ardenne(), config);

	}

	private static void DoRedirectConsole() {
		// This pipes everything from stdout/stdout into output file, for debugging.
		PrintStream printStream = null;

		PrintStream originalOut = System.out;
		PrintStream originalErr = System.err;

		try {
			printStream = new PrintStream(new FileOutputStream("debuglog.txt"), true, "UTF-8");
			System.setOut(printStream);
			System.setErr(printStream);

		} catch (Exception e) {

			e.printStackTrace();


			// If there are problems, close it and go back to console output
			if (printStream != null) {
				printStream.flush();
				printStream.close();
			}
			System.setOut(originalOut);
			System.setErr(originalErr);
		}


	}
}
