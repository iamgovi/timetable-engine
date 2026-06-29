/**
 * Generation orchestration components.
 *
 * Services prepare request-scoped data and persistence boundaries, while generator
 * implementations own assignment iteration and candidate selection. Future
 * curriculum-aware generators can plug in through {@link org.ideoholic.timetable.engine.generation.TimetableGenerator}
 * without changing controllers or allocation rules.
 */
package org.ideoholic.timetable.engine.generation;
