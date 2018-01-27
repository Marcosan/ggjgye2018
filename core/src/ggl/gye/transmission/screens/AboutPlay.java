package ggl.gye.transmission.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import ggl.gye.transmission.entities.Player;
import ggl.gye.transmission.entities.PlayerState;

/**
 * Created by dell on 10/04/17.
 */

public class AboutPlay implements InputProcessor, Screen {
    private static final int CAM_SIZE_X = 450;
    private static final int CAM_SIZE_Y = 350;
    private static final int NUM_NPC = 2;
    private static final int posIniX = 2080, posIniY = 1408, wPlayer = 25, hPlayer = 30;
    private static final int wBanner = 370, hBanner= 70;
    private final String name_map;
    //private static final int posIniX = 0, posIniY = 0, wPlayer = 25, hPlayer = 30;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private TextureAtlas playerAtlas;
    private Player player;

    private int[] floor1 = new int[] {0}, floor2 = new int[] {1}, wall1 = new int[] {2}, wall2 = new int[] {3},
            wall3 = new int[] {4}, ceiling1 = new int[] {5}, ceiling2 = new int[] {6};

    private ShapeRenderer sr;

    private int wMap, hMap, wScreen, hScreen;
    private float wPer = 0.3f, hPer = 0.3f;

    private int touchX, touchY;
    private int velocidad = 6;
    //private int posIniX = 100, posIniY = 500;


    private String capa = "Wall 1"; //  Paredes_cercas     paredes

    //NPCs
    private ArrayList<NPC> npcList;

    //TouchPad
    private TouchpadStyle touchpadStyle;
    private Drawable touchBackground;
    private Drawable touchKnob;
    private Touchpad touchpad;
    private Stage stage;
    private Viewport viewport;
    private Skin touchpadSkin;
    private Array<TiledMapTileLayer> collisionLayers;
    private Banner imageBanner;
    private Crono crono;

    public AboutPlay(String name) {
        this.name_map = name;
    }


    @Override
    public void show() {
        map = new TmxMapLoader().load("maps/" + name_map +".tmx"); // FiecFEPOL   mapaprueba
        wMap = Integer.parseInt(map.getProperties().get("width").toString()) * Integer.parseInt(map.getProperties().get("tilewidth").toString());
        hMap = Integer.parseInt(map.getProperties().get("height").toString()) * Integer.parseInt(map.getProperties().get("tileheight").toString());

        wScreen = Gdx.graphics.getWidth();
        hScreen = Gdx.graphics.getHeight();

        renderer = new OrthogonalTiledMapRenderer(map);
        sr = new ShapeRenderer();
        sr.setColor(Color.CYAN);
        //Gdx.gl.glLineWidth(3);

        camera = new OrthographicCamera();
        camera.setToOrtho(false,CAM_SIZE_X, CAM_SIZE_Y);

        npcList = new ArrayList<NPC>(NUM_NPC);

        crono=new Crono();
        crono.start();

        //create banner
        imageBanner = new Banner(new Texture(Gdx.files.internal("badlogic.jpg")), crono);
        //textureBanner = new Texture(Gdx.files.internal("badlogic.jpg"));
        //imageBanner = new Image(textureBanner);
        imageBanner.setSize(wBanner,hBanner);



        playerAtlas = new TextureAtlas("img/player.pack");
        Hashtable<String, Animation> animation = new Hashtable<String, Animation>();
        Animation still, left, right, up;
        still = new Animation(1f / 4f, playerAtlas.findRegions("still"));
        left = new Animation(1f / 4f, playerAtlas.findRegions("left"));
        right = new Animation(1f / 4f, playerAtlas.findRegions("right"));
        up = new Animation(1f / 4f, playerAtlas.findRegions("up"));

        still.setPlayMode(Animation.PlayMode.LOOP);
        left.setPlayMode(Animation.PlayMode.LOOP);
        right.setPlayMode(Animation.PlayMode.LOOP);
        up.setPlayMode(Animation.PlayMode.LOOP);

        animation.put("still", still);
        animation.put("left", left);
        animation.put("right", right);
        animation.put("up", up);

        //Layers para colision
        collisionLayers = new Array<TiledMapTileLayer>();
        collisionLayers.add((TiledMapTileLayer) map.getLayers().get("Wall 3"));
        collisionLayers.add((TiledMapTileLayer) map.getLayers().get("Wall 2"));
        collisionLayers.add((TiledMapTileLayer) map.getLayers().get("Wall 1"));

        player = new Player("payer", posIniX, posIniY, wPlayer, hPlayer,
                animation, collisionLayers, wMap, hMap, CAM_SIZE_X, CAM_SIZE_Y);

        for (int i = 0; i < NUM_NPC; i++){
            npcList.add(i, new NPC("npc" + i, posIniX, posIniY, wPlayer, hPlayer,
                    animation, collisionLayers, wMap, hMap, CAM_SIZE_X, CAM_SIZE_Y, crono, 2+i));
        }
        //player = new Player(still, left, right, (TiledMapTileLayer) map.getLayers().get("Paredes_cercas"), wMap, hMap, CAM_SIZE_X, CAM_SIZE_Y);
        //player.setPosition(11 * player.getCollisionLayer().getTileWidth(), (player.getCollisionLayer().getHeight() - 14) * player.getCollisionLayer().getTileHeight());
        //player.setPosition(posIniX,posIniY);

        //Gdx.input.setInputProcessor(player);
        createTouchpad();
        // ANIMATED TILES



        // frames
        Array<StaticTiledMapTile> frameTiles = new Array<StaticTiledMapTile>(2);
    }

    private void createTouchpad() {
        viewport = new FillViewport(camera.viewportWidth, camera.viewportHeight, camera);
        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("img/touchpad/touchBackground2.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("img/touchpad/touchKnob2.png"));
        //Create TouchPad Style
        touchpadStyle = new TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchPad Style
        touchKnob.setMinHeight(50);
        touchKnob.setMinWidth(50);
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        //Create new TouchPad with the created style
        touchpad = new Touchpad(5, touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(posIniX+100, posIniY-125, 100, 100);

        //Create a Stage and add TouchPad
        stage = new Stage(viewport, renderer.getBatch());
        stage.addActor(touchpad);
        Gdx.input.setInputProcessor(stage);


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        renderer.setView(camera);

        renderer.render(floor1);
        renderer.render(floor2);

        if(touchpad.isTouched()) {
            player.setMovimiento(true);
            player.setState(PlayerState.MOVING);
            //player.moveCharacter(touchpad.getKnobPercentX() * player.getSpeed(), touchpad.getKnobPercentY() * player.getSpeed());
            touchpad.setPosition(player.getX()+100, player.getY()-125);
        } else{
            player.setMovimiento(false);
        }
        player.update(delta);
        player.render(delta, renderer.getBatch(),touchpad.getKnobPercentX(), touchpad.getKnobPercentY());

        for (int i = 0; i < npcList.size(); i++){
            npcList.get(i).update(delta);
            npcList.get(i).render(delta, renderer.getBatch());
        }

        renderer.render(wall1);
        renderer.render(wall2);
        renderer.render(wall3);
        renderer.render(ceiling1);
        renderer.render(ceiling2);
        stage.act();
        stage.draw();

        renderer.getBatch().begin();

        //renderer.getBatch().draw(textureBanner, player.getX() + 100, player.getY() + 100);
        /*if (player.getX() < posIniX - 100){

            imageBanner.draw(renderer.getBatch(), 1);
            imageBanner.setPosition(player.getX() - 100, player.getY() + 100);

            if (crono.getNuSeg() % 2 != 0){

                imageBanner.remove();

            }

            if (crono.getNuSeg() % 4 != 0){
                imageBanner.draw(renderer.getBatch(), 1);

            }
        }


*/
        renderer.getBatch().end();

        imageBanner.drawBanner(renderer.getBatch(), player.getX(), player.getY());

        if(Gdx.input.isTouched()){
            camera.position.set(player.getX(), player.getY(), 0);
            //touchpad.setPosition(player.getX(), player.getY());
        }

    }

    public int[][] getPosNpc(){
        int pos_npc[][] = new int[NUM_NPC][2];
        pos_npc[0] = new int[]{1, 2};
        return pos_npc;
    }

    @Override
    public void resize(int width, int height) {
        //camera.viewportWidth = width;
        //camera.viewportHeight = height;
        camera.position.set(posIniX, posIniY, 0); //by default camera position on (0,0,0)
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
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
