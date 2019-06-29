package com.sciencebowlhub.scibowlgym.model;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

enum QuestionJSONKeys {
  QuestionText("qTxt"),
  QuestionAnswer("qAns"),
  AnswerChoices("ansCh"),
  Category("cat"),
  SetNumber("sNum"),
  RoundNumber("rNum"),
  QuestionNumber("qNum"),
  AnswerType("mc"),
  QuestionType("tb");

  @NonNull private final String keyString;

  QuestionJSONKeys(@NonNull String s) {
    keyString = s;
  }

  @NonNull
  public String key() {
    return keyString;
  }
}

public class Question implements Comparable<Question> {
  private String questionText;
  private Category category;
  private QuestionType questionType;
  private AnswerType answerType;
  private int setNumber;
  private int roundNumber;
  private int questionNumber;
  private String[] answerChoices;
  private String answer;

  public Question(JSONObject json) throws JSONException {
    questionText = json.getString(QuestionJSONKeys.QuestionText.key());
    answer = json.getString(QuestionJSONKeys.QuestionAnswer.key());
    setNumber = json.getInt(QuestionJSONKeys.SetNumber.key());
    roundNumber = json.getInt(QuestionJSONKeys.RoundNumber.key());
    questionNumber = json.getInt(QuestionJSONKeys.QuestionNumber.key());
    category = Category.fromString(json.getString(QuestionJSONKeys.Category.key()));
    answerType = AnswerType.fromString(json.getString(QuestionJSONKeys.AnswerType.key()));
    questionType = QuestionType.fromString(json.getString(QuestionJSONKeys.QuestionType.key()));

    if (answerType == AnswerType.MultipleChoice) {
      JSONArray ansChoicesArray = json.getJSONArray(QuestionJSONKeys.AnswerChoices.key());
      answerChoices = new String[4];
      answerChoices[0] = ansChoicesArray.getString(0);
      answerChoices[1] = ansChoicesArray.getString(1);
      answerChoices[2] = ansChoicesArray.getString(2);
      answerChoices[3] = ansChoicesArray.getString(3);
    } else {
      answerChoices = null;
    }
  }

  public int compareTo(Question o) {
    if (questionNumber == o.questionNumber) {
      if (questionType == o.questionType) {
        return 0;
      }
      if (questionType == QuestionType.Tossup) {
        return -1;
      }
      return 1;
    }
    return questionNumber - o.questionNumber;
  }

  // unique id for dup check
  public int id() {
    return (setNumber * 10000 + roundNumber * 100 + questionNumber)
        * (questionType == QuestionType.Tossup ? -1 : 1);
  }

  public String getQuestionText() {
    return questionText;
  }

  public Category getCategory() {
    return category;
  }

  public QuestionType getQuestionType() {
    return questionType;
  }

  public AnswerType getAnswerType() {
    return answerType;
  }

  public int getSetNumber() {
    return setNumber;
  }

  public int getRoundNumber() {
    return roundNumber;
  }

  public int getQuestionNumber() {
    return questionNumber;
  }

  public String[] getAnswerChoices() {
    return answerChoices;
  }

  public String getAnswer() {
    return answer;
  }

  public char getAnswerLetter() {
    return answer.charAt(0);
  }
}
