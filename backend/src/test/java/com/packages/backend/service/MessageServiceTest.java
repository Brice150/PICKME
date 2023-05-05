package com.packages.backend.service;

import com.packages.backend.messages.MessageRepository;
import com.packages.backend.messages.MessageService;
import com.packages.backend.user.RestrictedUserDTOMapper;
import com.packages.backend.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserService userService;
  private RestrictedUserDTOMapper restrictedUserDTOMapper;
  private MessageService messageService;

  @BeforeEach
  void setUp() {
    messageService = new MessageService(messageRepository, userService, restrictedUserDTOMapper);
  }
}
