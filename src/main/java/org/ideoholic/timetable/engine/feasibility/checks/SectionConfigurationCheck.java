package org.ideoholic.timetable.engine.feasibility.checks;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.feasibility.FeasibilityCheckResult;
import org.ideoholic.timetable.engine.feasibility.FeasibilityContext;
import org.ideoholic.timetable.engine.feasibility.FeasibilityValidator;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Section;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class SectionConfigurationCheck implements FeasibilityValidator {

    private static final String CHECK_NAME = "Section Configuration";

    @Override
    public String name() {
        return CHECK_NAME;
    }

    @Override
    public FeasibilityCheckResult validate(
            FeasibilityContext context) {

        FeasibilityCheckResult result = new FeasibilityCheckResult(CHECK_NAME);

        if (context.getClasses().isEmpty()) {
            result.error(
                    "No classes were resolved for the feasibility request.",
                    "Pass valid classIds or sectionIds for the selected academic year.");
            return result;
        }

        if (context.getSections().isEmpty()) {
            result.error(
                    "No sections are configured for the requested classes.",
                    "Create sections for each selected class in the selected academic year.");
            return result;
        }

        Map<Long, Long> sectionCountByClassId = context.getSections()
                .stream()
                .filter(section -> section.getClassMaster() != null)
                .collect(Collectors.groupingBy(
                        section -> section.getClassMaster().getId(),
                        Collectors.counting()));

        for (ClassMaster classMaster : context.getClasses()) {
            if (classMaster.getId() == null
                    || sectionCountByClassId.getOrDefault(classMaster.getId(), 0L) == 0) {
                result.error(
                        "Class " + classLabel(classMaster) + " has no configured sections.",
                        "Create at least one section for every selected class.");
            }
        }

        if (context.getRequest().getSectionIds() != null
                && !context.getRequest().getSectionIds().isEmpty()) {
            Set<Long> resolvedSectionIds = context.getSections()
                    .stream()
                    .map(Section::getId)
                    .collect(Collectors.toSet());

            for (Long sectionId : context.getRequest().getSectionIds()) {
                if (!resolvedSectionIds.contains(sectionId)) {
                    result.error(
                            "Requested section " + sectionId + " does not exist.",
                            "Pass only existing sectionIds.");
                }
            }
        }

        return result;
    }

    private String classLabel(
            ClassMaster classMaster) {

        if (classMaster == null) {
            return "unknown";
        }
        if (classMaster.getClassName() != null && !classMaster.getClassName().isBlank()) {
            return classMaster.getClassName();
        }
        return String.valueOf(classMaster.getId());
    }
}
