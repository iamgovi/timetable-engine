package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.dto.SubjectCategoryRequest;
import org.ideoholic.timetable.entity.SubjectCategory;
import org.ideoholic.timetable.repository.SubjectCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectCategoryService {

    private final SubjectCategoryRepository repository;

    public SubjectCategory create(
            SubjectCategoryRequest request) {

        repository.findByCategoryNameIgnoreCase(request.getCategoryName())
                .ifPresent(category -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Subject category already exists");
                });

        SubjectCategory category = new SubjectCategory();
        apply(category, request);
        return repository.save(category);
    }

    public SubjectCategory update(
            Long id,
            SubjectCategoryRequest request) {

        SubjectCategory category = findById(id);
        repository.findByCategoryNameIgnoreCase(request.getCategoryName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Subject category already exists");
                });

        apply(category, request);
        return repository.save(category);
    }

    public List<SubjectCategory> findAll() {
        return repository.findAll();
    }

    public SubjectCategory findById(
            Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Subject category not found"));
    }

    public void delete(
            Long id) {

        repository.delete(findById(id));
    }

    private void apply(
            SubjectCategory category,
            SubjectCategoryRequest request) {

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
    }
}
