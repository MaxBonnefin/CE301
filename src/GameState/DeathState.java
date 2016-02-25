package GameState;

import Main.GamePanel;
import Utilities.SoundManager;

import java.awt.*;
import java.awt.event.KeyEvent;


public class DeathState extends GameState{


    public double mouseX, mouseY;
    private int currentChoice = 0;
    private String[] options = {"Restart","Return"};

    private Color titleColor;
    private Font titleFont;
    private Font font;

    public DeathState(GameStateManager gsm){

        this.gsm = gsm;

        titleFont = new Font("Century Gothic", Font.PLAIN, 56);
        font = new Font("Arial", Font.PLAIN, 24);


    }

    @Override
    public void init(){}

    @Override
    public void update(){

    }

    @Override
    public void render(Graphics2D g){
        //clear screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);


        //draw title
        g.setFont(titleFont);
        g.setColor(Color.RED);
        g.drawString("Oh Dear, You Died!", 60, 150);

        //draw menu options
        g.setFont(font);
        for (int i = 0; i < options.length; i++){
            if(i == currentChoice){
                g.setColor(Color.RED);
            }
            else{
                g.setColor(new Color(125,0,0));
            }
            g.drawString(options[i], 290, 280 + i * 30);
        }

    }

    private void select(){
        if(currentChoice ==0){
            //restart
            gsm.setState(GameStateManager.PLAYSTATE);
        }
        if(currentChoice ==1){
            //quit to menu
            gsm.setState(GameStateManager.MENUSTATE);
        }
    }

    @Override
    public void keyPressed(int k){
        if (k== KeyEvent.VK_SPACE){
            SoundManager.play(SoundManager.menuSelect);
            select();
        }
        if (k== KeyEvent.VK_UP){
            currentChoice--;
            SoundManager.play(SoundManager.menuMove);
            if (currentChoice ==-1){
                currentChoice = options.length - 1;
            }
        }
        if (k== KeyEvent.VK_DOWN){
            currentChoice++;
            SoundManager.play(SoundManager.menuMove);
            if (currentChoice == options.length){
                currentChoice = 0;
            }
        }
    }

    @Override
    public void keyReleased(int k){

    }

    @Override
    public void mousePressed(int e) {

    }

    @Override
    public void mouseReleased(int e) {

    }

}

