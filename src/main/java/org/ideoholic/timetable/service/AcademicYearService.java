package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.repository.AcademicYearRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepository repository;

    public AcademicYear save(
            AcademicYear year) {

        return repository.save(year);
    }

    public List<AcademicYear> findAll() {

        return repository.findAll();
    }
}