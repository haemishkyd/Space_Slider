package spaceslider.com;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by haemish on 2016/08/14.
 */
public class supply_character extends sprite_character
{
    public static final int NORMAL = 0;
    public static final int ACCUM = 1;
    public static final int NONE = 2;

    public int SupplyType = NORMAL;

    public supply_character(GameView passedView, boolean draw_state, Bitmap passedPic,int type)
    {
        startX = 0;
        startY = 0;
        width = passedPic.getWidth();
        height = passedPic.getHeight();
        sourceImage = passedPic;
        SupplyType = type;
        DrawState = draw_state;

        myView = passedView;
    }

    public void updateSupply(Canvas canvas, int l_supply_speed)
    {
        if (DrawState == true)
        {
            if (OldSupplySpeed == 0)
            {
                OldSupplySpeed = l_supply_speed;
            }
            else
            {
                OldSupplySpeed++;
            }
            if (OldSupplySpeed >= l_supply_speed)
                OldSupplySpeed = l_supply_speed;
            current_line++;
            y = current_line*OldSupplySpeed;
        }
    }
}
