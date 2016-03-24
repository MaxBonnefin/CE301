package GameObjects;

import TileMap.TileMap;
import Utilities.PathNode;
import Utilities.SoundManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Brawler extends GameObject{

    private int health;
    private int maxHealth;
    private boolean dead=false;
    private BufferedImage sprite;
    private BufferedImage sprite2;
    private BufferedImage sprite3;
    private BufferedImage parrysword;
    private BufferedImage sword;
    public double angle;

    private double hitSpeed;
    private boolean hit;
    private double x1, y1;
    private boolean strong;
    private boolean fast;
    public boolean wantFight;

    //variables for A* pathfinding
    public Point target;
    public Point destination;
    private ArrayList<PathNode> path;

    //slash attack
    private boolean slashing;
    private int slashDamage;
    private int slashRange;

    //lunge attack
    private boolean lunging;
    private int lungeDamage;
    private int lungeRange;

    //parrying
    private boolean parrying;

    public Brawler(TileMap tm) {
        super(tm);
        width = 50;
        height = 50;

        //collision width and height are kept below sprite size of 50 so that they do not get trapped in single tiles due to their rotation
        collisionWidth = 25;
        collisionHeight = 25;

        moveSpeed = 0.4;
        maxSpeed = 1.6;
        hitSpeed = 6.0;
        stopSpeed = 0.5;

        slashDamage = 10;
        slashRange = 50;

        lungeDamage = 20;
        lungeRange = 120;
        Random r = new Random();
        int type = r.nextInt(3 - 1 + 1) + 1;

        if(type == 1){
            strong = false;
            fast = false;
            wantFight=true;
        }else if(type == 2){
            strong = true;
            fast = false;
            wantFight=true;
            slashDamage = 20;
            lungeDamage = 40;
        }else if(type == 3){
            strong = false;
            fast = false;
            wantFight=false;
        }

        if(strong){
            health = maxHealth = 100;
            try {
                sprite2 = ImageIO.read(getClass().getResourceAsStream("/Sprites/brawler2.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            health = maxHealth = 50;
        }

        if(fast){
            moveSpeed = 1.5;
            maxSpeed = 3;
            stopSpeed = 1;
            try {
                sprite3 = ImageIO.read(getClass().getResourceAsStream("/Sprites/brawler3.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(!strong&&!fast){
            try {
                sprite = ImageIO.read(getClass().getResourceAsStream("/Sprites/brawler.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try{
            sword = ImageIO.read(getClass().getResourceAsStream("/Sprites/sword.png"));
            parrysword = ImageIO.read(getClass().getResourceAsStream("/Sprites/parrysword.png"));

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList findPath(){

        ArrayList<PathNode> openList = new ArrayList<PathNode>();
        ArrayList<PathNode> closedList = new ArrayList<PathNode>();
        path = new ArrayList<PathNode>();
        path.clear();

        //round pos to closest node for simpler pathfinding
        if(x%5 != 0){
            x = Math.round(x/5) * 5;
        }
        if(y%5 != 0){
            y = Math.round(y/5) * 5;
        }

        //path found by implementing A* algorithm
        PathNode startPos = new PathNode(new Point((int)x,(int)y));
        startPos.f = 0;
        openList.add(startPos); // add original position to the open list

        PathNode current;

        int loops =0;
        main:
        while(!openList.isEmpty()){ //no path found
            loops++;
            if(loops>500){
                break main;
            }
            double smallestF = Integer.MAX_VALUE;
            int index = 0;
            PathNode check;
            for(int i= 0; i < openList.size(); i++){
                //find position with lowest f score
                check = openList.get(i);
                if(check.parent!=null){
                    check.g = check.parent.g + tileSize;
                }else{
                    check.g = 0;
                }
                check.h = Math.sqrt(Math.pow((destination.x - check.pos.x), 2) + Math.pow((destination.y - check.pos.y), 2));
                check.f = check.g + check.h;
                if (check.f < smallestF){
                    smallestF = check.f;
                    index = i;
                }
            }

            current=openList.get(index);
            openList.remove(current);

            //get all non-obstacle adjacent squares
            ArrayList<PathNode> adjTiles = new ArrayList<PathNode>();
            try{
                if(tileMap.getType(current.pos.y/tileSize-1,current.pos.x/tileSize)==0){
                    adjTiles.add(new PathNode(new Point(current.pos.x,current.pos.y-tileSize),current));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.pos.y/tileSize+1,current.pos.x/tileSize)==0){
                    adjTiles.add(new PathNode(new Point(current.pos.x,current.pos.y+tileSize),current));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.pos.y/tileSize-1,current.pos.x/tileSize-1)==0){
                    adjTiles.add(new PathNode(new Point(current.pos.x-tileSize,current.pos.y-tileSize),current));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.pos.y/tileSize+1,current.pos.x/tileSize+1)==0){
                    adjTiles.add(new PathNode(new Point(current.pos.x+tileSize,current.pos.y+tileSize),current));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.pos.y/tileSize-1,current.pos.x/tileSize+1)==0){
                    adjTiles.add(new PathNode(new Point(current.pos.x+tileSize,current.pos.y-tileSize),current));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.pos.y/tileSize+1,current.pos.x/tileSize-1)==0){
                    adjTiles.add(new PathNode(new Point(current.pos.x-tileSize,current.pos.y+tileSize),current));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.pos.y/tileSize,current.pos.x/tileSize+1)==0){
                    adjTiles.add(new PathNode(new Point(current.pos.x+tileSize,current.pos.y),current));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            try{
                if(tileMap.getType(current.pos.y/tileSize,current.pos.x/tileSize-1)==0){
                    adjTiles.add(new PathNode(new Point(current.pos.x-tileSize,current.pos.y),current));
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }

            neighbours:
            for (int i = 0; i < adjTiles.size(); i++){
                PathNode successor =adjTiles.get(i);
                successor.parent = current;
                if(successor.pos.equals(destination)){
                    closedList.add(successor);
                    break main;
                }
                successor.g = current.g + tileSize;
                successor.h = Math.sqrt(Math.pow((destination.x - successor.pos.x), 2) + Math.pow((destination.y - successor.pos.y), 2));
                successor.f = successor.g + successor.h;

                for (int n = 0; n < openList.size(); n++){
                    if(openList.get(n).pos.equals(successor.pos) && openList.get(n).f < successor.f){
                        continue neighbours;
                    }
                }
                for (int n = 0; n < closedList.size(); n++){
                    if(closedList.get(n).pos.equals(successor.pos) && closedList.get(n).f < successor.f){
                        continue neighbours;
                    }
                }

                openList.add(successor);
            }
            closedList.add(current);
        }

        if(closedList.size()==0){
            //getDestination();
            findPath();
        }

        //build path
        PathNode goal = closedList.get(closedList.size()-1);
        openList.clear();
        closedList.clear();
        while(goal != startPos){
            path.add(goal);
            goal = goal.parent;
        }
        return path;
    }

    public void calculateKnockback(double theta, int distance){
        hit = true;
        //using trigonometry to calculate point from angle
        x1 = x + distance * Math.cos(theta);
        y1 = y + distance * Math.sin(theta);
    }

    public void hit(int damage){
        SoundManager.play(SoundManager.brawlerHit);
        wantFight = true;
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


        if(destination==null||((int)x==destination.x && (int)y==destination.y)||path.isEmpty()){
            getDestination();
        }

        //update position
        getNextPosition();
        checkObstacleCollision();
        setPosition(xTemp, yTemp);

        //update rotation angle
        angle = Math.toDegrees(Math.atan2(target.y - this.x, target.x - this.y)- Math.PI/2);
        if(angle < 0){
            angle += 360;
        }


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
        if(!path.isEmpty()){
            target = path.get(path.size()-1).pos;
            path.remove(path.get(path.size() - 1));
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

        if(slashing||lunging){
            g.drawImage(sword, (int) ((x + xMap - 50 / 2)), (int) ((y + yMap - 20 / 2)), null);
        }
        if(parrying){
            g.drawImage(parrysword, (int) ((x + xMap - 50 / 2)), (int) ((y + yMap - 20 / 2)), null);
        }
        if(strong){
            g.drawImage(sprite2, (int) (x + xMap - width / 2), (int) (y + yMap - height / 2), null);
        }else if(fast){
            g.drawImage(sprite3, (int) (x + xMap - width / 2), (int) (y + yMap - height / 2), null);
        }
        else{
            g.drawImage(sprite, (int) (x + xMap - width / 2), (int) (y + yMap - height / 2), null);
        }

        //reset transform
        g.setTransform(reset);

        //render health bar
        g.setColor(new Color(16,133,12));
        g.fillRect((int) (x + xMap) - health / 2, (int) (y + yMap) + 25, health, 5);
        g.setColor(Color.black);
        g.drawRect((int) (x + xMap) - health / 2, (int) (y + yMap) + 25, health, 5);

        //DEBUG TEXT
        //g.setColor(Color.black);
        //g.drawString((x + ", "+ y),(int)(x + xMap),(int)(y + yMap));
        //target
        if(target!=null){
            g.setColor(Color.black);
            g.drawOval((int)(target.x + xMap - width / 2), (int)(target.y + yMap - height / 2), tileMap.getTileSize(), tileMap.getTileSize());
        }
        //dest
        if(destination!=null){
            g.setColor(Color.green);
            g.drawOval((int)(destination.x + xMap - width / 2), (int)(destination.y + yMap - height / 2), tileMap.getTileSize(), tileMap.getTileSize());
            g.setColor(Color.black);
        }

    }

    public int getHealth(){
        return health;
    }
    public int getMaxHealth(){
        return maxHealth;
    }
    public boolean isDead(){
        return dead;
    }

}
