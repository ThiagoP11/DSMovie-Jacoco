package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private Page<MovieEntity> page;
	private MovieEntity movieEntity;
	private Long existingMovieId;
	private Long nonExistingMovieId;
	private Long dependentMovieId;
	private MovieDTO dto;

	@BeforeEach
	void setUp() throws Exception {

		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		dependentMovieId = 3L;

		dto = MovieFactory.createMovieDTO();

		movieEntity = MovieFactory.createMovieEntity();
		page = new PageImpl<>(List.of(movieEntity));

		Mockito.when(repository.searchByTitle(ArgumentMatchers.any(), (Pageable)ArgumentMatchers.any()))
				.thenReturn(page);
		Mockito.when(repository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));
		Mockito.when(repository.findById(nonExistingMovieId)).thenReturn(Optional.empty())
				.thenThrow(ResourceNotFoundException.class);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(movieEntity);
		Mockito.when(repository.getReferenceById(existingMovieId)).thenReturn(movieEntity);
		Mockito.when(repository.getReferenceById(nonExistingMovieId)).thenThrow(EntityNotFoundException.class);

		Mockito.when(repository.existsById(existingMovieId)).thenReturn(true);
		Mockito.when(repository.existsById(dependentMovieId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistingMovieId)).thenReturn(false);

		Mockito.doNothing().when(repository).deleteById(existingMovieId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentMovieId);
	}
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Pageable pageable = PageRequest.of(0, 12);
		String name = "test";
		Page<MovieDTO> result = service.findAll(name, pageable);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getSize(), 1);

	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.findById(existingMovieId);

		Assertions.assertEquals(result.getId(), existingMovieId);
		Assertions.assertNotNull(result);

	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingMovieId);
		});
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
		MovieDTO movieDTO = service.insert(dto);
		Assertions.assertNotNull(movieDTO);
		Assertions.assertEquals(movieDTO.getId(), existingMovieId);
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		MovieDTO movieDTO = service.update(existingMovieId, dto);

		Assertions.assertNotNull(movieDTO);
		Assertions.assertEquals(movieDTO.getId(), existingMovieId);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingMovieId, dto);
		});
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingMovieId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingMovieId);
		});
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentMovieId));
	}
}
