package ggl.gye.transmission.entities;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by root on 19/07/17.
 */

public class TouchPadPlayer {
    private TouchpadStyle touchpadStyle;
    private Drawable touchBackground;
    private Drawable touchKnob;
    private Touchpad touchpad;
    private Stage stage;
    private Viewport viewport;
    private Skin touchpadSkin;

    private void createTouchpad() {
        //Create a touchpad skin
        /*touchpadSkin = new Skin();
        viewport = new FillViewport(camera.viewportWidth, camera.viewportHeight, camera);
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("img/touchpad/touchBackground.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("img/touchpad/touchKnob.png"));
        //Create TouchPad Style
        touchpadStyle = new Touchpad.TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(15, 15, 200, 200);

        //Create a Stage and add TouchPad
        stage = new Stage(viewport , renderer.getBatch());
        //stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, spritebatch);
        stage.addActor(touchpad);
        Gdx.input.setInputProcessor(stage);
*/
    }
}
