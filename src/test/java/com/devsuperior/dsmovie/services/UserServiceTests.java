package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CustomUserUtil userUtil;

	private List<UserDetailsProjection> userDetails;
	private UserEntity user;

	private String existingUsername;
	private String nonExistingUsername;

	@BeforeEach
	void setUp() throws Exception {

		existingUsername = "maria@gmail.com";
		nonExistingUsername = "thiago@gmail.com";
		user = UserFactory.createUserEntity();

		userDetails = UserDetailsFactory.createCustomAdminUser(existingUsername);

		Mockito.when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());
		Mockito.when(userRepository.searchUserAndRolesByUsername(existingUsername)).thenReturn(userDetails);
		Mockito.when(userRepository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(List.of());


	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
		UserEntity entity = service.authenticated();

		Assertions.assertNotNull(entity);
		Assertions.assertEquals(existingUsername, entity.getUsername());


	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();
		Assertions.assertThrows(UsernameNotFoundException.class, () -> service.authenticated());
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		UserDetails user = service.loadUserByUsername(existingUsername);

		Assertions.assertEquals(user.getUsername(), existingUsername);
		Assertions.assertNotNull(user);
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Assertions.assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(nonExistingUsername));
	}
}
