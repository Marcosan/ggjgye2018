package ggl.gye.transmission;

import com.badlogic.gdx.Game;

import ggl.gye.transmission.entities.NPCState;
import ggl.gye.transmission.screens.Play;
import ggl.gye.transmission.screens.Room01;

public class MainGame extends Game {
	private Play playScreen;
	private Room01 room01Screen;
	private String actualScreen = "general";
	private boolean canPassScreen = false;
	@Override
	public void create () {
		playScreen = new Play("eltroestedeaquiveras", NPCState.HIGH);
		room01Screen  = new Room01("ggjcity");
		setScreen(playScreen);
	}

	@Override
	public void render () {
		super.render();
		actualScreen = this.getScreen().toString();

		if (playScreen.canPass() || room01Screen.canPass() ) {

			canPassScreen = true;
		}
		try {
			setScreen();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setScreen() throws InterruptedException {
		if(actualScreen == "general" && canPassScreen){
			playScreen.setCanPass(false);
			room01Screen.setCanPass(false);
			canPassScreen = false;
			setScreen(playScreen);
		} else {

			if (actualScreen == "room01" && canPassScreen) {
				playScreen.setCanPass(false);
				room01Screen.setCanPass(false);
				canPassScreen = false;
				setScreen(room01Screen);
			}
		}

	}
	
	@Override
	public void dispose () {
		super.dispose();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
