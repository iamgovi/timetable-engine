// package org.ideoholic.timetable.engine.rules;

// import org.ideoholic.timetable.engine.context.TimetableContext;
// import org.ideoholic.timetable.entity.Subject;
// import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
// import org.springframework.core.annotation.Order;
// import org.springframework.stereotype.Component;

// import lombok.RequiredArgsConstructor;

// @Component
// @Order(5)
// @RequiredArgsConstructor
// public class WeeklySubjectPeriodsRule
//         implements TimetableRule {

//     private final TimetableAssignmentRepository repository;

//     @Override
//     public boolean validate(
//             TimetableContext context) {

//         if (context.getSection() == null
//                 || context.getSubject() == null) {
//             return false;
//         }

//         Subject subject = context.getSubject();
//         Integer weeklyPeriods = subject.getWeeklyPeriods();

//         if (weeklyPeriods == null) {
//             return true;
//         }

//         long existingCount = repository.countBySectionAndSubject(
//                 context.getSection(),
//                 subject);

//         return existingCount < weeklyPeriods;
//     }

//     @Override
//     public String getRuleName() {
//         return "Weekly Subject Periods Rule";
//     }
// }
