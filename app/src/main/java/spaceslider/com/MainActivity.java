package spaceslider.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    GameDatabase myDatabase;
    public GameView myGameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        myDatabase = new GameDatabase(MainActivity.this);
        myDatabase.getReadableDatabase();
        setContentView(R.layout.startup);
        populateStartupScreen();
    }

    public void saveAndExit(View r)
    {
        EditText playerName= new EditText(this);
        playerName = (EditText)findViewById(R.id.editText);
        myDatabase.addHighScore(playerName.getText().toString(),myGameView.score_in_game);
        setContentView(R.layout.startup);
        populateStartupScreen();
    }

    public void playAgainClicked(View r)
    {
        setContentView(R.layout.startup);
        populateStartupScreen();
    }

    public void gameStartButtonPushed(View view)
    {
        myGameView = new GameView(this);
        setContentView(myGameView);
    }

    @Override
    public void onBackPressed() {
        myGameView.KillGame();
        MainActivity.this.finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xval = event.getX();
        float yval = event.getY();
        if (myGameView.GameEndFlag)
        {
            myGameView.setVisibility(View.GONE);
            setContentView(R.layout.endgame);
            TextView t=new TextView(this);
            t=(TextView)findViewById(R.id.textView5);
            t.setText(Integer.toString(myGameView.score_in_game));
        }
        else
        {
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                myGameView.setControlAction(xval, yval, MotionEvent.ACTION_DOWN);
            }
            else if(event.getAction()==MotionEvent.ACTION_UP){
                myGameView.setControlAction(xval, yval, MotionEvent.ACTION_UP);
            }

        }
        return super.onTouchEvent(event);
    }

    private void populateStartupScreen()
    {
        TextView t=new TextView(this);
        t=(TextView)findViewById(R.id.textView2);
        t.setText(R.string.blurb);
        TextView t2=new TextView(this);
        t2=(TextView)findViewById(R.id.textView4);
        t2.setText(myDatabase.getHighScore());
    }
}
