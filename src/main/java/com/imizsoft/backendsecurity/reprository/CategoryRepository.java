package com.imizsoft.backendsecurity.reprository;

import com.imizsoft.backendsecurity.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findCategoryById(String id);

}
