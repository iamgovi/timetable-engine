package org.ideoholic.timetable.repository;

import java.util.List;
import java.util.Optional;

import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurriculumSubjectRepository
        extends JpaRepository<CurriculumSubject, Long> {

    List<CurriculumSubject> findByCurriculumOrderByDisplayOrderAscIdAsc(
            Curriculum curriculum);

    Optional<CurriculumSubject> findByCurriculumAndSubject(
            Curriculum curriculum,
            Subject subject);

    boolean existsByCurriculum(
            Curriculum curriculum);
}
