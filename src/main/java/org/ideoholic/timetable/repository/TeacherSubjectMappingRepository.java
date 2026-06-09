package org.ideoholic.timetable.repository;

import org.ideoholic.timetable.entity.TeacherSubjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherSubjectMappingRepository
        extends JpaRepository<TeacherSubjectMapping, Long> {
}
