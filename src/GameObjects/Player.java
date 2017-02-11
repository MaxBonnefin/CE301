package GameObjects;

import TileMap.TileMap;
import Utilities.SoundManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends GameObject {

    private int health;
    private int maxHealth;
    private boolean dead;
    private boolean hit;
    private BufferedImage sprite;
    private BufferedImage sword;
    private BufferedImage parrysword;

    public double angle;

    private long dodgeTime = 0;
    private long time = 0;

    private double x1, y1;

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

        lungeDamage = 20;
        lungeRange = 120;

        //load sprites
        try{
            sprite = ImageIO.read(getClass().getResourceAsStream("/Sprites/player.png"));
            sword = ImageIO.read(getClass().getResourceAsStream("/Sprites/sword.png"));
            parrysword = ImageIO.read(getClass().getResourceAsStream("/Sprites/parrysword.png"));

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void checkAttack(ArrayList<Brawler> brawlers){

        //check slash
        if(slashing||lunging){
            SoundManager.play(SoundManager.slash);
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
                if(slashing){
                    if(anglediff <= 45  && slashRange >= range){
                        if(range<20){
                            double newAngle = angle - 180;
                            if(newAngle < 0){
                                newAngle += 360;
                            }
                            b.calculateKnockback(newAngle,60);

                        }else{
                            b.calculateKnockback(angle,60);
                        }
                        b.hit(slashDamage);
                    }
                }
                if(lunging){
                    if(anglediff <= 45  && lungeRange >= range){
                        if(range<20){
                            double newAngle = angle - 180;
                            if(newAngle < 0){
                                newAngle += 360;
                            }
                            b.calculateKnockback(newAngle,200);

                        }else{
                            b.calculateKnockback(angle,200);
                        }
                        b.calculateKnockback(angle,200);
                        b.hit(lungeDamage);
                    }
                }
            }
        }
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
        if(!parrying){
            health -= damage;
        }
        if (health < 0){
            health = 0;
        }
        if (health == 0){
            SoundManager.play(SoundManager.brawlerDeath);
            dead = true;
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

        //update health
        if(health > maxHealth){
            health = maxHealth;
        }

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

        if(slashing||lunging){
            g.drawImage(sword, (int) ((x + xMap - 50 / 2)), (int) ((y + yMap - 20 / 2)), null);
        }
        if(parrying){
            g.drawImage(parrysword, (int) ((x + xMap - 50 / 2)), (int) ((y + yMap - 20 / 2)), null);
        }

        g.drawImage(sprite, (int) (x + xMap - width / 2), (int) (y + yMap - height / 2), null);
        //reset transform
        g.setTransform(reset);
        //render health bar
        g.setColor(new Color(16,133,12));
        g.fillRect((int) (x + xMap) - health / 2, (int) (y + yMap) + 25, health, 5);
        g.setColor(Color.black);
        g.drawRect((int) (x + xMap) - health / 2, (int) (y + yMap) + 25, health, 5);
    }

    public int getHealth(){
        return health;
    }
    public void setHealth(int h){
        health = h;
    }
    public int getMaxHealth(){
        return maxHealth;
    }
    public void setSlashing(boolean b){
        if(!dodging && !lunging && !parrying){
            slashing = b;
        }
    }
    public void setLunging(boolean b){
        if(!slashing && !parrying){
            setDodging(true);
            lunging = b;
        }
    }
    public void setParrying(boolean b){
        if(!dodging && !slashing && !lunging){
            if(!parrying){
                SoundManager.play(SoundManager.slash);
            }
            parrying = b;
        }
    }

    @Override
    public void setDodging(boolean b){
        dodging = b;
        if(dodging){
            SoundManager.play(SoundManager.swoosh);
            moveSpeed = 1.2;
            maxSpeed = 9;
            dodgeTime = System.nanoTime();
        }else if(!dodging){
            moveSpeed = 0.4;
            maxSpeed = 1.8;
        }
    }

}
