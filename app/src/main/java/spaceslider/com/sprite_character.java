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

    public int OldRockSpeed = 0;
    public int OldSupplySpeed = 0;
    /* End stops */
    public boolean AtLeftEnd = false;
    public boolean AtRightEnd = false;
    int startX;
    int startY;
    GameView myView;

    public void initialise_position(int initaly, int initalx)
    {
        if (initaly != 0)
        {
            //this is not a rock so initiliase the x value (ships)
            x = initalx;
        }
        y=initaly;
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