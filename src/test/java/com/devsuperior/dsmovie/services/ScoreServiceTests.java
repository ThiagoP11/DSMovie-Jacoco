package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private ScoreRepository scoreRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private UserService userService;

	private Long existingScoreId, nonExistingScoreId;

	private UserEntity client, admin;

	private ScoreEntity scoreEntity;
	private MovieEntity movieEntity;
	private ScoreDTO scoreDTO;
	private MovieDTO movieDTO;


	@BeforeEach
	public void setup() {}

	@BeforeEach
	void setUp() throws Exception {
		existingScoreId = 1L;
		nonExistingScoreId = 2L;

		scoreEntity = ScoreFactory.createScoreEntity();
		movieEntity = MovieFactory.createMovieEntity();
		movieEntity.getScores().add(scoreEntity);
		scoreDTO = ScoreFactory.createScoreDTO();
		movieDTO = MovieFactory.createMovieDTO();

		admin = UserFactory.createUserEntity();
		client = UserFactory.createUserEntity();


		Mockito.when(movieRepository.findById(movieDTO.getId())).thenReturn(Optional.of(movieEntity));
		Mockito.when(movieRepository.findById(nonExistingScoreId)).thenReturn(Optional.empty())
				.thenThrow(ResourceNotFoundException.class);
		Mockito.when(scoreRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(scoreEntity);
		Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movieEntity);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		Mockito.when(userService.authenticated()).thenReturn(admin);
		MovieDTO result = service.saveScore(scoreDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), movieEntity.getId());
		Assertions.assertEquals(result.getScore(), movieEntity.getScore());
		Assertions.assertEquals(result.getCount(), movieEntity.getCount());

	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(new ScoreDTO(nonExistingScoreId, 0.0));
		});
	}
}
