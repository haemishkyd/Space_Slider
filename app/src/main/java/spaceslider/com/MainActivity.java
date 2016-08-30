package spaceslider.com;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public GameDatabase myDatabase;
    public GameView myGameView;
    private boolean GameStarted = false;
    private ImageView img;
    public int currentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        myDatabase = new GameDatabase(MainActivity.this);
        myDatabase.getReadableDatabase();
        setContentView(R.layout.startup);
        currentView = 0;
        populateStartupScreen();
    }

    public void saveAndExit(View r)
    {
        EditText playerName;
        playerName = (EditText)findViewById(R.id.editText);
        if (playerName.getText().toString().length()==0)
        {
            Toast.makeText(getApplicationContext(),"Please enter your name!",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            myDatabase.addHighScore(playerName.getText().toString(),myGameView.score_in_game);
            setContentView(R.layout.startup);
            currentView = 0;
            populateStartupScreen();
        }

    }

    public void playAgainClicked(View r)
    {
        setContentView(R.layout.startup);
        currentView = 0;
        populateStartupScreen();
    }

    @Override
    public void onBackPressed() {
        if (myGameView != null)
        {
            myGameView.KillGame();
        }
        if (currentView == 0)
        {
            MainActivity.this.finish();
        }
        if ((currentView == 1)||(currentView==2))
        {
            myGameView.KillGame();
            GameStarted = false;
            setContentView(R.layout.startup);
            currentView = 0;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xval = event.getX();
        float yval = event.getY();

        if (GameStarted == false)
        {
            myGameView = new GameView(this);
            GameStarted = true;
            setContentView(myGameView);
            currentView = 1;
        }
        if (myGameView.GameEndFlag)
        {
            myGameView.setVisibility(View.GONE);
            setContentView(R.layout.endgame);
            currentView = 2;
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
        img = (ImageView) findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String all_names=" ";
                String all_scores=" ";
                List<high_score> temp_scores;
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.high_score);
                dialog.setTitle("High Score");
                TextView names;
                TextView scores;
                names=(TextView)dialog.findViewById(R.id.textView4);
                scores = (TextView)dialog.findViewById(R.id.textView7);
                temp_scores = myDatabase.getHighScore();
                for(high_score temp_value:temp_scores)
                {
                    all_names += temp_value.Name + "\n\r";
                    all_scores += temp_value.Score + "\n\r";
                }
                names.setText(all_names);
                scores.setText(all_scores);
                dialog.show();
            }
        });
        TextView t1;
        t1=(TextView)findViewById(R.id.textView2);
        t1.setText(R.string.blurb);

        TextView t2;
        t2=(TextView)findViewById(R.id.textView8);
        t2.setText(R.string.proceed);
        GameStarted = false;
    }
}
