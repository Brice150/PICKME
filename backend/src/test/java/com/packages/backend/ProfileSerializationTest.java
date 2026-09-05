package com.packages.backend;

import com.packages.backend.model.entity.Picture;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.PictureRepository;
import com.packages.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Serializes the responses that carry an association loaded lazily, against a real database and
 * through the whole stack.
 * <p>
 * The unit tests cannot catch this: they mock the services, so nothing ever reaches the JSON
 * writer. Yet the album of an account is a lazy collection, and whether it can still be read once
 * the controller has returned depends on a single setting of the persistence layer.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("serialization of the lazy associations")
class ProfileSerializationTest {

  private static final String EMAIL = "serialization@pickme.com";

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PictureRepository pictureRepository;

  private MockMvc mockMvc;

  /**
   * The class is deliberately not transactional: a transaction held open around the request would
   * keep the persistence session alive and hide the very thing these tests measure.
   */
  @BeforeEach
  void setUp(WebApplicationContext context) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    clearDatabase();
    User user = TestFixtures.user(null);
    user.setEmail(EMAIL);
    User saved = userRepository.saveAndFlush(user);
    pictureRepository.saveAndFlush(new Picture("main", true, saved));
    pictureRepository.saveAndFlush(new Picture("secondary", false, saved));
  }

  /**
   * Nothing rolls the rows back here, so the class leaves the database as it found it: the tests
   * that count the accounts would otherwise see the profile of this one.
   */
  @AfterEach
  void tearDown() {
    clearDatabase();
  }

  private void clearDatabase() {
    pictureRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("writes the album of the connected account into the response")
  @WithMockUser(username = EMAIL, roles = "USER")
  void theAlbumOfTheConnectedAccountIsSerialized() throws Exception {
    mockMvc.perform(get("/user"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.email").value(EMAIL))
      .andExpect(jsonPath("$.pictures.length()").value(2));
  }

  @Test
  @DisplayName("writes the album of the connected account on the login call as well")
  @WithMockUser(username = EMAIL, roles = "USER")
  void theLoginCallSerializesTheSameView() throws Exception {
    mockMvc.perform(get("/login"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.pictures.length()").value(2));
  }
}
