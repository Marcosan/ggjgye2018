package ggl.gye.transmission.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by root on 16/08/17.
 */

public class Tinder extends Image implements InputProcessor {
    private Hashtable<String, Float[]> profiles;
    private Set<String> keys;
    private Iterator<String> itr;
    private String str;
    private float x1, x2, y1, y2;
    private Crono crono;
    private int contador = 0;
    private int tIni = 0;
    private boolean dibuja = true;
    private String actualStr = "FIEC";
    private boolean isCont = true;

    public Tinder(Texture name, Crono crono){
        //textureBanner = new Texture(Gdx.files.internal(name));
        super(name);
        this.crono = crono;

        profiles = new Hashtable<String, Float[]>();
        fillProfiles();
        keys = profiles.keySet();

        //Obtaining iterator over set entries
        itr = keys.iterator();
    }

    //[0] x1, [1] x2 . [2] y1, [3] y2 -> x1<x2
    public void fillProfiles(){
        profiles.put("FIEC", new Float[]{      5440f,5664f,
                                            5920f, 6080f});

        profiles.put("CELEX", new Float[]{     5665f,5888f,
                5920f, 6080f});

        profiles.put("Banco", new Float[]{     5889f,5984f,
                5920f, 6080f});
    }

    public void drawTinder(Batch batch, float posX, float posY){

        batch.begin();

        while (itr.hasNext()) {
            // Getting Key
            str = itr.next();
            x1 = profiles.get(str)[0];
            x2 = profiles.get(str)[1];
            y1 = profiles.get(str)[2];
            y2 = profiles.get(str)[3];
            //System.out.println(posX + " - " + posY);
            if(false){
                //System.out.println(str);

                draw(batch, 1);
                setPosition(posX - 170, posY + 70);
                if (isCont) {
                    setDrawable(new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("img/tinder/" + str + ".png")))));
                    tIni = crono.getNuSeg();
                    isCont = false;
                }

                if ((crono.getNuSeg() - tIni) == 3){
                    //System.out.println("dejo de dibujar: " + crono.getNuSeg() + "-" + tIni);
                    dibuja = false;
                    remove();

                }
            }

        }

        itr = keys.iterator();

        batch.end();


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
        System.out.println("screenX: " + screenX + " - screenY: " + screenY + " - pointer: " + pointer + " - button: " + button);
        return true;
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

    //public boolean



}
