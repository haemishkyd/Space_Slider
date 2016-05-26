package spaceslider.com;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

public class GameLoopThread extends Thread
{
    private static final long FPS = 2;
    private static final int COLLISION_DELAY = 20;
    private static int CurrentRockXPosition = 800;
    private GameView view;
    private boolean running = false;
    private boolean collission = false;
    private int collission_counter = 50;
    private Random rnd;
    private int counted_spaces_between_rocks = 0;

    /* Stage information */
    private int rocks_per_line = 2;
    private int spaces_between_rocks = 4;

    public GameLoopThread(GameView view)
    {
        this.view = view;
        rnd = new Random();
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

    private void rock_control(Canvas c)
    {
        int rock_idx;
        int max_lines=c.getHeight()/view.PIXELS_PER_LINE;
        int counted_rocks_per_line = 0;
        int useable_screen_width = c.getWidth();

        if (counted_spaces_between_rocks == 0)
        {
        /* Initialise all the rocks to their first positions */
            for (rock_idx = 0; rock_idx < view.NUMBER_OF_ROCKS; rock_idx++)
            {
                if (view.rockarray[rock_idx].DrawState == false)
                {
                    view.rockarray[rock_idx].x = CurrentRockXPosition;
                    view.rockarray[rock_idx].DrawState = true;
                    CurrentRockXPosition = rnd.nextInt(useable_screen_width-800)+400;
                    counted_rocks_per_line++;
                    if (counted_rocks_per_line >= rocks_per_line)
                    {
                        break;
                    }
                }
            }
        }
        counted_spaces_between_rocks++;
        if (counted_spaces_between_rocks >= spaces_between_rocks)
        {
            counted_spaces_between_rocks = 0;
        }
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

        while (running)
        {
            startTime = System.currentTimeMillis();

            /* Draw the updated canvas */
            try
            {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder())
                {
                    rock_control(c);

                    /* Update all of the characters */
                    for (rock_idx=0;rock_idx<view.NUMBER_OF_ROCKS;rock_idx++)
                    {
                        if (view.rockarray[rock_idx].y > c.getHeight())
                        {
                            view.rockarray[rock_idx].DrawState = false;
                            view.rockarray[rock_idx].y = 0;
                            view.rockarray[rock_idx].current_line = 0;
                        }
                        view.rockarray[rock_idx].updateRock(c);
                    }
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

            /* Collision is passed to the game loop - we check here */
            if (collission == false)
            {
                view.checkCollisions();
            }
            else
            {
                for (rock_idx=0;rock_idx<view.NUMBER_OF_ROCKS;rock_idx++)
                {
                    view.rockarray[rock_idx].DrawState = false;
                    view.rockarray[rock_idx].y=0;
                    view.rockarray[rock_idx].current_line = 0;
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