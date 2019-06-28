package com.sciencebowlhub.scibowlgym.model;

import android.support.annotation.NonNull;

/**
 * Created by jakepolatty on 9/8/17.
 */

public enum QuestionType {
  Tossup("Tossup"),
  Bonus("Bonus");

  @NonNull private final String displayName;

  QuestionType(@NonNull String s) {
    displayName = s;
  }

  @NonNull
  public String toString() {
    return this.displayName;
  }

  public static QuestionType fromString(String s) {
    switch (s) {
      case "T":
        return Tossup;
      case "B":
        return Bonus;
      default:
        return Tossup;
    }
  }}
