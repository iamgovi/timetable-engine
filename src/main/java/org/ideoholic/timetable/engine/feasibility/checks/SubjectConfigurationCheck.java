package org.ideoholic.timetable.engine.feasibility.checks;

import org.ideoholic.timetable.engine.feasibility.FeasibilityCheckResult;
import org.ideoholic.timetable.engine.feasibility.FeasibilityContext;
import org.ideoholic.timetable.engine.feasibility.FeasibilityValidator;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Subject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(40)
public class SubjectConfigurationCheck implements FeasibilityValidator {

    private static final String CHECK_NAME = "Subject Configuration";

    @Override
    public String name() {
        return CHECK_NAME;
    }

    @Override
    public FeasibilityCheckResult validate(
            FeasibilityContext context) {

        FeasibilityCheckResult result = new FeasibilityCheckResult(CHECK_NAME);

        for (Curriculum curriculum : context.getCurricula()) {
            for (CurriculumSubject curriculumSubject : context.getCurriculumSubjectsByCurriculumId()
                    .getOrDefault(curriculum.getId(), java.util.Collections.emptyList())) {
                if (!isDemandSubject(curriculumSubject)) {
                    continue;
                }

                Subject subject = curriculumSubject.getSubject();
                if (subject == null) {
                    result.error(
                            "A curriculum subject has no linked subject.",
                            "Link every curriculum subject to an existing active subject.");
                } else if (Boolean.FALSE.equals(subject.getActive())) {
                    result.error(
                            "Subject " + subject.getSubjectName() + " is inactive but required by curriculum.",
                            "Activate the subject or remove it from active curriculum demand.");
                }
            }
        }

        return result;
    }

    private boolean isDemandSubject(
            CurriculumSubject curriculumSubject) {

        return curriculumSubject != null
                && !Boolean.FALSE.equals(curriculumSubject.getActive())
                && curriculumSubject.getWeeklyPeriods() != null
                && curriculumSubject.getWeeklyPeriods() > 0;
    }
}
