package com.packages.backend.registration;

import java.util.Date;

public class RegistrationRequest {
    private final String nickname;
    private final String email;
    private final String password;
    private final String gender;
    private final String genderSearch;
    private final String relationshipSearch;
    private final Date birthDate;
    private final String city;

    public RegistrationRequest(String nickname, String email, String password,String gender, String genderSearch, String relationshipSearch, Date birthDate, String city) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.genderSearch = genderSearch;
        this.relationshipSearch = relationshipSearch;
        this.birthDate = birthDate;
        this.city = city;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

  public String getGender() {
    return gender;
  }

  public String getGenderSearch() {
    return genderSearch;
  }

  public String getRelationshipSearch() {
    return relationshipSearch;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public String getCity() {
    return city;
  }

  @Override
  public String toString() {
    return "RegistrationRequest{" +
      "nickname='" + nickname + '\'' +
      ", email='" + email + '\'' +
      ", password='" + password + '\'' +
      ", genderSearch='" + genderSearch + '\'' +
      ", relationshipSearch='" + relationshipSearch + '\'' +
      ", birthDate='" + birthDate + '\'' +
      ", city='" + city + '\'' +
      '}';
  }
}
