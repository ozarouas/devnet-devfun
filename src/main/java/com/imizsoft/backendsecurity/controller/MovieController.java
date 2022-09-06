package com.imizsoft.backendsecurity.controller;

import com.imizsoft.backendsecurity.model.Movie;
import com.imizsoft.backendsecurity.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies(){
        List<Movie> movies = movieService.getAllMovies();
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable("id") String id){
        Movie movie = movieService.getMovieById(id);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie){
        Movie newMovie = movieService.addMovie(movie);
        return new ResponseEntity<>(newMovie, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Movie> updateMovie(@RequestBody Movie movie){
        Movie updatedMovie = movieService.updateMovie(movie);
        return new ResponseEntity<>(updatedMovie, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable("id") String id){
        movieService.deleteMovie(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
