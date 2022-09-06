package com.imizsoft.backendsecurity.service;

import com.imizsoft.backendsecurity.model.Category;
import com.imizsoft.backendsecurity.reprository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public Category getCategoryById(String id){
        return categoryRepository.findCategoryById(id).orElseThrow(()-> new RuntimeException("Category with id: "+id+" not found"));
    }

    public Category addCategory(Category category){
        return categoryRepository.save(category);
    }

}
