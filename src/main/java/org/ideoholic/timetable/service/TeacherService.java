package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository repository;

    public Teacher save(
            Teacher teacher) {

        return repository.save(teacher);
    }

    public List<Teacher> findAll() {

        return repository.findAll();
    }
}