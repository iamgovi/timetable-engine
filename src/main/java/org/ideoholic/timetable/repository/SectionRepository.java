package org.ideoholic.timetable.repository;

import org.ideoholic.timetable.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository
        extends JpaRepository<Section, Long> {
}
