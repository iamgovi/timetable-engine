-- Baseline academic and curriculum dataset.
-- Spring Boot loads this after Hibernate creates/updates the tables.

INSERT INTO academic_year (year_name, is_current)
VALUES ('2026-27', true)
ON DUPLICATE KEY UPDATE
is_current = VALUES(is_current);

INSERT INTO class_master (class_name)
SELECT 'Class 6'
WHERE NOT EXISTS (SELECT 1 FROM class_master WHERE class_name = 'Class 6');

INSERT INTO class_master (class_name)
SELECT 'Class 7'
WHERE NOT EXISTS (SELECT 1 FROM class_master WHERE class_name = 'Class 7');

INSERT INTO class_master (class_name)
SELECT 'Class 8'
WHERE NOT EXISTS (SELECT 1 FROM class_master WHERE class_name = 'Class 8');

INSERT INTO class_master (class_name)
SELECT 'Class 9'
WHERE NOT EXISTS (SELECT 1 FROM class_master WHERE class_name = 'Class 9');

INSERT INTO class_master (class_name)
SELECT 'Class 10'
WHERE NOT EXISTS (SELECT 1 FROM class_master WHERE class_name = 'Class 10');

INSERT INTO subject_category (category_name, description, is_active) VALUES
('Theory', 'Classroom theory subject', true),
('Lab', 'Laboratory-based subject', true),
('Practical', 'Practical or skills-based subject', true),
('Activity', 'Activity-based subject', true),
('Language', 'Language subject', true),
('Elective', 'Student choice subject', true),
('Core', 'Mandatory core subject', true)
ON DUPLICATE KEY UPDATE
description = VALUES(description),
is_active = VALUES(is_active);

INSERT INTO subject (subject_name, subject_code, is_lab, weekly_periods, daily_periods, is_active) VALUES
('Mathematics', 'MATH', false, 6, 2, true),
('English', 'ENG', false, 5, 2, true),
('Hindi', 'HIN', false, 4, 2, true),
('Second Language', 'LANG2', false, 3, 1, true),
('Science', 'SCI', false, 5, 2, true),
('Social Science', 'SST', false, 4, 2, true),
('Computer Science', 'CS', false, 2, 1, true),
('Physical Education', 'PE', false, 2, 1, true),
('Art Education', 'ART', false, 1, 1, true),
('Library', 'LIB', false, 1, 1, true),
('Physics', 'PHY', false, 5, 2, true),
('Chemistry', 'CHEM', false, 5, 2, true),
('Biology', 'BIO', false, 5, 2, true),
('Physics Lab', 'PHY-LAB', true, 1, 1, true),
('Chemistry Lab', 'CHEM-LAB', true, 1, 1, true),
('Biology Lab', 'BIO-LAB', true, 1, 1, true),
('Work Education', 'WORK-EDU', false, 1, 1, true)
ON DUPLICATE KEY UPDATE
subject_name = VALUES(subject_name),
is_lab = VALUES(is_lab),
is_active = VALUES(is_active);

INSERT INTO curriculum (curriculum_name, class_id, academic_year_id, description, is_active)
SELECT CONCAT(cm.class_name, ' Curriculum'), cm.id, ay.id, CONCAT(cm.class_name, ' baseline weekly curriculum'), true
FROM class_master cm
JOIN academic_year ay ON ay.year_name = '2026-27'
WHERE cm.class_name IN ('Class 6', 'Class 7', 'Class 8', 'Class 9', 'Class 10')
ON DUPLICATE KEY UPDATE
curriculum_name = VALUES(curriculum_name),
description = VALUES(description),
is_active = VALUES(is_active);

INSERT INTO curriculum_subject
(curriculum_id, subject_id, category_id, weekly_periods, daily_period_limit, requirement_type, stream_name, elective_group, is_optional, display_order, is_active)
SELECT c.id, s.id, sc.id, req.weekly_periods, req.daily_limit, req.requirement_type, req.stream_name, req.elective_group, req.is_optional, req.display_order, true
FROM (
    SELECT 'Class 6' class_name, 'Mathematics' subject_name, 'Core' category_name, 6 weekly_periods, 2 daily_limit, 'CORE' requirement_type, null stream_name, null elective_group, false is_optional, 10 display_order
    UNION ALL SELECT 'Class 6', 'English', 'Language', 5, 2, 'CORE', null, null, false, 20
    UNION ALL SELECT 'Class 6', 'Hindi', 'Language', 4, 2, 'CORE', null, null, false, 30
    UNION ALL SELECT 'Class 6', 'Science', 'Theory', 5, 2, 'CORE', null, null, false, 40
    UNION ALL SELECT 'Class 6', 'Social Science', 'Theory', 4, 2, 'CORE', null, null, false, 50
    UNION ALL SELECT 'Class 6', 'Computer Science', 'Practical', 2, 1, 'CORE', null, null, false, 60
    UNION ALL SELECT 'Class 6', 'Physical Education', 'Activity', 2, 1, 'CORE', null, null, false, 70
    UNION ALL SELECT 'Class 6', 'Art Education', 'Activity', 1, 1, 'CORE', null, null, false, 80
    UNION ALL SELECT 'Class 6', 'Library', 'Activity', 1, 1, 'CORE', null, null, false, 90

    UNION ALL SELECT 'Class 7', 'Mathematics', 'Core', 6, 2, 'CORE', null, null, false, 10
    UNION ALL SELECT 'Class 7', 'English', 'Language', 5, 2, 'CORE', null, null, false, 20
    UNION ALL SELECT 'Class 7', 'Hindi', 'Language', 4, 2, 'CORE', null, null, false, 30
    UNION ALL SELECT 'Class 7', 'Science', 'Theory', 5, 2, 'CORE', null, null, false, 40
    UNION ALL SELECT 'Class 7', 'Social Science', 'Theory', 4, 2, 'CORE', null, null, false, 50
    UNION ALL SELECT 'Class 7', 'Computer Science', 'Practical', 2, 1, 'CORE', null, null, false, 60
    UNION ALL SELECT 'Class 7', 'Physical Education', 'Activity', 2, 1, 'CORE', null, null, false, 70
    UNION ALL SELECT 'Class 7', 'Art Education', 'Activity', 1, 1, 'CORE', null, null, false, 80
    UNION ALL SELECT 'Class 7', 'Library', 'Activity', 1, 1, 'CORE', null, null, false, 90

    UNION ALL SELECT 'Class 8', 'Mathematics', 'Core', 6, 2, 'CORE', null, null, false, 10
    UNION ALL SELECT 'Class 8', 'English', 'Language', 5, 2, 'CORE', null, null, false, 20
    UNION ALL SELECT 'Class 8', 'Hindi', 'Language', 4, 2, 'CORE', null, null, false, 30
    UNION ALL SELECT 'Class 8', 'Science', 'Theory', 5, 2, 'CORE', null, null, false, 40
    UNION ALL SELECT 'Class 8', 'Social Science', 'Theory', 4, 2, 'CORE', null, null, false, 50
    UNION ALL SELECT 'Class 8', 'Computer Science', 'Practical', 2, 1, 'CORE', null, null, false, 60
    UNION ALL SELECT 'Class 8', 'Physical Education', 'Activity', 2, 1, 'CORE', null, null, false, 70
    UNION ALL SELECT 'Class 8', 'Art Education', 'Activity', 1, 1, 'CORE', null, null, false, 80
    UNION ALL SELECT 'Class 8', 'Work Education', 'Activity', 1, 1, 'CORE', null, null, false, 90

    UNION ALL SELECT 'Class 9', 'Mathematics', 'Core', 7, 2, 'CORE', null, null, false, 10
    UNION ALL SELECT 'Class 9', 'English', 'Language', 5, 2, 'CORE', null, null, false, 20
    UNION ALL SELECT 'Class 9', 'Second Language', 'Language', 3, 1, 'CORE', null, null, false, 30
    UNION ALL SELECT 'Class 9', 'Physics', 'Theory', 3, 1, 'CORE', 'Science', null, false, 40
    UNION ALL SELECT 'Class 9', 'Chemistry', 'Theory', 3, 1, 'CORE', 'Science', null, false, 50
    UNION ALL SELECT 'Class 9', 'Biology', 'Theory', 3, 1, 'CORE', 'Science', null, false, 60
    UNION ALL SELECT 'Class 9', 'Social Science', 'Theory', 4, 2, 'CORE', null, null, false, 70
    UNION ALL SELECT 'Class 9', 'Computer Science', 'Elective', 2, 1, 'ELECTIVE', null, 'TECH', true, 80
    UNION ALL SELECT 'Class 9', 'Physical Education', 'Activity', 2, 1, 'CORE', null, null, false, 90
    UNION ALL SELECT 'Class 9', 'Physics Lab', 'Lab', 1, 1, 'CORE', 'Science', null, false, 100
    UNION ALL SELECT 'Class 9', 'Chemistry Lab', 'Lab', 1, 1, 'CORE', 'Science', null, false, 110
    UNION ALL SELECT 'Class 9', 'Biology Lab', 'Lab', 1, 1, 'CORE', 'Science', null, false, 120

    UNION ALL SELECT 'Class 10', 'Mathematics', 'Core', 7, 2, 'CORE', null, null, false, 10
    UNION ALL SELECT 'Class 10', 'English', 'Language', 5, 2, 'CORE', null, null, false, 20
    UNION ALL SELECT 'Class 10', 'Second Language', 'Language', 3, 1, 'CORE', null, null, false, 30
    UNION ALL SELECT 'Class 10', 'Physics', 'Theory', 4, 2, 'CORE', 'Science', null, false, 40
    UNION ALL SELECT 'Class 10', 'Chemistry', 'Theory', 4, 2, 'CORE', 'Science', null, false, 50
    UNION ALL SELECT 'Class 10', 'Biology', 'Theory', 4, 2, 'CORE', 'Science', null, false, 60
    UNION ALL SELECT 'Class 10', 'Social Science', 'Theory', 4, 2, 'CORE', null, null, false, 70
    UNION ALL SELECT 'Class 10', 'Computer Science', 'Elective', 0, 1, 'ELECTIVE', null, 'TECH', true, 80
    UNION ALL SELECT 'Class 10', 'Physical Education', 'Activity', 2, 1, 'CORE', null, null, false, 90
    UNION ALL SELECT 'Class 10', 'Physics Lab', 'Lab', 1, 1, 'CORE', 'Science', null, false, 100
    UNION ALL SELECT 'Class 10', 'Chemistry Lab', 'Lab', 1, 1, 'CORE', 'Science', null, false, 110
    UNION ALL SELECT 'Class 10', 'Biology Lab', 'Lab', 1, 1, 'CORE', 'Science', null, false, 120
) req
JOIN class_master cm ON cm.class_name = req.class_name
JOIN academic_year ay ON ay.year_name = '2026-27'
JOIN curriculum c ON c.class_id = cm.id AND c.academic_year_id = ay.id
JOIN subject s ON s.subject_name = req.subject_name
LEFT JOIN subject_category sc ON sc.category_name = req.category_name
ON DUPLICATE KEY UPDATE
category_id = VALUES(category_id),
weekly_periods = VALUES(weekly_periods),
daily_period_limit = VALUES(daily_period_limit),
requirement_type = VALUES(requirement_type),
stream_name = VALUES(stream_name),
elective_group = VALUES(elective_group),
is_optional = VALUES(is_optional),
display_order = VALUES(display_order),
is_active = VALUES(is_active);
