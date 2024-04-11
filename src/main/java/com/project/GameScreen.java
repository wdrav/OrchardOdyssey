package com.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static org.slf4j.LoggerFactoryFriend.reset;

public class GameScreen extends JPanel implements Runnable{
   final int originalTile = 16;
   final int scale = 3;
   public final int scaledTile = originalTile * scale;
   final int maxScreenCol = 16;
   final int maxScreenRow = 12;
   final int screenWidth = scaledTile * maxScreenCol;
   final int screenHeight = scaledTile * maxScreenRow;
   private int score;
   private int count;
   int FPS = 60;
   KeyInput keyI = new KeyInput();
   Thread gameThread;
   Player player = new Player(this,keyI);
   Obstacles obstacles = new Obstacles((int)(screenWidth * 1.5));
   Fruits fruits = new Fruits((int)(screenWidth*1.5));

   private boolean running = false;
   private boolean gameOver = false;
   int playerSpeed = 4;
    public  GameScreen(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.cyan);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyI);
        this.setFocusable(true);

    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        running = true;

        while (running) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }

            if (gameOver) {
                System.out.println("Game Over! Score: " + score);
                break;
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
        }
    }

    private void restartGame() {
        score = 0;
        count = 0;
        gameOver = false;
        player = new Player(this, keyI);
        obstacles = new Obstacles((int)(screenWidth * 1.5));

        startGameThread();
    }


    public void update(){
        count += 1;
        if(count % 100 == 0){
            score += 1;
        }


        player.update();
        obstacles.update();
        fruits.update();

        if(obstacles.hasCollidedObstacle(player)){
            player.die();
            repaint();
            running = false;
            gameOver = true;
        }



        if(fruits.hasCollidedFruit()){
            score += 8;

        }

    }
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Graphics2D graphics2 = (Graphics2D) graphics;
        graphics.setFont(new Font("Times New Roman", Font.BOLD,25));
        graphics.drawString(Integer.toString(score),getWidth()/2-5,100);
        player.draw(graphics2);
        obstacles.create(graphics);
        fruits.create(graphics);
        //graphics2.dispose();
    }


}
