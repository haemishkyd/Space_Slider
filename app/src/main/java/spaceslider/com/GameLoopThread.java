package spaceslider.com;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

public class GameLoopThread extends Thread
{
    private static final long FPS = 20;
    private static final int COLLISION_DELAY = 20;
    private static int ROCK_SPEED = 10;
    private static int NUMBER_OF_STARS_PER_LINE = 6;
    private static int CurrentRockXPosition = 800;
    private GameView view;
    private boolean running = false;
    private boolean collission = false;
    private int collission_counter = 50;
    private Random rnd;
    private int counted_spaces_between_rocks = 0;
    private int counter_spaces_between_stars = 0;

    /* Stage information */
    private int rocks_per_line = 2;
    private int spaces_between_rocks = 5;
    private int spaces_between_stars = 5;
    private int rock_speed = 0;
    /* Star counter */
    private int star_counter;

    private Canvas TheCanvas;

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

    public Canvas getCanvas()
    {
        return TheCanvas;
    }

    private void star_control(Canvas c)
    {
        int star_idx;
        if (counter_spaces_between_stars == 0)
        {
            for (star_idx = 0; star_idx < view.NUMBER_OF_STARS; star_idx++)
            {
                if ((star_counter < NUMBER_OF_STARS_PER_LINE) && (!view.stars[star_idx].display_star))
                {
                    star_counter++;
                    view.stars[star_idx].display_star = true;
                    view.stars[star_idx].initialise_star(c);
                }
                if (view.stars[star_idx].display_star == true)
                {
                    view.stars[star_idx].update_star();
                    view.stars[star_idx].check_star(c);
                }
            }
            star_counter = 0;
        }
        counter_spaces_between_stars++;
        if (counter_spaces_between_stars >= spaces_between_stars)
        {
            counter_spaces_between_stars = 0;
        }
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
                /* Hurl the rocks at a specific rate */
                if (view.rockarray[rock_idx].DrawState == false)
                {
                    view.rockarray[rock_idx].x = CurrentRockXPosition;
                    view.rockarray[rock_idx].DrawState = true;
                    CurrentRockXPosition = rnd.nextInt(useable_screen_width-600-view.rockarray[rock_idx].width)+300;
                    counted_rocks_per_line++;
                    if (counted_rocks_per_line >= rocks_per_line)
                    {
                        break;
                    }
                }
            }
        }
        for (rock_idx = 0; rock_idx < view.NUMBER_OF_ROCKS; rock_idx++)
        {
            /* Update the score and reset rocks to the top */
            if (view.rockarray[rock_idx].y > c.getHeight()) {
                view.updateScore(1);
                view.rockarray[rock_idx].DrawState = false;
                view.rockarray[rock_idx].y = 0;
                view.rockarray[rock_idx].current_line = 0;
            }
            /* Run the rocks */
            view.rockarray[rock_idx].updateRock(c);
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
                TheCanvas = c;
                synchronized (view.getHolder())
                {
                    /* Handle the rocks for every cycle */
                    rock_speed++;
                    if (rock_speed > (ROCK_SPEED-view.game_level)) {
                        rock_speed = 0;
                        rock_control(c);
                    }
                    /* Handle the stars for every cycle */
                    star_control(c);
                    /* Draw everything */
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
                collission_counter = 50;
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
                    if (view.number_of_lives == 0) {
                        setRunning(false);
                    }
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