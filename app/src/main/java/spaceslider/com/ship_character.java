package spaceslider.com;

import android.graphics.Bitmap;

/**
 * Created by haemish on 2016/08/14.
 */
public class ship_character extends sprite_character
{
    public Bitmap sourceImage_explosion_1;
    public Bitmap sourceImage_explosion_2;
    public Bitmap sourceImage_explosion_3;
    public Bitmap sourceImage_explosion_4;
    public Bitmap sourceImage_explosion_5;
    public Bitmap sourceImage_with_normal_supplies;
    public Bitmap sourceImage_with_accum_supplies;

    public static final int NORMAL      = 0;
    public static final int EXPLOSION_1 = 1;
    public static final int EXPLOSION_2 = 2;
    public static final int EXPLOSION_3 = 3;
    public static final int EXPLOSION_4 = 4;
    public static final int EXPLOSION_5 = 5;
    public static final int WITH_NORMAL_SUPPLIES = 6;
    public static final int WITH_ACCUM_SUPPLIES = 7;

    public int WithSuppliesState;

    public ship_character(GameView passedView, boolean draw_state, Bitmap passedPic)
    {
        startX = 0;
        startY = 0;
        width = passedPic.getWidth();
        height = passedPic.getHeight();
        sourceImage = passedPic;

        DrawState = draw_state;

        myView = passedView;
        WithSuppliesState = supply_character.NONE;
    }

    public void setShipImages(Bitmap exp_1,Bitmap exp_2,Bitmap exp_3,Bitmap exp_4,Bitmap exp_5,Bitmap supplies_norm,Bitmap supplies_accum)
    {
        sourceImage_explosion_1= exp_1;
        sourceImage_explosion_2= exp_2;
        sourceImage_explosion_3= exp_3;
        sourceImage_explosion_4= exp_4;
        sourceImage_explosion_5= exp_5;
        sourceImage_with_normal_supplies=supplies_norm;
        sourceImage_with_accum_supplies=supplies_accum;
    }

    public Bitmap sourceImageChange(int newImage)
    {
        switch(newImage)
        {
            case NORMAL:
                width = sourceImage.getWidth();
                height = sourceImage.getHeight();
                return sourceImage;
            case EXPLOSION_1:
                width = sourceImage_explosion_1.getWidth();
                height = sourceImage_explosion_1.getHeight();
                return sourceImage_explosion_1;
            case EXPLOSION_2:
                width = sourceImage_explosion_2.getWidth();
                height = sourceImage_explosion_2.getHeight();
                return sourceImage_explosion_2;
            case EXPLOSION_3:
                width = sourceImage_explosion_3.getWidth();
                height = sourceImage_explosion_3.getHeight();
                return sourceImage_explosion_3;
            case EXPLOSION_4:
                width = sourceImage_explosion_4.getWidth();
                height = sourceImage_explosion_4.getHeight();
                return sourceImage_explosion_4;
            case EXPLOSION_5:
                width = sourceImage_explosion_5.getWidth();
                height = sourceImage_explosion_5.getHeight();
                return sourceImage_explosion_5;
            case WITH_NORMAL_SUPPLIES:
                width = sourceImage_with_normal_supplies.getWidth();
                height = sourceImage_with_normal_supplies.getHeight();
                return sourceImage_with_normal_supplies;
            case WITH_ACCUM_SUPPLIES:
                width = sourceImage_with_accum_supplies.getWidth();
                height = sourceImage_with_accum_supplies.getHeight();
                return sourceImage_with_accum_supplies;
        }
        width = sourceImage.getWidth();
        height = sourceImage.getHeight();
        return sourceImage;
    }

    public void updateShip(int canvasWidth,int direction)
    {
        int ship_increment = 23;
        if (direction == myView.RIGHT)
        {
            if ((x + width) < myView.controlRightLeft)
            {
                x += ship_increment;
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
                x -= ship_increment;
                AtLeftEnd = false;
                AtRightEnd = false;
            }
            else
            {
                AtLeftEnd = true;
            }
        }
    }
}
