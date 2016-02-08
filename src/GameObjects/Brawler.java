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
    private double hitSpeed;
    private boolean hit;
    private double x1, y1;
    public Point target;

    public Brawler(TileMap tm) {
        super(tm);
        width = 50;
        height = 50;
        collisionWidth = 50;
        collisionHeight = 50;

        moveSpeed = 0.4;
        maxSpeed = 1.8;
        hitSpeed = 6.0;
        stopSpeed = 0.5;

        health = maxHealth = 50;

        //load sprites
        try{
            sprite = ImageIO.read(getClass().getResourceAsStream("/Sprites/brawler.png"));
            sword = ImageIO.read(getClass().getResourceAsStream("/Sprites/sword.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
        getTarget();
    }

    public void calculateKnockback(double theta, int distance){
        hit = true;
        //using trigonometry to calculate point from angle
        x1 = x + distance * Math.cos(theta);
        y1 = y + distance * Math.sin(theta);

    }

    public void hit(int damage){
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
        //getNextLocation();
        getNextPosition();
        checkObstacleCollision();
        setPosition(xTemp, yTemp);

        //update rotation angle
        angle = Math.toDegrees(Math.atan2(target.y - this.y, target.x - this.x)- Math.PI/2);
        if(angle < 0){
            angle += 360;
        }
    }

    public Point getTarget(){
        Random rand = new Random();
        int rx, ry;

        rx = rand.nextInt(tileMap.getNumCols());
        ry = rand.nextInt(tileMap.getNumRows());

        while(tileMap.getType(ry, rx) != 0){
            Random r = new Random();

            rx = r.nextInt(tileMap.getNumCols());
            ry = r.nextInt(tileMap.getNumRows());
        }

        return target = new Point(rx * tileMap.getTileSize() + tileMap.getTileSize() / 2,ry * tileMap.getTileSize() + tileMap.getTileSize() / 2);
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

            if(target.x > x){
                left = false;
                right = true;
            }
            if(target.x < x){
                right = false;
                left = true;
            }
            if(x > target.x - 1 && x < target.x + 1){
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
            if(y > target.y - 1 && y < target.y + 1){
                up = false;
                down = false;
            }
            if(!up && !down && !left && !right){
                getTarget();
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

        //DEBUG TEXT
        //g.drawString((x + ", "+ y),(int)(x + xMap),(int)(y + yMap));

        //reset transform
        g.setTransform(reset);

        //target
        g.drawOval((int)(target.x + xMap - width / 2), (int)(target.y + yMap - height / 2), tileMap.getTileSize(), tileMap.getTileSize());
    }

    public int getHealth(){
        return health;
    }
    public int getMaxHealth(){
        return maxHealth;
    }
    public boolean isDead(){ return dead; }

}
