package com.sciencebowlhub.scibowlgym.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sciencebowlhub.scibowlgym.R;
import com.sciencebowlhub.scibowlgym.model.Category;
import com.sciencebowlhub.scibowlgym.model.Question;
import com.sciencebowlhub.scibowlgym.model.QuestionJSONParser;
import com.sciencebowlhub.scibowlgym.model.QuestionType;
import com.sciencebowlhub.scibowlgym.model.QuizModeStats;

import java.util.Calendar;

import katex.hourglass.in.mathlib.MathView;

public class QuizModePage extends AppCompatActivity {
    // Question text fields
    private TextView roundSetNumLabel;
    private TextView questionNumLabel;
    private TextView categoryTypeLabel;
    private MathView questionTextLabel;

    // Option buttons
    private MathView optionWButton;
    private MathView optionXButton;
    private MathView optionYButton;
    private MathView optionZButton;

    // Toolbar Buttons
    private Button menuButton;
    private Button nextButton;

    // Timer Label
    private TextView timerLabel;

    // Intent fields
    private String category;
    private int tossupTime;
    private int bonusTime;
    private QuizModeStats stats;

    private Question question;
    private CountDownTimer timer;

    abstract class TouchListener implements View.OnTouchListener {
        private static final int MAX_CLICK_DURATION = 200;
        private long startClickTime;

        abstract public void select(View v);

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if (clickDuration < MAX_CLICK_DURATION) {
                        select(v);
                    }
                }
            }
            return true;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_mode_page);
        Intent intent = getIntent();
        category = intent.getStringExtra("CATEGORY");
        tossupTime = intent.getIntExtra("TOSSUP_TIME", 10);
        bonusTime = intent.getIntExtra("BONUS_TIME", 10);
        stats = intent.getParcelableExtra("STATS");

        getQuestionForParameters();
        if (question == null) {
            QuestionJSONParser.getInstance(getApplicationContext());
            getQuestionForParameters();
        }

        int seconds;
        if (question.getQuestionType() == QuestionType.Tossup) {
            seconds = tossupTime;
        } else {
            seconds = bonusTime;
        }

        roundSetNumLabel = findViewById(R.id.roundSetNumLabel);
        roundSetNumLabel.setText("Question Set " + question.getSetNumber() + " Round " + question.getRoundNumber());

        questionNumLabel = findViewById(R.id.questionNumLabel);
        questionNumLabel.setText("Question " + question.getQuestionNumber() + " " + question.getQuestionType().toString());

        categoryTypeLabel = findViewById(R.id.categoryTypeLabel);
        categoryTypeLabel.setText(question.getCategory().toString() + " " + question.getAnswerType().toString());

        questionTextLabel = findViewById(R.id.questionTextLabel);
        questionTextLabel.setDisplayText(question.getQuestionText());

        timerLabel = findViewById(R.id.timerLabel);
        timerLabel.setText(seconds + " Seconds Left");

        optionWButton = findViewById(R.id.optionWButton);
        optionWButton.setDisplayText(question.getAnswerChoices()[0]);
        optionWButton.setOnTouchListener(new TouchListener() {
            @Override
            public void select(View v) {
                selectOption(v, 'W', optionWButton);
            }
        });

        optionXButton = findViewById(R.id.optionXButton);
        optionXButton.setDisplayText(question.getAnswerChoices()[1]);
        optionXButton.setOnTouchListener(new TouchListener() {
            @Override
            public void select(View v) {
                selectOption(v, 'X', optionXButton);
            }
        });

        optionYButton = findViewById(R.id.optionYButton);
        optionYButton.setDisplayText(question.getAnswerChoices()[2]);
        optionYButton.setOnTouchListener(new TouchListener() {
            @Override
            public void select(View v) {
                selectOption(v, 'Y', optionYButton);
            }
        });

        optionZButton = findViewById(R.id.optionZButton);
        optionZButton.setDisplayText(question.getAnswerChoices()[3]);
        optionZButton.setOnTouchListener(new TouchListener() {
            @Override
            public void select(View v) {
                selectOption(v, 'Z', optionZButton);
            }
        });

        menuButton = findViewById(R.id.menuButton);
        nextButton = findViewById(R.id.nextButton);

        timer = new CountDownTimer(seconds * 1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerLabel.setText(Math.round(Math.ceil(millisUntilFinished/1000.0)) + " Seconds Left");
            }

            @Override
            public void onFinish() {
                timerLabel.setText("Time's Up");
                stats.addNotAnswered();
                timedOut();
            }
        };
        timer.start();
    }

    private void disableButtons() {
        optionWButton.setEnabled(false);
        optionXButton.setEnabled(false);
        optionYButton.setEnabled(false);
        optionZButton.setEnabled(false);
    }

    private void makeCorrectAnswerButtonGreen() {
        char answerLetter = question.getAnswerLetter();
        if (answerLetter == 'W') {
            optionWButton.setBackgroundResource(R.drawable.quizoptionbuttoncorrect);
        } else if (answerLetter == 'X') {
            optionXButton.setBackgroundResource(R.drawable.quizoptionbuttoncorrect);
        } else if (answerLetter == 'Y') {
            optionYButton.setBackgroundResource(R.drawable.quizoptionbuttoncorrect);
        } else if (answerLetter == 'Z') {
            optionZButton.setBackgroundResource(R.drawable.quizoptionbuttoncorrect);
        }
    }

    private void optionSelected() {
        timer.cancel();
        disableButtons();
        makeCorrectAnswerButtonGreen();
        nextButton.setVisibility(View.VISIBLE);
        timerLabel.setVisibility(View.INVISIBLE);
    }

    private void timedOut() {
        disableButtons();
        makeCorrectAnswerButtonGreen();
        nextButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        // Prevents user from moving back through questions
    }

    private void selectOption(View view, char A, MathView button) {
        optionSelected();
        if (question.getAnswerLetter() == A) {
            stats.addCorrect();
        } else {
            button.setBackgroundResource(R.drawable.quizoptionbuttonwrong);
            stats.addIncorrect();
        }
    }

    public void finishSet(View view) {
        menuButton.setTextColor(Color.parseColor("#94cffe"));

        Intent intent = new Intent(QuizModePage.this, QuizModeStatsPage.class);
        intent.putExtra("STATS", stats);
        intent.putExtra("CATEGORY", category);

        startActivity(intent);
    }

    public void loadNextQuestion(View view) {
        nextButton.setTextColor(Color.parseColor("#94cffe"));
        Intent intent = new Intent(QuizModePage.this, QuizModePage.class);

        intent.putExtra("CATEGORY", category);
        intent.putExtra("TOSSUP_TIME", tossupTime);
        intent.putExtra("BONUS_TIME", bonusTime);
        intent.putExtra("STATS", stats);

        startActivity(intent);
    }

    private void getQuestionForParameters() {
        if (category.equals("Random")) {
            question = QuestionJSONParser.getInstance().getMCQuestion();
        } else {
            Category parsedCategory = getCategoryForString(category);
            question = QuestionJSONParser.getInstance().getMCQuestionForCategory(parsedCategory);
        }
    }

    private Category getCategoryForString(String s) {
        switch (s) {
            case "Biology": return Category.Biology;
            case "Chemistry": return Category.Chemistry;
            case "Earth and Space": return Category.EarthAndSpace;
            case "Energy": return Category.Energy;
            case "Math": return Category.Mathematics;
            case "Physics": return Category.Physics;
            default: return Category.GeneralScience;
        }
    }
}
