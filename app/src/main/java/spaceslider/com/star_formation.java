package spaceslider.com;

import android.graphics.Canvas;
import java.util.Random;

public class star_formation
{
    public int x;
    public int y;
    public boolean display_star;
    Random myRand;

    public star_formation()
    {
        myRand = new Random();
        display_star = false;
        y=0;
        x=0;
    }

    public void initialise_star(Canvas c, float offsetVal)
    {
        x = (myRand.nextInt(c.getWidth()-((int)offsetVal*2))+(int)offsetVal);
    }

    public void update_star()
    {
        y=y+80;
    }

    public void check_star(Canvas c)
    {
        if (y>c.getHeight())
        {
            display_star = false;
            y=0;
        }
    }
}