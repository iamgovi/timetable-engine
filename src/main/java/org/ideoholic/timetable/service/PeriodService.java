package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.repository.PeriodRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PeriodService {

    private final PeriodRepository repository;

    public Period save(
            Period period) {

        return repository.save(period);
    }

    public List<Period> findAll() {

        return repository.findAll();
    }
}