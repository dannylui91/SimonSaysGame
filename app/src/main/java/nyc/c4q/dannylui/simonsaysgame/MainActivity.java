package nyc.c4q.dannylui.simonsaysgame;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
    public TextView round_view;
    public int delay = 3000;

    public static int rounds = 1;

    public static List<String> colorsByPlayer = new ArrayList<String>();
    public static List<String> colorsByComputer = new ArrayList<String>();

    public boolean solved;
    public boolean isWrong;

    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        green_button = (Button) findViewById(R.id.button_green);
        red_button = (Button) findViewById(R.id.button_red);
        blue_button = (Button) findViewById(R.id.button_blue);
        yellow_button = (Button) findViewById(R.id.button_yellow);
        round_view = (TextView) findViewById(R.id.round_view);

        round_view.setText("Round " + rounds);
        initButtonClicks();

        playRounds();

    }

    private void initButtonClicks() {
        green_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorsByPlayer.add("G");
                solved = checkBothSequence();
                checkSolved();
            }
        });

        red_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorsByPlayer.add("R");
                solved = checkBothSequence();
                checkSolved();
            }
        });

        blue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorsByPlayer.add("B");
                solved = checkBothSequence();
                checkSolved();
            }
        });

        yellow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorsByPlayer.add("Y");
                solved = checkBothSequence();
                checkSolved();
            }
        });
    }

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

    public void playRounds() {
        colorsByComputer.add(getRandomColor());

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

        delay = 1000;
    }

    public boolean checkBothSequence() {
        if (colorsByPlayer.equals(colorsByComputer))
            return true;
        return false;
    }

    public void checkSolved() {
        if (solved) {
            Toast.makeText(MainActivity.this, "You win this round!", Toast.LENGTH_SHORT).show();
            rounds++;
            colorsByPlayer.clear();
            round_view.setText("Round " + rounds);
            playRounds();
        }
        else {
            if (colorsByPlayer.size() < colorsByComputer.size()){
                for (int i = 0; i < colorsByPlayer.size(); i++) {
                    if (colorsByPlayer.get(i) != colorsByComputer.get(i))
                        Toast.makeText(MainActivity.this, "You lose!", Toast.LENGTH_SHORT).show();
                }
            }
            else if (colorsByPlayer.size() == colorsByComputer.size())
                Toast.makeText(MainActivity.this, "You lose!", Toast.LENGTH_SHORT).show();
        }
    }

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
        return "B";

    }
}
