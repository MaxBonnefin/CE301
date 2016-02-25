package GameState;

import java.util.ArrayList;

public class GameStateManager {

    private ArrayList<GameState> gameStates;
    private int currentState;

    public static final int MENUSTATE = 0;
    public static final int PLAYSTATE = 1;
    public static final int DEATHSTATE = 2;

    public GameStateManager(){

        gameStates = new ArrayList<GameState>();

        currentState = MENUSTATE;
        gameStates.add(new MenuState(this));
        gameStates.add(new PlayState(this));
        gameStates.add(new DeathState(this));
    }

    public void setState(int state){
        currentState = state;
        gameStates.get(currentState).init();
    }

    public void update(){
        gameStates.get(currentState).update();
    }

    public void draw(java.awt.Graphics2D g){
        gameStates.get(currentState).render(g);
    }

    public void keyPressed(int k){
        gameStates.get(currentState).keyPressed(k);
    }

    public void keyReleased(int k){
        gameStates.get(currentState).keyReleased(k);
    }

    public void mousePressed(int e){
        gameStates.get(currentState).mousePressed(e);
    }
    public void mouseReleased(int e){
        gameStates.get(currentState).mouseReleased(e);
    }

}