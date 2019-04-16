package com.sciencebowlhub.scibowlgym.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sciencebowlhub.scibowlgym.R;
import com.sciencebowlhub.scibowlgym.model.AnswerType;
import com.sciencebowlhub.scibowlgym.model.Category;
import com.sciencebowlhub.scibowlgym.model.Question;
import com.sciencebowlhub.scibowlgym.model.QuestionJSONParser;

import katex.hourglass.in.mathlib.MathView;

public class StudyModePage extends AppCompatActivity {
    // Question text fields
    private TextView roundSetNumLabel;
    private TextView questionNumLabel;
    private TextView categoryTypeLabel;
    private MathView questionTextLabel;
    private MathView answerOptionsLabel;
    private MathView answerLabel;

    // Toolbar Buttons
    private Button menuButton;
    private Button nextButton;

    private Button showAnswerButton;

    // Intent fields
    private String category;
    private int round;

    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_mode_page);

        Intent intent = getIntent();
        category = intent.getStringExtra("CATEGORY");
        round = intent.getIntExtra("ROUND", 0);

        getQuestionForParameters();
        if (question == null) {
            QuestionJSONParser.getInstance(getApplicationContext());
            getQuestionForParameters();
        }

        roundSetNumLabel = findViewById(R.id.roundSetNumLabel);
        roundSetNumLabel.setText("Question Set " + question.getSetNumber() + ", Round " + question.getRoundNumber());

        questionNumLabel = findViewById(R.id.questionNumLabel);
        questionNumLabel.setText("Question " + question.getQuestionNumber() + ", " + question.getQuestionType().toString());

        categoryTypeLabel = findViewById(R.id.categoryTypeLabel);
        categoryTypeLabel.setText(question.getCategory().toString() + ", " + question.getAnswerType().toString());

        questionTextLabel = findViewById(R.id.questionTextLabel);
        questionTextLabel.setDisplayText(question.getQuestionText());

        answerOptionsLabel = findViewById(R.id.answerOptionsLabel);
        if (question.getAnswerType() == AnswerType.MultipleChoice && question.getAnswerChoices().length == 4) {
            System.out.println("Test");
            answerOptionsLabel.setDisplayText(
                    question.getAnswerChoices()[0]+"<br>"+
                    question.getAnswerChoices()[1]+"<br>"+
                    question.getAnswerChoices()[2]+"<br>"+
                    question.getAnswerChoices()[3]
            );
        }

        answerLabel = findViewById(R.id.answerLabel);
        answerLabel.setDisplayText("Answer: " + question.getAnswer());

        menuButton = findViewById(R.id.menuButton);
        nextButton = findViewById(R.id.nextButton);

        showAnswerButton = findViewById(R.id.showAnswerButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nextButton.setTextColor(Color.parseColor("#ffffff"));
    }

    public void returnMainMenu(View view) {
        menuButton.setTextColor(Color.parseColor("#94cffe"));
        Intent intent = new Intent(StudyModePage.this, HomePage.class);
        startActivity(intent);
    }

    public void loadNextQuestion(View view) {
        nextButton.setTextColor(Color.parseColor("#94cffe"));
        Intent intent = new Intent(StudyModePage.this, StudyModePage.class);

        intent.putExtra("CATEGORY", category);
        intent.putExtra("ROUND", round);

        startActivity(intent);
    }

    public void showAnswer(View view) {
        showAnswerButton.setVisibility(View.INVISIBLE);
        answerLabel.setVisibility(View.VISIBLE);
    }

    private void getQuestionForParameters() {
        if (category.equals("Random")) {
            if (round == 0) {
                question = QuestionJSONParser.getInstance().getRandomQuestion();
            } else {
                question = QuestionJSONParser.getInstance().getQuestionForRound(round);
            }
        } else {
            Category parsedCategory = getCategoryForString(category);
            if (round == 0) {
                question = QuestionJSONParser.getInstance().getQuestionForCategory(parsedCategory);
            } else {
                question = QuestionJSONParser.getInstance().getQuestionForCategoryAndRound(parsedCategory, round);
            }
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
