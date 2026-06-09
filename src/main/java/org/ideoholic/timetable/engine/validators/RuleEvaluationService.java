package org.ideoholic.timetable.engine.validators;

import java.util.List;

import org.ideoholic.timetable.engine.context.TimetableContext;
import org.ideoholic.timetable.engine.rules.TimetableRule;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RuleEvaluationService {

    private final List<TimetableRule> rules;

    public boolean validate(
            TimetableContext context) {

        for (TimetableRule rule : rules) {

            boolean result =
                    rule.validate(context);

            if (!result) {

                System.out.println(
                        "Rule Failed : "
                        + rule.getRuleName());

                return false;
            }
        }

        return true;
    }
}