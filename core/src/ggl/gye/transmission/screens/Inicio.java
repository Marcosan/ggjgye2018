package ggl.gye.transmission.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Hashtable;

import ggl.gye.transmission.entities.Banner;
import ggl.gye.transmission.entities.Crono;
import ggl.gye.transmission.entities.NPC;
import ggl.gye.transmission.entities.NPCState;
import ggl.gye.transmission.entities.Player;
import ggl.gye.transmission.entities.PlayerState;
import ggl.gye.transmission.entities.Tinder;

/**
 * Created by dell on 10/04/17.
 */

public class Inicio implements Screen {
    private static final int CAM_SIZE_X = 450;
    private static final int CAM_SIZE_Y = 350;
    private static final int NUM_NPC = 30;
    private final String name_map;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private TextureAtlas playerAtlas;
    private Player player;

    private int[] floor1 = new int[] {0}, floor2 = new int[] {1}, floor3 = new int[] {2}, floor4 = new int[] {3},
            wall1 = new int[] {4}, wall2 = new int[] {5}, wall3 = new int[] {6};

    private ShapeRenderer sr;

    private int wMap, hMap, wScreen, hScreen;


    private Crono crono;
    private int radio;

    private String goToScreen = "inicio";
    private boolean isAuthorized = false;
    private NPCState local_probability;
    private boolean drawTinder;

    public Inicio(String name, NPCState local_probability) {
        this.local_probability = local_probability;
        this.name_map = name;
    }


    @Override
    public void show() {
        map = new TmxMapLoader().load("maps/juego/" + name_map +".tmx");
        wMap = Integer.parseInt(map.getProperties().get("width").toString()) * Integer.parseInt(map.getProperties().get("tilewidth").toString());
        hMap = Integer.parseInt(map.getProperties().get("height").toString()) * Integer.parseInt(map.getProperties().get("tileheight").toString());

        wScreen = Gdx.graphics.getWidth();
        hScreen = Gdx.graphics.getHeight();

        renderer = new OrthogonalTiledMapRenderer(map);
        sr = new ShapeRenderer();
        sr.setColor(Color.CYAN);

        camera = new OrthographicCamera();
        camera.setToOrtho(false,CAM_SIZE_X, CAM_SIZE_Y);


        crono=new Crono();
        crono.start();

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        renderer.setView(camera);
        //Gdx.input.getX()

        if(Gdx.input.isTouched()) {
            goToScreen = "general";
        }

    }

    @Override
    public void resize(int width, int height) {
        //camera.viewportWidth = width;
        //camera.viewportHeight = height;
        camera.position.set(0, 0, 0); //by default camera position on (0,0,0)
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        sr.dispose();
        playerAtlas.dispose();
    }

    @Override
    public String toString(){
        return goToScreen;
    }

    public boolean canPass(){
        return isAuthorized;
    }

    public void setCanPass(boolean p){
        this.isAuthorized = p;
    }
}
