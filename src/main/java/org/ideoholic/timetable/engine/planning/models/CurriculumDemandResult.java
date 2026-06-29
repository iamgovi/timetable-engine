package org.ideoholic.timetable.engine.planning.models;

import java.util.ArrayList;
import java.util.List;

public class CurriculumDemandResult {

    private List<ClassDemand> classDemands = new ArrayList<>();

    private List<SubjectDemand> subjectDemands = new ArrayList<>();

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
}
