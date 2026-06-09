package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.entity.TeacherAvailability;
import org.ideoholic.timetable.repository.TeacherAvailabilityRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherAvailabilityService {

    private final TeacherAvailabilityRepository repository;

    public TeacherAvailability save(
            TeacherAvailability availability) {

        return repository.save(availability);
    }

    public List<TeacherAvailability> findAll() {

        return repository.findAll();
    }
}