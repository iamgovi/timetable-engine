package org.ideoholic.timetable.repository;

import java.util.List;
import java.util.Optional;

import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurriculumRepository
        extends JpaRepository<Curriculum, Long> {

    Optional<Curriculum> findByClassMasterAndAcademicYear(
            ClassMaster classMaster,
            AcademicYear academicYear);

    List<Curriculum> findByClassMasterId(
            Long classId);

    List<Curriculum> findByAcademicYearId(
            Long academicYearId);
}
