package GameObjects;

import TileMap.TileMap;
import Utilities.SoundManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Brawler extends GameObject {

    private int health;
    private int maxHealth;
    private boolean dead;
    private BufferedImage sprite;
    private BufferedImage sword;
    public double angle;
    long time = 0;
    private double hitSpeed;
    private boolean hit;
    private double x1, y1;

    //variables for A* navigation
    public Point target;
    public Point destination;
    public ArrayList<Point> closedList;
    public ArrayList<Point> openList;

    public Brawler(TileMap tm) {
        super(tm);
        width = 50;
        height = 50;
        collisionWidth = 50;
        collisionHeight = 50;

        moveSpeed = 0.4;
        maxSpeed = 1.6;
        hitSpeed = 6.0;
        stopSpeed = 0.5;

        health = maxHealth = 50;

        //load sprites
        try{
            sprite = ImageIO.read(getClass().getResourceAsStream("/Sprites/brawlertrim.png"));
            sword = ImageIO.read(getClass().getResourceAsStream("/Sprites/sword.png"));
        }catch(Exception e){
            e.printStackTrace();
        }

        openList = new ArrayList<Point>();
        closedList = new ArrayList<Point>();

    }

    public ArrayList findPath(){
        openList = new ArrayList<Point>();
        closedList = new ArrayList<Point>();
       //path found by implementing A* algorithm
        Point startPos = new Point((int)x,(int)y);
        openList.add(startPos); // add original position to the open list

        do{
            int loops = 0;
            Point current;
            double smallestF = Integer.MAX_VALUE;
            int index = 0;
            for(int i= 0; i < openList.size(); i++){
                //find position with lowest f score
                current = openList.get(i);
                double g = loops * tileSize;
                double h = Math.sqrt(Math.pow((destination.x - current.x), 2) + Math.pow((destination.y - current.y), 2));
                double f = g + h;
                if (f<smallestF){
                    smallestF = f;
                    index = i;
                }
            }
            current = openList.get(index);

            closedList.add(current);
            openList.remove(current);

            if (closedList.contains(destination)) {
                //found path
                break;
            }

            //get all non-obstacle adjacent squares
            ArrayList<Point> adjTiles = new ArrayList<Point>();
            try{
                if(tileMap.getType(current.y/tileSize-1,current.x/tileSize)==0){
                    adjTiles.add(new Point(current.x,current.y-tileSize));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.y/tileSize+1,current.x/tileSize)==0){
                    adjTiles.add(new Point(current.x,current.y+tileSize));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.y/tileSize-1,current.x/tileSize-1)==0){
                    adjTiles.add(new Point(current.x-tileSize,current.y-tileSize));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.y/tileSize+1,current.x/tileSize+1)==0){
                    adjTiles.add(new Point(current.x+tileSize,current.y+tileSize));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.y/tileSize-1,current.x/tileSize+1)==0){
                    adjTiles.add(new Point(current.x+tileSize,current.y-tileSize));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.y/tileSize+1,current.x/tileSize-1)==0){
                    adjTiles.add(new Point(current.x-tileSize,current.y+tileSize));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.y/tileSize,current.x/tileSize+1)==0){
                    adjTiles.add(new Point(current.x+tileSize,current.y));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.y/tileSize,current.x/tileSize-1)==0){
                    adjTiles.add(new Point(current.x-tileSize,current.y));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            for (int i= 0; i < adjTiles.size(); i++){
                if(closedList.contains(adjTiles.get(i))){
                    continue;
                }
                if(!openList.contains(adjTiles.get(i))){
                    double newg = loops * tileSize;
                    double newh = Math.sqrt(Math.pow((destination.x - adjTiles.get(i).x), 2) + Math.pow((destination.y - adjTiles.get(i).y), 2));
                    double newf = newg + newh;
                    openList.add(adjTiles.get(i));
                }else{

                }
            }

            loops++;
        }while(!openList.isEmpty()); //no path

        return closedList;
    }

    public void calculateKnockback(double theta, int distance){
        hit = true;
        //using trigonometry to calculate point from angle
        x1 = x + distance * Math.cos(theta);
        y1 = y + distance * Math.sin(theta);

    }

    public void hit(int damage){
        SoundManager.play(SoundManager.brawlerHit);
        if(dead){
            return;
        }
        health -= damage;
        if (health < 0){
            health = 0;
        }
        if (health == 0){
            SoundManager.play(SoundManager.brawlerDeath);
            dead = true;
        }
    }

    public void update(){

        //update position
        if(destination == null){
             getDestination();
        }
        getNextPosition();
        checkObstacleCollision();
        setPosition(xTemp, yTemp);

        //update rotation angle
        angle = Math.toDegrees(Math.atan2(target.y - this.y, target.x - this.x)- Math.PI/2);
        if(angle < 0){
            angle += 360;
        }
        System.out.println("Dest: "+destination.toString());
        System.out.println("Path: "+closedList.toString());


    }



    public Point getDestination(){
        Random rand = new Random();
        int rx, ry;

        rx = rand.nextInt(tileMap.getNumCols());
        ry = rand.nextInt(tileMap.getNumRows());

        while(tileMap.getType(ry, rx) != 0){
            Random r = new Random();

            rx = r.nextInt(tileMap.getNumCols());
            ry = r.nextInt(tileMap.getNumRows());
        }

        destination = new Point(rx * tileMap.getTileSize() + tileMap.getTileSize() / 2, ry * tileMap.getTileSize() + tileMap.getTileSize() / 2);
        findPath();
        return destination;
    }

    public Point getTarget(){

        target = closedList.get(0);
        closedList.remove(0);
        if(closedList.isEmpty()){
            getDestination();
        }
        return target;
    }

    private void getNextPosition() {

        //attack knockback
        if(hit){
            if(x <= x1){
                dx += hitSpeed;
            }else if(x >= x1){
                dx -= hitSpeed;
            }
            if(y <= y1){
                dy += hitSpeed;
            }else if(y >= y1){
                dy -= hitSpeed;
            }

            hit = false;

        }else{

            //direction selection

            if(!up && !down && !left && !right){
                getTarget();
            }
            if(target.x > x){
                left = false;
                right = true;
            }
            if(target.x < x){
                right = false;
                left = true;
            }
            if(x > target.x - tileSize/16 && x < target.x + tileSize/16){
                left = false;
                right = false;
            }
            if(target.y > y){
                up = false;
                down = true;
            }
            if(target.y < y){
                down = false;
                up = true;
            }
            if(y > target.y - tileSize/16 && y < target.y + tileSize/16){
                up = false;
                down = false;
            }


            //actual movement
            if(left){
                dx -= moveSpeed;
                if(dx < - maxSpeed){
                    dx = - maxSpeed;
                }
            }else if(right){
                dx += moveSpeed;
                if(dx > maxSpeed){
                    dx = maxSpeed;
                }
            } else{
                if(dx > 0){
                    dx -= stopSpeed;
                    if(dx < 0){
                        dx = 0;
                    }
                }else if(dx < 0){
                    dx += stopSpeed;
                    if(dx > 0){
                        dx = 0;
                    }
                }
            }
            if(up){
                dy -= moveSpeed;
                if(dy < - maxSpeed){
                    dy = - maxSpeed;
                }
            }else if(down){
                dy += moveSpeed;
                if(dy > maxSpeed){
                    dy = maxSpeed;
                }
            }else{
                if(dy > 0){
                    dy -= stopSpeed;
                    if(dy < 0){
                        dy = 0;
                    }
                }else if(dy < 0){
                    dy += stopSpeed;
                    if(dy > 0){
                        dy = 0;
                    }
                }
            }
        }
    }

    public void render(Graphics2D g){
        setMapPosition();
        AffineTransform reset = g.getTransform();
        AffineTransform trans = new AffineTransform();
        //apply rotation to brawler sprite
        trans.rotate(Math.toRadians(angle), x + xMap, y + yMap);
        //render brawler
        g.transform(trans);
        g.drawImage(sprite, (int) (x + xMap - width / 2), (int) (y + yMap - height / 2), null);



        //reset transform
        g.setTransform(reset);
        //DEBUG TEXT
        /*
        g.drawString((x + ", "+ y),(int)(x + xMap),(int)(y + yMap));
        //target
        if(target!=null){
            g.drawOval((int)(target.x + xMap - width / 2), (int)(target.y + yMap - height / 2), tileMap.getTileSize(), tileMap.getTileSize());
        }
        //dest
        if(destination!=null){
            g.setColor(Color.green);
            g.drawOval((int)(destination.x + xMap - width / 2), (int)(destination.y + yMap - height / 2), tileMap.getTileSize(), tileMap.getTileSize());
            g.setColor(Color.black);
        }
        */
    }

    public int getHealth(){
        return health;
    }
    public int getMaxHealth(){
        return maxHealth;
    }
    public boolean isDead(){ return dead; }

}
