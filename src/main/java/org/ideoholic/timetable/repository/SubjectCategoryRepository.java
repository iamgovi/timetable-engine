package org.ideoholic.timetable.repository;

import java.util.Optional;

import org.ideoholic.timetable.entity.SubjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectCategoryRepository
        extends JpaRepository<SubjectCategory, Long> {

    Optional<SubjectCategory> findByCategoryNameIgnoreCase(
            String categoryName);
}
