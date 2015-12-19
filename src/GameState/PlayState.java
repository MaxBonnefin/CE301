package GameState;

import GameObjects.Brawler;
import GameObjects.Player;
import Main.GamePanel;
import TileMap.TileMap;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;

public class PlayState extends GameState{

    public static double mouseX;
    public static double mouseY;
    private TileMap tileMap;

    private Player player;
    private Brawler brawler;
    private Brawler brawler2;


    public PlayState(GameStateManager gsm){
        this.gsm = gsm;
        init();
    }

    @Override
    public void init() {

        tileMap = new TileMap(50); //TODO get proper tileset
        tileMap.loadTiles("/Tilesets/tileset.png");
        tileMap.loadMap("/Maps/map1.map");
        tileMap.setPosition(0,0);

        player = new Player(tileMap);
        player.setPosition(100,100);

        brawler = new Brawler(tileMap);
        brawler.setPosition(100,300);
        brawler2 = new Brawler(tileMap);
        brawler2.setPosition(100, 400);
    }

    @Override
    public void update() {
        //update player
        player.update();
        //update brawlers
        brawler.update();
        brawler2.update();
        //keeps camera centered on player
        tileMap.setPosition(GamePanel.WIDTH / 2 - player.getX(), GamePanel.HEIGHT / 2 - player.getY());
    }

    @Override
    public void render(Graphics2D g) {
        //clear screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        //render tilemap
        tileMap.render(g);

        //render brawlers
        brawler.render(g);
        brawler2.render(g);
        //render player
        player.render(g);

        //render hud
        Font font;
        font = new Font("Arial", Font.PLAIN, 24);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("Health: " + player.getHealth()+ "/" + player.getMaxHealth(), 5, 25);

    }

    @Override
    public void keyPressed(int k) {
        //player controls
        if(k == KeyEvent.VK_W){
            player.setUp(true);
        }
        if(k == KeyEvent.VK_A){
            player.setLeft(true);
        }
        if(k == KeyEvent.VK_S){
            player.setDown(true);
        }
        if(k == KeyEvent.VK_D){
            player.setRight(true);
        }

        //saving game
        if(k == KeyEvent.VK_F5){
            try{
                // Create file
                File file = new File("save.game");
                FileWriter fstream = new FileWriter(file);
                //Write to file
                fstream.write("Player pos: " + player.getX()+ ", " + player.getY());
                fstream.write(" Health: " + player.getHealth());
                //Flush and close the output stream
                fstream.flush();
                fstream.close();
            }catch (Exception e){//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void keyReleased(int k) {
        if(k == KeyEvent.VK_W){
            player.setUp(false);
        }
        if(k == KeyEvent.VK_A){
            player.setLeft(false);
        }
        if(k == KeyEvent.VK_S){
            player.setDown(false);
        }
        if(k == KeyEvent.VK_D){
            player.setRight(false);
        }

        if(k == KeyEvent.VK_SPACE){
            player.setSlashing(false);
        }
    }

    @Override
    public void mousePressed(int e) {
        if(e == MouseEvent.BUTTON1){
            player.setSlashing(true);
        }
    }

    @Override
    public void mouseReleased(int e) {
        if(e == MouseEvent.BUTTON1){
            player.setSlashing(false);
        }
    }
}
