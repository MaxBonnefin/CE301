package GameObjects;
import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;

public abstract class GameObject {

    //reusing tilemap code for all gameobject sprites
    protected TileMap tileMap;
    protected int tileSize;
    protected double xMap, yMap;

    public double x, y; // for positions
    protected double dx, dy; //vector

    protected int width, height;

    //collision boundaries
    protected int collisionWidth, collisionHeight;

    //collisions
    protected int currentRow, currentCol;
    protected double xDest, yDest, xTemp, yTemp;
    protected boolean topLeft, topRight, bottomLeft, bottomRight; //for checking the collisions on each corner of the object

    //animation
    //protected Animation animation;//TODO
    protected int currentAction;
    protected int previousAction;

    //movement
    protected boolean left, right, up, down;
    protected boolean dodging;

    protected double moveSpeed;
    protected double maxSpeed;
    protected double stopSpeed;

    public GameObject(TileMap tm){
        tileMap = tm;
        tileSize = tm.getTileSize();
    }

    public boolean intersects(GameObject other){

        return x < other.x + other.width && x + width > other.x && y < other.y + other.height && y + height > other.y;

    }

    public void checkObstacleCollision(){
        currentCol = (int)x / tileSize;
        currentRow = (int)y / tileSize;

        xDest = x + dx;
        yDest = y + dy;

        xTemp = x;
        yTemp = y;

        //checks for collision with obstacles in the x direction
        calculateCorners(xDest, y);
        if(dx < 0){
            if(topLeft||bottomLeft){
                dx = 0;
                xTemp = currentCol * tileSize + collisionWidth / 2;
            }else{
                xTemp += dx;
            }
        }
        if(dx > 0){
            if(topRight||bottomRight){
                dx = 0;
                xTemp = (currentCol + 1)* tileSize - collisionHeight / 2;
            }else{
                xTemp += dx;
            }
        }

        //checks for collision with obstacles in the y direction
        calculateCorners(x, yDest);
        if(dy < 0){
            if(topLeft||topRight){
                dy = 0;
                yTemp = currentRow * tileSize + collisionHeight / 2;
            }else{
                yTemp += dy;
            }
        }
        if(dy > 0){
            if(bottomLeft||bottomRight){
                dy = 0;
                yTemp = (currentRow + 1)* tileSize - collisionHeight / 2;
            }else{
                yTemp += dy;
            }
        }

    }

    protected void calculateCorners(double x, double y){
        int leftTile = ((int)x - collisionWidth / 2) / tileSize;
        int rightTile = ((int)x + collisionWidth / 2 - 1) / tileSize;
        int topTile = ((int)y - collisionHeight / 2) / tileSize;
        int bottomTile = ((int)y + collisionHeight / 2 - 1) / tileSize;

        if(topTile < 0 || bottomTile >= tileMap.getNumRows() ||
                leftTile < 0 || rightTile >= tileMap.getNumCols()) {
            topLeft = topRight = bottomLeft = bottomRight = false;
            return;
        }
        int tLeft = tileMap.getType(topTile, leftTile);
        int tRight = tileMap.getType(topTile, rightTile);
        int bLeft = tileMap.getType(bottomTile, leftTile);
        int bRight = tileMap.getType(bottomTile, rightTile);

        topLeft = tLeft == Tile.OBSTACLE;
        topRight = tRight == Tile.OBSTACLE;
        bottomLeft = bLeft == Tile.OBSTACLE;
        bottomRight = bRight == Tile.OBSTACLE;

    }

    public int getX(){
        return (int)x;
    }
    public int getY(){
        return (int)y;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public int getCollisionWidth() {
        return collisionWidth;
    }
    public int getCollisionHeight() {
        return collisionWidth;
    }

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }
    public void setVector(double x, double y){
        this.dx = dx;
        this.dy = dy;
    }
    public void setMapPosition(){
        xMap = tileMap.getX();
        yMap = tileMap.getY();
    }

    //returns true if the game object is on the screen because if not then it will not need to be rendered.
    public boolean notOnScreen(){
        return (x + xMap + width < 0) || (x + xMap - width > GamePanel.WIDTH) || (y + yMap + height < 0) || (y + yMap - height > GamePanel.HEIGHT);
    }

    public void setLeft(boolean b){
        left = b;
    }
    public void setRight(boolean b){
        right = b;
    }
    public void setUp(boolean b){
        up = b;
    }
    public void setDown(boolean b){
        down = b;
    }
    public void setDodging(boolean b){
        dodging = b;
    }

    public abstract void calculateKnockback(double newAngle, int i);

    public abstract void hit(int slashDamage);
}
