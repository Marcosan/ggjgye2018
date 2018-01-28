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

public class Biblioteca implements Screen {
    private static final int CAM_SIZE_X = 450;
    private static final int CAM_SIZE_Y = 350;
    private static final int NUM_NPC = 30;
    private static final int posIniX = 30*32, posIniY = 70*32, wPlayer = 25, hPlayer = 30;
    private static final int wBanner = 370, hBanner= 70;
    private static final int wTinder= 250, hTinder= 200;
    private static final int wIcon= 15, hIcon= 15;
    private static final int RADIO = 32*3;
    private static final int SEC_SEARCH = 32*3;
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

    private int touchX, touchY;
    private int velocidad = 6;
    //private int posIniX = 100, posIniY = 500;


    //NPCs
    private ArrayList<NPC> npcList;
    private ArrayList<Integer> npc_around;
    private ArrayList<Integer> npc_definitivamente_infectados;

    //TouchPad
    private TouchpadStyle touchpadStyle;
    private Drawable touchBackground;
    private Drawable touchKnob;
    private Touchpad touchpad;
    private Stage stage;
    private Viewport viewport;
    private Skin touchpadSkin;
    private Array<TiledMapTileLayer> collisionLayers;

    private Image button_tinder, icon_status, icon_infect;
    private Banner imageBanner;
    private Tinder imageTinder;
    private Crono crono;
    private int radio;

    private String goToScreen = "biblio";
    private boolean isAuthorized = false;
    private NPCState local_probability;
    private boolean drawTinder;

    public Biblioteca(String name, NPCState local_probability) {
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

        npcList = new ArrayList<NPC>(NUM_NPC);
        npc_around = new ArrayList<Integer>();
        npc_definitivamente_infectados = new ArrayList<Integer>();

        crono=new Crono();
        crono.start();

        //create banner
        imageBanner = new Banner(new Texture(Gdx.files.internal("badlogic.jpg")), crono);
        imageBanner.setSize(wBanner,hBanner);

        imageTinder = new Tinder(new Texture(Gdx.files.internal("badlogic.jpg")), crono);
        imageTinder.setSize(wTinder,hTinder);

        icon_status = new Image(new Texture(Gdx.files.internal("img/tinder/status1.png")));
        icon_status.setSize(wIcon, hIcon);

        button_tinder = new Image(new Texture(Gdx.files.internal("img/tinder/buttontinder.png")));
        button_tinder.setSize(wIcon+40, hIcon+40);

        icon_infect = new Image(new Texture(Gdx.files.internal("img/tinder/infectado.png")));
        icon_infect.setSize(wIcon, hIcon);

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
        collisionLayers.add((TiledMapTileLayer) map.getLayers().get("Arboles"));
        collisionLayers.add((TiledMapTileLayer) map.getLayers().get("Edificios"));
        collisionLayers.add((TiledMapTileLayer) map.getLayers().get("autos"));

        player = new Player("payer", posIniX, posIniY, wPlayer, hPlayer,
                animation, collisionLayers, wMap, hMap, CAM_SIZE_X, CAM_SIZE_Y);

        for (int i = 0; i < NUM_NPC; i++){
            npcList.add(i, new NPC("npc" + i, posIniX, posIniY, wPlayer, hPlayer,
                    animation, collisionLayers, wMap, hMap, CAM_SIZE_X, CAM_SIZE_Y, crono, 2+i,
                    NPCState.MEDIUM));
        }
        createTouchpad();
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
        renderer.render(floor3);
        renderer.render(floor4);

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
        stage.act();
        stage.draw();



        imageBanner.drawBanner(renderer.getBatch(), player.getX(), player.getY());


        //Gdx.input.getX()

        if(Gdx.input.isTouched()){
            //System.out.println( Gdx.input.getX());
            if (player.getX() < 40 && player.getX() > 0)
                if (player.getY() < 40 && player.getY() > 0) {
                    isAuthorized = true;
                    goToScreen = "room01";
                }
            camera.position.set(player.getX(), player.getY(), 0);
        }
        renderer.getBatch().begin();

        if (crono.getNuSeg() % 1 == 0){
            for (int i = 0; i < npcList.size(); i++){
                float x_npc = npcList.get(i).getX();
                float y_npc = npcList.get(i).getY();

                if (npcList.get(i).getInfectado()){
                    icon_infect.draw(renderer.getBatch(), 1);
                    icon_infect.setBounds(x_npc + 28, y_npc, wIcon, hIcon);
                }


                if (inArea(player.getX(), player.getY(), x_npc, y_npc)){
                    npc_around.add(i);
                    //System.out.println("El personaje " + i + " esta cerca");
                    icon_status.draw(renderer.getBatch(), 1);
                    icon_status.setBounds(x_npc + 28, y_npc + 30, wIcon, hIcon);
                }
            }
        }

        if (crono.getNuSeg() % 5 == 0){
            int max=npc_around.size()-1;
            int min=0;
            int result = min + (int)(Math.random() * ((max - min) + 1));
            NPC tmp_npc = npcList.get(npc_around.get(result));
            if (!tmp_npc.getDefinitivamenteInfectado()){
                tmp_npc.setInfectado();
            }

        }

        createButton();

        renderer.getBatch().end();
        if(Gdx.input.justTouched()){
            float touch_x = Gdx.input.getX();
            float touch_y = Gdx.input.getY();
            System.out.println(touch_x);
            if (touch_x >= 160 && touch_x <= 300){
                if (touch_y >= 800 && touch_y <= 960){
                    if (drawTinder == false)
                        drawTinder = true;
                    else
                        drawTinder = false;
                }
            }
        }

        if (drawTinder){
            imageTinder.drawTinder(renderer.getBatch(), player.getX(), player.getY());
        } else {
            imageTinder.remove();
        }

    }

    private void createButton(){
        button_tinder.draw(renderer.getBatch(), 1);
        button_tinder.setBounds(player.getX() - 200, player.getY() - 115, wIcon + 50, hIcon + 50);
    }

    /*
    *   Funcion de circunferencia
    * */
    private boolean inArea(float x_center, float y_center, float x_around, float y_around){
        //if((Math.pow(x - player.getX(),2) + Math.pow(y - player.getY(),2) ) <= radio)
        float a = Math.abs(x_center - x_around);
        float b = Math.abs(y_center - y_around);
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
