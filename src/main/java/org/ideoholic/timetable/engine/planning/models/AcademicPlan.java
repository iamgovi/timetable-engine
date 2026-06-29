package org.ideoholic.timetable.engine.planning.models;

import java.util.ArrayList;
import java.util.List;

public class AcademicPlan {

    private List<ClassDemand> classDemands = new ArrayList<>();

    private List<SubjectDemand> subjectDemands = new ArrayList<>();

    private List<TeacherRequirement> teacherRequirements = new ArrayList<>();

    private List<TeacherUtilization> teacherUtilizations = new ArrayList<>();

    private PlanningSummary summary;

    public List<ClassDemand> getClassDemands() {
        return classDemands;
    }

    public void setClassDemands(List<ClassDemand> classDemands) {
        this.classDemands = classDemands;
    }

    public List<SubjectDemand> getSubjectDemands() {
        return subjectDemands;
    }

    public void setSubjectDemands(List<SubjectDemand> subjectDemands) {
        this.subjectDemands = subjectDemands;
    }

    public List<TeacherRequirement> getTeacherRequirements() {
        return teacherRequirements;
    }

    public void setTeacherRequirements(List<TeacherRequirement> teacherRequirements) {
        this.teacherRequirements = teacherRequirements;
    }

    public List<TeacherUtilization> getTeacherUtilizations() {
        return teacherUtilizations;
    }

    public void setTeacherUtilizations(List<TeacherUtilization> teacherUtilizations) {
        this.teacherUtilizations = teacherUtilizations;
    }

    public PlanningSummary getSummary() {
        return summary;
    }

    public void setSummary(PlanningSummary summary) {
        this.summary = summary;
    }
}
