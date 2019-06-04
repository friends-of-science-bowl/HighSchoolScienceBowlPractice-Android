package com.sciencebowlhub.scibowlgym.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sciencebowlhub.scibowlgym.R;

public class HelpPage extends AppCompatActivity {
  private Button menuButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help_page);

    menuButton = findViewById(R.id.menuButton);
  }

  public void returnMainMenu(View view) {
    menuButton.setTextColor(Color.parseColor("#94cffe"));
    Intent intent = new Intent(HelpPage.this, HomePage.class);
    startActivity(intent);
  }
}
