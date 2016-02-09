package GameState;

import GameObjects.Brawler;
import GameObjects.PickUp;
import GameObjects.Player;
import Main.GamePanel;
import TileMap.TileMap;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

public class PlayState extends GameState{

    private TileMap tileMap;
    private Player player;
    public ArrayList<Brawler> brawlers;
    public ArrayList<PickUp> pickups;

    private PickUp pickUp;

    private int score = 0;
    private int wave = 1;
    private int lastHealth;

    //double-tap variables
    private int lastKey = 0;
    private final static int W = KeyEvent.VK_W;
    private long lastWPress = System.currentTimeMillis();
    private final static int A = KeyEvent.VK_A;
    private long lastAPress = System.currentTimeMillis();
    private final static int S = KeyEvent.VK_S;
    private long lastSPress = System.currentTimeMillis();
    private final static int D = KeyEvent.VK_D;
    private long lastDPress = System.currentTimeMillis();
    private final static int limit = 250;

    private long timer = System.currentTimeMillis();
    private int map = 1;

    public PlayState(GameStateManager gsm){
        this.gsm = gsm;
        init();
    }

    @Override
    public void init() {

        tileMap = new TileMap(50);
        tileMap.loadTiles("/Tilesets/tileset.png");
        tileMap.loadMap("/Maps/map1.map");

        tileMap.setPosition(0,0);

        player = new Player(tileMap);
        player.setPosition(100,100);

        pickups = new ArrayList<PickUp>();
        pickUp = new PickUp(tileMap);
        pickups.add(pickUp);
        pickUp.setPosition(200,200);

        brawlers = new ArrayList<Brawler>();

        populateBrawlers();
    }

    private void populateBrawlers() {
        ArrayList<Point> points = new ArrayList<Point>();

        if(brawlers.isEmpty()){
            Brawler b;


            Random rand = new Random();

            int numBrawlers = rand.nextInt((25 - 15) + 1) + 15;
            //int numBrawlers =1;
            for(int i = 0; i < numBrawlers; i++){
                int rx, ry;

                rx = rand.nextInt(tileMap.getNumCols());
                ry = rand.nextInt(tileMap.getNumRows());

                while(tileMap.getType(ry, rx) != 0){
                    Random r = new Random();

                    rx = r.nextInt(tileMap.getNumCols());
                    ry = r.nextInt(tileMap.getNumRows());
                }
                points.add(new Point(rx * tileMap.getTileSize() + tileMap.getTileSize() / 2, ry * tileMap.getTileSize() + tileMap.getTileSize() / 2));
            }


            for(int i = 0; i < points.size(); i++) {
                b = new Brawler(tileMap);
                b.setPosition(points.get(i).x, points.get(i).y);
                brawlers.add(b);
            }
        }
        points.clear();

    }
    @Override
    public void update() {
        //new wave
        Random rand = new Random();

        if(brawlers.isEmpty()&& System.currentTimeMillis() > timer + 2000){
            wave++;
            score += 100;
            int r = rand.nextInt(2);
            if(r == 0){
                tileMap.reset(50);
                tileMap.loadTiles("/Tilesets/tileset.png");
                tileMap.loadMap("/Maps/map1.map");
                tileMap.setPosition(0,0);
                populateBrawlers();
                player.setPosition(100,100);
            }
            if(r == 1){
                tileMap.reset(50);
                tileMap.loadTiles("/Tilesets/tileset.png");
                tileMap.loadMap("/Maps/map2.map");
                tileMap.setPosition(0,0);
                populateBrawlers();
                player.setPosition(75,75);
            }

        }


        //update player
        player.update();

        //keeps camera centered on player
        tileMap.setPosition(GamePanel.WIDTH / 2 - player.getX(), GamePanel.HEIGHT / 2 - player.getY());

        //check pickups
        for(int i = 0; i < pickups.size(); i++){
            if(player.getHealth() < player.getMaxHealth()){
                if(pickups.get(i).intersects(player)){
                    pickups.get(i).activate(player);
                    pickups.remove(i);
                }
            }
        }

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

        //update score
        if(player.getHealth()< lastHealth){
            score--;
        }

        lastHealth = player.getHealth();

    }

    @Override
    public void render(Graphics2D g) {
        //clear screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        //render tilemap
        tileMap.render(g);

        //render pickups
        for (int i = 0; i < pickups.size(); i++) {
            pickups.get(i).render(g);
        }

        //render brawlers
        for (int i = 0; i < brawlers.size(); i++) {
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
        g.drawString("WAVE " + wave, 525, 25);


    }

    @Override
    public void keyPressed(int k) {

        //player controls
        if(k == KeyEvent.VK_W){
            player.setUp(true);
            if(System.currentTimeMillis() - lastWPress < limit && lastKey == KeyEvent.VK_W){
                player.setDodging(true);
            }
        }
        if(k == KeyEvent.VK_A){
            player.setLeft(true);
            if(System.currentTimeMillis() - lastAPress < limit && lastKey == KeyEvent.VK_A){
                player.setDodging(true);
            }
        }
        if(k == KeyEvent.VK_S){
            player.setDown(true);
            if(System.currentTimeMillis() - lastSPress < limit && lastKey == KeyEvent.VK_S){
                player.setDodging(true);
            }
        }
        if(k == KeyEvent.VK_D){
            player.setRight(true);
            if(System.currentTimeMillis() - lastDPress < limit && lastKey == KeyEvent.VK_D){
                player.setDodging(true);
            }
        }
        //TODO cheats
        if(k == KeyEvent.VK_M){
            for(int i = 0; i < brawlers.size(); i++){
                Brawler b = brawlers.get(i);
                brawlers.remove(i);
                i--;
            }
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
            lastWPress = System.currentTimeMillis();
            lastKey = KeyEvent.VK_W;
        }
        if(k == KeyEvent.VK_A){
            player.setLeft(false);
            lastAPress = System.currentTimeMillis();
            lastKey = KeyEvent.VK_A;
        }
        if(k == KeyEvent.VK_S){
            player.setDown(false);
            lastSPress = System.currentTimeMillis();
            lastKey = KeyEvent.VK_S;
        }
        if(k == KeyEvent.VK_D){
            player.setRight(false);
            lastDPress = System.currentTimeMillis();
            lastKey = KeyEvent.VK_D;
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
