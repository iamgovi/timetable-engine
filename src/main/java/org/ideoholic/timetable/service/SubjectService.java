package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;

    public Subject save(
            Subject subject) {

        return repository.save(subject);
    }

    public List<Subject> findAll() {

        return repository.findAll();
    }
}