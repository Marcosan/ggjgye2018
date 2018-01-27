package ggl.gye.transmission.entities;

import com.badlogic.gdx.Gdx;
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

public class Banner extends Image {
    private Hashtable<String, Float[]> areas;
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

    public Banner(Texture name, Crono crono){
        //textureBanner = new Texture(Gdx.files.internal(name));
        super(name);
        this.crono = crono;

        areas = new Hashtable<String, Float[]>();
        fillAreas();
        keys = areas.keySet();

        //Obtaining iterator over set entries
        itr = keys.iterator();
    }

    //[0] x1, [1] x2 . [2] y1, [3] y2 -> x1<x2
    public void fillAreas(){
        areas.put("FIEC", new Float[]{      5440f,5664f,
                                            5920f, 6080f});

        areas.put("CELEX", new Float[]{     5665f,5888f,
                5920f, 6080f});

        areas.put("Banco", new Float[]{     5889f,5984f,
                5920f, 6080f});
    }

    public void drawBanner(Batch batch, float posX, float posY){

        batch.begin();

        while (itr.hasNext()) {
            // Getting Key
            str = itr.next();
            x1 = areas.get(str)[0];
            x2 = areas.get(str)[1];
            y1 = areas.get(str)[2];
            y2 = areas.get(str)[3];
            //System.out.println(posX + " - " + posY);
            if(posX < x2 && posX > x1){
                if (posY < y2 && posY > y1){
                    System.out.println(str);
                    if(str != actualStr){
                        System.out.println("cambio de lugar");
                        dibuja = true;
                        isCont = true;
                    }
                    if (dibuja) {
                        draw(batch, 1);
                        setPosition(posX - 170, posY + 70);
                        if (isCont) {
                            setDrawable(new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("img/banners/" + str + ".png")))));
                            tIni = crono.getNuSeg();
                            isCont = false;
                        }
                        actualStr = str;
                    }



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

    //public boolean



}
