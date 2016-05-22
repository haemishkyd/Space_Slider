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
    /* Sprite Size */
    public int width;
    public int height;
    int startX;
    int startY;
    GameView myView;
    int [] presetStartX;

    public sprite_character(GameView passedView, int mostleft, int mosttop, int sizewidth, int sizeheight, int initalx, int initaly, boolean draw_state)
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

        DrawState = draw_state;

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
        current_line = 0;
        x = presetStartX[position];
    }

    public void updateRock(Canvas canvas)
    {
        if (DrawState == true)
        {
            current_line++;
            y = current_line*80;
        }
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