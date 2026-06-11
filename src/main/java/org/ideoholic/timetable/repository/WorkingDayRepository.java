package org.ideoholic.timetable.repository;

import org.ideoholic.timetable.entity.WorkingDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkingDayRepository
        extends JpaRepository<WorkingDay, Long> {

    WorkingDay findByDayNameIgnoreCase(String dayName);
}