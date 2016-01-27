package GameObjects;


import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PickUp extends GameObject{
    private BufferedImage pickUp;

    public PickUp(TileMap tm) {
        super(tm);
        width = 9;
        height = 26;
        collisionWidth = 9;
        collisionHeight = 26;

        //load sprites
        try{
            pickUp = ImageIO.read(getClass().getResourceAsStream("/Sprites/rum.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void activate(Player player){
        player.setHealth(player.getHealth() + 20);
    }

    public void render(Graphics2D g){
        setMapPosition();

        //render pickup
        g.drawImage(pickUp, (int) (x + xMap - width / 2), (int) (y + yMap - height / 2), null);
    }
}
