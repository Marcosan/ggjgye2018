package ggl.gye.transmission;

import com.badlogic.gdx.Game;

import ggl.gye.transmission.entities.NPCState;
import ggl.gye.transmission.screens.Biblioteca;
import ggl.gye.transmission.screens.Cafeteria;
import ggl.gye.transmission.screens.Inicio;
import ggl.gye.transmission.screens.Play;
import ggl.gye.transmission.screens.Room01;
import ggl.gye.transmission.screens.Score;

public class MainGame extends Game {
	private Play playScreen;
	private Room01 room01Screen;
	private Cafeteria cafeteriaScreen;
	private Biblioteca biblioScreen;
	private Inicio iniScreen;
	private Score scoreScreen;


	private String actualScreen = "general";
	private boolean canPassScreen = false;
	@Override
	public void create () {
		playScreen = new Play("Base", NPCState.HIGH);
		cafeteriaScreen = new Cafeteria("Cafeteria", NPCState.HIGH);
		biblioScreen = new Biblioteca("Biblio", NPCState.HIGH);
		iniScreen = new Inicio("Inicio", NPCState.HIGH);
		scoreScreen = new Score("Score", NPCState.HIGH);

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
			biblioScreen.setCanPass(false);
			cafeteriaScreen.setCanPass(false);
			iniScreen.setCanPass(false);
			scoreScreen.setCanPass(false);
			canPassScreen = false;
			setScreen(playScreen);
		} else {

			if (actualScreen == "biblio" && canPassScreen) {
				playScreen.setCanPass(false);
				biblioScreen.setCanPass(false);
				cafeteriaScreen.setCanPass(false);
				iniScreen.setCanPass(false);
				scoreScreen.setCanPass(false);
				canPassScreen = false;
				setScreen(biblioScreen);
			}else {
				if (actualScreen == "cafe" && canPassScreen) {
					playScreen.setCanPass(false);
					biblioScreen.setCanPass(false);
					cafeteriaScreen.setCanPass(false);
					iniScreen.setCanPass(false);
					scoreScreen.setCanPass(false);
					canPassScreen = false;
					setScreen(cafeteriaScreen);
				} else {
					if (actualScreen == "score" && canPassScreen) {
						playScreen.setCanPass(false);
						biblioScreen.setCanPass(false);
						cafeteriaScreen.setCanPass(false);
						iniScreen.setCanPass(false);
						scoreScreen.setCanPass(false);
						canPassScreen = false;
						setScreen(scoreScreen);
					}else {
						if (actualScreen == "inicio" && canPassScreen) {
							playScreen.setCanPass(false);
							biblioScreen.setCanPass(false);
							cafeteriaScreen.setCanPass(false);
							iniScreen.setCanPass(false);
							scoreScreen.setCanPass(false);
							canPassScreen = false;
							setScreen(iniScreen);
						}
					}
				}
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
