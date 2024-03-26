package com.packages.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.packages.backend.model.enums.*;

import javax.persistence.*;

@Entity
@Table(name = "preferences")
public class Preferences {
  @Id
  @Column(name = "user_id")
  private Long id;
  @Enumerated(EnumType.STRING)
  private AlcoholDrinking alcoholDrinking;
  @Enumerated(EnumType.STRING)
  private Smokes smokes;
  @Enumerated(EnumType.STRING)
  private Organised organised;
  @Enumerated(EnumType.STRING)
  private Personality personality;
  @Enumerated(EnumType.STRING)
  private SportPractice sportPractice;
  @Enumerated(EnumType.STRING)
  private Animals animals;
  @Enumerated(EnumType.STRING)
  private Parenthood parenthood;
  @Enumerated(EnumType.STRING)
  private Gamer gamer;
  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  @JsonBackReference(value = "preferences")
  private User fkUser;

  public Preferences() {
  }

  public Preferences(AlcoholDrinking alcoholDrinking, Smokes smokes, Organised organised, Personality personality, SportPractice sportPractice, Animals animals, Parenthood parenthood, Gamer gamer, User fkUser) {
    this.alcoholDrinking = alcoholDrinking;
    this.smokes = smokes;
    this.organised = organised;
    this.personality = personality;
    this.sportPractice = sportPractice;
    this.animals = animals;
    this.parenthood = parenthood;
    this.gamer = gamer;
    this.fkUser = fkUser;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AlcoholDrinking getAlcoholDrinking() {
    return alcoholDrinking;
  }

  public void setAlcoholDrinking(AlcoholDrinking alcoholDrinking) {
    this.alcoholDrinking = alcoholDrinking;
  }

  public Smokes getSmokes() {
    return smokes;
  }

  public void setSmokes(Smokes smokes) {
    this.smokes = smokes;
  }

  public Organised getOrganised() {
    return organised;
  }

  public void setOrganised(Organised organised) {
    this.organised = organised;
  }

  public Personality getPersonality() {
    return personality;
  }

  public void setPersonality(Personality personality) {
    this.personality = personality;
  }

  public SportPractice getSportPractice() {
    return sportPractice;
  }

  public void setSportPractice(SportPractice sportPractice) {
    this.sportPractice = sportPractice;
  }

  public Animals getAnimals() {
    return animals;
  }

  public void setAnimals(Animals animals) {
    this.animals = animals;
  }

  public Parenthood getParenthood() {
    return parenthood;
  }

  public void setParenthood(Parenthood parenthood) {
    this.parenthood = parenthood;
  }

  public Gamer getGamer() {
    return gamer;
  }

  public void setGamer(Gamer gamer) {
    this.gamer = gamer;
  }

  public User getFkUser() {
    return fkUser;
  }

  public void setFkUser(User fkUser) {
    this.fkUser = fkUser;
  }
}
