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
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class QuestionJSONParser {
  // Singleton definition
  private static QuestionJSONParser ourInstance;

  public static QuestionJSONParser getInstance(Context context) {
    if (ourInstance == null) {
      ourInstance = new QuestionJSONParser(context);
    }

    return ourInstance;
  }

  public static QuestionJSONParser getInstance() {
    return ourInstance;
  }

  private Random rand;

  private ArrayList<Question> parsedQuestions;
  private ArrayList<Question> currentReaderSet;

  private EnumMap<Category, ArrayList<Question>> categoryQuestions;
  private SparseArray<SparseArray<ArrayList<Question>>> setRoundQuestions;

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

    categoryQuestions = new EnumMap<>(Category.class);
    for (Category cat : Category.values()) {
      categoryQuestions.put(cat, new ArrayList<Question>());
    }

    setRoundQuestions = new SparseArray<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        Question question = new Question(jsonObject);
        parsedQuestions.add(question);
        categoryQuestions.get(question.getCategory()).add(question);
        if (setRoundQuestions.get(question.getSetNumber()) == null) {
          setRoundQuestions.put(question.getSetNumber(), new SparseArray<ArrayList<Question>>());
        }
        SparseArray<ArrayList<Question>> roundQuestions =
            setRoundQuestions.get(question.getSetNumber());
        if (roundQuestions.get(question.getRoundNumber()) == null) {
          roundQuestions.put(question.getRoundNumber(), new ArrayList<Question>());
        }
        roundQuestions.get(question.getRoundNumber()).add(question);
      } catch (JSONException e) {
        // If something is wrong with the initialization, the element won't be added
        continue;
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
    return getRandomQuestion(categoryQuestions.get(category));
  }

  public Question getQuestionForCategoryAndRound(Category category, int round) {
    while (true) {
      Question question = getQuestionForCategory(category);
      if (question.getRoundNumber() == round) {
        return question;
      }
    }
    // Will never time out because of limited category and round selections
  }

  public Question getQuestionForRound(int round) {
    while (true) {
      Question question = getRandomQuestion();
      if (question.getRoundNumber() == round) {
        return question;
      }
    }
    // Will never time out because of limited round selections
  }

  public Question getQuestionForSetAndRound(int set, int round) {
    while (true) {
      Question question = getRandomQuestion();
      if (question.getSetNumber() == set && question.getRoundNumber() == round) {
        return question;
      }
    }
    // Will never time out because of limited set and round selections
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

  // Full question set methods

  public void saveQuestionSetForSetAndRound(int set, int round) {
    currentReaderSet = new ArrayList<>();
    for (Question question : parsedQuestions) {
      if (question.getSetNumber() == set && question.getRoundNumber() == round) {
        currentReaderSet.add(question);
      }
    }
    Collections.sort(currentReaderSet, new Comparator<Question>() {
      @Override
      public int compare(Question q1, Question q2) {
        if (q1.getQuestionNumber() == q2.getQuestionNumber()) {
          if (q1.getQuestionType() == q2.getQuestionType()) {
            return 0;
          }
          if (q1.getQuestionType() == QuestionType.Tossup) {
            return -1;
          }
          return 1;
        }
        return q1.getQuestionNumber() - q2.getQuestionNumber();
      }
    });
  }

  public Question getCurrentReaderQuestion(int index) {
    return currentReaderSet.get(index);
  }

  public int getCurrentReaderSetLength() {
    return currentReaderSet.size();
  }
}
