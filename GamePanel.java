import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 20;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 90;
    int[] x = new int[GAME_UNITS];
    int[] y = new int[GAME_UNITS];
    int bodyParts = 5;
    int points = 0;
    int appleX, appleY;
    char direction = 'R';
    boolean running = false, gameOver = false;
    int highscore = 0;
    boolean toogleGrid = true;
    Timer timer;
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        timer = new Timer(DELAY, this);
        startGame();
    }
    public void startGame(){
        newApple();
        running = true;
        gameOver = false;
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        bodyParts = 5;
        points = 0;
        direction = 'R';
        timer.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if(running) {
            if(toogleGrid){
                for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                    g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                    g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(0, 255, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    int changeB = 0;
                    int changeG = 255;
                    int changeR = 0;
                    if((i<50)){
                        changeB = i*5;
                    } else if(i>=50 && i<100){
                        changeB = 255;
                        changeR = ((i-50)*5);
                    } else if(i>=100){
                        changeB = 255; changeR = 255;
                    }
                    g.setColor(new Color(changeR, changeG,changeB));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            //shows score
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 15)); //Font properties
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("SCORE "+points, (SCREEN_WIDTH-metrics.stringWidth("SCORE "+points))/2, SCREEN_HEIGHT-10);
            //shows highscore
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 15)); //Font properties
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString("HIGHSCORE "+highscore, (SCREEN_WIDTH-metrics2.stringWidth("HIGHSCORE "+highscore))/2,20);
            //G to toggle grid
            String grid;
            if(toogleGrid) {grid = "ON";}else{grid = "OFF";}
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 10)); //Font properties
            g.drawString("PRESS G TO TOGGLE GRID: "+grid, SCREEN_WIDTH-200, SCREEN_HEIGHT-10);
        } else {
            gameOver(g);
        }
    }
    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }
    public void move(){
        for(int i=bodyParts; i>0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction){
            case 'R': x[0] = x[0] + UNIT_SIZE; break;
            case 'L': x[0] = x[0] - UNIT_SIZE; break;
            case 'U': y[0] = y[0] - UNIT_SIZE; break;
            case 'D': y[0] = y[0] + UNIT_SIZE; break;
        }
    }
    public void checkPoint(){
        if((x[0] == appleX) && (y[0] == appleY)){
            bodyParts = bodyParts+20;
            points = points+20;
            newApple();
        }
    }
    public void checkCollisions(){
        //checks for head collision on body;
        for(int i = bodyParts; i>0; i--){
            if((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }
        //checks if head touches border
        if(x[0]<0 || x[0]>SCREEN_WIDTH || y[0]<0 || y[0]>SCREEN_HEIGHT){
            running = false;
        }
        if(!running){
            timer.stop();
        }
    }
    public void gameOver(Graphics g){
        if(points>highscore){
            highscore = points;
        }
        gameOver = true;
        //Game Over Display
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 75)); //Font properties
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("GAME OVER", (SCREEN_WIDTH-metrics1.stringWidth("GAME OVER"))/2, SCREEN_HEIGHT/2);
        //Score Display
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 40)); //Font properties
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("SCORE "+points, (SCREEN_WIDTH-metrics2.stringWidth("SCORE "+points))/2, (SCREEN_HEIGHT/2)+50);
        //Press R To Restart
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 25)); //Font properties
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("PRESS R TO RESTART", (SCREEN_WIDTH-metrics3.stringWidth("PRESS R TO RESTART"))/2, (SCREEN_HEIGHT/2)-65);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkPoint();
            checkCollisions();
        }
        repaint();

    }
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_D:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_A:
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_W:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_S:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;
            }
            if(e.getKeyCode() == KeyEvent.VK_R && gameOver){
                startGame();
            }
            if(e.getKeyCode() == KeyEvent.VK_G){
                if(toogleGrid){
                    toogleGrid = false;
                }else{
                    toogleGrid = true;
                }

            }
        }
    }
}
