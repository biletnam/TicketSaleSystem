package ru.tersoft.ticketsale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.ticketsale.entity.Attraction;
import ru.tersoft.ticketsale.entity.Category;
import ru.tersoft.ticketsale.repository.AttractionRepository;
import ru.tersoft.ticketsale.repository.CategoryRepository;
import ru.tersoft.ticketsale.service.CategoryService;
import ru.tersoft.ticketsale.utils.ResponseFactory;

import java.util.List;
import java.util.UUID;

@Service("CategoryService")
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final AttractionRepository attractionRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, AttractionRepository attractionRepository) {
        this.categoryRepository = categoryRepository;
        this.attractionRepository = attractionRepository;
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseFactory.createResponse(categoryRepository.findAll());
    }

    @Override
    public Category get(UUID id) {
        return categoryRepository.findOne(id);
    }

    @Override
    public ResponseEntity<?> add(Category category) {
        if(category != null)
            return ResponseFactory.createResponse(categoryRepository.saveAndFlush(category));
        else
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty category");
    }

    public ResponseEntity<?> delete(UUID id) {
        Category category = categoryRepository.findOne(id);
        if(category != null) {
            List<Attraction> attractions = (List<Attraction>)attractionRepository.findByCategory(category);
            for(int i = 0; i < attractions.size(); i++) {
                attractions.get(i).setCategory(null);
                attractionRepository.saveAndFlush(attractions.get(i));
            }
            categoryRepository.delete(id);
            return ResponseFactory.createResponse();
        } else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Category with such id was not found");
    }

    @Override
    public ResponseEntity<?> edit(Category category) {
        if(category != null) {
            Category existingCategory = categoryRepository.findOne(category.getId());
            if(existingCategory != null) {
                if(category.getName() != null && !category.getName().isEmpty())
                    existingCategory.setName(category.getName());
                if(category.getMinAge() != null)
                    existingCategory.setMinAge(category.getMinAge());
                if(category.getMinHeight() != null)
                    existingCategory.setMinHeight(category.getMinHeight());
                return ResponseFactory.createResponse(categoryRepository.saveAndFlush(existingCategory));
            }
            else
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Category with such id was not found");
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty category");
        }
    }
}
