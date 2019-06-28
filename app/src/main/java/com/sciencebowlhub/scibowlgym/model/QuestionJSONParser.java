package com.sciencebowlhub.scibowlgym.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Random;
import java.util.Scanner;

public class QuestionJSONParser {
  // Singleton definition
  private static QuestionJSONParser ourInstance;

  public static void createInstance(Context context) {
    if (ourInstance == null) {
      ourInstance = new QuestionJSONParser(context);
    }
  }

  @NonNull
  public static QuestionJSONParser getInstance() {
    return ourInstance;
  }

  private Random rand;

  private @NonNull ArrayList<Question> parsedQuestions;
  private ArrayList<Question> currentReaderSet;

  // indexed access: by category, by round, by category and round, by set and round.
  private @NonNull EnumMap<Category, ArrayList<Question>> byCategory;
  private @NonNull SparseArray<ArrayList<Question>> byRound;
  private @NonNull EnumMap<Category, SparseArray<ArrayList<Question>>> byCategoryRound;
  private @NonNull SparseArray<SparseArray<ArrayList<Question>>> bySetRound;

  // Initialization from json

  private QuestionJSONParser(Context context) {
    JSONArray jsonArray = new JSONArray();
    try {
      InputStream stream = context.getAssets().open("questions.json");
      Scanner s = new Scanner(stream).useDelimiter("\\A");
      String jsonString = s.hasNext() ? s.next() : "";
      jsonArray = new JSONArray(jsonString);
    } catch (JSONException | IOException e) {
      // File will always exist, so this case should never be reached
      e.printStackTrace();
    }

    parsedQuestions = new ArrayList<>();

    byCategory = new EnumMap<>(Category.class);
    byCategoryRound = new EnumMap<>(Category.class);
    for (Category cat : Category.values()) {
      byCategory.put(cat, new ArrayList<Question>());
      byCategoryRound.put(cat, new SparseArray<ArrayList<Question>>());
    }

    byRound = new SparseArray<>();
    bySetRound = new SparseArray<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        Question question = new Question(jsonObject);
        parsedQuestions.add(question);

        // add to indexes
        final Category cat = question.getCategory();
        final int round = question.getRoundNumber();
        final int set = question.getSetNumber();
        // cat
        byCategory.get(cat).add(question);
        // round
        if (byRound.get(round) == null) {
          byRound.put(round, new ArrayList<Question>());
        }
        byRound.get(round).add(question);
        // (cat, round)
        SparseArray<ArrayList<Question>> catRound = byCategoryRound.get(cat);
        if (catRound.get(round) == null) {
          catRound.put(round, new ArrayList<Question>());
        }
        catRound.get(round).add(question);
        // (set, round)
        if (bySetRound.get(set) == null) {
          bySetRound.put(set, new SparseArray<ArrayList<Question>>());
        }
        SparseArray<ArrayList<Question>> setRound = bySetRound.get(set);
        if (setRound.get(round) == null) {
          setRound.put(round, new ArrayList<Question>());
        }
        setRound.get(round).add(question);
      } catch (JSONException e) {
        // If something is wrong with the initialization, the element won't be added
      }

      // sort in set/round
      for (int si = 0; si < bySetRound.size(); ++si) {
        SparseArray<ArrayList<Question>> round = bySetRound.valueAt(si);
        for (int ri = 0; ri < round.size(); ++ri) {
          Collections.sort(round.valueAt(ri));
        }
      }
    }

    rand = new Random();
  }

  // Random question access methods

  private Question getRandomQuestion(@NonNull ArrayList<Question> lst) {
    int randomIndex = rand.nextInt(lst.size());
    return lst.get(randomIndex);
  }

  public Question getRandomQuestion() {
    return getRandomQuestion(parsedQuestions);
  }

  public Question getQuestionForCategory(@NonNull Category category) {
    return getRandomQuestion(byCategory.get(category));
  }

  public Question getQuestionForCategoryAndRound(Category category, int round) {
    return getRandomQuestion(byCategoryRound.get(category).get(round));
  }

  public Question getQuestionForRound(int round) {
    return getRandomQuestion(byRound.get(round));
  }

  public int getNumberOfRoundsForSet(int set) {
    return bySetRound.get(set).size();
  }

  public Question getMCQuestion() {
    while (true) {
      Question question = getRandomQuestion();
      if (question.getAnswerType() == AnswerType.MultipleChoice) {
        return question;
      }
    }
  }

  public Question getMCQuestionForCategory(Category category) {
    while (true) {
      Question question = getQuestionForCategory(category);
      if (question.getAnswerType() == AnswerType.MultipleChoice) {
        return question;
      }
    }
    // Will never time out because of limited category selections
  }

  public void saveQuestionSetForSetAndRound(int set, int round) {
    currentReaderSet = bySetRound.get(set).get(round);
  }

  public Question getCurrentReaderQuestion(int index) {
    return currentReaderSet.get(index);
  }

  public int getCurrentReaderSetLength() {
    return currentReaderSet.size();
  }
}
