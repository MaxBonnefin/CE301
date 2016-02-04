package TileMap;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class TileMap {

    private double x, y; //position
    private int xMin, yMin, xMax, yMax; //bounds

    private double tween; //smooth camera panning TODO

    private int[][]map;
    private int numRows, numCols;
    private int width, height;
    private int tileSize;

    //graphics for the tiles
    private BufferedImage tileset;
    private int tilesAcross;
    private Tile[][] tiles; //contains tile images from tileset

    //to ensure we are only drawing tiles that should be visible on the screen
    public int rowOffset;
    public int colOffset;
    private int rowsToDraw, colsToDraw;

    public TileMap(int tileSize){
        reset(tileSize);
    }

    //reset tilemap
    public void reset(int tileSize){
        x = 0;
        y = 0;
        xMin = 0;
        yMin = 0;
        xMax = 0;
        yMax = 0;
        map = null;
        numRows = 0;
        numCols = 0;
        width = 0;
        height = 0;
        tileset = null;
        tilesAcross = 0;
        tiles = null;
        rowOffset = 0;
        colOffset = 0;
        rowsToDraw = 0;
        colsToDraw = 0;

        this.tileSize = tileSize;
        rowsToDraw = GamePanel.HEIGHT / tileSize + 3; //extra 3 is just for padding
        colsToDraw = GamePanel.WIDTH / tileSize + 3;

        tween = 0.06;
    }

    //loads tileset file
    public void loadTiles(String s){
        try{
            tileset = ImageIO.read(getClass().getResourceAsStream(s));
            tilesAcross = tileset.getWidth() / tileSize; //number of tiles in a row
            tiles = new Tile[2][tilesAcross];

            BufferedImage subimage;
            for(int col =0; col < tilesAcross; col++){
                subimage = tileset.getSubimage(col * tileSize, 0, tileSize, tileSize);
                tiles[0][col] = new Tile(subimage, Tile.NORMAL); //no collision on these tiles
                subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
                tiles[1][col] = new Tile (subimage, Tile.OBSTACLE); //collision on these tiles
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("No tileset found at '" + s +"'.");
        }
    }

    //loads map file
    public void loadMap(String s){
        try{
            InputStream in = getClass().getResourceAsStream(s);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            //map files are set up so the first line is the number of columns the second line is the number of rows and the rest of the file is the map data
            numCols = Integer.parseInt(br.readLine());
            numRows = Integer.parseInt(br.readLine());
            map = new int[numRows][numCols];
            width = numCols * tileSize;
            height = numRows * tileSize;

            xMin = GamePanel.WIDTH - width;
            xMax = 0;
            yMin = GamePanel.HEIGHT - height;
            yMax = 0;
            String delims = "\\s+"; //whitespace
            for(int row = 0; row < numRows; row++){
                String line = br.readLine();
                String[] tokens = line.split(delims);
                for(int col = 0; col < numCols; col++){
                    map[row][col] = Integer.parseInt(tokens[col]);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("No map file found at '" + s +"'.");
        }
    }

    public int getTileSize(){
        return tileSize;
    }
    public int getX(){
        return (int)x;
    }
    public int getY(){
        return (int)y;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }

    public int getType(int row, int col){
        int rc = map[row][col];
        int r = rc / tilesAcross;
        int c = rc % tilesAcross;
        return tiles[r][c].getType();
    }

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;

        fixBounds();

        colOffset = (int)-this.x / tileSize;
        rowOffset = (int)-this.y / tileSize;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    private void fixBounds() {
        if(x < xMin) x = xMin;
        if(x > xMax) x = xMax;
        if(y < yMin) y = yMin;
        if(y > yMax) y = yMax;
    }

    public void render(Graphics2D g){
        for(int row = rowOffset; row < rowOffset + rowsToDraw; row++){
            if(row >= numRows){
                break;
            }
                for(int col = colOffset; col < colOffset + colsToDraw; col++){
                    if(col >= numCols){
                        break;
                    }

                    if(map[row][col] == 0){ //first tile in the tileset is empty
                        continue;
                    }

                    int rc = map[row][col];
                    int r = rc / tilesAcross;
                    int c = rc % tilesAcross;
                    g.drawImage(tiles[r][c].getImage(), (int)x + (col * tileSize), (int)y + (row * tileSize), null);
                    //DEBUG TEXT
                    //g.drawString(String.valueOf(tiles[r][c].getType()),(int)x + (col * tileSize) +25,(int)y + (row * tileSize)+25);
                }
        }
    }
}
