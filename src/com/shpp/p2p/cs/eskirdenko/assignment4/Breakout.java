package com.shpp.p2p.cs.eskirdenko.assignment4;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;


public class Breakout extends WindowProgram {
    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /**
     * Dimensions of game board (usually the same)
     */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;

    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 10;

    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 10;

    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /**
     * Width of a brick
     */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;

//    private static final double PADDLE_LIMIT = WIDTH-PADDLE_WIDTH;

    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /**
     * Number of turns
     */
    private static final int NTURNS = 3;

    private double vx, vy;

    // Variable for count how many turn left.
    private int countAttempts = NTURNS;

    // Variable for count how many bricks left.
    private int countBricks = NBRICK_ROWS * NBRICKS_PER_ROW;

    // Variables for game object. (ball, paddle, text label)
    private final GOval ball = ball();
    private final GRect paddle = paddle();
    private final GLabel labelText = labelText();

    // Array with colors for create bricks.
    private final Color[] colors = {Color.red, Color.orange, Color.yellow, Color.green, Color.cyan};

    public void run() {
        addMouseListeners();
        game();
        gameOver();
    }

    // Method for start game.
    // At start add paddle with bricks.
    // Then if attempts more than 0 game continue.
    // If all brick broken or ball falls down game end
    // Player have 3 attempts to break all bricks.
    private void game() {
        add(paddle);
        createBricks();
        while (countAttempts > 0 && countBricks > 0) {
            add(labelText);
            labelText.setLabel("Attempts left: " + countAttempts);
            labelText.setLocation((WIDTH - labelText.getWidth())/2, 250);
            add(ball, WIDTH/2.0-BALL_RADIUS, HEIGHT/2.0-BALL_RADIUS);
            waitForClick();
            remove(labelText);
            while ((countBricks > 0) && (ball.getY() + 2 * BALL_RADIUS < HEIGHT)) {
                actions();
                ball.move(vx, vy);
                pause(15);
            }
            remove(ball);
            countAttempts--;
        }
    }

    // Method for actions if ball was collided.
    private void actions() {
        // Take 4 coordinates for ball position (top left\right, bottom left\right corners).
        double x1ball = ball.getX();
        double x2ball = ball.getX() + 2 * BALL_RADIUS;
        double y1ball = ball.getY();
        double y2ball = ball.getY() + 2 * BALL_RADIUS;

        GObject collide = getCollidingObject();

        // If ball collide left or right wall.
        if ((x2ball >= WIDTH) || (x1ball <= 0)) {
            vx = -vx;
        }

        // If ball collide top wall or paddle.
        if ((y1ball <= 0)) {
            vy = -vy;
        }

        if (collide == paddle ) {
            if (vy > 0) {
                vy = -vy;
            }
        }

        // If ball collide brick.
        if (collide != null && collide != paddle) {
            remove(collide);
            vy = -vy;
            countBricks--;
        }
    }

    // Method for getting object that ball collided.
    private GObject getCollidingObject() {
        double x1ball = ball.getX();
        double y1ball = ball.getY();
        double x2ball = ball.getX() + 2 * BALL_RADIUS;
        double y2ball = ball.getY() + 2 * BALL_RADIUS;

        if (getElementAt(x1ball, y2ball) != null) {
            return getElementAt(x1ball, y2ball);
        } else if (getElementAt(x2ball, y2ball) != null) {
            return getElementAt(x2ball, y2ball);
        } else if (getElementAt(x1ball, y1ball) != null) {
            return getElementAt(x1ball, y1ball);
        } else if (getElementAt(x2ball, y1ball) != null) {
            return getElementAt(x2ball, y1ball);
        } else {
            return null;
        }
    }

    // Method for end game. Win or loss.
    // Win - if all bricks were break.
    // Loss - if no more attempts left.
    private void gameOver() {
        if (countAttempts == 0) {
            add(labelText);
            labelText.setLabel("YOU LOST");
            labelText.setColor(Color.red);
            labelText.setLocation((WIDTH - labelText.getWidth())/2, 250);
        } else if (countBricks == 0) {
            add(labelText);
            labelText.setLabel("YOU WIN");
            labelText.setColor(Color.green);
            labelText.setLocation((WIDTH - labelText.getWidth())/2, 250);
        }
    }

    // Create paddle.
    public GRect paddle() {
        GRect paddle = new GRect(
                (getWidth() - PADDLE_WIDTH) / 2.0,
                getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET,
                PADDLE_WIDTH,
                PADDLE_HEIGHT
        );
        paddle.setFilled(true);
        paddle.setColor(Color.black);
        return paddle;
    }

    // Create ball and set vx/vy for it.
    public GOval ball() {
        GOval ball = new GOval(0,0,
                BALL_RADIUS * 2,
                BALL_RADIUS * 2
        );
        ball.setFilled(true);
        ball.setFillColor(Color.black);
        ball.setColor(Color.red);

        RandomGenerator rgen = RandomGenerator.getInstance();
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5)) {
            vx = -vx;
        }
        vy = 3.0;

        return ball;
    }

    // Method for create bricks.
    private void createBricks() {
        int xStart = (WIDTH - ((NBRICKS_PER_ROW * BRICK_WIDTH) + ((NBRICKS_PER_ROW - 1) * BRICK_SEP))) / 2;
        int yStart = BRICK_Y_OFFSET;

        for (int i = 0; i < NBRICK_ROWS; i++) {
            for (int k = 0; k < NBRICKS_PER_ROW; k++) {
                GRect b = new GRect(xStart + k * (BRICK_WIDTH + BRICK_SEP),
                        yStart + (i * (BRICK_HEIGHT + BRICK_SEP)),
                        BRICK_WIDTH,
                        BRICK_HEIGHT
                );
                b.setFilled(true);
                b.setFillColor(colors[i / 2]);
                b.setColor(colors[i / 2]);
                add(b);
            }
        }
    }

    // Label for create text in game ("Turns left", "You win", "You lost")
    private GLabel labelText() {
        GLabel l = new GLabel("", 0,0);
        l.setFont("Arial -50");
        return l;
    }

    // Mouse listener for control paddle with mouse.
    public void mouseMoved(MouseEvent e) {
        double x = e.getX() - PADDLE_WIDTH / 2.0;
        if (x > getWidth() - PADDLE_WIDTH) {
            x = getWidth() - PADDLE_WIDTH;
        } else if (x < 0) {
            x = 0;
        }
        paddle.setLocation(x, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
    }
}
