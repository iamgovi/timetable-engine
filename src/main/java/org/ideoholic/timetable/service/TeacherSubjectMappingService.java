package org.ideoholic.timetable.service;

import java.util.List;
import java.util.Optional;

import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.TeacherSubjectMapping;
import org.ideoholic.timetable.repository.TeacherSubjectMappingRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherSubjectMappingService {

    private final TeacherSubjectMappingRepository repository;

    public TeacherSubjectMapping save(
            TeacherSubjectMapping mapping) {

        return repository.save(mapping);
    }

    public List<TeacherSubjectMapping> findAll() {

        return repository.findAll();
    }

    public Subject findSubjectForTeacher(Long teacherId) {
        Optional<TeacherSubjectMapping> mapping = repository.findByTeacherId(teacherId);

        if (mapping.isPresent()) {
            return mapping.get().getSubject();
        }

        return null;
    }

    public java.util.List<TeacherSubjectMapping> findBySubjectId(Long subjectId) {
        return repository.findBySubjectId(subjectId);
    }

    public boolean isTeacherMappedToSubject(Long teacherId, Long subjectId) {
        if (teacherId == null || subjectId == null) {
            return false;
        }

        Optional<TeacherSubjectMapping> mapping = repository.findByTeacherId(teacherId);
        return mapping.isPresent() && mapping.get().getSubject() != null
                && mapping.get().getSubject().getId().equals(subjectId);
    }
}