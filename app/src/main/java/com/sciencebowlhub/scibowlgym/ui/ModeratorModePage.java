package com.sciencebowlhub.scibowlgym.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sciencebowlhub.scibowlgym.R;
import com.sciencebowlhub.scibowlgym.model.AnswerType;
import com.sciencebowlhub.scibowlgym.model.Question;
import com.sciencebowlhub.scibowlgym.model.QuestionJSONParser;
import com.sciencebowlhub.scibowlgym.model.QuestionType;

import katex.hourglass.in.mathlib.MathView;

public class ModeratorModePage extends AppCompatActivity {
  // Question text fields
  private TextView roundSetNumLabel;
  private TextView questionNumLabel;
  private TextView categoryTypeLabel;
  private MathView questionTextLabel;
  private MathView answerOptionsLabel;
  private MathView answerLabel;
  private TextView timerLabel;

  // Toolbar Buttons
  private Button menuButton;
  private Button nextButton;

  private Button startTimerButton;

  // Round questionTimer components
  private LinearLayout timerBar;
  private TextView roundTimeLabel;
  private ToggleButton roundTimerStartToggle;

  // Intent fields
  private int questionIndex;
  private int tossupTime;
  private int bonusTime;
  private boolean isTimedRound;
  private long roundTimeRemaining;
  private int halfNum;
  private boolean isTimerRunning;

  private CountDownTimer questionTimer;
  private CountDownTimer roundTimer;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_moderator_mode_page);

    Intent intent = getIntent();

    tossupTime = intent.getIntExtra("TOSSUP_TIME", 5);
    bonusTime = intent.getIntExtra("BONUS_TIME", 20);
    questionIndex = intent.getIntExtra("INDEX", 0);

    isTimedRound = intent.getBooleanExtra("TIMED_ROUND", false);
    if (isTimedRound) {
      roundTimeRemaining = intent.getLongExtra("TIME_REMAINING", 480000);
      halfNum = intent.getIntExtra("HALF", 1);
      isTimerRunning = intent.getBooleanExtra("TIMER_RUNNING", false);
    }

    Question question = QuestionJSONParser.getInstance().getCurrentReaderQuestion(questionIndex);

    int seconds;
    if (question.getQuestionType() == QuestionType.Tossup) {
      seconds = tossupTime;
    } else {
      seconds = bonusTime;
    }

    roundSetNumLabel = findViewById(R.id.roundSetNumLabel);
    roundSetNumLabel
        .setText("Question Set " + question.getSetNumber() + " Round " + question.getRoundNumber());

    questionNumLabel = findViewById(R.id.questionNumLabel);
    questionNumLabel.setText(
        "Question " + question.getQuestionNumber() + " " + question.getQuestionType().toString());

    categoryTypeLabel = findViewById(R.id.categoryTypeLabel);
    categoryTypeLabel
        .setText(question.getCategory().toString() + " " + question.getAnswerType().toString());

    questionTextLabel = findViewById(R.id.questionTextLabel);
    questionTextLabel.setDisplayText(question.getQuestionText());

    answerOptionsLabel = findViewById(R.id.answerOptionsLabel);
    if (question.getAnswerType() == AnswerType.MultipleChoice &&
        question.getAnswerChoices().length == 4) {
      System.out.println("Test");
      answerOptionsLabel.setDisplayText(
          question.getAnswerChoices()[0] + "<br>" +
              question.getAnswerChoices()[1] + "<br>" +
              question.getAnswerChoices()[2] + "<br>" +
              question.getAnswerChoices()[3]
      );
    }

    answerLabel = findViewById(R.id.answerLabel);
    answerLabel.setDisplayText("Answer: " + question.getAnswer());

    timerLabel = findViewById(R.id.timerLabel);
    timerLabel.setText(seconds + " Seconds Left");

    menuButton = findViewById(R.id.menuButton);

    nextButton = findViewById(R.id.nextButton);
    if (questionIndex == QuestionJSONParser.getInstance().getCurrentReaderSetLength() - 1) {
      nextButton.setText("Finish Set");
      nextButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          nextButton.setTextColor(Color.parseColor("#94cffe"));
          Intent intent = new Intent(ModeratorModePage.this, ModeratorModeSettingsPage.class);
          startActivity(intent);
        }
      });
    }

    startTimerButton = findViewById(R.id.startTimerButton);

    timerBar = findViewById(R.id.timerBar);
    roundTimeLabel = findViewById(R.id.roundTimeLabel);
    roundTimerStartToggle = findViewById(R.id.roundTimerStartToggle);

    if (!isTimedRound) {
      timerBar.setVisibility(View.INVISIBLE);
      timerBar.removeAllViews();
      timerBar.setMinimumHeight(0);
    } else {
      roundTimeLabel.setText(getRoundTimerString(roundTimeRemaining));

      if (roundTimeRemaining < 1000) {
        roundTimeLabel.setText("Round Over");
        roundTimerStartToggle.setVisibility(View.INVISIBLE);
        roundTimerStartToggle.setChecked(false);
      } else if (roundTimeRemaining < 480000) {
        roundTimerStartToggle.setTextOff("Resume");
        roundTimerStartToggle.setChecked(false);
      }
    }

    questionTimer = new CountDownTimer(seconds * 1000, 100) {
      @Override
      public void onTick(long millisUntilFinished) {
        timerLabel.setText(Math.round(Math.ceil(millisUntilFinished / 1000.0)) + " Seconds Left");
      }

      @Override
      public void onFinish() {
        timerLabel.setText("Time's Up");
      }
    };

    if (isTimedRound) {
      roundTimer = createRoundTimer();

      if (isTimerRunning) {
        roundTimerStartToggle.setChecked(true);
        roundTimer.start();
      }
    }
  }

  private CountDownTimer createRoundTimer() {
    return new CountDownTimer(roundTimeRemaining, 100) {
      @Override
      public void onTick(long millisUntilFinished) {
        roundTimeRemaining = millisUntilFinished;
        roundTimeLabel.setText(getRoundTimerString(millisUntilFinished));
      }

      @Override
      public void onFinish() {
        isTimerRunning = false;
        if (halfNum == 1) {
          roundTimeLabel.setText("Halftime");
          roundTimerStartToggle.setTextOff("Start Half 2");
          roundTimerStartToggle.setChecked(false);
          isTimerRunning = false;
          roundTimeRemaining = 480000;
          halfNum = 2;
        } else {
          roundTimeLabel.setText("Round Over");
          roundTimerStartToggle.setVisibility(View.INVISIBLE);
          roundTimerStartToggle.setChecked(false);
          isTimerRunning = false;
        }
      }
    };
  }

  private String getRoundTimerString(long millis) {
    String timeString = "";

    int minutes = (int) ((millis / (1000 * 60)) % 60);
    int seconds = (int) ((millis / 1000) % 60);
    if (halfNum == 1) {
      timeString = (minutes + ":" + String.format("%02d", seconds) + " (Half 1)");
    } else {
      timeString = (minutes + ":" + String.format("%02d", seconds) + " (Half 2)");
    }

    return timeString;
  }

  @Override
  protected void onResume() {
    super.onResume();
    nextButton.setTextColor(Color.parseColor("#ffffff"));
  }

  public void startTimer(View view) {
    startTimerButton.setVisibility(View.INVISIBLE);
    timerLabel.setVisibility(View.VISIBLE);
    questionTimer.start();
  }

  public void returnMainMenu(View view) {
    questionTimer.cancel();
    menuButton.setTextColor(Color.parseColor("#94cffe"));
    Intent intent = new Intent(ModeratorModePage.this, HomePage.class);
    startActivity(intent);
  }

  public void loadNextQuestion(View view) {
    questionTimer.cancel();
    timerLabel.setVisibility(View.INVISIBLE);
    nextButton.setTextColor(Color.parseColor("#94cffe"));
    Intent intent = new Intent(ModeratorModePage.this, ModeratorModePage.class);

    intent.putExtra("TOSSUP_TIME", tossupTime);
    intent.putExtra("BONUS_TIME", bonusTime);
    intent.putExtra("INDEX", questionIndex + 1);

    intent.putExtra("TIMED_ROUND", isTimedRound);
    if (isTimedRound) {
      intent.putExtra("TIME_REMAINING", roundTimeRemaining);
      intent.putExtra("HALF", halfNum);
      intent.putExtra("TIMER_RUNNING", isTimerRunning);
    }

    startActivity(intent);
  }

  public void toggleRoundTimer(View view) {
    if (roundTimerStartToggle.isChecked()) { // Running timer
      roundTimer = createRoundTimer();
      roundTimer.start();
      isTimerRunning = true;
    } else { // Paused
      roundTimer.cancel();
      roundTimerStartToggle.setTextOff("Resume");
      roundTimerStartToggle.setChecked(false);
      isTimerRunning = false;
    }
  }
}
