package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.entity.WorkingDay;
import org.ideoholic.timetable.repository.WorkingDayRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkingDayService {

    private final WorkingDayRepository repository;

    public WorkingDay save(
            WorkingDay workingDay) {

        return repository.save(workingDay);
    }

    public List<WorkingDay> findAll() {

        return repository.findAll();
    }
}