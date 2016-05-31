package spaceslider.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    public GameView myGameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.startup);
    }

    public void gameStartButtonPushed(View view)
    {
        myGameView = new GameView(this);
        setContentView(myGameView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        float xval = event.getX();
        float yval = event.getY();
        myGameView.setControlAction(xval,yval);
        return super.onTouchEvent(event);
    }
}
