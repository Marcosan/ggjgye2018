package ggl.gye.transmission.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Hashtable;

//public NPC(Hashtable<String, Animation> animation, TiledMapTileLayer collisionLayer, int wMap, int hMap, int wCam, int hCam, NpcWalk npcWalk, Crono crono, int seg_wait) {


public class NPC {

    private static float CONST = 2;
    /** the movement velocity */
    private Vector2 velocity = new Vector2();

    private float speed = 0.5f , gravity = 0, increment;
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

    private Crono crono;
    private int seg_wait;
    private boolean izquierda, derecha, arriba, abajo;

    public NPC(String name, int pointX, int pointY, int width, int height,
               Hashtable<String, Animation> animation, Array<TiledMapTileLayer> collisionLayer, int wMap, int hMap, int wCam, int hCam, Crono crono, int seg_wait) {
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

        this.crono = crono;
        this.seg_wait = seg_wait;


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

    public void render(float delta, Batch batch) {
        TextureRegion currentFrame = (TextureRegion) currentAnimation.getKeyFrame(stateTime, true);

        settingMove();
        moveCharacterY();


        moveCharacterX();
        for (TiledMapTileLayer collision: collisionLayers) {
            collisionLayer = collision;
            collisionCharacterY();
            collisionCharacterX();
        }



        batch.begin();

        batch.draw(currentFrame, pointX, pointY, width, height);

        //batch.draw();

        batch.end();


    }

    private void moveCharacterY() {
        //Para que no se salga del mapa
        if (getY() >= hMap || getY() <= 0){
            //System.out.println("player: "+getY() + ", point: " + relationY);
            velocity.y = 0;
        }

        //se detiene en el punto marcado velocity.y = 0
        if (!movimiento){
            //System.out.println("player: "+getY() + ", point: " + relationY);
            velocity.y = 0;
        }else if(abajo){
            //abajo
            oldY = getY();
            direction = Direction.STILL;
            velocity.y = -speed;
        }else if(arriba){
            //arriba
            oldY = getY();
            direction = Direction.UP;
            velocity.y = speed;
        } else {
            velocity.y = 0;
        }

        // move on y
        setPosition(getX(),getY() + velocity.y);

    }

    private void moveCharacterX() {
        //Para que no se salga del mapa
        if (getX() >= wMap || getX() <= 0){
            velocity.x = 0;
        }
        //se detiene en el punto marcado velocity.x = 0
        if (!movimiento){
            velocity.x = 0;
        }else if(derecha){
            //derecha
            oldX = getX();
            direction = Direction.RIGHT;
            velocity.x = speed;
        }else if(izquierda){
            //izquierda
            oldX = getX();
            direction = Direction.LEFT;
            velocity.x = -speed;
        } else {
            velocity.x = 0;
        }
        //setPosition(getX() + velocity.x * delta,getY());
        setPosition(getX() + velocity.x,getY());
    }

    private void settingMove() {
        if (crono.getNuSeg() % seg_wait != 0){
            //Durante el movimiento
            movimiento = true;
            state = PlayerState.MOVING;
        } else{
            movimiento = false;
            //despues de n segundos
            int signoX = (int) (Math.random() * 3);
            if (signoX != 0)
                signoX = (int) Math.pow((-1), signoX);

            if(signoX < 0){
                izquierda = true;
                derecha = false;
            } else if(signoX > 0){
                derecha = true;
                izquierda = false;
            } else {
                derecha = false;
                izquierda = false;
            }

            int signoY = (int) (Math.random() * 3);
            if (signoY != 0)
                signoY = (int) Math.pow((-1), signoY);

            if(signoY < 0){
                abajo = true;
                arriba = false;
            } else if(signoY > 0){
                arriba = true;
                abajo = false;
            } else {
                abajo = false;
                arriba = false;
            }

        }
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
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
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
}
