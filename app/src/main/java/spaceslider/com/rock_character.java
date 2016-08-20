package spaceslider.com;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by haemish on 2016/08/14.
 */
public class rock_character extends sprite_character
{
    public rock_character(GameView passedView, boolean draw_state, Bitmap passedPic)
    {
        startX = 0;
        startY = 0;
        width = passedPic.getWidth();
        height = passedPic.getHeight();
        sourceImage = passedPic;

        DrawState = draw_state;

        myView = passedView;
    }

    public void updateRock(Canvas canvas, int l_rock_speed)
    {
        if (DrawState == true)
        {
            if (OldRockSpeed == 0)
            {
                OldRockSpeed = l_rock_speed;
            }
            else
            {
                OldRockSpeed++;
            }
            if (OldRockSpeed >= l_rock_speed)
                OldRockSpeed = l_rock_speed;
            current_line++;
            y = current_line*OldRockSpeed;
        }
    }
}
