package spaceslider.com;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

public class GameLoopThread extends Thread
{
    static final long FPS = 10;
    static final int COLLISION_DELAY = 20;
    private GameView view;
    private boolean running = false;
    private boolean collission = false;
    private int collission_counter = 50;

    public GameLoopThread(GameView view)
    {
        this.view = view;
    }

    public void setRunning(boolean run)
    {
        running = run;
    }

    /* From the game view the collisions are sent here */
    public void setCollission(boolean coll)
    {
        collission = coll;
        collission_counter = COLLISION_DELAY;
    }

    public boolean getCollission()
    {
        return collission;
    }

    private void control_rock_reset(sprite_character rock_to_reset)
    {
        Random l_rn;

        l_rn=new Random();

        rock_to_reset.resetRock(l_rn.nextInt(7));
    }

    @Override
    public void run()
    {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        Canvas c = null;
        int rock_idx;

        /* Initialise all the rocks to their first positions */
        for (rock_idx=0;rock_idx<view.NUMBER_OF_ROCKS;rock_idx++)
        {
            control_rock_reset(view.rockarray[rock_idx]);
        }

        while (running)
        {
            startTime = System.currentTimeMillis();

            /* Draw the updated canvas */
            try
            {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder())
                {

                    /* Update all of the characters */
                    for (rock_idx=0;rock_idx<view.NUMBER_OF_ROCKS;rock_idx++)
                    {
                        view.rockarray[rock_idx].updateRock(c);
                    }
                    view.CharacterDraw(c);

                    /* Reset the rock to the top of the screen */
                    for (rock_idx=0;rock_idx<view.NUMBER_OF_ROCKS;rock_idx++)
                    {
                        if (view.rockarray[rock_idx].y > c.getHeight())
                        {
                            control_rock_reset(view.rockarray[rock_idx]);
                        }
                    }
                }
            }
            finally
            {
                if (c != null)
                {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }


            /* Collision is passed to the game loop - we check here */
            if (collission == false)
            {
                view.checkCollisions();
            }
            else
            {
                for (rock_idx=0;rock_idx<view.NUMBER_OF_ROCKS;rock_idx++)
                {
                    control_rock_reset(view.rockarray[rock_idx]);
                }
                collission_counter--;
                if (collission_counter < 0)
                {
                    collission = false;
                }
            }

            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try
            {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            }
            catch (Exception e)
            {
            }
        }
        /* Do a final canvas draw on exit */
        try
        {
            c = view.getHolder().lockCanvas();
            synchronized (view.getHolder())
            {
                view.CharacterDraw(c);
            }
        }
        finally
        {
            if (c != null)
            {
                view.getHolder().unlockCanvasAndPost(c);
            }
        }
    }
}