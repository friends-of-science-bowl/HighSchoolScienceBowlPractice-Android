package com.sciencebowlhub.scibowlgym.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ToggleButton;

import com.sciencebowlhub.scibowlgym.R;
import com.sciencebowlhub.scibowlgym.model.Category;
import com.sciencebowlhub.scibowlgym.model.QuestionJSONParser;

public class StudyModeSettingsPage extends AppCompatActivity {
  private static final String[] roundOptions =
      {"All Rounds", "Round 1", "Round 2", "Round 3", "Round 4", "Round 5", "Round 6", "Round 7",
          "Round 8", "Round 9", "Round 10", "Round 11", "Round 12", "Round 13", "Round 14",
          "Round 15", "Round 16", "Round 17"};

  private String selectedCategory = "Random";

  // Topic option toggle buttons
  private ToggleButton biologyButton;
  private ToggleButton generalScienceButton;
  private ToggleButton earthAndSpaceButton;
  private ToggleButton energyButton;
  private ToggleButton mathButton;
  private ToggleButton physicsButton;
  private ToggleButton randomButton;

  private NumberPicker roundNumPicker;

  private Button menuButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_study_mode_settings_page);

    biologyButton = (ToggleButton) findViewById(R.id.biologyButton);
    generalScienceButton = (ToggleButton) findViewById(R.id.generalScienceButton);
    earthAndSpaceButton = (ToggleButton) findViewById(R.id.earthAndSpaceButton);
    energyButton = (ToggleButton) findViewById(R.id.energyButton);
    mathButton = (ToggleButton) findViewById(R.id.mathButton);
    physicsButton = (ToggleButton) findViewById(R.id.physicsButton);
    randomButton = (ToggleButton) findViewById(R.id.randomButton);

    roundNumPicker = (NumberPicker) findViewById(R.id.roundNumPicker);
    roundNumPicker.setMinValue(0);
    roundNumPicker.setMaxValue(roundOptions.length - 1);
    roundNumPicker.setWrapSelectorWheel(false);
    roundNumPicker.setDisplayedValues(roundOptions);

    menuButton = (Button) findViewById(R.id.menuButton);
  }

  public void startStudyMode(View view) {
    Intent intent = new Intent(StudyModeSettingsPage.this, StudyModePage.class);
    intent.putExtra("CATEGORY", selectedCategory);

    int roundNum = roundNumPicker.getValue();
    intent.putExtra("ROUND", roundNum);

    QuestionJSONParser parser = QuestionJSONParser.getInstance();

    //all rounds selected
    if (roundNum==0) {
      if (selectedCategory.equals("Random")) {
        //no specific category selected, all questions valid
        parser.generateRandomQuestionList();
      }
      else {
        //specific category selected, all rounds valid
        Category cat = getCategoryForString(selectedCategory);
        parser.generateRandomQuestionsbyCat(cat);
      }
    }
    //specific round selected
    else {
      if (selectedCategory.equals("Random")) {
        //no specific category selected
        parser.generateRandomQuestionforRound(roundNum);
      }
      else {
        //specific category and round, random order
        Category cat = getCategoryForString(selectedCategory);
        parser.generateRandomRoundCat(roundNum, cat);
      }
    }

    startActivity(intent);
  }

  public void returnMainMenu(View view) {
    menuButton.setTextColor(Color.parseColor("#94cffe"));
    Intent intent = new Intent(StudyModeSettingsPage.this, HomePage.class);
    startActivity(intent);
  }

  private void toggleOff() {
    biologyButton.setChecked(false);
    biologyButton.setSelected(false);
    generalScienceButton.setChecked(false);
    generalScienceButton.setSelected(false);
    earthAndSpaceButton.setChecked(false);
    earthAndSpaceButton.setSelected(false);
    energyButton.setChecked(false);
    energyButton.setSelected(false);
    mathButton.setChecked(false);
    mathButton.setSelected(false);
    physicsButton.setChecked(false);
    physicsButton.setSelected(false);
    randomButton.setChecked(false);
    randomButton.setSelected(false);
  }

  public void selectBiology(View view) {
    toggleOff();
    selectedCategory = "Biology";
    biologyButton.setChecked(true);
    biologyButton.setSelected(true);
  }

  public void selectGeneralScience(View view) {
    toggleOff();
    selectedCategory = "General Science";
    generalScienceButton.setChecked(true);
    generalScienceButton.setSelected(true);
  }

  public void selectEarthAndSpace(View view) {
    toggleOff();
    selectedCategory = "Earth and Space";
    earthAndSpaceButton.setChecked(true);
    earthAndSpaceButton.setSelected(true);
  }

  public void selectEnergy(View view) {
    toggleOff();
    selectedCategory = "Energy";
    energyButton.setChecked(true);
    energyButton.setSelected(true);
  }

  public void selectMath(View view) {
    toggleOff();
    selectedCategory = "Math";
    mathButton.setChecked(true);
    mathButton.setSelected(true);
  }

  public void selectPhysics(View view) {
    toggleOff();
    selectedCategory = "Physics";
    physicsButton.setChecked(true);
    physicsButton.setSelected(true);
  }

  public void selectRandom(View view) {
    toggleOff();
    selectedCategory = "Random";
    randomButton.setChecked(true);
    randomButton.setSelected(true);
  }

  private Category getCategoryForString(String s) {
    switch (s) {
      case "Biology":
        return Category.Biology;
      case "Chemistry":
        return Category.Chemistry;
      case "Earth and Space":
        return Category.EarthAndSpace;
      case "Energy":
        return Category.Energy;
      case "Math":
        return Category.Mathematics;
      case "Physics":
        return Category.Physics;
      default:
        return Category.GeneralScience;
    }
  }
}
