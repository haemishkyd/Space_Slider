package spaceslider.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;

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
    private Bitmap supply_normal_item_pic;
    private Bitmap supply_accum_item_pic;
    private Bitmap ship_with_supplies_pic;
    private Bitmap ship_with_accum_supplies_pic;
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
    public static final int NUMBER_OF_SUPPLIES = 2;
    public static final int NUMBER_OF_STARS = 80;
    public static final int PIXELS_PER_LINE = 80;
    public static final int LEVEL_UP_SCORE = 20;
    public static final int COLLISION_DELAY_COUNT = 4;
    public static final int SUPPLY_DELAY_COUNT = 100;
    public static final int SUPPLY_DELIVERED_COUNT=50;

    /* Declare all of the screen objects */
    public rock_character rockarray[];
    public star_formation stars[];
    public ship_character ship_character;

    public supply_character supply_item_array[];

    public boolean initial_positions_set = false;

    public boolean GameEndFlag=false;
    /* Game parameters */
    public  int   number_of_lives = 3;
    public  int   score_in_game = 0;
    public  int   game_level = 1;

    private int   collision_draw=0;
    private int   collision_delay = 0;

    private int   supply_countdown = 0;
    public  int   delivered_countdown = 0;

    public int   supplies_retrieved = 0;
    public int   acc_supplies_retrieved = 0;

    public static final int RIGHT = 223;
    public static final int LEFT = 189;


    public static final int LEFT_BEING_PUSHED = 1;
    public static final int RIGHT_BEING_PUSHED = 2;
    public static final int NOTHING_BEING_PUSHED = 3;

    public int currentScreenInput = NOTHING_BEING_PUSHED;

    MainActivity localActivity;
    public GameView(Context context)
    {
        super(context);
        int rock_idx;
        Canvas c = null;

        localActivity = (MainActivity)context;

        //Create game loop and game characters
        gameLoopThread = new GameLoopThread(this);

        //create all the characters
        spaceship_normal                        = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px);
        spaceship_col_1                         = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_1);
        spaceship_col_2                         = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_2);
        spaceship_col_3                         = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_3);
        spaceship_col_4                         = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_4);
        spaceship_col_5                         = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_5);
        ship_with_supplies_pic                  = BitmapFactory.decodeResource(getResources(),R.drawable.kspaceduel_spaceship_with_supplies);
        ship_with_accum_supplies_pic            = BitmapFactory.decodeResource(getResources(),R.drawable.kspaceduel_spaceship_with_supplies_accum);

        supply_normal_item_pic                  = BitmapFactory.decodeResource(getResources(),R.drawable.supplies_normal);
        supply_accum_item_pic                   = BitmapFactory.decodeResource(getResources(),R.drawable.supplies_accum);

        rock_1                                  = BitmapFactory.decodeResource(getResources(),R.drawable.asteroid_01);
        rock_2                                  = BitmapFactory.decodeResource(getResources(),R.drawable.asteroid_02);
        left_arrow                              = BitmapFactory.decodeResource(getResources(),R.drawable.left_arrow);
        right_arrow                             = BitmapFactory.decodeResource(getResources(),R.drawable.right_arrow);

        supply_acceptor_pic                     = BitmapFactory.decodeResource(getResources(),R.drawable.supply_acceptor);
        supply_acceptor_pic_right               = BitmapFactory.decodeResource(getResources(),R.drawable.supply_acceptor_right);
        supply_acceptor_pic_with_supplies       = BitmapFactory.decodeResource(getResources(),R.drawable.supply_acceptor_with_supplies);
        supply_acceptor_pic_with_supplies_right = BitmapFactory.decodeResource(getResources(),R.drawable.supply_acceptor_with_supplies_right);

        /* Initialise the Ship and all animations associated */
        ship_character      = new ship_character(this,true,spaceship_normal);
        ship_character.setShipImages(spaceship_col_1,spaceship_col_2,spaceship_col_3,spaceship_col_4,spaceship_col_5,ship_with_supplies_pic,ship_with_accum_supplies_pic);

        /* Initialise the supplies */
        supply_item_array = new supply_character[NUMBER_OF_SUPPLIES];
        supply_item_array[0]  = new supply_character(this,false,supply_normal_item_pic,supply_character.NORMAL);
        supply_item_array[1]   = new supply_character(this,false,supply_accum_item_pic,supply_character.ACCUM);

        /* Initialise the rocks */
        rockarray = new rock_character[NUMBER_OF_ROCKS];
        for (rock_idx=0;rock_idx<NUMBER_OF_ROCKS;rock_idx++)
        {
            if (rock_idx%2 == 0)
            {
                rockarray[rock_idx] = new rock_character(this,false, rock_1);
            }
            else
            {
                rockarray[rock_idx] = new rock_character(this,false, rock_2);
            }
        }

        /* Initialise the stars */
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
        int supply_idx;
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
        for (supply_idx = 0;supply_idx<NUMBER_OF_SUPPLIES;supply_idx++)
        {
            if (((supply_item_array[supply_idx].x + (supply_item_array[supply_idx].width * 0.9)) > shipx) && (supply_item_array[supply_idx].x < (shipx + shipwidth)))
            {
                if (((supply_item_array[supply_idx].y + (supply_item_array[supply_idx].height * 0.5)) > shipy) && (supply_item_array[supply_idx].y < (shipy + (ship_character.height * 0.5))))
                {
                    gameLoopThread.setSupplyCollission(true,ship_character,supply_item_array[supply_idx].SupplyType);
                    supply_item_array[supply_idx].DrawState = false;
                    supply_item_array[supply_idx].y = 0;
                    supply_item_array[supply_idx].current_line = 0;
                    supply_countdown = SUPPLY_DELAY_COUNT;
                }
            }
        }
    }

    public void setControlAction(float xtouch, float ytouch,int action)
    {
        if (action == MotionEvent.ACTION_UP)
        {
            currentScreenInput = NOTHING_BEING_PUSHED;
        }
        else
        {
            if ((xtouch > controlLeftLeft) && (xtouch < controlLeftRight))
            {
                currentScreenInput = LEFT_BEING_PUSHED;
            }
            if ((xtouch > controlRightLeft) && (xtouch < controlRightRight))
            {
                currentScreenInput = RIGHT_BEING_PUSHED;
            }
        }
    }

    public void KillGame()
    {
        gameLoopThread.setRunning(false);
    }

    protected void CharacterDraw(Canvas canvas)
    {
        int textSize;
        int supplyCircleRadius;
        int rock_idx;
        int supply_idx;
        int bottom_of_level_ind;
        canvas.drawColor(Color.BLACK);

        if (!initial_positions_set)
        {
            initial_positions_set=true;
            ship_character.initialise_position((canvas.getHeight() - spaceship_normal.getHeight()),((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));

            for (rock_idx=0;rock_idx<NUMBER_OF_ROCKS;rock_idx++)
            {
                rockarray[rock_idx].initialise_position(0,((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            }
            for (supply_idx=0;supply_idx<NUMBER_OF_SUPPLIES;supply_idx++)
            {
                supply_item_array[supply_idx].initialise_position(0,((canvas.getWidth()/2)-(spaceship_normal.getWidth()/2)));
            }
        }

        //Get the value for drawing the two controls
        controlLeftLeft = 0;
        controlLeftBottom = canvas.getHeight();
        controlLeftRight = canvas.getWidth()/5-(canvas.getHeight()/35);
        controlRightTop = (canvas.getHeight()/3)*2;
        controlLeftTop = (canvas.getHeight()/3)*2;
        controlRightLeft = canvas.getWidth() - (canvas.getWidth()/5) + (canvas.getHeight()/35);
        controlRightBottom = canvas.getHeight();
        controlRightRight = canvas.getWidth();
        //Set the general text size
        textSize = canvas.getHeight()/25;
        //Supply circle radius
        supplyCircleRadius = canvas.getHeight()/25;

        //Draw the left right arrows based on the values above
        Paint myPaint = new Paint();
        myPaint.setColor(Color.rgb(0, 64, 127));
        myPaint.setStrokeWidth(10);
        canvas.drawRect(0, 0, controlRightRight, canvas.getHeight()/20, myPaint);
        Rect src = new Rect(0, 0, right_arrow.getWidth(), right_arrow.getHeight());
        Rect dst = new Rect((int)controlRightLeft, (int)controlRightTop, (int)controlRightRight, (int)controlRightBottom);
        canvas.drawBitmap(right_arrow,src,dst,null);
        src = new Rect(0, 0, left_arrow.getWidth(), left_arrow.getHeight());
        dst = new Rect((int)controlLeftLeft, (int)controlLeftTop, (int)controlLeftRight, (int)controlLeftBottom);
        canvas.drawBitmap(left_arrow,src,dst,null);

        /* Write the number of lives on the screen*/
        myPaint.setColor(Color.WHITE);
        myPaint.setTextSize(textSize);
        String lives_string = "Ships: "+ Integer.toString(number_of_lives);
        canvas.drawText(lives_string, canvas.getWidth()-(lives_string.length()*40), textSize, myPaint);
        if (number_of_lives == 0)
        {
            myPaint.setColor(Color.RED);
            canvas.drawText("Game Over", canvas.getWidth()/2-160, canvas.getHeight()/2, myPaint);
            if (GameEndFlag == true)
            {
                canvas.drawText("Touch the screen to continue!", canvas.getWidth()/2-320, canvas.getHeight()/2+canvas.getHeight()/20, myPaint);
            }
        }

        if (currentScreenInput == LEFT_BEING_PUSHED)
        {
            ship_character.updateShip(gameLoopThread.getCanvasWidth(), LEFT);
        }
        else if (currentScreenInput == RIGHT_BEING_PUSHED)
        {
            ship_character.updateShip(gameLoopThread.getCanvasWidth(), RIGHT);
        }
        /* Write the number of lives on the screen*/
        myPaint.setColor(Color.GREEN);
        myPaint.setTextSize(textSize);
        lives_string = "Score: "+ Integer.toString(score_in_game);
        canvas.drawText(lives_string, 10, textSize, myPaint);

        for (rock_idx=0;rock_idx<NUMBER_OF_STARS;rock_idx++)
        {
            if (stars[rock_idx].display_star)
            {
            /* Draw the stars*/
                myPaint.setColor(Color.WHITE);
                myPaint.setTextSize(textSize);
                lives_string = ".";
                canvas.drawText(lives_string, stars[rock_idx].x, stars[rock_idx].y, myPaint);
            }
        }

        /* Number of supplies received */
        myPaint.setStrokeWidth(2);
        int last_top=canvas.getHeight()/8;
        int draw_filled_in = 0;
        for (int x_1=2; x_1>0;x_1--)
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
                    myPaint.setColor(Color.YELLOW);
                    myPaint.setStyle(Paint.Style.STROKE);
                }
                canvas.drawCircle(supplyCircleRadius+(supplyCircleRadius*x_2*2), last_top, supplyCircleRadius, myPaint);
            }
            last_top = last_top + supplyCircleRadius*2;
        }
        /* Accumulated supplies */
        last_top=canvas.getHeight()/8+(3*(supplyCircleRadius*2));
        draw_filled_in = 0;
        for (int x_1=3; x_1>0;x_1--)
        {
            for (int x_2=0; x_2<3;x_2++)
            {
                if (draw_filled_in < acc_supplies_retrieved)
                {
                    draw_filled_in++;
                    myPaint.setColor(Color.BLUE);
                    myPaint.setStyle(Paint.Style.FILL);
                }
                else
                {
                    myPaint.setColor(Color.BLUE);
                    myPaint.setStyle(Paint.Style.STROKE);
                }
                canvas.drawCircle(supplyCircleRadius+(supplyCircleRadius*x_2*2), last_top, supplyCircleRadius, myPaint);
            }
            last_top = last_top + supplyCircleRadius*2;
        }


        if (delivered_countdown > 0)
        {
            delivered_countdown--;
        }
        if ((ship_character.AtLeftEnd) && (delivered_countdown > 0))
        {
            /* Supply Acceptor Left */
            src = new Rect(0, 0, supply_acceptor_pic_with_supplies.getWidth(), supply_acceptor_pic_with_supplies.getHeight());
            dst = new Rect((int) controlLeftRight, (int) controlLeftBottom - supply_acceptor_pic_with_supplies.getHeight(), (int) controlLeftRight+(supply_acceptor_pic.getWidth()/2), (int) controlLeftBottom);
            canvas.drawBitmap(supply_acceptor_pic_with_supplies, src, dst, null);
        }
        else
        {
            /* Supply Acceptor Left */
            src = new Rect(0, 0, supply_acceptor_pic.getWidth(), supply_acceptor_pic.getHeight());
            dst = new Rect((int) controlLeftRight, (int) controlLeftBottom - supply_acceptor_pic.getHeight(), (int) controlLeftRight+(supply_acceptor_pic.getWidth()/2), (int) controlLeftBottom);
            canvas.drawBitmap(supply_acceptor_pic, src, dst, null);
        }
        if ((ship_character.AtRightEnd) && (delivered_countdown > 0))
        {
            /* Supply Acceptor Right */
            src = new Rect(0, 0, supply_acceptor_pic_with_supplies_right.getWidth(), supply_acceptor_pic_with_supplies_right.getHeight());
            dst = new Rect((int) controlRightLeft-(supply_acceptor_pic.getWidth()/2), (int) controlRightBottom - supply_acceptor_pic_with_supplies_right.getHeight(), (int) controlRightLeft, (int) controlRightBottom);
            canvas.drawBitmap(supply_acceptor_pic_with_supplies_right, src, dst, null);
        } else
        {
            /* Supply Acceptor Right */
            src = new Rect(0, 0, supply_acceptor_pic_right.getWidth(), supply_acceptor_pic_right.getHeight());
            dst = new Rect((int) controlRightLeft-(supply_acceptor_pic.getWidth()/2), (int) controlRightBottom - supply_acceptor_pic_right.getHeight(), (int) controlRightLeft, (int) controlRightBottom);
            canvas.drawBitmap(supply_acceptor_pic_right, src, dst, null);
        }

        /* Draw the actual characters */
        if (gameLoopThread.getCollission() == false)
        {
            if (gameLoopThread.getSupplyCollission(ship_character) != supply_character.NONE)
            {
                supply_countdown--;
                if (supply_countdown > 0)
                {
                    if (gameLoopThread.getSupplyCollission(ship_character) == supply_character.NORMAL)
                    {
                        ship_character.drawShip(canvas, ship_character.sourceImageChange(ship_character.WITH_NORMAL_SUPPLIES));
                    }
                    else if (gameLoopThread.getSupplyCollission(ship_character) == supply_character.ACCUM)
                    {
                        ship_character.drawShip(canvas, ship_character.sourceImageChange(ship_character.WITH_ACCUM_SUPPLIES));
                    }
                }
                else
                {
                    gameLoopThread.setSupplyCollission(false,ship_character,supply_character.NORMAL);
                    gameLoopThread.setSupplyCollission(false,ship_character,supply_character.ACCUM);
                    ship_character.drawShip(canvas, ship_character.sourceImageChange(ship_character.NORMAL));
                }
            }
            else
            {
                ship_character.drawShip(canvas, ship_character.sourceImageChange(ship_character.NORMAL));
            }

            for (rock_idx=0;rock_idx<NUMBER_OF_ROCKS;rock_idx++)
            {
                if (rockarray[rock_idx].DrawState == true)
                {
                    rockarray[rock_idx].drawRock(canvas, rockarray[rock_idx].sourceImage);
                }
            }
            for (supply_idx=0;supply_idx<NUMBER_OF_SUPPLIES;supply_idx++)
            {
                if (supply_item_array[supply_idx].DrawState == true)
                {
                    if (supply_idx == 0)
                    {
                        supply_item_array[supply_idx].drawSupply(canvas, supply_normal_item_pic);
                    }
                    else
                    {
                        supply_item_array[supply_idx].drawSupply(canvas, supply_accum_item_pic);
                    }
                }
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
                    ship_character.drawShip(canvas, ship_character.sourceImageChange(ship_character.EXPLOSION_1));
                    break;
                case 1:
                    ship_character.drawShip(canvas, ship_character.sourceImageChange(ship_character.EXPLOSION_2));
                    break;
                case 2:
                    ship_character.drawShip(canvas, ship_character.sourceImageChange(ship_character.EXPLOSION_3));
                    break;
                case 3:
                    ship_character.drawShip(canvas, ship_character.sourceImageChange(ship_character.EXPLOSION_4));
                    break;
                case 4:
                    ship_character.drawShip(canvas, ship_character.sourceImageChange(ship_character.EXPLOSION_5));
                    break;
                case 5:
                    ship_character.drawShip(canvas, ship_character.sourceImageChange(ship_character.NORMAL));
                    break;
            }
            if (collision_delay >= COLLISION_DELAY_COUNT)
            {
                collision_draw ++;
                collision_delay = 0;
            }
            if (collision_draw>5)
            {
                collision_draw = 5;
            }
        }
    }
}

