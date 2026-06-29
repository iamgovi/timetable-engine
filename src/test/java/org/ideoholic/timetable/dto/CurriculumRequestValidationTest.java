package org.ideoholic.timetable.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CurriculumRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void curriculumRejectsNullRequiredFields() {
        CurriculumRequest request = new CurriculumRequest();

        Set<ConstraintViolation<CurriculumRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void curriculumSubjectRejectsNegativeWeeklyPeriods() {
        CurriculumSubjectRequest request = new CurriculumSubjectRequest();
        request.setSubjectId(1L);
        request.setWeeklyPeriods(-1);

        Set<ConstraintViolation<CurriculumSubjectRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void curriculumSubjectRejectsNullRequiredFields() {
        CurriculumSubjectRequest request = new CurriculumSubjectRequest();

        Set<ConstraintViolation<CurriculumSubjectRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void subjectCategoryRejectsBlankName() {
        SubjectCategoryRequest request = new SubjectCategoryRequest();
        request.setCategoryName("");

        Set<ConstraintViolation<SubjectCategoryRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}
