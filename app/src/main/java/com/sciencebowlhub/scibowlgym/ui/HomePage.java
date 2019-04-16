package com.sciencebowlhub.scibowlgym.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sciencebowlhub.scibowlgym.R;
import com.sciencebowlhub.scibowlgym.model.QuestionJSONParser;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        QuestionJSONParser.getInstance(getApplicationContext());
    }

    public void openQuizModeSettings(View view) {
        Intent intent = new Intent(HomePage.this, QuizModeSettingsPage.class);
        startActivity(intent);
    }

    public void openReaderModeSettings(View view) {
        Intent intent = new Intent(HomePage.this, ReaderModeSettingsPage.class);
        startActivity(intent);
    }

    public void openStudyModeSettings(View view) {
        Intent intent = new Intent(HomePage.this, StudyModeSettingsPage.class);
        startActivity(intent);
    }

    public void openAboutPage(View view) {
        Intent intent = new Intent(HomePage.this, AboutPage.class);
        startActivity(intent);
    }

    public void openHelpPage(View view) {
        Intent intent = new Intent(HomePage.this, HelpPage.class);
        startActivity(intent);
    }
}
