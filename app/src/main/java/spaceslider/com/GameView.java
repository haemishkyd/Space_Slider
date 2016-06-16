package spaceslider.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.Random;

/**
 * Created by haemish on 2016/04/18.
 */
public class GameView extends SurfaceView
{
    private Bitmap spaceship_normal;
    private Bitmap spaceship_col_1;
    private Bitmap spaceship_col_2;
    private Bitmap spaceship_col_3;
    private Bitmap spaceship_col_4;
    private Bitmap spaceship_col_5;
    private Bitmap rock_1;
    private Bitmap rock_2;
    private Bitmap right_arrow;
    private Bitmap left_arrow;
    private Bitmap supply_item_pic;
    private Bitmap ship_with_supplies_pic;
    private Bitmap supply_acceptor_pic;
    private Bitmap supply_acceptor_pic_right;
    private Bitmap supply_acceptor_pic_with_supplies;
    private Bitmap supply_acceptor_pic_with_supplies_right;

    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
    public  float controlLeftTop;
    public  float controlLeftLeft;
    public  float controlLeftBottom;
    public  float controlLeftRight;
    public  float controlRightTop;
    public  float controlRightLeft;
    public  float controlRightBottom;
    public  float controlRightRight;

    /* Game characters */
    public static final int NUMBER_OF_ROCKS = 8;
    public static final int NUMBER_OF_STARS = 80;
    public static final int PIXELS_PER_LINE = 80;
    public static final int LEVEL_UP_SCORE = 20;
    public static final int COLLISION_DELAY_COUNT = 4;
    public static final int SUPPLY_DELAY_COUNT = 100;
    public static final int SUPPLY_DELIVERED_COUNT=50;
    public sprite_character rockarray[];
    public star_formation stars[];
    public sprite_character ship_character;
    public sprite_character ship_collission_1;
    public sprite_character ship_collission_2;
    public sprite_character ship_collission_3;
    public sprite_character ship_collission_4;
    public sprite_character ship_collission_5;
    public sprite_character supply_item;
    public sprite_character ship_with_supplies;
    public boolean initial_positions_set = false;

    /* Game parameters */
    public  int   number_of_lives = 3;
    public  int   score_in_game = 0;
    public  int   game_level = 1;

    private int   collision_draw=0;
    private int   collision_delay = 0;

    private int   supply_countdown = 0;
    public  int   delivered_countdown = 0;

    public int   supplies_retrieved = 0;

    public static final int RIGHT = 223;
    public static final int LEFT = 189;

    public GameView(Context context)
    {
        super(context);
        int rock_idx;
        Canvas c = null;

        //Create game loop and game characters
        gameLoopThread = new GameLoopThread(this);

        //create all the characters
        spaceship_normal = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px);
        spaceship_col_1 = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_1);
        spaceship_col_2 = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_2);
        spaceship_col_3 = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_3);
        spaceship_col_4 = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_4);
        spaceship_col_5 = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_5);
        rock_1 = BitmapFactory.decodeResource(getResources(),R.drawable.asteroid_01);
        rock_2 = BitmapFactory.decodeResource(getResources(),R.drawable.asteroid_02);
        left_arrow = BitmapFactory.decodeResource(getResources(),R.drawable.left_arrow);
        right_arrow = BitmapFactory.decodeResource(getResources(),R.drawable.right_arrow);
        supply_item_pic = BitmapFactory.decodeResource(getResources(),R.drawable.supplies);
        ship_with_supplies_pic = BitmapFactory.decodeResource(getResources(),R.drawable.kspaceduel_spaceship_with_supplies);
        supply_acceptor_pic = BitmapFactory.decodeResource(getResources(),R.drawable.supply_acceptor);
        supply_acceptor_pic_right = BitmapFactory.decodeResource(getResources(),R.drawable.supply_acceptor_right);
        supply_acceptor_pic_with_supplies = BitmapFactory.decodeResource(getResources(),R.drawable.supply_acceptor_with_supplies);
        supply_acceptor_pic_with_supplies_right = BitmapFactory.decodeResource(getResources(),R.drawable.supply_acceptor_with_supplies_right);


        ship_character = new sprite_character(this,true,spaceship_normal);
        ship_collission_1 = new sprite_character(this,true,spaceship_col_1);
        ship_collission_2 = new sprite_character(this,true,spaceship_col_2);
        ship_collission_3 = new sprite_character(this,true, spaceship_col_3);
        ship_collission_4 = new sprite_character(this,true, spaceship_col_4);
        ship_collission_5 = new sprite_character(this,true, spaceship_col_5);
        supply_item = new sprite_character(this,false,supply_item_pic);
        ship_with_supplies = new sprite_character(this,false,ship_with_supplies_pic);

        rockarray = new sprite_character[NUMBER_OF_ROCKS];
        for (rock_idx=0;rock_idx<NUMBER_OF_ROCKS;rock_idx++)
        {
            if (rock_idx%2 == 0)
            {
                rockarray[rock_idx] = new sprite_character(this,false, rock_1);
            }
            else
            {
                rockarray[rock_idx] = new sprite_character(this,false, rock_2);
            }
        }
        stars = new star_formation[NUMBER_OF_STARS];
        for (rock_idx=0;rock_idx<NUMBER_OF_STARS;rock_idx++)
        {
            stars[rock_idx] = new star_formation();
        }
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry)
                {
                    try
                    {
                        gameLoopThread.join();
                        retry = false;
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height)
            {
            }
        });

    }

    public void updateScore(int amount)
    {
        score_in_game += amount;
        if (((score_in_game%LEVEL_UP_SCORE) == 0) && (score_in_game > 0))
        {
            level_up_func();
        }
    }

    public void level_up_func()
    {
        game_level++;
    }

    public void checkCollisions()
    {
        int rock_idx;
        int shipx;
        int shipy;
        int shipwidth;

        shipx = ship_character.x;
        shipy = ship_character.y;
        shipwidth = ship_character.width;

        for (rock_idx=0;rock_idx<NUMBER_OF_ROCKS;rock_idx++)
        {
            if (((rockarray[rock_idx].x+(rockarray[rock_idx].width*0.9))>shipx) &&  (rockarray[rock_idx].x < (shipx+shipwidth)))
            {
                if (((rockarray[rock_idx].y+(rockarray[rock_idx].height*0.5))>shipy) && (rockarray[rock_idx].y < (shipy+(ship_character.height*0.5))))
                {
                    gameLoopThread.setCollission(true);
                    number_of_lives--;
                    return;
                }
            }
        }
        if (((supply_item.x+(supply_item.width*0.9))>shipx) &&  (supply_item.x < (shipx+shipwidth)))
        {
            if (((supply_item.y+(supply_item.height*0.5))>shipy) && (supply_item.y < (shipy+(ship_character.height*0.5))))
            {
                gameLoopThread.setSupplyCollission(true);
                supply_item.DrawState = false;
                supply_item.y = 0;
                supply_item.current_line = 0;
                supply_countdown = SUPPLY_DELAY_COUNT;
            }
        }
    }

    public void setControlAction(float xtouch, float ytouch)
    {
        if ((xtouch > controlLeftLeft) && (xtouch < controlLeftRight))
        {
            ship_character.updateShip(gameLoopThread.getCanvas(),LEFT);
            ship_collission_1.updateShip(gameLoopThread.getCanvas(),LEFT);
            ship_collission_2.updateShip(gameLoopThread.getCanvas(),LEFT);
            ship_collission_3.updateShip(gameLoopThread.getCanvas(),LEFT);
            ship_collission_4.updateShip(gameLoopThread.getCanvas(),LEFT);
            ship_collission_5.updateShip(gameLoopThread.getCanvas(),LEFT);
            ship_with_supplies.updateShip(gameLoopThread.getCanvas(),LEFT);
        }
        if ((xtouch > controlRightLeft) && (xtouch < controlRightRight))
        {
            ship_character.updateShip(gameLoopThread.getCanvas(),RIGHT);
            ship_collission_1.updateShip(gameLoopThread.getCanvas(),RIGHT);
            ship_collission_2.updateShip(gameLoopThread.getCanvas(),RIGHT);
            ship_collission_3.updateShip(gameLoopThread.getCanvas(),RIGHT);
            ship_collission_4.updateShip(gameLoopThread.getCanvas(),RIGHT);
            ship_collission_5.updateShip(gameLoopThread.getCanvas(),RIGHT);
            ship_with_supplies.updateShip(gameLoopThread.getCanvas(),RIGHT);
        }
    }

    protected void CharacterDraw(Canvas canvas)
    {
        int rock_idx;
        int bottom_of_level_ind;
        canvas.drawColor(Color.BLACK);

        if (!initial_positions_set)
        {
            initial_positions_set=true;
            ship_character.initialise_position((canvas.getHeight() - spaceship_normal.getHeight()),((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            ship_collission_1.initialise_position((canvas.getHeight() - spaceship_normal.getHeight()),((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            ship_collission_2.initialise_position((canvas.getHeight() - spaceship_normal.getHeight()),((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            ship_collission_3.initialise_position((canvas.getHeight() - spaceship_normal.getHeight()),((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            ship_collission_4.initialise_position((canvas.getHeight() - spaceship_normal.getHeight()),((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            ship_collission_5.initialise_position((canvas.getHeight() - spaceship_normal.getHeight()),((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            supply_item.initialise_position(0,((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            ship_with_supplies.initialise_position((canvas.getHeight() - spaceship_normal.getHeight()),((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            for (rock_idx=0;rock_idx<NUMBER_OF_ROCKS;rock_idx++)
            {
                rockarray[rock_idx].initialise_position(0,((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            }
        }
        //Get the control area
        controlLeftTop = canvas.getHeight()/2;
        controlLeftLeft = 0;
        controlLeftBottom = canvas.getHeight();
        controlLeftRight = canvas.getWidth()/5;
        controlRightTop = canvas.getHeight()/2;
        controlRightLeft = canvas.getWidth() - (canvas.getWidth()/5);
        controlRightBottom = canvas.getHeight();
        controlRightRight = canvas.getWidth();
        //draw the control rectangles
        Paint myPaint = new Paint();
        myPaint.setColor(Color.rgb(0, 64, 127));
        myPaint.setStrokeWidth(10);
        canvas.drawRect(0, 0, controlRightRight, canvas.getHeight()/20, myPaint);
        Rect src = new Rect(0, 0, right_arrow.getWidth(), right_arrow.getHeight());
        Rect dst = new Rect((int)controlRightLeft+(canvas.getHeight()/40), (int)controlRightTop, (int)controlRightRight, (int)controlRightBottom);
        canvas.drawBitmap(right_arrow,src,dst,null);
        src = new Rect(0, 0, left_arrow.getWidth(), left_arrow.getHeight());
        dst = new Rect((int)controlLeftLeft, (int)controlLeftTop, (int)controlLeftRight-(canvas.getHeight()/40), (int)controlLeftBottom);
        canvas.drawBitmap(left_arrow,src,dst,null);

        /* Write the number of lives on the screen*/
        myPaint.setColor(Color.WHITE);
        myPaint.setTextSize(canvas.getHeight()/25);
        String lives_string = "Ships: "+ Integer.toString(number_of_lives);
        canvas.drawText(lives_string, canvas.getWidth()-(lives_string.length()*40), canvas.getHeight()/25, myPaint);
        if (number_of_lives == 0)
        {
            myPaint.setColor(Color.RED);
            canvas.drawText("Game Over", canvas.getWidth()/2-200, canvas.getHeight()/2, myPaint);
        }

        /* Write the number of lives on the screen*/
        myPaint.setColor(Color.GREEN);
        myPaint.setTextSize(canvas.getHeight()/25);
        lives_string = "Score: "+ Integer.toString(score_in_game);
        canvas.drawText(lives_string, 10, canvas.getHeight()/25, myPaint);

        for (rock_idx=0;rock_idx<NUMBER_OF_STARS;rock_idx++)
        {
            if (stars[rock_idx].display_star)
            {
            /* Draw the stars*/
                myPaint.setColor(Color.WHITE);
                myPaint.setTextSize(canvas.getHeight()/25);
                lives_string = ".";
                canvas.drawText(lives_string, stars[rock_idx].x, stars[rock_idx].y, myPaint);
            }
        }

        /* Number of supplies received */
        myPaint.setStrokeWidth(2);
        int last_top=120;
        int draw_filled_in = 0;
        for (int x_1=6; x_1>0;x_1--)
        {
            for (int x_2=0; x_2<3;x_2++)
            {
                if (draw_filled_in < supplies_retrieved)
                {
                    draw_filled_in++;
                    myPaint.setColor(Color.YELLOW);
                    myPaint.setStyle(Paint.Style.FILL);
                }
                else
                {
                    myPaint.setColor(Color.RED);
                    myPaint.setStyle(Paint.Style.STROKE);
                }
                canvas.drawCircle(canvas.getHeight()/25+(canvas.getHeight()/25*x_2), last_top, canvas.getHeight()/50, myPaint);
            }
            last_top = last_top + canvas.getHeight()/25;
        }


        if (delivered_countdown > 0)
        {
            delivered_countdown--;
        }
        if ((ship_with_supplies.AtLeftEnd) && (delivered_countdown > 0))
        {
                /* Supply Acceptor Left */
            src = new Rect(0, 0, supply_acceptor_pic_with_supplies.getWidth(), supply_acceptor_pic_with_supplies.getHeight());
            dst = new Rect((int) controlLeftRight - 30, (int) controlLeftBottom - supply_acceptor_pic_with_supplies.getHeight(), (int) controlLeftRight + 40, (int) controlLeftBottom);
            canvas.drawBitmap(supply_acceptor_pic_with_supplies, src, dst, null);
        } else
        {
                /* Supply Acceptor Left */
            src = new Rect(0, 0, supply_acceptor_pic.getWidth(), supply_acceptor_pic.getHeight());
            dst = new Rect((int) controlLeftRight - 30, (int) controlLeftBottom - supply_acceptor_pic.getHeight(), (int) controlLeftRight + 40, (int) controlLeftBottom);
            canvas.drawBitmap(supply_acceptor_pic, src, dst, null);
        }
        if ((ship_with_supplies.AtRightEnd) && (delivered_countdown > 0))
        {
                /* Supply Acceptor Right */
            src = new Rect(0, 0, supply_acceptor_pic_with_supplies_right.getWidth(), supply_acceptor_pic_with_supplies_right.getHeight());
            dst = new Rect((int) controlRightLeft - 40, (int) controlRightBottom - supply_acceptor_pic_with_supplies_right.getHeight(), (int) controlRightLeft + 30, (int) controlRightBottom);
            canvas.drawBitmap(supply_acceptor_pic_with_supplies_right, src, dst, null);
        } else
        {
                /* Supply Acceptor Right */
            src = new Rect(0, 0, supply_acceptor_pic_right.getWidth(), supply_acceptor_pic_right.getHeight());
            dst = new Rect((int) controlRightLeft - 40, (int) controlRightBottom - supply_acceptor_pic_right.getHeight(), (int) controlRightLeft + 30, (int) controlRightBottom);
            canvas.drawBitmap(supply_acceptor_pic_right, src, dst, null);
        }

        /* Draw the actual characters */
        if (gameLoopThread.getCollission() == false)
        {
            if (gameLoopThread.getSupplyCollission() == true)
            {
                supply_countdown--;
                if (supply_countdown > 0)
                {
                    ship_with_supplies.drawShip(canvas, ship_with_supplies_pic);
                }
                else
                {
                    gameLoopThread.setSupplyCollission(false);
                    ship_character.drawShip(canvas, ship_character.sourceImage);
                }
            }
            else
            {
                ship_character.drawShip(canvas, ship_character.sourceImage);
            }
            for (rock_idx=0;rock_idx<NUMBER_OF_ROCKS;rock_idx++)
            {
                if (rockarray[rock_idx].DrawState == true)
                {
                    rockarray[rock_idx].drawRock(canvas, rockarray[rock_idx].sourceImage);
                }
            }
            if (supply_item.DrawState == true)
            {
                supply_item.drawSupply(canvas, supply_item_pic);
            }
            collision_draw = 0;
            collision_delay = 0;
        }
        else
        {
            collision_delay++;
            switch(collision_draw)
            {
                case 0:
                    ship_collission_1.x=ship_character.x;
                    ship_collission_1.y=ship_character.y;
                    ship_collission_1.drawShip(canvas, ship_collission_1.sourceImage);
                    if (collision_delay >= COLLISION_DELAY_COUNT)
                    {
                        collision_draw = 1;
                        collision_delay = 0;
                    }
                    break;
                case 1:
                    ship_collission_2.x=ship_character.x;
                    ship_collission_2.y=ship_character.y;
                    ship_collission_2.drawShip(canvas,ship_collission_2.sourceImage);
                    if (collision_delay >= COLLISION_DELAY_COUNT)
                    {
                        collision_draw = 2;
                        collision_delay = 0;
                    }
                    break;
                case 2:
                    ship_collission_3.x=ship_character.x;
                    ship_collission_3.y=ship_character.y;
                    ship_collission_3.drawShip(canvas,ship_collission_3.sourceImage);
                    if (collision_delay >= COLLISION_DELAY_COUNT)
                    {
                        collision_draw = 3;
                        collision_delay = 0;
                    }
                    break;
                case 3:
                    ship_collission_4.x=ship_character.x;
                    ship_collission_4.y=ship_character.y;
                    ship_collission_4.drawShip(canvas,ship_collission_4.sourceImage);
                    if (collision_delay >= COLLISION_DELAY_COUNT)
                    {
                        collision_draw = 4;
                        collision_delay = 0;
                    }
                    break;
                case 4:
                    ship_collission_5.x=ship_character.x;
                    ship_collission_5.y=ship_character.y;
                    ship_collission_5.drawShip(canvas,ship_collission_5.sourceImage);
                    if (collision_delay >= COLLISION_DELAY_COUNT)
                    {
                        collision_draw = 5;
                        collision_delay = 0;
                    }
                    break;
                case 5:
                    ship_character.drawShip(canvas, ship_character.sourceImage);
                    break;
            }
        }
    }
}

