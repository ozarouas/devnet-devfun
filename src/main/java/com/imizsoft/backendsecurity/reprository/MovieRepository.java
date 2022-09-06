package com.imizsoft.backendsecurity.reprository;

import com.imizsoft.backendsecurity.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    Optional<Movie> findMovieById(String id);

}
