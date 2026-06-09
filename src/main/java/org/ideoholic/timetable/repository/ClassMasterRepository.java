package org.ideoholic.timetable.repository;

import org.ideoholic.timetable.entity.ClassMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassMasterRepository
        extends JpaRepository<ClassMaster, Long> {
}