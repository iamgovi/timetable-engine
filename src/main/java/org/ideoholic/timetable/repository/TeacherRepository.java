package org.ideoholic.timetable.repository;

import org.ideoholic.timetable.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository
        extends JpaRepository<Teacher, Long> {

}