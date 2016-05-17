package spaceslider.com;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.Random;

public class sprite_character
{
    Random rn;
    /* Sprite position and path */
    int gradient;
    public int x;
    public int y;
    /* Sprite Size */
    public int width;
    public int height;
    int startX;
    int startY;
    GameView myView;
    int [] presetStartX;

    public sprite_character(GameView passedView, int mostleft, int mosttop, int sizewidth, int sizeheight, int initalx, int initaly)
    {
        int temp_value;
        startX = mostleft;
        startY = mosttop;
        width = sizewidth;
        height = sizeheight;
        presetStartX = new int[7];

        presetStartX[0] = 200;
        presetStartX[1] = 400;
        presetStartX[2] = 600;
        presetStartX[3] = 800;
        presetStartX[4] = 1000;
        presetStartX[5] = 1200;
        presetStartX[6] = 1400;

        myView = passedView;
        x=initalx;
        y=initaly;
    }

    public void updateShip(int direction)
    {
        if (direction == myView.RIGHT)
        {
            x += 25;
        }
        if (direction == myView.LEFT)
        {
            x -= 25;
        }
    }

    public void resetRock(int position)
    {
        y = 0;
        x = presetStartX[position];
    }

    public void updateRock(Canvas canvas)
    {
        y += 20;
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