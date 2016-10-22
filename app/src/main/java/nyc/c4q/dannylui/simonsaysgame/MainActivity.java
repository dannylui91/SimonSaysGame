package nyc.c4q.dannylui.simonsaysgame;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
    private Button greenButton;
    private Button redButton;
    private Button blueButton;
    private Button yellowButton;
    private Button roundView;
    private Button highScoreView;

    private MediaPlayer mpGreenButton;
    private MediaPlayer mpRedButton;
    private MediaPlayer mpBlueButton;
    private MediaPlayer mpYellowButton;
    private MediaPlayer mpRoundView;
    private MediaPlayer mpHighScoreView;

    private Drawable originalBackgroundG;
    private Drawable originalBackgroundR;
    private Drawable originalBackgroundB;
    private Drawable originalBackgroundY;
    private Drawable originalBackgroundRV;

    private static List<String> colorsByPlayer = new ArrayList<String>();
    private static List<String> colorsByComputer = new ArrayList<String>();

    private static int buttonFlashDelay = 3000;
    private static int delayForRoundView = 0;
    private static int flashDuration = 500;

    private SharedPreferences prefs;
    private final static String HIGH_SCORE_KEY = "score_key";

    private Handler handler;
    private static int highestScore = 0;
    private static int rounds = 1;
    private static boolean freePlay = false;
    private static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect java objects with xml
        greenButton = (Button) findViewById(R.id.button_green);
        redButton = (Button) findViewById(R.id.button_red);
        blueButton = (Button) findViewById(R.id.button_blue);
        yellowButton = (Button) findViewById(R.id.button_yellow);
        roundView = (Button) findViewById(R.id.round_view);
        highScoreView = (Button) findViewById(R.id.high_score);

        //initialize button clicks, sounds and original backgrounds
        initButtonClicks();
        initButtonSounds();
        initButtonBackgrounds();

        //set unable to click for these two buttons except during free play
        roundView.setEnabled(true);
        highScoreView.setEnabled(false);

        //load prefs
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        highScoreView.setText(Integer.toString(prefs.getInt(HIGH_SCORE_KEY, 0)));

        //play the round when start is touched in the roundView
        if (!freePlay) {
            disableButtonClicks();
            roundView.setText("Start");
            roundView.setTextSize(35.0f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetGame();
    }

    //-------------------------------- Buttons Stuff --------------------------------
    public void initButtonClicks() {
        greenButton.setOnClickListener(this);
        redButton.setOnClickListener(this);
        blueButton.setOnClickListener(this);
        yellowButton.setOnClickListener(this);
        roundView.setOnClickListener(this);
        highScoreView.setOnClickListener(this);
    }

    public void initButtonSounds() {
        mpGreenButton = MediaPlayer.create(this, R.raw.futurama_bell1);
        mpRedButton = MediaPlayer.create(this, R.raw.futurama_bell2);
        mpBlueButton = MediaPlayer.create(this, R.raw.futurama_bell3);
        mpYellowButton = MediaPlayer.create(this, R.raw.futurama_bell4);
        mpRoundView = MediaPlayer.create(this, R.raw.futurama_bell5);
        mpHighScoreView = MediaPlayer.create(this, R.raw.futurama_bell6);
    }

    public void initButtonBackgrounds() {
        originalBackgroundG = greenButton.getBackground();
        originalBackgroundR = redButton.getBackground();
        originalBackgroundB = blueButton.getBackground();
        originalBackgroundY = yellowButton.getBackground();
        originalBackgroundRV = roundView.getBackground();
    }

    public void disableButtonClicks() {
        greenButton.setClickable(false);
        redButton.setClickable(false);
        blueButton.setClickable(false);
        yellowButton.setClickable(false);
    }

    public void enableButtonClick() {
        greenButton.setClickable(true);
        redButton.setClickable(true);
        blueButton.setClickable(true);
        yellowButton.setClickable(true);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_green:
                resetSound();
                mpGreenButton.start();
                if(!freePlay) {
                    colorsByPlayer.add("G");
                    checkSolved();
                }
                break;
            case R.id.button_red:
                resetSound();
                mpRedButton.start();
                if(!freePlay) {
                    colorsByPlayer.add("R");
                    checkSolved();
                }
                break;
            case R.id.button_blue:
                resetSound();
                mpBlueButton.start();
                if(!freePlay) {
                    colorsByPlayer.add("B");
                    checkSolved();
                }
                break;
            case R.id.button_yellow:
                resetSound();
                mpYellowButton.start();
                if(!freePlay) {
                    colorsByPlayer.add("Y");
                    checkSolved();
                }
                break;
            case R.id.round_view:
                if (roundView.getText().toString().equals("Start")) {
                    roundView.setEnabled(false);
                    enableButtonClick();
                    playRounds();
                }
                resetSound();
                mpRoundView.start();
                break;
            case R.id.high_score:
                resetSound();
                mpHighScoreView.start();
                break;
        }
    }

    //-------------------------------- Menu Stuff --------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.easy_action:
                if (flashDuration != 750 || freePlay == true) {
                    freePlay= false;
                    roundView.setEnabled(false);
                    highScoreView.setEnabled(false);
                    flashDuration = 750;
                    resetGame();
                    playRounds();
                }
                break;
            case R.id.normal_action:
                if (flashDuration != 500 || freePlay == true) {
                    freePlay= false;
                    roundView.setEnabled(false);
                    highScoreView.setEnabled(false);
                    flashDuration = 500;
                    resetGame();
                    playRounds();
                }
                break;
            case R.id.hard_action:
                if (flashDuration != 250 || freePlay == true) {
                    freePlay= false;
                    roundView.setEnabled(false);
                    highScoreView.setEnabled(false);
                    flashDuration = 250;
                    resetGame();
                    playRounds();
                }
                break;
            case R.id.free_play_action:
                freePlay = true;
                roundView.setEnabled(true);
                highScoreView.setEnabled(true);
                resetGame();
                roundView.setText("∞");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //-------------------------------- Play Game Stuff --------------------------------
    public void playRounds() {
        //blink and set the round text
        flashRoundView(roundView, R.drawable.round_number_flash_image);
        roundView.setText(Integer.toString(rounds));

        //grab a random color
        colorsByComputer.add(getRandomColor());

        disableButtonClicks();

        //flash through the color(s)
        for (int i = 0; i < colorsByComputer.size(); i++) {
            String color = colorsByComputer.get(i);
            switch (color) {
                case "G":
                    flashButton(greenButton, R.drawable.green_button_flash_image);
                    break;
                case "B":
                    flashButton(blueButton, R.drawable.blue_button_flash_image);
                    break;
                case "R":
                    flashButton(redButton, R.drawable.red_button_flash_image);
                    break;
                case "Y":
                    flashButton(yellowButton, R.drawable.yellow_button_flash_image);
                    break;
            }
        }

        //reset delay
        buttonFlashDelay = 1000;
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
            if (colorsByPlayer.size() <= (colorsByComputer.size())) {
                //checks correct matching after every click
                for (int i = 0; i < colorsByPlayer.size(); i++) {
                    if (colorsByPlayer.get(i) != colorsByComputer.get(i)) { //you lose
                        showOptions(this);
                    }
                }
            }
        }
    }

    public String getRandomColor() { //get random color
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

    public void recordHighScore() {
        if (rounds > highestScore) {
            highestScore = rounds;
            highScoreView.setText(Integer.toString(highestScore));
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(HIGH_SCORE_KEY, highestScore);
        editor.apply();
    }

    //-------------------------------- Popup Window Stuff --------------------------------
    public void showOptions(Context context) { //when you lose
        recordHighScore();
        resetGame();

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

        play_again_button.setOnClickListener(new View.OnClickListener(){ //play again
            @Override
            public void onClick(View v) {
                optionsPopup.dismiss();
                playRounds();
            }
        });

        quit_button.setOnClickListener(new View.OnClickListener(){ //exit app
            @Override
            public void onClick(View v) {
                System.exit(1);
            }
        });
    }

    //-------------------------------- Flashy Stuff --------------------------------
    public void flashButton(final Button button, final int resId) { //flash a button and play sound
        buttonFlashDelay+=flashDuration;
        final Drawable originalBackground = button.getBackground();

        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                button.setBackgroundResource(resId);
                if (button.getId() == greenButton.getId()) {
                    resetSound();
                    mpGreenButton.start();
                }
                if (button.getId() == redButton.getId()) {
                    resetSound();
                    mpRedButton.start();
                }
                if (button.getId() == blueButton.getId()) {
                    resetSound();
                    mpBlueButton.start();
                }
                if (button.getId() == yellowButton.getId()) {
                    resetSound();
                    mpYellowButton.start();
                }
            }
        };

        handler.postDelayed(r, buttonFlashDelay);

        buttonFlashDelay+=flashDuration;
        System.out.println("DEFORE DELAY IS " + buttonFlashDelay);
        final Runnable r2 = new Runnable() {
            public void run() {
                button.setBackground(originalBackground);
                count++;
                if (count == rounds) {
                    enableButtonClick();
                    count = 0;
                }
            }
        };
        handler.postDelayed(r2, buttonFlashDelay);
    }

    public void flashRoundView(final TextView textView, final int resId) { //flash the round view
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

    //-------------------------------- Reset Stuff --------------------------------
    public void resetGame() {
        resetButtonBackground();
        resetSound();
        colorsByComputer.clear();
        colorsByPlayer.clear();
        rounds = 1;
    }

    public void resetButtonBackground() {
        greenButton.setBackground(originalBackgroundG);
        redButton.setBackground(originalBackgroundR);
        blueButton.setBackground(originalBackgroundB);
        yellowButton.setBackground(originalBackgroundY);
        roundView.setBackground(originalBackgroundRV);
    }

    public final void resetSound() {
        try {
            if (mpGreenButton.isPlaying()) {
                mpGreenButton.stop();
                mpGreenButton.release();
                mpGreenButton = MediaPlayer.create(this, R.raw.futurama_bell1);
            }
            if (mpRedButton.isPlaying()) {
                mpRedButton.stop();
                mpRedButton.release();
                mpRedButton = MediaPlayer.create(this, R.raw.futurama_bell2);
            }
            if (mpBlueButton.isPlaying()) {
                mpBlueButton.stop();
                mpBlueButton.release();
                mpBlueButton = MediaPlayer.create(this, R.raw.futurama_bell3);
            }
            if (mpYellowButton.isPlaying()) {
                mpYellowButton.stop();
                mpYellowButton.release();
                mpYellowButton = MediaPlayer.create(this, R.raw.futurama_bell4);
            }
            if (mpRoundView.isPlaying()) {
                mpRoundView.stop();
                mpRoundView.release();
                mpRoundView = MediaPlayer.create(this, R.raw.futurama_bell5);
            }
            if (mpHighScoreView.isPlaying()) {
                mpHighScoreView.stop();
                mpHighScoreView.release();
                mpHighScoreView = MediaPlayer.create(this, R.raw.futurama_bell6);
            }
        } catch(Exception e) { e.printStackTrace(); }
    }
}
