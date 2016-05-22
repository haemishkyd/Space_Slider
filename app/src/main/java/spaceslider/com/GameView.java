package spaceslider.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

/**
 * Created by haemish on 2016/04/18.
 */
public class GameView extends SurfaceView
{
    private Bitmap bmp;
    private Bitmap spaceship_normal;
    private Bitmap spaceship_col_1;
    private Bitmap spaceship_col_2;
    private Bitmap spaceship_col_3;
    private Bitmap spaceship_col_4;
    private Bitmap spaceship_col_5;

    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
    private float controlLeftTop;
    private float controlLeftLeft;
    private float controlLeftBottom;
    private float controlLeftRight;
    private float controlRightTop;
    private float controlRightLeft;
    private float controlRightBottom;
    private float controlRightRight;

    /* Game characters */
    public static final int NUMBER_OF_ROCKS = 8;
    public sprite_character rockarray[];
    public sprite_character ship_character;
    public sprite_character ship_collission_1;
    public sprite_character ship_collission_2;
    public sprite_character ship_collission_3;
    public sprite_character ship_collission_4;
    public sprite_character ship_collission_5;

    private int   number_of_lives = 3;
    private int   level_of_game = 1;
    private int collision_draw=0;

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
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.myspaceship);
        spaceship_normal = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px);
        spaceship_col_1 = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_1);
        spaceship_col_2 = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_2);
        spaceship_col_3 = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_3);
        spaceship_col_4 = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_4);
        spaceship_col_5 = BitmapFactory.decodeResource(getResources(), R.drawable.kspaceduel_spaceship_128px_expl_5);
        //ship_character = new sprite_character(this, bmp.getWidth()/3, ((bmp.getHeight()/8)*2), bmp.getWidth()/3, (bmp.getHeight()/8), 800, 750,true);
        //ship_collission_1 = new sprite_character(this, 0, (int) (bmp.getHeight()/2.3), bmp.getWidth()/9, bmp.getHeight()/11, 800, 750,true);
        //ship_collission_2 = new sprite_character(this,bmp.getWidth()/6, (int) (bmp.getHeight()/2.3), bmp.getWidth()/6, (bmp.getHeight()/10), 800, 750,true);
        //ship_collission_3 = new sprite_character(this,(bmp.getWidth()/6)*2, (int) (bmp.getHeight()/2.3), bmp.getWidth()/4, (bmp.getHeight()/10), 800, 750,true);
        //ship_collission_4 = new sprite_character(this, (int) ((bmp.getWidth()/6)*3.5), (int) (bmp.getHeight()/2.3), (int) (bmp.getWidth()/2.5), (bmp.getHeight()/8), 800, 750,true);
        ship_character = new sprite_character(this, 0, 0,spaceship_normal.getWidth(), spaceship_normal.getHeight(), 800, 650,true);
        ship_collission_1 = new sprite_character(this, 0, 0, spaceship_col_1.getWidth(), spaceship_col_1.getHeight(), 800, 650,true);
        ship_collission_2 = new sprite_character(this, 0, 0, spaceship_col_2.getWidth(), spaceship_col_2.getHeight(), 800, 650,true);
        ship_collission_3 = new sprite_character(this, 0, 0, spaceship_col_3.getWidth(), spaceship_col_3.getHeight(), 800, 650,true);
        ship_collission_4 = new sprite_character(this, 0, 0, spaceship_col_4.getWidth(), spaceship_col_4.getHeight(), 800, 650,true);
        ship_collission_5 = new sprite_character(this, 0, 0, spaceship_col_5.getWidth(), spaceship_col_5.getHeight(), 800, 650,true);

        rockarray = new sprite_character[NUMBER_OF_ROCKS];
        for (rock_idx=0;rock_idx<NUMBER_OF_ROCKS;rock_idx++)
        {
            if (rock_idx%2 == 0)
            {
                rockarray[rock_idx] = new sprite_character(this, 0, (int) (bmp.getHeight() / 1.45), bmp.getWidth() / 2, bmp.getHeight() / 6, 800, 0,false);
            }
            else
            {
                rockarray[rock_idx] = new sprite_character(this,0, (int) (bmp.getHeight()/1.16), bmp.getWidth()/2, bmp.getHeight()/6, 800, 0,false);
            }
        }
        //rockarray[1] = new sprite_character(this, bmp.getWidth()/2, (int) (bmp.getHeight()/1.4),bmp.getWidth()/3, bmp.getHeight()/7, 800, 0);
        //rockarray[2] = new sprite_character(this, bmp.getWidth()/2, (int) (bmp.getHeight()/1.1), (int) (bmp.getWidth()/3.5), bmp.getHeight()/7, 800, 0);
        //rockarray[3] = new sprite_character(this,0, (int) (bmp.getHeight()/1.16), bmp.getWidth()/2, bmp.getHeight()/6, 800, 0);
        //rockarray[4] = new sprite_character(this,0, (int) (bmp.getHeight()/1.45), bmp.getWidth()/2, bmp.getHeight()/6, 800, 0);

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
            if (((rockarray[rock_idx].x+rockarray[rock_idx].width)>shipx) &&  (rockarray[rock_idx].x < (shipx+shipwidth)))
            {
                if ((rockarray[rock_idx].y+rockarray[rock_idx].height)>shipy)
                {
                    gameLoopThread.setCollission(true);
                    number_of_lives--;
                    if (number_of_lives == 0)
                    {
                        gameLoopThread.setRunning(false);
                    }
                    return;
                }
            }
        }
    }

    public void setControlAction(float xtouch, float ytouch)
    {
        if ((xtouch > controlLeftLeft) && (xtouch < controlLeftRight))
        {
            ship_character.updateShip(LEFT);
            ship_collission_1.updateShip(LEFT);
            ship_collission_2.updateShip(LEFT);
            ship_collission_3.updateShip(LEFT);
            ship_collission_4.updateShip(LEFT);
            ship_collission_5.updateShip(LEFT);
        }
        if ((xtouch > controlRightLeft) && (xtouch < controlRightRight))
        {
            ship_character.updateShip(RIGHT);
            ship_collission_1.updateShip(RIGHT);
            ship_collission_2.updateShip(RIGHT);
            ship_collission_3.updateShip(RIGHT);
            ship_collission_4.updateShip(RIGHT);
            ship_collission_5.updateShip(RIGHT);
        }
    }

    protected void CharacterDraw(Canvas canvas)
    {
        int rock_idx;
        canvas.drawColor(Color.BLACK);

        //Get the control area
        controlLeftTop = 0;
        controlLeftLeft = 0;
        controlLeftBottom = canvas.getHeight();
        controlLeftRight = 200;
        controlRightTop = 0;
        controlRightLeft = canvas.getWidth() - 200;
        controlRightBottom = canvas.getHeight();
        controlRightRight = canvas.getWidth();
        //draw the control rectangles
        Paint myPaint = new Paint();
        myPaint.setColor(Color.rgb(127, 127, 0));
        myPaint.setStrokeWidth(10);
        canvas.drawRect(controlLeftLeft, controlLeftTop, controlLeftRight, controlLeftBottom, myPaint);
        canvas.drawRect(controlRightLeft, controlRightTop, controlRightRight, controlRightBottom, myPaint);

        /* Write the number of lives on the screen*/
        myPaint.setColor(Color.WHITE);
        myPaint.setTextSize(100);
        canvas.drawText(Integer.toString(number_of_lives), 1500, 110, myPaint);
        if (number_of_lives == 0)
        {
            myPaint.setColor(Color.RED);
            canvas.drawText("Game Over", canvas.getWidth()/2-200, canvas.getHeight()/2, myPaint);
        }

        /* Write the number of lives on the screen*/
        myPaint.setColor(Color.GREEN);
        myPaint.setTextSize(100);
        canvas.drawText(Integer.toString(level_of_game), 200, 110, myPaint);

        /* Draw the actual characters */
        if (gameLoopThread.getCollission() == false)
        {
            ship_character.drawShip(canvas, spaceship_normal);
            for (rock_idx=0;rock_idx<NUMBER_OF_ROCKS;rock_idx++)
            {
                if (rockarray[rock_idx].DrawState == true)
                {
                    rockarray[rock_idx].drawRock(canvas, bmp);
                }
            }
            collision_draw = 0;
        }
        else
        {
            switch(collision_draw)
            {
                case 0:
                    ship_collission_1.x=ship_character.x;
                    ship_collission_1.y=ship_character.y;
                    ship_collission_1.drawShip(canvas, spaceship_col_1);
                    collision_draw = 1;
                    break;
                case 1:
                    ship_collission_2.x=ship_character.x;
                    ship_collission_2.y=ship_character.y;
                    ship_collission_2.drawShip(canvas,spaceship_col_2);
                    collision_draw = 2;
                    break;
                case 2:
                    ship_collission_3.x=ship_character.x;
                    ship_collission_3.y=ship_character.y;
                    ship_collission_3.drawShip(canvas,spaceship_col_3);
                    collision_draw = 3;
                    break;
                case 3:
                    ship_collission_4.x=ship_character.x;
                    ship_collission_4.y=ship_character.y;
                    ship_collission_4.drawShip(canvas,spaceship_col_4);
                    collision_draw = 4;
                    break;
                case 4:
                    ship_collission_5.x=ship_character.x;
                    ship_collission_5.y=ship_character.y;
                    ship_collission_5.drawShip(canvas,spaceship_col_5);
                    collision_draw = 5;
                    break;
                case 5:
                    ship_character.drawShip(canvas, spaceship_normal);
                    break;
            }
        }
    }
}