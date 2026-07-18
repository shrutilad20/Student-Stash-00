package com.studentstash.service;

import com.studentstash.dto.CategoryResponse;
import com.studentstash.entity.Category;
import com.studentstash.exception.BadRequestException;
import com.studentstash.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(String name, String description) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Category already exists: " + name);
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);

        Category saved = categoryRepository.save(category);
        return new CategoryResponse(saved.getId(), saved.getName(), saved.getDescription());
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getDescription()))
                .collect(Collectors.toList());
    }

}