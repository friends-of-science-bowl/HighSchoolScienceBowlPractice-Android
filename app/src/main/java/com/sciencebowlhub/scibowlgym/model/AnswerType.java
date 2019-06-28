package com.sciencebowlhub.scibowlgym.model;

import android.support.annotation.NonNull;

/**
 * Created by jakepolatty on 9/8/17.
 */

public enum AnswerType {
  MultipleChoice("Multiple Choice"),
  ShortAnswer("Short Answer");

  @NonNull private final String displayName;

  AnswerType(@NonNull String s) {
    displayName = s;
  }

  @NonNull
  public String toString() {
    return displayName;
  }

  public static AnswerType fromString(String s) {
    switch (s) {
      case "MC":
        return MultipleChoice;
      case "SA":
        return ShortAnswer;
      default:
        return ShortAnswer;
    }
  }}
