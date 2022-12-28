package com.packages.backend.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.packages.backend.likes.Like;
import com.packages.backend.matches.Match;
import com.packages.backend.messages.Message;
import com.packages.backend.pictures.Picture;
import com.packages.backend.registration.token.ConfirmationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String nickname;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private Boolean locked = false;
    private Boolean enabled = false;
    private String gender;
    private String genderSearch;
    private String relationshipType;
    private Date birthDate;
    private String city;
    private Long height;
    private String languages;
    private String job;
    private String description;
    private String smokes;
    private String alcoholDrinking;
    private String organised;
    private String personality;
    private String sportPractice;
    private String animals;
    private String parenthood;
    private String gamer;
    private String activities;
    @OneToMany(mappedBy = "fkUser", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "pictures")
    private List<Picture> pictures;
    @OneToMany(mappedBy = "fkUserToken", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "tokens")
    private List<ConfirmationToken> tokens;
    @OneToMany(mappedBy = "fkReceiver", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "likesSent")
    private List<Like> likes;
    @OneToMany(mappedBy = "fkSender", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "matchesSent")
    private List<Match> matches;
    @OneToMany(mappedBy = "fkSender", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "messagesSent")
    private List<Message> messagesSent;
    @OneToMany(mappedBy = "fkReceiver", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "messagesReceived")
    private List<Message> messagesReceived;

    public User() {
    }

    public User(String nickname, String email, String password, UserRole userRole,String gender, String genderSearch, String relationshipType, Date birthDate, String city) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.gender = gender;
        this.genderSearch = genderSearch;
        this.relationshipType = relationshipType;
        this.birthDate = birthDate;
        this.city = city;
    }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public List<Picture> getPictures() {
    return pictures;
  }

  public void setPictures(List<Picture> pictures) {
    this.pictures = pictures;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Long getHeight() {
    return height;
  }

  public void setHeight(Long height) {
    this.height = height;
  }

  public String getLanguages() {
    return languages;
  }

  public void setLanguages(String languages) {
    this.languages = languages;
  }

  public String getJob() {
    return job;
  }

  public void setJob(String job) {
    this.job = job;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getGenderSearch() {
    return genderSearch;
  }

  public void setGenderSearch(String genderSearch) {
    this.genderSearch = genderSearch;
  }

  public String getRelationshipType() {
    return relationshipType;
  }

  public void setRelationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
  }

  public String getSmokes() {
    return smokes;
  }

  public void setSmokes(String smokes) {
    this.smokes = smokes;
  }

  public String getAlcoholDrinking() {
    return alcoholDrinking;
  }

  public void setAlcoholDrinking(String alcoholDrinking) {
    this.alcoholDrinking = alcoholDrinking;
  }

  public String getOrganised() {
    return organised;
  }

  public void setOrganised(String organised) {
    this.organised = organised;
  }

  public String getPersonality() {
    return personality;
  }

  public void setPersonality(String personality) {
    this.personality = personality;
  }

  public String getSportPractice() {
    return sportPractice;
  }

  public void setSportPractice(String sportPractice) {
    this.sportPractice = sportPractice;
  }

  public String getAnimals() {
    return animals;
  }

  public void setAnimals(String animals) {
    this.animals = animals;
  }

  public String getParenthood() {
    return parenthood;
  }

  public void setParenthood(String parenthood) {
    this.parenthood = parenthood;
  }

  public String getGamer() {
    return gamer;
  }

  public void setGamer(String gamer) {
    this.gamer = gamer;
  }

  public String getActivities() {
    return activities;
  }

  public void setActivities(String activities) {
    this.activities = activities;
  }

  public List<Like> getLikes() {
    return likes;
  }

  public void setLikes(List<Like> likes) {
    this.likes = likes;
  }

  public List<Match> getMatches() {
    return matches;
  }

  public void setMatches(List<Match> matches) {
    this.matches = matches;
  }

  public List<Message> getMessagesSent() {
    return messagesSent;
  }

  public void setMessagesSent(List<Message> messagesSent) {
    this.messagesSent = messagesSent;
  }

  public List<Message> getMessagesReceived() {
    return messagesReceived;
  }

  public void setMessagesReceived(List<Message> messagesReceived) {
    this.messagesReceived = messagesReceived;
  }

  public void setTokens(List<ConfirmationToken> tokens) {
    this.tokens = tokens;
  }

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", nickname='" + nickname + '\'' +
      ", email='" + email + '\'' +
      ", password='" + password + '\'' +
      ", userRole=" + userRole +
      ", locked=" + locked +
      ", enabled=" + enabled +
      ", gender='" + gender + '\'' +
      ", genderSearch='" + genderSearch + '\'' +
      ", relationshipType='" + relationshipType + '\'' +
      ", birthDate='" + birthDate + '\'' +
      ", city='" + city + '\'' +
      ", height='" + height + '\'' +
      ", languages='" + languages + '\'' +
      ", job='" + job + '\'' +
      ", description='" + description + '\'' +
      ", smokes='" + smokes + '\'' +
      ", alcoholDrinking='" + alcoholDrinking + '\'' +
      ", organised='" + organised + '\'' +
      ", personality='" + personality + '\'' +
      ", sportPractice='" + sportPractice + '\'' +
      ", animals='" + animals + '\'' +
      ", parenthood='" + parenthood + '\'' +
      ", gamer='" + gamer + '\'' +
      ", activities='" + activities + '\'' +
      ", pictures=" + pictures +
      ", tokens=" + tokens +
      ", likes=" + likes +
      ", matches=" + matches +
      ", messagesSent=" + messagesSent +
      ", messagesReceived=" + messagesReceived +
      '}';
  }
}
