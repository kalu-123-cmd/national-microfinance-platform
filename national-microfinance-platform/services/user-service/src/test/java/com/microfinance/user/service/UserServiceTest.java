package com.microfinance.user.service;

import com.microfinance.common.exception.BusinessException;
import com.microfinance.event.KafkaTopics;
import com.microfinance.event.user.UserRegisteredEvent;
import com.microfinance.event.user.UserSuspendedEvent;
import com.microfinance.user.domain.model.AccountType;
import com.microfinance.user.domain.model.KycStatus;
import com.microfinance.user.domain.model.User;
import com.microfinance.user.domain.model.UserStatus;
import com.microfinance.user.domain.repository.UserDocumentRepository;
import com.microfinance.user.domain.repository.UserRepository;
import com.microfinance.user.dto.CreateUserRequest;
import com.microfinance.user.dto.UserResponse;
import com.microfinance.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDocumentRepository documentRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KafkaOperations<String, Object> kafkaTemplate;

    @Mock
    private FileStorageService fileStorageService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
            userRepository,
            documentRepository,
            userMapper,
            kafkaTemplate,
            fileStorageService
        );
    }

    @Test
    void createUserStoresDefaultsAndPublishesRegisteredEvent() {
        CreateUserRequest request = validCreateUserRequest();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserResponse.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .country(user.getCountry())
                .preferredLanguage(user.getPreferredLanguage())
                .status(user.getStatus())
                .kycStatus(user.getKycStatus())
                .build();
        });

        UserResponse response = userService.createUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(response.getId()).startsWith("USER-");
        assertThat(savedUser.getCountry()).isEqualTo("Ethiopia");
        assertThat(savedUser.getPreferredLanguage()).isEqualTo("en");
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
        assertThat(savedUser.getKycStatus()).isEqualTo(KycStatus.NOT_STARTED);

        ArgumentCaptor<UserRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq(KafkaTopics.USER_REGISTERED), eventCaptor.capture());
        assertThat(eventCaptor.getValue().getUserId()).isEqualTo(savedUser.getId());
    }

    @Test
    void createUserRejectsDuplicatePhoneNumber() {
        CreateUserRequest request = validCreateUserRequest();
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("phone number");

        verify(userRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void suspendUserUpdatesStatusAndPublishesSuspendedEvent() {
        User user = User.builder()
            .id("USER-2026-000001")
            .phoneNumber("+251911111111")
            .email("user@example.com")
            .firstName("Abel")
            .lastName("Tesfaye")
            .accountType(AccountType.BASIC)
            .status(UserStatus.ACTIVE)
            .kycStatus(KycStatus.APPROVED)
            .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.suspendUser(user.getId(), "risk review");

        verify(userRepository).updateUserStatus(user.getId(), UserStatus.SUSPENDED, "ADMIN");
        ArgumentCaptor<UserSuspendedEvent> eventCaptor = ArgumentCaptor.forClass(UserSuspendedEvent.class);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq(KafkaTopics.USER_SUSPENDED), eventCaptor.capture());
        assertThat(eventCaptor.getValue().getUserId()).isEqualTo(user.getId());
        assertThat(eventCaptor.getValue().getReason()).isEqualTo("risk review");
    }

    private CreateUserRequest validCreateUserRequest() {
        return CreateUserRequest.builder()
            .phoneNumber("+251911111111")
            .email("user@example.com")
            .firstName("Abel")
            .lastName("Tesfaye")
            .accountType(AccountType.BASIC)
            .termsAccepted(true)
            .privacyPolicyAccepted(true)
            .build();
    }
}
