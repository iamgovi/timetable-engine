package org.ideoholic.timetable.service;

import java.util.List;
import java.util.stream.Collectors;

import org.ideoholic.timetable.entity.TimetableAssignment;
import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimetableAssignmentService {

    private final TimetableAssignmentRepository repository;

    public List<TimetableAssignment> findAll() {
        return repository.findAll();
    }

    public List<TimetableAssignment> findByTeacherId(Long teacherId) {
        return repository.findAll()
                .stream()
                .filter(a -> a.getTeacher() != null && a.getTeacher().getId().equals(teacherId))
                .collect(Collectors.toList());
    }

    public List<TimetableAssignment> findBySectionId(Long sectionId) {
        return repository.findAll()
                .stream()
                .filter(a -> a.getSection() != null && a.getSection().getId().equals(sectionId))
                .collect(Collectors.toList());
    }
}
