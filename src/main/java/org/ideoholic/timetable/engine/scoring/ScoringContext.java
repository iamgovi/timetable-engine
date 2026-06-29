package org.ideoholic.timetable.engine.scoring;

import java.util.HashMap;
import java.util.Map;

import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.ideoholic.timetable.engine.strategy.models.GenerationContext;
import org.ideoholic.timetable.engine.strategy.models.SubjectPriority;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;

public class ScoringContext {

    private GenerationContext generationContext;

    private SubjectPriority subjectPriority;

    private Teacher teacher;

    private Map<Long, Integer> teacherUsageCount = new HashMap<>();

    private Map<Long, TeacherUtilization> teacherUtilizationByTeacherId = new HashMap<>();

    private Map<Long, String> categoryBySubjectId = new HashMap<>();

    public GenerationContext getGenerationContext() {
        return generationContext;
    }

    public void setGenerationContext(GenerationContext generationContext) {
        this.generationContext = generationContext;
    }

    public SubjectPriority getSubjectPriority() {
        return subjectPriority;
    }

    public void setSubjectPriority(SubjectPriority subjectPriority) {
        this.subjectPriority = subjectPriority;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Map<Long, Integer> getTeacherUsageCount() {
        return teacherUsageCount;
    }

    public void setTeacherUsageCount(Map<Long, Integer> teacherUsageCount) {
        this.teacherUsageCount = teacherUsageCount;
    }

    public Map<Long, TeacherUtilization> getTeacherUtilizationByTeacherId() {
        return teacherUtilizationByTeacherId;
    }

    public void setTeacherUtilizationByTeacherId(
            Map<Long, TeacherUtilization> teacherUtilizationByTeacherId) {
        this.teacherUtilizationByTeacherId = teacherUtilizationByTeacherId;
    }

    public Map<Long, String> getCategoryBySubjectId() {
        return categoryBySubjectId;
    }

    public void setCategoryBySubjectId(Map<Long, String> categoryBySubjectId) {
        this.categoryBySubjectId = categoryBySubjectId;
    }

    public Subject getSubject() {
        return subjectPriority == null ? null : subjectPriority.getSubject();
    }

    public String getCategoryName() {
        if (subjectPriority != null && subjectPriority.getCategoryName() != null) {
            return subjectPriority.getCategoryName();
        }

        Subject subject = getSubject();
        if (subject == null || subject.getId() == null) {
            return null;
        }

        return categoryBySubjectId.get(subject.getId());
    }
}
