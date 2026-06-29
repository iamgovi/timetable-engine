package org.ideoholic.timetable.engine.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.service.TeacherSubjectMappingService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TeacherSubjectCatalog {

    private final TeacherSubjectMappingService teacherSubjectMappingService;

    public Catalog build(
            List<Teacher> teachers) {

        Map<Long, Subject> teacherSubjectMap = new HashMap<>();
        for (Teacher teacher : teachers) {
            teacherSubjectMap.put(teacher.getId(),
                    teacherSubjectMappingService.findSubjectForTeacher(
                            teacher.getId()));
        }

        Map<Long, Subject> subjectById = new HashMap<>();
        Map<Long, List<Teacher>> teachersBySubjectId = new HashMap<>();

        for (Teacher teacher : teachers) {
            Subject subject = teacherSubjectMap.get(teacher.getId());
            if (subject == null || subject.getId() == null) {
                continue;
            }

            subjectById.putIfAbsent(subject.getId(), subject);
            teachersBySubjectId.computeIfAbsent(subject.getId(), k -> new ArrayList<>())
                    .add(teacher);
        }

        return new Catalog(
                new ArrayList<>(subjectById.values()),
                teachersBySubjectId);
    }

    public static class Catalog {

        private final List<Subject> subjects;

        private final Map<Long, List<Teacher>> teachersBySubjectId;

        Catalog(
                List<Subject> subjects,
                Map<Long, List<Teacher>> teachersBySubjectId) {

            this.subjects = subjects;
            this.teachersBySubjectId = teachersBySubjectId;
        }

        public List<Subject> getSubjects() {
            return subjects;
        }

        public Map<Long, List<Teacher>> getTeachersBySubjectId() {
            return teachersBySubjectId;
        }
    }
}
