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
import java.util.ArrayList;

public class PlayState extends GameState{

    private TileMap tileMap;
    private Player player;
    public ArrayList<Brawler> brawlers;


    private int score = 0;
    private int wave = 1;
    private int lastWave = 1;
    private int lastHealth;


    public PlayState(GameStateManager gsm){
        this.gsm = gsm;
        init();
    }

    @Override
    public void init() {

        tileMap = new TileMap(50); //TODO get proper tileset
        tileMap.loadTiles("/Tilesets/tileset.png");
        tileMap.loadMap("/Maps/map2.map");
        tileMap.setPosition(0,0);

        player = new Player(tileMap);
        player.setPosition(100,100);

        brawlers = new ArrayList<Brawler>();
        Brawler b;
        Point[] points = new Point[] {
                new Point(200, 50),
                new Point(300, 150),
                new Point(400, 50),
                new Point(500, 50),
                new Point(600, 50),
        };
        for(int i = 0; i < points.length; i++){
            b = new Brawler(tileMap);
            b.setPosition(points[i].x, points[i].y);
            brawlers.add(b);
        }

    }

    @Override
    public void update() {
        //update player
        player.update();


        //update brawlers
        for(int i = 0; i < brawlers.size(); i++){
            Brawler b = brawlers.get(i);
            b.update();
            if(b.isDead()){
                brawlers.remove(i);
                score += 10;
                i--;
            }
        }

        //keeps camera centered on player
        tileMap.setPosition(GamePanel.WIDTH / 2 - player.getX(), GamePanel.HEIGHT / 2 - player.getY());

        //update score
        if(player.getHealth()< lastHealth){
            score--;
        }
        lastHealth = player.getHealth();

        if(wave>lastWave){
            score += wave * 100;
        }
        lastWave = wave;

    }

    @Override
    public void render(Graphics2D g) {
        //clear screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        //render tilemap
        tileMap.render(g);

        //render brawlers
        for(int i = 0; i < brawlers.size(); i++){
            brawlers.get(i).render(g);
        }

        //render player
        player.render(g);

        //render hud
        Font font;
        font = new Font("Arial", Font.PLAIN, 24);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("Health: " + player.getHealth()+ "/" + player.getMaxHealth(), 5, 25);
        g.drawString("Score: " + score, 5, 50);


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
        if(k == KeyEvent.VK_SHIFT){
            player.setDodging(true);
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
        if(k == KeyEvent.VK_SHIFT){
            player.setDodging(false);
        }
    }

    @Override
    public void mousePressed(int e) {
        if(e == MouseEvent.BUTTON1){
            player.setSlashing(true);
            //check player attacks
            player.checkAttack(brawlers);
        }
        if(e == MouseEvent.BUTTON2){
            player.setLunging(true);
            //check player attacks
            player.checkAttack(brawlers);
        }
        if(e == MouseEvent.BUTTON3){
            player.setParrying(true);
        }
    }

    @Override
    public void mouseReleased(int e) {
        if(e == MouseEvent.BUTTON1){
            player.setSlashing(false);
        }
        if(e == MouseEvent.BUTTON2){
            player.setLunging(false);
        }
        if(e == MouseEvent.BUTTON3){
            player.setParrying(false);
        }
    }

}
