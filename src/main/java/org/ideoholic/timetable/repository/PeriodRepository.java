package org.ideoholic.timetable.repository;

import org.ideoholic.timetable.entity.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepository
        extends JpaRepository<Period, Long> {

}