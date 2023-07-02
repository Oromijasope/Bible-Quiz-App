package com.example.quiz;

import static com.example.quiz.SplashActivity.list;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.SoundPool;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.wasabeef.blurry.Blurry;

public class DashboardActivity extends FullScreenActivity {

    CountDownTimer countDownTimer;
    int timerValue = 20;
    int score = 0;
    int scores = 0;
    int highScore = 0;
    private long totalDurationInMillis = 15000;
    List<Modelclass> allQuestionsList;
    private List<String> options;
    Modelclass modelclass;
    int index = 0;
    int currentQuestion;
    TextView card_question, optionA, optionB, optionC, optionD;
    TextView coinText;
    CardView cardOA, cardOB, cardOC, cardOD;
    private View rootLayout;
    private Dialog Correctdialog; // Declare global Dialog object
    int correctCount = 1;
    int wrongCount = 0;
    private int coinsEarned = 10;
    private long remainingTimeInMillis = 15000;
    private long timeLeftInMillis;
    private float animationProgress = 0f;
    private Dialog dialog;
    private boolean isTimerFrozen = false;
    private boolean isTimerPaused = false;
    private boolean isContinueClicked = false;
    LinearLayout nextButton;
    LinearLayout nextQuestionButton;
    LinearLayout resumeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);




        Hooks();

        ViewGroup rootLayout = (ViewGroup) findViewById(android.R.id.content);

        // Shared Preference for High Score
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        highScore = sharedPref.getInt("high_score", 0);
        TextView highScoreTextView = findViewById(R.id.highScoreText);
        highScoreTextView.setText("High Score: " + highScore);

        // Shared preferences for Coins
        SharedPreferences prefs = ((MyApplication) getApplication()).getPrefs();
        final int[] currentCoins = {prefs.getInt("coins", 100)};
        TextView coinTextView = findViewById(R.id.coinText);
        coinTextView.setText(String.valueOf(currentCoins[0]));



        allQuestionsList = list;
        Collections.shuffle(list);
        modelclass = list.get(index);

        options = new ArrayList<>();
        options.add(modelclass.getoA());
        options.add(modelclass.getoB());
        options.add(modelclass.getoC());
        options.add(modelclass.getoD());




        Collections.shuffle(options);






        cardOA.setBackgroundColor(getResources().getColor(R.color.white));
        cardOB.setBackgroundColor(getResources().getColor(R.color.white));
        cardOC.setBackgroundColor(getResources().getColor(R.color.white));
        cardOD.setBackgroundColor(getResources().getColor(R.color.white));

        nextButton.setClickable(false);


        LottieAnimationView animationView = findViewById(R.id.timer_lottie);
        animationView.setAnimation(R.raw.timeranim);
        animationView.setRepeatCount(0);
        animationView.setSpeed(1.0f);
        animationView.playAnimation();
        long totalDurationInMillis = 15000; // initialize with total time
        long interval = 1000;

        final long[] timeRemainingInMillis = { totalDurationInMillis };
        timerValue = (int) (totalDurationInMillis / interval);
        countDownTimer = new CountDownTimer(timeRemainingInMillis[0], interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isTimerPaused && !isTimerFrozen) {
                    timeRemainingInMillis[0] = millisUntilFinished;
                    timerValue = (int) (timeRemainingInMillis[0] / interval);
                }
            }

            @Override
            public void onFinish() {
               showTimeOutDialog();
            }
        }.start();
        animationView.setMinAndMaxProgress(0f, 1f);
        animationView.playAnimation();;

        setAllData();



        ImageView menu_icon = findViewById(R.id.menu_icon);
        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(DashboardActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                View dialogView = LayoutInflater.from(DashboardActivity.this).inflate(R.layout.pause_dialog, null);
                dialog.setContentView(dialogView);

                LinearLayout resumeButton = dialogView.findViewById(R.id.resumeButton);

                resumeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                LinearLayout homeButton = dialogView.findViewById(R.id.homeButton);
                homeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Stop the countdown timer and reset the time remaining
                        countDownTimer.cancel();
                        timeRemainingInMillis[0] = totalDurationInMillis;


                        // Start the menu activity
                        Intent intent = new Intent(DashboardActivity.this, MenuActivity.class);
                        startActivity(intent);



                    }
                });


                dialog.show();
            }
        });


        // freeze timer
        ImageView freezeTimer = findViewById(R.id.freeze_time);
        freezeTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pause previous timer and animation
                countDownTimer.cancel();
                animationProgress = animationView.getProgress();
                animationView.pauseAnimation();

                // Deduct 100 coins when the timer is frozen
                if (!isTimerFrozen && currentCoins[0] >= 100) {
                    currentCoins[0] -= 100;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("coins", currentCoins[0]);
                    editor.apply();
                    coinTextView.setText(String.valueOf(currentCoins[0]));
                }
                else {
                    // Show not enough coins alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                    builder.setTitle("Not enough coins");
                    builder.setMessage("You need at least 100 coins to continue.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            // Resume animation and timer with remaining time and progress
                            countDownTimer = new CountDownTimer(remainingTimeInMillis, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    if (!isTimerPaused && !isTimerFrozen) {
                                        remainingTimeInMillis = millisUntilFinished;
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    if (!isTimerFrozen) {
                                       /* showTimeOutDialog();*/
                                    }
                                }
                            }.start();
                            animationView.playAnimation();
                            animationView.setProgress(animationProgress);
                            isTimerFrozen = !isTimerFrozen;
                        }
                    });
                    builder.show();
                    return;
                }

                // Start new timer with remaining time
                countDownTimer = new CountDownTimer(remainingTimeInMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (!isTimerPaused && !isTimerFrozen) {
                            remainingTimeInMillis = millisUntilFinished;
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (!isTimerFrozen) {
                            showTimeOutDialog();
                        }
                    }
                }.start();
                isTimerFrozen = !isTimerFrozen;
            }
        });


        // Skip Question
        ImageView skipQuestion = findViewById(R.id.skipQuestion);
        skipQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset the timer animation
                animationView.cancelAnimation();
                animationView.setProgress(0f);

                // Reset the timer
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                long totalDurationInMillis = 15000; // initialize with total time
                long interval = 1000;

                final long[] timeRemainingInMillis = { totalDurationInMillis };
                animationView.playAnimation();

                countDownTimer = new CountDownTimer(timeRemainingInMillis[0], interval ) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if(!isTimerPaused && !isTimerFrozen) {
                            timeRemainingInMillis[0] = millisUntilFinished;
                        }
                    }

                    @Override
                    public void onFinish() {
                       /* showTimeOutDialog();*/
                    }

                }.start();

                int cost = 200; // Cost to use the skip question lifeline

                // Check if the user has enough coins
                if (currentCoins[0] >= cost) {
                    // Deduct coins from the user's account
                    currentCoins[0] -= cost;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("coins", currentCoins[0]);
                    editor.apply();
                    coinTextView.setText(String.valueOf(currentCoins[0]));

                    // Move to the next question
                    index++;

                    // If there are more questions, show the next question
                    if (index < list.size()) {
                        modelclass = list.get(index);
                        resetColor();
                        setAllData();
                    }
                } else {
                    // Alert the user that they do not have enough coins
                    new AlertDialog.Builder(DashboardActivity.this)
                            .setTitle("Not Enough Coins")
                            .setMessage("You do not have enough coins to use this lifeline.")
                            .setPositiveButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });



    }

    private void showTimeOutDialog() {
        ViewGroup rootLayout = findViewById(android.R.id.content);
        Blurry.with(DashboardActivity.this).radius(25).sampling(2).onto(rootLayout);
        Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.setContentView(R.layout.time_out_dialog);
        dialog.show();


        dialog.findViewById(R.id.continueGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset questions and shuffle
                Collections.shuffle(list);
                index = 0;
                modelclass = list.get(index);
                resetColor();
                setAllData();
                enableButton();



                // Check if user has enough coins
                SharedPreferences prefs = ((MyApplication) getApplication()).getPrefs();
                int currentCoins = prefs.getInt("coins", 100);

                if (currentCoins >= 500) {
                    // Deduct coins and continue
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("coins", currentCoins - 500);
                    editor.apply();

                    updateScoreTextView(scores);
                } else {
                    Blurry.delete(rootLayout);

                    // Show not enough coins alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                    builder.setTitle("Not enough coins");
                    builder.setMessage("You need at least 500 coins to continue.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            // Display wrong dialog
                            showWrongDialog();
                        }
                    });
                    builder.show();
                }
            }
        });

        dialog.findViewById(R.id.buttonTryAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset remaining time to maximum time
                remainingTimeInMillis = totalDurationInMillis;
                Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

        dialog.findViewById(R.id.buttonGoToMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        if (dialog != null && dialog.isShowing()) {
            // do nothing
        } else {
            super.onBackPressed();
        }
    }


    private void setAllData() {

        card_question.setText(modelclass.getQuestion());
        optionA.setText(modelclass.getoA());
        optionB.setText(modelclass.getoB());
        optionC.setText(modelclass.getoC());
        optionD.setText(modelclass.getoD());
        timerValue = 20;
        countDownTimer.cancel();
        isTimerPaused = false;
        countDownTimer.start();

    }

    private void Hooks() {

        LottieAnimationView animationView = findViewById(R.id.timer_lottie);
        card_question = findViewById(R.id.card_question);
        optionA = findViewById(R.id.card_optiona);
        optionB = findViewById(R.id.card_optionb);
        optionC = findViewById(R.id.card_optionc);
        optionD = findViewById(R.id.card_optiond);

        cardOA = findViewById(R.id.cardA);
        cardOB = findViewById(R.id.cardB);
        cardOC = findViewById(R.id.cardC);
        cardOD = findViewById(R.id.cardD);
        TextView scoreText = findViewById(R.id.scoreText);
        TextView highScoreText = findViewById(R.id.highScoreText);

        nextButton = findViewById(R.id.nextButton);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.correct_dialog, null);
        LinearLayout nextQuestionButton = dialogView.findViewById(R.id.nextQuestionButton);


    }

    public void loadQuestion(Modelclass question) {

        currentQuestion = question.getCurrentQuestion(index);
        card_question.setText(question.getQuestion());
        optionA.setText(question.getoA());
        optionB.setText(question.getoB());
        optionC.setText(question.getoC());
        optionD.setText(question.getoD());

    }



    public void Correct(CardView cardView) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.correct_dialog, null);
        LinearLayout nextQuestionButton = dialogView.findViewById(R.id.nextQuestionButton);
        cardView.setBackgroundColor(getResources().getColor(R.color.green));

        correctCount++;
        coinsEarned = 10;
        if (index == 0) {
            coinsEarned = 10;
        }


        // Update the current score
        score += coinsEarned - 10;


        // Update the score in the dashboard activity
        TextView scoreText = findViewById(R.id.scoreText);
        scoreText.setText("Score: " + score);

        updateScore(10);
        countDownTimer.cancel();

        updateCoin();
        ViewGroup rootLayout = (ViewGroup) findViewById(android.R.id.content);
        Blurry.with(DashboardActivity.this).radius(25).sampling(2).onto(rootLayout);

        showCorrectDialog(dialogView, nextQuestionButton);


        LottieAnimationView animationView = findViewById(R.id.timer_lottie);
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < list.size() - 1) { // Check if index is within bounds
                    index++;
                    modelclass = list.get(index);
                    resetColor();
                    showWonDialog();
                    // Cancel previous timer
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    animationView.cancelAnimation(); // Cancel previous animation
                    // Start new animation and timer
                    long totalDurationInMillis = 15000; // initialize with total time
                    long interval = 1000;

                    final long[] timeRemainingInMillis = { totalDurationInMillis };
                    animationView.playAnimation();

                    new CountDownTimer(timeRemainingInMillis[0], interval ) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if(!isTimerPaused && !isTimerFrozen) {
                                timeRemainingInMillis[0] = millisUntilFinished;
                            }
                        }

                        @Override
                        public void onFinish() {
                            /* showTimeOutDialog();*/

                        }

                    }.start();
                    setAllData();
                    enableButton();
                } else {
                    // Handle end of questions
                }
                Blurry.delete(rootLayout); // Remove the blur effect
                // dismiss the dialog
                if (Correctdialog != null) {
                    Correctdialog.dismiss();
                }
            }
        });
    }

    private void showCorrectDialog(View dialogView, LinearLayout nextQuestionButton) {
        Correctdialog = new Dialog(DashboardActivity.this); // Use the global Dialog object
        Correctdialog.setCanceledOnTouchOutside(false);
        Correctdialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        Correctdialog.setContentView(dialogView);
        Correctdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Correctdialog.show();

        LottieAnimationView animationView = findViewById(R.id.timer_lottie);
        countDownTimer.cancel();

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void Wrong(CardView cardView) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.wrong_dialog, null);
        cardView.setBackgroundColor(getResources().getColor(R.color.red));

        wrongCount++;

        Dialog wrongDialog = new Dialog(DashboardActivity.this);
        wrongDialog.setContentView(dialogView);
        wrongDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrongDialog.setCancelable(false);


        countDownTimer.cancel();
        LottieAnimationView animationView = findViewById(R.id.timer_lottie);
        animationView.cancelAnimation(); // cancel the animation

        showContinueDialog();



        // Reset questions and shuffle
            Collections.shuffle(list);
            index = 0;
            modelclass = list.get(index);
            resetColor();
            setAllData();
            enableButton();


        }



    private void showContinueDialog() {
        countDownTimer.cancel();
        ViewGroup rootLayout = (ViewGroup) findViewById(android.R.id.content);

        Dialog continueDialog = new Dialog(DashboardActivity.this);
        continueDialog.setCanceledOnTouchOutside(false);
        continueDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        continueDialog.setContentView(R.layout.continue_dialog);
        continueDialog.setCancelable(false);

        LottieAnimationView animationView = continueDialog.findViewById(R.id.timerAnimation);
        animationView.setAnimation(R.raw.timer);
        animationView.setFrame((int)animationView.getMaxFrame());
        // start animation from the end
        animationView.playAnimation();

        // add listener to detect when animation ends
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                continueDialog.dismiss();
                // Display wrong dialog
                showWrongDialog();
            }
        });

        Button continueButton = continueDialog.findViewById(R.id.continue_button);
        TextView skipText = continueDialog.findViewById(R.id.skipText);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueDialog.dismiss();

                // Reset questions and shuffle
                Collections.shuffle(list);
                index = 0;
                modelclass = list.get(index);
                resetColor();
                setAllData();
                enableButton();



                // Check if user has enough coins
                SharedPreferences prefs = ((MyApplication) getApplication()).getPrefs();
                int currentCoins = prefs.getInt("coins", 100);

                if (currentCoins >= 500) {
                    // Deduct coins and continue
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("coins", currentCoins - 500);
                    editor.apply();

                    updateScoreTextView(scores);
                } else {
                    Blurry.delete(rootLayout);

                    // Show not enough coins alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                    builder.setTitle("Not enough coins");
                    builder.setMessage("You need at least 500 coins to continue.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            // Display wrong dialog
                            showWrongDialog();
                        }
                    });
                    builder.show();
                }
            }
        });

        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueDialog.dismiss();
                // Display wrong dialog
                showWrongDialog();
            }
        });

        continueDialog.show();
    }



    private void showWrongDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.wrong_dialog, null);

        wrongCount++;
        countDownTimer.cancel();
        int correctAnswers = score / 10;


        ViewGroup rootLayout = (ViewGroup) findViewById(android.R.id.content);
        Blurry.with(DashboardActivity.this).radius(25).sampling(2).onto(rootLayout);
        Dialog wrongDialog = new Dialog(DashboardActivity.this);
        wrongDialog.setContentView(dialogView);
        wrongDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrongDialog.setCancelable(false);

        TextView scoreNumberTextView = dialogView.findViewById(R.id.scoreNumber);
        scoreNumberTextView.setText(("You answered: " + String.valueOf(correctAnswers) + " questions"));

        TextView scoreTextView = dialogView.findViewById(R.id.score);
        scoreTextView.setText(String.valueOf(score));

        LinearLayout startOverButton = wrongDialog.findViewById(R.id.startOverButton);
        startOverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0; // reset score to 0
                countDownTimer.cancel(); // cancel the old timer
                timeLeftInMillis = 15000;
                countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) { // create a new instance of the timer
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeftInMillis = millisUntilFinished;
                    }

                    @Override
                    public void onFinish() {
                        timeLeftInMillis = 0;
                        showTimeOutDialog();
                    }
                }.start();
                LottieAnimationView animationView = findViewById(R.id.timer_lottie);
                animationView.cancelAnimation(); // cancel the animation
                animationView.setProgress(0f); // reset the progress to the beginning
                animationView.playAnimation(); // start the animation again



                wrongDialog.dismiss(); // dismiss the wrong dialog
                Blurry.delete(rootLayout); // remove the blur effect
                // Reset questions and shuffle
                Collections.shuffle(list);
                index = 0;
                modelclass = list.get(index);
                resetColor();
                setAllData();
                enableButton();
                updateScore(0); // update the score to display 0 in the TextView

            }
        });

        wrongDialog.show();
    }



    private void updateScoreTextView(int scores) {
        TextView scoreText = findViewById(R.id.scoreText);
        scoreText.setText("Score: " + scores);
    }


    private void updateCoin() {
        TextView coinText = findViewById(R.id.coinText);
        SharedPreferences prefs = ((MyApplication) getApplication()).getPrefs();
        int currentCoins = prefs.getInt("coins", 100);
// Add 10 to the current number of coins for every correct answer
        currentCoins += 10;
// Add the coins earned from the current question to the current number of coins
        currentCoins += coinsEarned;
// Save the updated number of coins to the shared preference
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coins", currentCoins);
        editor.apply();
// Update the coin text view with the updated number of coins
        coinText.setText(String.valueOf(currentCoins));
    }





    private void updateScore(int points) {
        score += points;
        TextView scoreTextView = findViewById(R.id.scoreText);
        scoreTextView.setText("Score: " + score);
        if (score > highScore) {
            highScore = score;
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("high_score", highScore);
            editor.apply();
            TextView highScoreTextView = findViewById(R.id.highScoreText);
            highScoreTextView.setText("High Score: " + highScore);
        }
    }





    private void GameWon() {
        Intent intent = new Intent(DashboardActivity.this, wonActivity.class);
        intent.putExtra("correct", correctCount);
        intent.putExtra("wrong", wrongCount);
        startActivity(intent);
    }

    public void enableButton() {
        cardOA.setClickable(true);
        cardOB.setClickable(true);
        cardOC.setClickable(true);
        cardOD.setClickable(true);
    }

    public void disableButton() {
        cardOA.setClickable(false);
        cardOB.setClickable(false);
        cardOC.setClickable(false);
        cardOD.setClickable(false);
    }

    public void resetColor() {
        cardOA.setBackgroundColor(getResources().getColor(R.color.white));
        cardOB.setBackgroundColor(getResources().getColor(R.color.white));
        cardOC.setBackgroundColor(getResources().getColor(R.color.white));
        cardOD.setBackgroundColor(getResources().getColor(R.color.white));
    }

    public void optionAClick(View view) {
        disableButton();
        nextButton.setClickable(true);
        if (modelclass.getoA().equals(modelclass.getAns())) {
            cardOA.setCardBackgroundColor(getResources().getColor(R.color.green));
            // Cancel previous timer
            countDownTimer.cancel();

            if (index < list.size() - 1) {
                Correct(cardOA);

            } else {
                GameWon();
            }
        } else {
            Wrong(cardOA);
        }
    }

    public void optionBClick(View view) {
        disableButton();
        nextButton.setClickable(true);
        if (modelclass.getoB().equals(modelclass.getAns())) {
            cardOB.setCardBackgroundColor(getResources().getColor(R.color.green));
            // Cancel previous timer
            countDownTimer.cancel();



            if (index < list.size() - 1) {
                Correct(cardOB);

            } else {
                GameWon();
            }
        } else {
            Wrong(cardOB);
        }
    }

    public void optionCClick(View view) {
        disableButton();
        nextButton.setClickable(true);
        if (modelclass.getoC().equals(modelclass.getAns())) {
            cardOC.setCardBackgroundColor(getResources().getColor(R.color.green));
            // Cancel previous timer
            countDownTimer.cancel();


            if (index < list.size() - 1) {
                Correct(cardOC);

            } else {
                GameWon();
            }
        } else {
            Wrong(cardOC);
        }
    }

    public void optionDClick(View view) {
        disableButton();
        nextButton.setClickable(true);
        if (modelclass.getoD().equals(modelclass.getAns())) {
            cardOD.setCardBackgroundColor(getResources().getColor(R.color.green));
            // Cancel previous timer
                countDownTimer.cancel();




            if (index < list.size() - 1) {

                Correct(cardOD);

            } else {
                GameWon();
            }
        } else {
            Wrong(cardOD);
        }
    }

    private void showWonDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.correct_dialog, null);

        LinearLayout nextQuestionButton = dialogView.findViewById(R.id.nextQuestionButton);

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correctCount++;
                if (index < list.size()) {
                    index++;
                    modelclass = list.get(index);
                    resetColor();
                    showWonDialog();
                    setAllData();
                    enableButton();
                }

            }

        });


    }
}