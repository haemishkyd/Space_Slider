package spaceslider.com;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

public class GameLoopThread extends Thread
{
;

    private int CurrentRockXPosition = 800;
    private int[] LastCurrentRockXPosition;
    private int PositionIndex = 0;
    private GameView view;
    private boolean running = false;
    private boolean collission = false;
    private int collission_counter = 50;
    private Random rnd;
    private int counted_spaces_between_rocks = 0;
    private int counter_spaces_between_stars = 0;
    private int counter_between_extra_lives = 0;

    /* Stage information */
    private int rocks_per_line = 2;
    private int spaces_between_rocks = 400;
    private int spaces_between_stars = 5;
    private int rock_speed = 0;
    /* Star counter */
    private int star_counter;


    private int TheCanvasWidth;

    public GameLoopThread(GameView view)
    {
        this.view = view;
        rnd = new Random();
        LastCurrentRockXPosition = new int[GameView.POSITION_HISTORY_POINTS];
    }

    public void setRunning(boolean run)
    {
        running = run;
    }

    /* From the game view the collisions are sent here */
    public void setCollission(boolean coll)
    {
        collission = coll;
        collission_counter = GameView.COLLISION_DELAY;
    }

    public boolean getCollission()
    {
        return collission;
    }

    public void setSupplyCollission(boolean supply_coll,ship_character local_ship_char,int type)
    {
        if (type == supply_character.EX_LIVES)
        {
            local_ship_char.WithSuppliesState = type;
            view.number_of_lives++;
        }
        else
        {
            local_ship_char.WithSuppliesState = type;
        }
    }

    public int getSupplyCollission(ship_character local_ship_char)
    {
        return local_ship_char.WithSuppliesState;
    }

    public int getCanvasWidth()
    {
        return TheCanvasWidth;
    }

    private void star_control(Canvas c)
    {
        int star_idx;
        if (counter_spaces_between_stars == 0)
        {
            for (star_idx = 0; star_idx < view.GameVar_NUMBER_OF_STARS; star_idx++)
            {
                if ((star_counter < GameView.NUMBER_OF_STARS_PER_LINE) && (!view.stars[star_idx].display_star))
                {
                    star_counter++;
                    view.stars[star_idx].display_star = true;
                    view.stars[star_idx].initialise_star(c,view.controlLeftRight);
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

    private void calculate_random_position()
    {
        int check_pos_idx;
        int position_state_machine = 0;
        int lower_bound_of_random_field;
        int size_of_random_field;

        while (position_state_machine != 3)
        {
            switch (position_state_machine)
            {
                case 0:
                    if (view.ship_character.AtLeftEnd)
                    {
                        size_of_random_field = (int) view.controlRightRight - ((int) (view.controlLeftRight * 1.2 * 2.0));
                        /* This concentrates the asteroids on the left side of the field */
                        size_of_random_field /= 2;
                        lower_bound_of_random_field = (int) (view.controlLeftRight);
                    }
                    else if (view.ship_character.AtRightEnd)
                    {
                        size_of_random_field = (int) view.controlRightRight - ((int) (view.controlLeftRight * 1.2 * 2.0));
                        /* This concentrates the asteroids on the right side of the field */
                        size_of_random_field /= 2;
                        lower_bound_of_random_field = (int) (view.controlLeftRight);
                        lower_bound_of_random_field += size_of_random_field;
                    }
                    else
                    {
                        size_of_random_field = (int) view.controlRightRight - ((int) (view.controlLeftRight * 1.2 * 2.0));
                        lower_bound_of_random_field = (int) (view.controlLeftRight);
                    }
                    CurrentRockXPosition = rnd.nextInt(size_of_random_field) + lower_bound_of_random_field;
                    position_state_machine = 1;
                    break;
                case 1:
                    position_state_machine = 2;
                    for (check_pos_idx = 0; check_pos_idx < GameView.POSITION_HISTORY_POINTS; check_pos_idx++)
                    {
                        if ((Math.abs(CurrentRockXPosition - LastCurrentRockXPosition[check_pos_idx]) < (((int) view.controlRightRight) / 20)))
                        {
                            position_state_machine = 0;
                        }
                    }
                    break;
                case 2:
                    /* Store the last position **********************************************************************************************/
                    LastCurrentRockXPosition[PositionIndex] = CurrentRockXPosition;
                    PositionIndex++;
                    if (PositionIndex >= GameView.POSITION_HISTORY_POINTS)
                    {
                        PositionIndex = 0;
                    }
                    position_state_machine = 3;
                    break;
                case 3:
                    position_state_machine = 0;
                    break;
                default:
                    position_state_machine = 0;
                    break;
            }
        }
    }

    private void obstacle_control(Canvas c,int l_rock_speed)
    {
        int rock_idx;
        int supply_idx;
        int counted_rocks_per_line = 0;
        boolean already_had_a_supply=false;

        /* New Rocks *********************************************************************************************************************/
        if (counted_spaces_between_rocks == 0)
        {
            /* Initialise all the rocks to their first positions */
            for (rock_idx = 0; rock_idx < view.GameVar_NUMBER_OF_ROCKS; rock_idx++)
            {
                /* Initialise new rocks if required */
                if (view.rockarray[rock_idx].DrawState == false)
                {
                    calculate_random_position();

                    /* Now decide what we want the next obstacle to be ********************************************************************/
                    int is_it_a_supply_item = rnd.nextInt(1000);
                    if((is_it_a_supply_item >480) && (is_it_a_supply_item <520) && (!view.supply_item_array[supply_character.ACCUM].DrawState) && (!already_had_a_supply))
                    {
                        counter_between_extra_lives++;
                        if (counter_between_extra_lives > GameView.NUMBER_OF_SUPPLIES_BETWEEN_EXTRA_LIVES)
                        {
                            view.supply_item_array[supply_character.EX_LIVES].DrawState = true;
                            view.supply_item_array[supply_character.EX_LIVES].x = CurrentRockXPosition;
                            counter_between_extra_lives = 0;
                            already_had_a_supply = true;
                        }
                        else
                        {
                            view.supply_item_array[supply_character.ACCUM].DrawState = true;
                            view.supply_item_array[supply_character.ACCUM].x = CurrentRockXPosition;
                            already_had_a_supply = true;
                        }
                    }
                    else if((is_it_a_supply_item >300) && (is_it_a_supply_item <700) && (!view.supply_item_array[0].DrawState) && (!already_had_a_supply))
                    {
                        view.supply_item_array[supply_character.NORMAL].DrawState = true;
                        view.supply_item_array[supply_character.NORMAL].x = CurrentRockXPosition;
                        already_had_a_supply = true;
                    }
                    else
                    {
                        view.rockarray[rock_idx].DrawState = true;
                        view.rockarray[rock_idx].x = CurrentRockXPosition;
                    }

                    /* Make sure we only have the requisite rocks every cycle ***************************************************************/
                    /* If the screen is large increase the number of rocks per line */
                    if (c.getWidth() > 2000)
                    {
                        rocks_per_line = 3;
                    }
                    else
                    {
                        rocks_per_line = 2;
                    }
                    counted_rocks_per_line++;
                    if (counted_rocks_per_line >= rocks_per_line)
                    {
                        break;
                    }
                }
            }
        }
        /* New Rocks Done now what **********************************************************************************************************/
        for (rock_idx = 0; rock_idx < view.GameVar_NUMBER_OF_ROCKS; rock_idx++)
        {
            /* Update the score and reset rocks to the top */
            if (view.rockarray[rock_idx].y > c.getHeight()) {
                view.updateScore(1);
                view.rockarray[rock_idx].DrawState = false;
                view.rockarray[rock_idx].y = 0;
                view.rockarray[rock_idx].current_line = 0;
            }
            /* Run the rocks */
            view.rockarray[rock_idx].updateRock(c,l_rock_speed);
        }
        for (supply_idx = 0; supply_idx < view.NUMBER_OF_SUPPLIES; supply_idx++)
        {
        /* Update the supply items */
            if (view.supply_item_array[supply_idx].y > c.getHeight())
            {
                view.supply_item_array[supply_idx].DrawState = false;
                view.supply_item_array[supply_idx].y = 0;
                view.supply_item_array[supply_idx].current_line = 0;
            }

            view.supply_item_array[supply_idx].updateSupply(c, l_rock_speed);
        }

        if ((view.ship_character.AtLeftEnd) || (view.ship_character.AtRightEnd))
        {
            if (view.ship_character.WithSuppliesState != supply_character.NONE)
            {
                if (view.ship_character.AtLeftEnd)
                {
                    view.left_delivered_countdown = view.SUPPLY_DELIVERED_COUNT;
                }
                if (view.ship_character.AtRightEnd)
                {
                    view.right_delivered_countdown = view.SUPPLY_DELIVERED_COUNT;
                }

                if (view.ship_character.WithSuppliesState == supply_character.NORMAL)
                {
                    view.updateScore(5);
                    view.supplies_retrieved++;
                    view.ship_character.WithSuppliesState = supply_character.NONE;
                }
                if (view.ship_character.WithSuppliesState == supply_character.ACCUM)
                {
                    view.updateScore(10);
                    view.acc_supplies_retrieved++;
                    view.ship_character.WithSuppliesState = supply_character.NONE;
                }
                if (view.supplies_retrieved >= GameView.NUMBER_OF_SUPPLIES_BEFORE_ACCUM)
                {
                    view.supplies_retrieved = 0;
                    view.acc_supplies_retrieved++;
                }
            }

        }
        /* Rock spacing */
        counted_spaces_between_rocks++;
        if ((counted_spaces_between_rocks*rock_speed) >= spaces_between_rocks)
        {
            counted_spaces_between_rocks = 0;
        }
    }

    private int calcRockSpeed(int game_level)
    {
        int temp_rock_speed = 8;

        switch (game_level)
        {
            case 1:
                temp_rock_speed = 8;
                break;
            case 2:
                temp_rock_speed = 9;
                break;
            case 3:
                temp_rock_speed = 10;
                break;
            case 4:
                temp_rock_speed = 11;
                break;
            case 5:
                temp_rock_speed = 12;
                break;
            case 6:
                temp_rock_speed = 13;
                break;
            case 7:
                temp_rock_speed = 14;
                break;
            case 8:
                temp_rock_speed = 15;
                break;
            case 9:
                temp_rock_speed = 16;
                break;
            case 10:
                temp_rock_speed = 17;
                break;
            case 11:
                temp_rock_speed = 18;
                break;
            case 12:
                temp_rock_speed = 19;
                break;
            case 13:
                temp_rock_speed = 19;
                break;
            case 14:
                temp_rock_speed = 20;
                break;
            case 15:
                temp_rock_speed = 20;
                break;
            case 16:
                temp_rock_speed = 21;
                break;
            case 17:
                temp_rock_speed = 22;
                break;
            case 18:
                temp_rock_speed = 17;
                break;
            case 19:
                temp_rock_speed = 17;
                break;
            case 20:
                temp_rock_speed = 17;
                break;
            default:
                break;
        }

        return temp_rock_speed;
    }

    @Override
    public void run()
    {
        long ticksPS = 1000 / GameView.FPS;
        long startTime;
        long sleepTime;
        Canvas c = null;
        int rock_idx;
        int supply_idx;

        while (running)
        {
            startTime = System.currentTimeMillis();

            /* Draw the updated canvas */
            try
            {
                c = view.getHolder().lockCanvas();
                TheCanvasWidth = c.getWidth();
                synchronized (view.getHolder())
                {
                    view.setScreenParameters(c);
                    /* Get the rock speed */
                    rock_speed = calcRockSpeed(view.game_level);
                    /* Handle the obstacles for every cycle */
                    obstacle_control(c,rock_speed);
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
                for (rock_idx=0;rock_idx<view.GameVar_NUMBER_OF_ROCKS;rock_idx++)
                {
                    view.rockarray[rock_idx].DrawState = false;
                    view.rockarray[rock_idx].y=0;
                    view.rockarray[rock_idx].current_line = 0;
                }
                for (supply_idx=0;supply_idx<view.NUMBER_OF_SUPPLIES;supply_idx++)
                {
                    view.supply_item_array[supply_idx].DrawState = false;
                    view.supply_item_array[supply_idx].y = 0;
                    view.supply_item_array[supply_idx].current_line = 0;
                }
                view.ship_character.WithSuppliesState = supply_character.NONE;
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
            if (view.getHolder()!=null)
            {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder())
                {
                    view.GameEndFlag = true;
                    view.CharacterDraw(c);
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
    }
}