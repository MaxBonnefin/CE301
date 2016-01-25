package GameObjects;

import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends GameObject {

    private int health;
    private int maxHealth;
    private boolean dead;
    private BufferedImage sprite;
    private BufferedImage sword;
    public double angle;

    private long dodgeTime = 0;
    private long time = 0;

    //slash attack
    private boolean slashing;
    private int slashDamage;
    private int slashRange;

    public Player(TileMap tm) {
        super(tm);
        width = 50;
        height = 50;
        collisionWidth = 50;
        collisionHeight = 50;

        moveSpeed = 0.4;
        maxSpeed = 1.8;
        stopSpeed = 0.5;

        health = maxHealth = 50;

        slashDamage = 10;
        slashRange = 50;

        //load sprites
        try{
            sprite = ImageIO.read(getClass().getResourceAsStream("/Sprites/player.png"));
            sword = ImageIO.read(getClass().getResourceAsStream("/Sprites/sword.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void checkAttack(ArrayList<Brawler> brawlers){

        //check slash
        if(slashing){

            for(int i = 0; i < brawlers.size(); i++){
                Brawler b = brawlers.get(i);

                double theta = Math.toDegrees(Math.atan2((b.getY() - y) , (b.getX() - x)) - Math.PI/2);
                if(theta < 0){
                    theta += 360;
                }
                //calculate distance using pythagorean theorem
                double range = Math.sqrt((Math.pow((b.getY() - y), 2) + Math.pow((b.getX()  - x), 2)));
                //if within acceptable angle and within attacking range
                double anglediff = Math.min(Math.abs(angle - theta), 360 - Math.abs(theta));
                if(anglediff <= 45  && slashRange >= range){
                    b.calculateKnockback(angle,60);
                    b.hit(slashDamage);
                }
            }
        }
    }

    private void getNextPosition(){

        //movement
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

    public void update(){
        //update position
        getNextPosition();
        checkObstacleCollision();
        setPosition(xTemp, yTemp);

        //update dodging
        long newTime = System.nanoTime();
        if(dodging && newTime >= (dodgeTime + 125000000)){
            setDodging(false);
            moveSpeed = 0.4;
            maxSpeed = 1.8;
        }

        //update rotation angle
        angle = Math.atan2(MouseInfo.getPointerInfo().getLocation().getY() - y - yMap - height / 2, MouseInfo.getPointerInfo().getLocation().getX() - x - xMap - width / 2) - Math.PI/2;
        angle = Math.toDegrees(angle);
        if(angle < 0){
            angle += 360;
        }

    }

    public void render(Graphics2D g){
        setMapPosition();
        AffineTransform reset = g.getTransform();
        AffineTransform trans = new AffineTransform();
        //apply rotation to player sprite
        trans.rotate(Math.toRadians(angle), x + xMap, y + yMap);
        //render player
        g.transform(trans);
        g.drawImage(sprite, (int) (x + xMap - width / 2), (int) (y + yMap - height / 2), null);

        if(slashing){
            g.drawImage(sword, (int) ((x + xMap - 50 / 2)), (int) ((y + yMap - 20 / 2)), null);
        }
        //reset transform
        g.setTransform(reset);
    }

    public int getHealth(){
        return health;
    }
    public int getMaxHealth(){
        return maxHealth;
    }
    public void setSlashing(boolean b){
        if(!dodging){
            slashing = b;
        }
    }

    @Override
    public void setDodging(boolean b){
        if(System.nanoTime() - time > 500000000){
            dodging = b;
            if(dodging){
                moveSpeed = 0.8;
                maxSpeed = 9;
                dodgeTime = System.nanoTime();
            }else if(!dodging){
                moveSpeed = 0.4;
                maxSpeed = 1.8;
            }
            time = System.nanoTime();
        }
    }


}
