package org.ideoholic.timetable.repository;

import org.ideoholic.timetable.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicYearRepository
        extends JpaRepository<AcademicYear, Long> {

    List<AcademicYear> findByCurrentTrue();
}
