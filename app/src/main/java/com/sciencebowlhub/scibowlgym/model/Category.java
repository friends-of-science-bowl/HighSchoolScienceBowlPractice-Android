package com.sciencebowlhub.scibowlgym.model;

import android.support.annotation.NonNull;

/**
 * Created by jakepolatty on 9/7/17.
 */

public enum Category {
  Biology("Life Science"),
  Chemistry("Chemistry"),
  EarthAndSpace("Earth and Space"),
  Energy("Energy"),
  Mathematics("Mathematics"),
  Physics("Physical Science"),
  GeneralScience("General Science"),
  ComputerScience("Computer Science");

  @NonNull private final String displayName;

  Category(@NonNull String s) {
    displayName = s;
  }

  @NonNull
  public String toString() {
    return displayName;
  }

  public static Category fromString(String s) {
    switch (s) {
      case "BIO":
        return Biology;
      case "CHEM":
        return Chemistry;
      case "EAS":
        return EarthAndSpace;
      case "ENG":
        return Energy;
      case "MATH":
        return Mathematics;
      case "PHY":
        return Physics;
      case "GS":
        return GeneralScience;
      case "CS":
        return ComputerScience;
      default:
        return GeneralScience;
    }
  }}
