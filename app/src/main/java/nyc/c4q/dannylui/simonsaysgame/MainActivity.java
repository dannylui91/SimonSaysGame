package nyc.c4q.dannylui.simonsaysgame;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
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

public class MainActivity extends AppCompatActivity {
    public Button green_button;
    public Button red_button;
    public Button blue_button;
    public Button yellow_button;
    public Button currentButton;

    public TextView round_view;
    public int delay = 3000;

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
        green_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorsByPlayer.add("G");
                checkSolved();
            }
        });

        red_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorsByPlayer.add("R");
                checkSolved();
            }
        });

        blue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorsByPlayer.add("B");
                checkSolved();
            }
        });

        yellow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorsByPlayer.add("Y");
                checkSolved();
            }
        });

    }

    //method that flashes a single button
    public void flashButton(final Button button, final int resId) {

        int duration = 0;
        //smoother flash transition when the next button is different
        /*if (currentButton == button)
            duration = 75;
        else
            currentButton = button;
        */

        delay+=duration;
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

    public void playRounds() {
        //set the round text
        round_view.setText("Round " + rounds);

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
        final PopupWindow optionspu = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //create a popupwindow with this view

        optionspu.setOutsideTouchable(false); //outside the popup cannot be clicked
        View view = findViewById(R.id.activity_main);  //get the view for activity_main.xml
        optionspu.showAtLocation(view, Gravity.CENTER, 0, 0); //where in activity_main will this popup be, center with offset 0,0
        //optionspu.setAnimationStyle(R.anim.popup_show); //set the popup to this animation, doesn't work? because of setAnimation above?

        Button play_again_button = (Button) layout.findViewById(R.id.play_again);
        Button quit_button = (Button) layout.findViewById(R.id.quit);

        play_again_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                optionspu.dismiss();
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
