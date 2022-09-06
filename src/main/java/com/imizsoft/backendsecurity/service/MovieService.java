package com.imizsoft.backendsecurity.service;

import com.imizsoft.backendsecurity.model.Movie;
import com.imizsoft.backendsecurity.reprository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies(){
        return movieRepository.findAll();
    }

    public Movie getMovieById(String id){
        return movieRepository.findMovieById(id).orElseThrow(()-> new RuntimeException("Movie with id: "+id+" not found"));
    }

    public Movie addMovie(Movie movie){
        movie.setReleaseDate(LocalDate.now());
        movie.setMovieCode(UUID.randomUUID().toString());
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Movie movie){
        return movieRepository.save(movie);
    }

    public void deleteMovie(String id){
        movieRepository.deleteById(id);
    }

}
