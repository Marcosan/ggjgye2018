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

public class Room01 implements Screen {
    private static final int CAM_SIZE_X = 450;
    private static final int CAM_SIZE_Y = 350;
    private static final int NUM_NPC = 0;
    private static final int posIniX = 100, posIniY = 100, wPlayer = 25, hPlayer = 30;
    private static final int wBanner = 370, hBanner= 70;
    private static final int wTinder= 250, hTinder= 200;
    private static final int RADIO = 32*3;
    private final String name_map;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private TextureAtlas playerAtlas;
    private Player player;

    private int[] floor1 = new int[] {0}, floor2 = new int[] {1}, wall1 = new int[] {2}, wall2 = new int[] {3},
            wall3 = new int[] {4}, ceiling1 = new int[] {5}, ceiling2 = new int[] {6};

    private ShapeRenderer sr;

    private int wMap, hMap, wScreen, hScreen;

    private int touchX, touchY;
    private int velocidad = 6;
    //private int posIniX = 100, posIniY = 500;


    //NPCs
    private ArrayList<NPC> npcList;
    private ArrayList<Integer> npc_around;

    //TouchPad
    private TouchpadStyle touchpadStyle;
    private Drawable touchBackground;
    private Drawable touchKnob;
    private Touchpad touchpad;
    private Stage stage;
    private Viewport viewport;
    private Skin touchpadSkin;
    private Array<TiledMapTileLayer> collisionLayers;

    private Image button;
    private Banner imageBanner;
    private Tinder imageTinder;
    private Crono crono;
    private int radio;

    private String goToScreen = "room01";
    private boolean isAuthorized = false;

    public Room01(String name) {
        this.name_map = name;
    }


    @Override
    public void show() {
        map = new TmxMapLoader().load("maps/" + name_map +".tmx");
        wMap = Integer.parseInt(map.getProperties().get("width").toString()) * Integer.parseInt(map.getProperties().get("tilewidth").toString());
        hMap = Integer.parseInt(map.getProperties().get("height").toString()) * Integer.parseInt(map.getProperties().get("tileheight").toString());

        wScreen = Gdx.graphics.getWidth();
        hScreen = Gdx.graphics.getHeight();

        renderer = new OrthogonalTiledMapRenderer(map);
        sr = new ShapeRenderer();
        sr.setColor(Color.CYAN);

        camera = new OrthographicCamera();
        camera.setToOrtho(false,CAM_SIZE_X, CAM_SIZE_Y);

        npcList = new ArrayList<NPC>(NUM_NPC);
        npc_around = new ArrayList<Integer>();

        crono=new Crono();
        crono.start();

        //create banner
        imageBanner = new Banner(new Texture(Gdx.files.internal("badlogic.jpg")), crono);
        imageBanner.setSize(wBanner,hBanner);

        imageTinder = new Tinder(new Texture(Gdx.files.internal("badlogic.jpg")), crono);
        imageTinder.setSize(wTinder,hTinder);



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
        collisionLayers.add((TiledMapTileLayer) map.getLayers().get("Wall03"));
        collisionLayers.add((TiledMapTileLayer) map.getLayers().get("Wall02"));
        collisionLayers.add((TiledMapTileLayer) map.getLayers().get("Wall01"));

        player = new Player("payer", posIniX, posIniY, wPlayer, hPlayer,
                animation, collisionLayers, wMap, hMap, CAM_SIZE_X, CAM_SIZE_Y);

        for (int i = 0; i < NUM_NPC; i++){
            npcList.add(i, new NPC("npc" + i, posIniX, posIniY, wPlayer, hPlayer,
                    animation, collisionLayers, wMap, hMap, CAM_SIZE_X, CAM_SIZE_Y, crono, 2+i,
                    NPCState.HIGH));
        }
        createTouchpad();
    }

    private void createButton(){

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

        renderer.getBatch().end();

        imageBanner.drawBanner(renderer.getBatch(), player.getX(), player.getY());
        imageTinder.drawTinder(renderer.getBatch(), player.getX(), player.getY());

        //Gdx.input.getX()
        //(Gdx.input.isTouched()
        if(Gdx.input.isTouched()){
            //System.out.println( Gdx.input.getX());
            if (player.getX() < 32 && player.getX() > 0)
                if (player.getY() < 32 && player.getY() > 0) {
                    isAuthorized = true;
                    goToScreen = "general";
                }
            camera.position.set(player.getX(), player.getY(), 0);
        }

        if (crono.getNuSeg() % 1 == 0){
            for (int i = 0; i < npcList.size(); i++){
                if (inArea(npcList.get(i).getX(), npcList.get(i).getY())){
                    npc_around.add(i);
                    System.out.println("El personaje " + i + " esta cerca");
                }
            }
        }

    }

    /*
    *   Funcion de circunferencia
    * */
    private boolean inArea(float x, float y){
        //if((Math.pow(x - player.getX(),2) + Math.pow(y - player.getY(),2) ) <= radio)
        float a = Math.abs(player.getX() - x);
        float b = Math.abs(player.getY() - y);
        if ( a <= RADIO &&  b <= RADIO  ) {
            //System.out.println("diferencia: " + (a-b));
            return true;
        }
        return false;
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
