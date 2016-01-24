package GameObjects;

import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Brawler extends GameObject {

    private int health;
    private int maxHealth;
    private boolean dead;
    private BufferedImage sprite;
    private BufferedImage sword;
    public double angle;
    long time = 0;


    public Brawler(TileMap tm) {
        super(tm);
        width = 50;
        height = 50;
        collisionWidth = 50;
        collisionHeight = 50;

        moveSpeed = 0.4;
        maxSpeed = 1.8;
        stopSpeed = 0.5;

        health = maxHealth = 50;

        //load sprites
        try{
            sprite = ImageIO.read(getClass().getResourceAsStream("/Sprites/brawler.png"));
            sword = ImageIO.read(getClass().getResourceAsStream("/Sprites/sword.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void hit(int damage, double theta){
        if(dead){
            return;
        }
        health -= damage;
        if (health < 0){
            health = 0;
        }
        if (health == 0){
            dead = true;
        }

    }

    public void update(){
        //update position
        getNextPosition();
        checkObstacleCollision();
        setPosition(xTemp, yTemp);

        //update rotation angle (currently only the 8 cardinal directions)
        if(left){
            if(up){
                angle = 3 * Math.PI / 4;
            }else if(down){
                angle = Math.PI / 4;
            }else{
                angle = Math.PI / 2;
            }
        }else if(right){
            if(up){
                angle = 5 * Math.PI / 4;
            }else if(down){
                angle = 7 * Math.PI / 4;
            }else{
                angle = 3 * Math.PI / 2;
            }
        }else if(up){
            angle = Math.PI;
        }else if(down){
            angle =  2 * Math.PI;
        }
    }

    private void getNextPosition() {


        if(System.nanoTime() - time > 500000000){ //checks if direction has been changed in the past half second

            //deciding movement
            Random rand = new Random();
            int r = rand.nextInt(8);

            if(r == 0){
                //left
                right = false;
                up = false;
                down = false;
                left = true;
            }else if(r == 1){
                //right
                left = false;
                up = false;
                down = false;
                right = true;
            }else if(r == 2){
                //up
                left = false;
                right = false;
                down = false;
                up = true;
            }else if(r == 3){
                //down
                left = false;
                right = false;
                up = false;
                down = true;
            }else if(r == 4){
                //up & left
                right = false;
                down = false;
                up = true;
                left = true;
            }else if(r == 5){
                //up & right
                left = false;
                down = false;
                right = true;
                up = true;
            }else if(r == 6){
                //left & down
                right = false;
                up = false;
                left = true;
                down = true;
            }
            else if(r == 7){
                //right & down
                right = false;
                up = false;
                right = true;
                down = true;
            }
            time = System.nanoTime();
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

    public void render(Graphics2D g){
        setMapPosition();
        AffineTransform reset = g.getTransform();
        AffineTransform trans = new AffineTransform();
        //apply rotation to brawler sprite
        trans.rotate(angle, x + xMap, y + yMap);
        //render brawler
        g.transform(trans);
        g.drawImage(sprite, (int) (x + xMap - width / 2), (int) (y + yMap - height / 2), null);
        //reset transform
        g.setTransform(reset);
    }

    public int getHealth(){
        return health;
    }
    public int getMaxHealth(){
        return maxHealth;
    }
    public boolean isDead(){ return dead; }

}
