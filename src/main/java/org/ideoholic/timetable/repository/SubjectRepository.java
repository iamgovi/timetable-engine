package org.ideoholic.timetable.repository;

import org.ideoholic.timetable.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository
        extends JpaRepository<Subject, Long> {

}