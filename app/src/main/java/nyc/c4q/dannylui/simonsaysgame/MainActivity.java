package nyc.c4q.dannylui.simonsaysgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public Button green_button;
    public Button red_button;
    public Button blue_button;
    public Button yellow_button;
    public Button currentButton;

    public TextView round_view;
    public int delay = 3000;

    public int delayForRoundView = 0;

    public static int rounds = 1;

    public static List<String> colorsByPlayer = new ArrayList<String>();
    public static List<String> colorsByComputer = new ArrayList<String>();

    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect java objects with xml
        green_button = (Button) findViewById(R.id.button_green);
        red_button = (Button) findViewById(R.id.button_red);
        blue_button = (Button) findViewById(R.id.button_blue);
        yellow_button = (Button) findViewById(R.id.button_yellow);
        round_view = (TextView) findViewById(R.id.round_view);

        //initialize button clicks
        initButtonClicks();

        //play the first round
        playRounds();
    }

    private void initButtonClicks() {
        green_button.setOnClickListener(this);
        red_button.setOnClickListener(this);
        blue_button.setOnClickListener(this);
        yellow_button.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //fixme - implement restartQuiz and add a way to save the quiz taker's score
        switch(item.getItemId()) {
            case R.id.easy_action:
            case R.id.normal_action:
            case R.id.hard_action:
                Toast.makeText(this, "No implementation found.", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_green:
                colorsByPlayer.add("G");
                checkSolved();
                break;
            case R.id.button_red:
                colorsByPlayer.add("R");
                checkSolved();
                break;
            case R.id.button_blue:
                colorsByPlayer.add("B");
                checkSolved();
                break;
            case R.id.button_yellow:
                colorsByPlayer.add("Y");
                checkSolved();
                break;
        }
    }

    //method that flashes a single button
    public void flashButton(final Button button, final int resId) {
        delay+=500;
        final Drawable originalBackground = button.getBackground();

        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                button.setBackgroundResource(resId);
            }
        };
        handler.postDelayed(r, delay);

        delay+=500;
        final Runnable r2 = new Runnable() {
            public void run() {
                button.setBackground(originalBackground);
            }
        };
        handler.postDelayed(r2, delay);
    }

    public void blinkRoundView(final TextView textView, final int resId) {
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                textView.setBackgroundResource(0x00000000);
            }
        };
        handler.postDelayed(r, delayForRoundView);

        delayForRoundView+=250;
        final Runnable r2 = new Runnable() {
            public void run() {
                textView.setBackgroundResource(resId);
            }
        };
        handler.postDelayed(r2, delayForRoundView);
        delayForRoundView = 0;
    }

    public void playRounds() {
        //blink and set the round text
        blinkRoundView(round_view, R.drawable.round_number_flash_image);
        round_view.setText(Integer.toString(rounds));

        //grab a random color
        colorsByComputer.add(getRandomColor());

        //flash through the color(s)
        for (int i = 0; i < colorsByComputer.size(); i++) {
            String color = colorsByComputer.get(i);
            switch (color) {
                case "G":
                    flashButton(green_button, R.drawable.green_button_flash_image);
                    System.out.println("Green - BLINKED");
                    break;
                case "B":
                    flashButton(blue_button, R.drawable.blue_button_flash_image);
                    System.out.println("Blue - BLINKED");
                    break;
                case "R":
                    flashButton(red_button, R.drawable.red_button_flash_image);
                    System.out.println("Red - BLINKED");
                    break;
                case "Y":
                    flashButton(yellow_button, R.drawable.yellow_button_flash_image);
                    System.out.println("Yellow - BLINKED");
                    break;
            }
        }

        //reset delay
        delay = 1000;
    }

    //determine whether player loses or wins the round
    public void checkSolved() {
        //if both array sequence match then you win the round (both array must be equal size)
        if (colorsByPlayer.equals(colorsByComputer)) {
            Toast.makeText(MainActivity.this, "You win this round!", Toast.LENGTH_SHORT).show();
            rounds++;
            colorsByPlayer.clear();
            playRounds();
        }
        //sometimes the size is different but players can be matching correctly
        else {
            //checks correct matching after every click
            for (int i = 0; i < colorsByPlayer.size(); i++) {
                if (colorsByPlayer.get(i) != colorsByComputer.get(i)) { //you lose
                    showOptions(this);
                }
            }
        }
    }

    //method to return a random color to be added to the colorsByComputer list
    public String getRandomColor() {
        Random rand = new Random();
        int randomNum = rand.nextInt(4);

        switch(randomNum) {
            case 0:
                return "G";
            case 1:
                return "R";
            case 2:
                return "B";
            case 3:
                return "Y";
        }
        return "B"; //will never get to this
    }

    //popup window when you lose
    public void showOptions(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE); //create a layout inflater
        View layout = inflater.inflate(R.layout.options_layout, null); //use layout inflater to inflate the options_layout.xml into a view
        layout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popup_show)); //set the animation for this layout
        final PopupWindow optionsPopup = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //create a popupwindow with this view

        optionsPopup.setOutsideTouchable(false); //outside the popup cannot be clicked
        View view = findViewById(R.id.activity_main);  //get the view for activity_main.xml
        optionsPopup.showAtLocation(view, Gravity.CENTER, 0, 0); //where in activity_main will this popup be, center with offset 0,0
        //optionspu.setAnimationStyle(R.anim.popup_show); //set the popup to this animation, doesn't work? because of setAnimation above?

        Button play_again_button = (Button) layout.findViewById(R.id.play_again);
        Button quit_button = (Button) layout.findViewById(R.id.quit);

        play_again_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                optionsPopup.dismiss();
                colorsByComputer.clear();
                colorsByPlayer.clear();
                rounds = 1;
                playRounds();
            }
        });

        quit_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.exit(1);
            }
        });
    }
}
