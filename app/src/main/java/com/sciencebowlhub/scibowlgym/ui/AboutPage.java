package com.sciencebowlhub.scibowlgym.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sciencebowlhub.scibowlgym.R;

public class AboutPage extends AppCompatActivity {
    private Button menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        menuButton = findViewById(R.id.menuButton);

        TextView creator = findViewById(R.id.creatorLabel);
        creator.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void returnMainMenu(View view) {
        menuButton.setTextColor(Color.parseColor("#94cffe"));
        Intent intent = new Intent(AboutPage.this, HomePage.class);
        startActivity(intent);
    }
}
