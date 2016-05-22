package spaceslider.com;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

public class GameLoopThread extends Thread
{
    static final long FPS = 2;
    static final int COLLISION_DELAY = 20;
    private GameView view;
    private boolean running = false;
    private boolean collission = false;
    private int collission_counter = 50;
    private int rocks_per_line = 2;

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
        int x_pos;
        int rock_idx;
        boolean exit=true;

        l_rn=new Random();

        do
        {
            exit = true;
            x_pos = l_rn.nextInt(7);
            for (rock_idx = 0; rock_idx < view.NUMBER_OF_ROCKS; rock_idx++)
            {
                if ((view.rockarray[rock_idx].current_line == 0) && (view.rockarray[rock_idx].DrawState == true))
                {
                    if (view.rockarray[rock_idx].x == view.rockarray[rock_idx].presetStartX[x_pos])
                    {
                        exit = false;
                    }
                }
            }
        } while (exit == false);

        rock_to_reset.resetRock(x_pos);
    }

    @Override
    public void run()
    {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        Canvas c = null;
        int rock_idx;
        int line_idx;

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
                    int max_lines=c.getHeight()/20;
                    int rocks_on_lines_array[];

                    rocks_on_lines_array = new int[max_lines+1];

                    /* Where are all the rocks */
                    for (rock_idx=0;rock_idx<view.NUMBER_OF_ROCKS;rock_idx++)
                    {
                        rocks_on_lines_array[view.rockarray[rock_idx].current_line]++;
                    }

                    int local_counter = 0;
                    if (rocks_on_lines_array[1] == 0)
                    {
                        for (rock_idx=0;rock_idx<view.NUMBER_OF_ROCKS;rock_idx++)
                        {
                            if (view.rockarray[rock_idx].DrawState == false)
                            {
                                view.rockarray[rock_idx].DrawState = true;
                                control_rock_reset(view.rockarray[rock_idx]);
                                local_counter++;
                                if (local_counter >= rocks_per_line)
                                {
                                    break;
                                }
                            }
                        }
                    }

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
                    view.rockarray[rock_idx].DrawState = false;
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