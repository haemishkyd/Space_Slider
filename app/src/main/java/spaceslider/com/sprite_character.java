package spaceslider.com;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.Random;

public class sprite_character
{
    Random rn;
    /* Sprite position and path */
    public int x;
    public int current_line;
    public int y;
    public boolean DrawState;
    public Bitmap sourceImage;
    /* Sprite Size */
    public int width;
    public int height;

    /* End stops */
    public boolean AtLeftEnd = false;
    public boolean AtRightEnd = false;
    int startX;
    int startY;
    GameView myView;
    int [] presetStartX;

    public sprite_character(GameView passedView, boolean draw_state, Bitmap passedPic)
    {
        int temp_value;
        startX = 0;
        startY = 0;
        width = passedPic.getWidth();
        height = passedPic.getHeight();
        presetStartX = new int[7];
        sourceImage = passedPic;

        presetStartX[0] = 200;
        presetStartX[1] = 400;
        presetStartX[2] = 600;
        presetStartX[3] = 800;
        presetStartX[4] = 1000;
        presetStartX[5] = 1200;
        presetStartX[6] = 1400;

        DrawState = draw_state;

        myView = passedView;

    }

    public void initialise_position(int initaly, int initalx)
    {
        x=initalx;
        y=initaly;
    }

    public void updateShip(Canvas canvas,int direction)
    {
        if (direction == myView.RIGHT)
        {
            if ((x + width) < myView.controlRightLeft)
            {
                x += 25;
                AtLeftEnd = false;
                AtRightEnd = false;
            }
            else
            {
                AtRightEnd = true;
            }
        }

        if (direction == myView.LEFT)
        {
            if(x > myView.controlLeftRight)
            {
                x -= 25;
                AtLeftEnd = false;
                AtRightEnd = false;
            }
            else
            {
                AtLeftEnd = true;
            }
        }
    }

    public void resetRock(int position)
    {
        y = 0;
        current_line = 0;
        x = presetStartX[position];
    }

    public void updateSupply(Canvas canvas,int l_supply_speed)
    {
        if (DrawState == true)
        {
            current_line++;
            y = current_line*l_supply_speed;
        }
    }

    public void updateRock(Canvas canvas,int l_rock_speed)
    {
        if (DrawState == true)
        {
            current_line++;
            y = current_line*l_rock_speed;
        }
    }

    public void drawSupply(Canvas canvas, Bitmap bmp)
    {
        Rect src = new Rect(startX, startY, startX + width, startY + height);
        Rect dst = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    public void drawRock(Canvas canvas, Bitmap bmp)
    {
        Rect src = new Rect(startX, startY, startX + width, startY + height);
        Rect dst = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    public void drawShip(Canvas canvas, Bitmap bmp)
    {
        Rect src = new Rect(startX, startY, startX + width, startY + height);
        Rect dst = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }
}