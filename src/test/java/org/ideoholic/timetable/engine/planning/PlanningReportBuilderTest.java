package org.ideoholic.timetable.engine.planning;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.ClassDemand;
import org.ideoholic.timetable.engine.planning.models.CurriculumDemandResult;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.junit.jupiter.api.Test;

class PlanningReportBuilderTest {

    private final PlanningReportBuilder builder = new PlanningReportBuilder();

    @Test
    void marksPlanFeasibleWhenNoShortagesOrOverCapacityExist() {
        CurriculumDemandResult demandResult = demandResult();
        TeacherRequirement requirement = new TeacherRequirement();
        requirement.setStatus("OK");
        TeacherUtilization utilization = new TeacherUtilization();

        AcademicPlan plan = builder.build(
                demandResult,
                Collections.singletonList(requirement),
                Collections.singletonList(utilization));

        assertTrue(plan.getSummary().isGenerationFeasible());
    }

    @Test
    void marksPlanInfeasibleWhenCurriculumIsMissing() {
        AcademicPlan plan = builder.build(
                new CurriculumDemandResult(),
                Collections.emptyList(),
                Collections.emptyList());

        assertFalse(plan.getSummary().isGenerationFeasible());
    }

    @Test
    void marksPlanInfeasibleWhenNoSectionsExist() {
        ClassDemand classDemand = new ClassDemand();
        classDemand.setSectionCount(0);
        classDemand.setTotalWeeklyPeriods(0);

        CurriculumDemandResult demandResult = new CurriculumDemandResult();
        demandResult.setClassDemands(Collections.singletonList(classDemand));

        TeacherRequirement requirement = new TeacherRequirement();
        requirement.setStatus("OK");

        AcademicPlan plan = builder.build(
                demandResult,
                Collections.singletonList(requirement),
                Collections.emptyList());

        assertFalse(plan.getSummary().isGenerationFeasible());
    }

    private CurriculumDemandResult demandResult() {
        ClassDemand classDemand = new ClassDemand();
        classDemand.setSectionCount(2);
        classDemand.setTotalWeeklyPeriods(30);

        CurriculumDemandResult demandResult = new CurriculumDemandResult();
        demandResult.setClassDemands(Collections.singletonList(classDemand));
        return demandResult;
    }
}
