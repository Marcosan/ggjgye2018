package ggl.gye.transmission.entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Hashtable;

public class Player implements InputProcessor{

    private static float CONST = 2;
    /** the movement velocity */
    private Vector2 velocity = new Vector2();

    private float speed = 2 , gravity = 0, increment;
    private float wPer = 0.3f, hPer = 0.3f;

    private boolean canJump=false;

    private boolean movimiento=false;

    private Hashtable<String, Animation> animation;
    private Array<TiledMapTileLayer> collisionLayers;
    private TiledMapTileLayer collisionLayer;

    private String blockedKey = "blocked";

    private int wMap, hMap, wCam, hCam;
    private float wScreen, hScreen, pointX, pointY;
    private PlayerState state = PlayerState.STANDING;
    private Direction direction = Direction.STILL;
    private float width = 0, height = 0;
    private String name;

    private float stateTime = 0f;

    private Animation currentAnimation;
    private int touchX, touchY;
    private float oldX, oldY;

    public Player(String name, int pointX, int pointY, int width, int height,
                  Hashtable<String, Animation> animation, Array<TiledMapTileLayer> collisionLayer, int wMap, int hMap, int wCam, int hCam) {
        //super((TextureRegion) animation.get("still").getKeyFrame(0));
        this.wMap = wMap;
        this.hMap = hMap;
        this.wCam = wCam;
        this.hCam = hCam;
        this.animation = animation;
        this.collisionLayers = collisionLayer;
        //setSize(collisionLayer.getWidth(), collisionLayer.getHeight() );
        setPosition(pointX, pointY);
        setSize(width,height);
        wScreen = Gdx.graphics.getWidth();
        hScreen = Gdx.graphics.getHeight();

        this.name = name;
        stateTime = 0f;
    }


    public void update(float delta) {
        if (movimiento){
            stateTime += Gdx.graphics.getDeltaTime();
        }
        switch(state) {
            case STANDING:
                if(currentAnimation != animation.get("still"))
                    currentAnimation = animation.get("still");
                break;
            case MOVING:
                switch (direction){
                    case RIGHT:
                        currentAnimation = animation.get("right");
                        //System.out.println("derecha");
                        break;
                    case LEFT:
                        currentAnimation = animation.get("left");
                        //System.out.println("izquierda");
                        break;
                    case STILL:
                        currentAnimation = animation.get("still");
                        //System.out.println("abajo");
                        break;
                    case UP:
                        currentAnimation = animation.get("up");
                        //System.out.println("arriba");
                        break;
                }

                break;
            case ACTING:
                //Animation anim = new Animation(0.06f, Utils.loadTextureAtlas(currentAction.getName(), "textures/characters/animations/" + getName() + "/").getRegions());
                if(currentAnimation != animation.get("still"))
                    currentAnimation = animation.get("still");
                break;
        }


    }

    public void render(float delta, Batch batch, float x, float y) {
        TextureRegion currentFrame = (TextureRegion) currentAnimation.getKeyFrame(stateTime, true);
        moveCharacterY(x, y);


        moveCharacterX(x, y);
        for (TiledMapTileLayer collision: collisionLayers) {
            collisionLayer = collision;
            //System.out.println(collisionLayer.toString());
            collisionCharacterY();
            collisionCharacterX();
        }



        batch.begin();

        batch.draw(currentFrame, pointX, pointY, width, height);

        batch.end();


    }

    private void moveCharacterY(float x, float y) {
        //Para que no se salga del mapa
        if (getY() >= hMap || getY() <= 0){
            //System.out.println("player: "+getY() + ", point: " + relationY);
            velocity.y = 0;
        }

        //se detiene en el punto marcado velocity.y = 0
        if (!movimiento){
            velocity.y = 0;
        }else if(isDown(x,y)){
            //abajo
            oldY = getY();
            direction = Direction.STILL;
            velocity.y = speed * y;
        }else if(isUp(x,y)){
            //arriba
            oldY = getY();
            direction = Direction.UP;
            velocity.y = speed * y;
        } else {
            velocity.y = 0;
        }

        // move on y
        setPosition(getX(),getY() + velocity.y);

    }

    private void moveCharacterX(float x, float y) {
        //Para que no se salga del mapa
        if (getX() >= wMap || getX() <= 0){
            velocity.x = 0;
        }
        //se detiene en el punto marcado velocity.x = 0
        if (!movimiento){
            velocity.x = 0;
        }else if(isRight(x,y)){
            //derecha
            oldX = getX();
            direction = Direction.RIGHT;
            velocity.x = speed * x;
        }else if(isLeft(x,y)){
            //izquierda
            oldX = getX();
            direction = Direction.LEFT;
            velocity.x = speed *x;
        } else {
            velocity.x = 0;
        }
        setPosition(getX() + velocity.x,getY());
    }

    private boolean isRight(float x, float y) {
        boolean cond1 = false, cond2 = false, cond = false;
        if (x > 0 && y > 0){
            float y1 = fRect(x, CONST);
            if(y < y1) cond1 = true;
        }
        if(x > 0 && y < 0){
            float y2 = fRect(x, -CONST);
            if(y > y2) cond2 = true;
        }
        if (cond1 || cond2) cond = true;
        return cond;
    }

    private boolean isLeft(float x, float y) {
        boolean cond1 = false, cond2 = false, cond = false;
        if (x < 0 && y > 0){
            float y2 = fRect(x, -CONST);
            if(y < y2) cond1 = true;
        }
        if(x < 0 && y < 0){
            float y1 = fRect(x, CONST);
            if(y > y1) cond2 = true;
        }
        if (cond1 || cond2) cond = true;
        return cond;
    }

    private boolean isUp(float x, float y) {
        boolean cond1 = false, cond2 = false, cond = false;
        if (x > 0 && y > 0){
            float y1 = fRect(x, (1/CONST));
            if(y > y1) cond1 = true;
        }
        if(x < 0 && y > 0){
            float y2 = fRect(x, -(1/CONST));
            if(y > y2) cond2 = true;
        }
        if (cond1 || cond2) cond = true;
        return cond;
    }

    private boolean isDown(float x, float y) {
        boolean cond1 = false, cond2 = false, cond = false;
        if (x > 0 && y < 0){
            float y2 = fRect(x, -(1/CONST));
            if(y < y2) cond1 = true;
        }
        if(x < 0 && y < 0){
            float y1 = fRect(x, (1/CONST));
            if(y < y1) cond2 = true;
        }
        if (cond1 || cond2) cond = true;
        return cond;
    }

    private float fRect(float x, float n){
        return n * x;
    }

    private void collisionCharacterX(){
        // save old position

        boolean collisionX = false;

        // calculate the increment for step in #collidesLeft() and #collidesRight()
        increment = collisionLayer.getTileWidth();
        increment = getWidth() < increment ? getWidth() / 2 : increment / 2;

        if(velocity.x < 0) // going left
            collisionX = collidesLeft();
        else if(velocity.x > 0) // going right
            collisionX = collidesRight();

        // react to x collision
        if(collisionX) {
            setX(oldX);
            velocity.x = 0;
            movimiento = false;
        }
    }

    private void collisionCharacterY(){

        boolean collisionY = false;
        // calculate the increment for step in #collidesBottom() and #collidesTop()
        increment = collisionLayer.getTileHeight();
        increment = getHeight() < increment ? getHeight() / 2 : increment / 2;
        if(velocity.y < 0) // going down
            canJump = collisionY = collidesBottom();
        else if(velocity.y > 0) // going up
            collisionY = collidesTop();

        // react to y collision
        if(collisionY) {
            setY(oldY);
            velocity.y = 0;
            movimiento = false;
        }
    }

    private boolean isCellBlocked(float x, float y) {
        Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey(blockedKey);
    }

    private boolean collidesRight() {
        for(float step = 0; step <= getHeight(); step += increment)
            if(isCellBlocked(getX() + getWidth(), getY() + step))
                return true;
        return false;
    }

    private boolean collidesLeft() {
        for(float step = 0; step <= getHeight(); step += increment)
            if(isCellBlocked(getX(), getY() + step))
                return true;
        return false;
    }

    private boolean collidesTop() {
        for(float step = 0; step <= getWidth(); step += increment)
            if(isCellBlocked(getX() + step, getY() + getHeight()))
                return true;
        return false;

    }

    private boolean collidesBottom() {
        for(float step = 0; step <= getWidth(); step += increment)
            if(isCellBlocked(getX() + step, getY()))
                return true;
        return false;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }


    public boolean isMovimiento() {
        return movimiento;
    }

    public void setMovimiento(boolean movimiento) {
        this.movimiento = movimiento;
    }


    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Direction getDirection() {
        return direction;
    }

    public PlayerState getState() {
        return state;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public float getX() {
        return pointX;
    }

    public float getY() {
        return pointY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setX(float x) {
        this.pointX = x;
    }

    public void setY(float y) {
        this.pointY = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    private void setSize(float w, float h) {
        this.width = w;
        this.height = h;
    }

    private void setPosition(float x, float y) {
        this.pointX = x;
        this.pointY = y;
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
}