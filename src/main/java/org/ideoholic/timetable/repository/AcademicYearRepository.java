package org.ideoholic.timetable.repository;

import org.ideoholic.timetable.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicYearRepository
        extends JpaRepository<AcademicYear, Long> {

}