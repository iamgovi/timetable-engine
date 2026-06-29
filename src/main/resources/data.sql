-- Baseline academic and curriculum dataset.
-- Spring Boot loads this after Hibernate creates/updates the tables.

INSERT INTO academic_year (year_name, is_current)
VALUES ('2026-27', true)
ON DUPLICATE KEY UPDATE
is_current = VALUES(is_current);

UPDATE academic_year
SET is_current = false
WHERE year_name <> '2026-27';

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

INSERT INTO section (section_name, class_id, academic_year_id)
SELECT req.section_name, cm.id, ay.id
FROM (
    SELECT 'Class 6' class_name, 'A' section_name
    UNION ALL SELECT 'Class 6', 'B'
    UNION ALL SELECT 'Class 7', 'A'
    UNION ALL SELECT 'Class 7', 'B'
    UNION ALL SELECT 'Class 8', 'A'
    UNION ALL SELECT 'Class 8', 'B'
    UNION ALL SELECT 'Class 8', 'C'
    UNION ALL SELECT 'Class 8', 'D'
    UNION ALL SELECT 'Class 9', 'A'
    UNION ALL SELECT 'Class 9', 'B'
    UNION ALL SELECT 'Class 10', 'A'
    UNION ALL SELECT 'Class 10', 'B'
) req
JOIN class_master cm ON cm.class_name = req.class_name
JOIN academic_year ay ON ay.year_name = '2026-27'
WHERE NOT EXISTS (
    SELECT 1
    FROM section existing
    WHERE existing.section_name = req.section_name
      AND existing.class_id = cm.id
      AND existing.academic_year_id = ay.id
);

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
weekly_periods = VALUES(weekly_periods),
daily_periods = VALUES(daily_periods),
is_active = VALUES(is_active);

INSERT INTO working_day (day_name, is_working) VALUES
('Monday', true),
('Tuesday', true),
('Wednesday', true),
('Thursday', true),
('Friday', true),
('Saturday', true)
ON DUPLICATE KEY UPDATE
is_working = VALUES(is_working);

INSERT INTO period (period_number, start_time, end_time, is_break)
SELECT req.period_number, req.start_time, req.end_time, req.is_break
FROM (
    SELECT 1 period_number, '08:30:00' start_time, '09:15:00' end_time, false is_break
    UNION ALL SELECT 2, '09:15:00', '10:00:00', false
    UNION ALL SELECT 3, '10:00:00', '10:45:00', false
    UNION ALL SELECT 4, '11:00:00', '11:45:00', false
    UNION ALL SELECT 5, '11:45:00', '12:30:00', false
    UNION ALL SELECT 6, '13:15:00', '14:00:00', false
    UNION ALL SELECT 7, '14:00:00', '14:45:00', false
) req
WHERE NOT EXISTS (
    SELECT 1
    FROM period existing
    WHERE existing.period_number = req.period_number
);

INSERT INTO teacher (teacher_name, employee_code, email, mobile, is_active) VALUES
('Anita Sharma', 'TCH001', 'anita.sharma@school.local', '9000000001', true),
('Rahul Mehta', 'TCH002', 'rahul.mehta@school.local', '9000000002', true),
('Priya Nair', 'TCH003', 'priya.nair@school.local', '9000000003', true),
('Meera Iyer', 'TCH004', 'meera.iyer@school.local', '9000000004', true),
('Vikram Singh', 'TCH005', 'vikram.singh@school.local', '9000000005', true),
('Sunita Rao', 'TCH006', 'sunita.rao@school.local', '9000000006', true),
('Amit Verma', 'TCH007', 'amit.verma@school.local', '9000000007', true),
('Nisha Kapoor', 'TCH008', 'nisha.kapoor@school.local', '9000000008', true),
('Kavita Menon', 'TCH009', 'kavita.menon@school.local', '9000000009', true),
('Suresh Nambiar', 'TCH010', 'suresh.nambiar@school.local', '9000000010', true),
('Farah Khan', 'TCH011', 'farah.khan@school.local', '9000000011', true),
('Arun Das', 'TCH012', 'arun.das@school.local', '9000000012', true),
('Leena Thomas', 'TCH013', 'leena.thomas@school.local', '9000000013', true),
('Gaurav Jain', 'TCH014', 'gaurav.jain@school.local', '9000000014', true),
('Neha Bhatia', 'TCH015', 'neha.bhatia@school.local', '9000000015', true),
('Rakesh Pillai', 'TCH016', 'rakesh.pillai@school.local', '9000000016', true),
('Pooja Kulkarni', 'TCH017', 'pooja.kulkarni@school.local', '9000000017', true),
('Manoj Joshi', 'TCH018', 'manoj.joshi@school.local', '9000000018', true),
('Deepa Krishnan', 'TCH019', 'deepa.krishnan@school.local', '9000000019', true),
('Harish Gupta', 'TCH020', 'harish.gupta@school.local', '9000000020', true),
('Ritu Sethi', 'TCH021', 'ritu.sethi@school.local', '9000000021', true),
('Ajay Balan', 'TCH022', 'ajay.balan@school.local', '9000000022', true),
('Seema Chawla', 'TCH023', 'seema.chawla@school.local', '9000000023', true),
('Naveen Roy', 'TCH024', 'naveen.roy@school.local', '9000000024', true),
('Isha Mukherjee', 'TCH025', 'isha.mukherjee@school.local', '9000000025', true)
ON DUPLICATE KEY UPDATE
teacher_name = VALUES(teacher_name),
mobile = VALUES(mobile),
is_active = VALUES(is_active);

INSERT INTO teacher_subject_mapping (teacher_id, subject_id)
SELECT t.id, s.id
FROM (
    SELECT 'TCH001' employee_code, 'Mathematics' subject_name
    UNION ALL SELECT 'TCH002', 'Mathematics'
    UNION ALL SELECT 'TCH003', 'Mathematics'
    UNION ALL SELECT 'TCH004', 'English'
    UNION ALL SELECT 'TCH005', 'English'
    UNION ALL SELECT 'TCH006', 'Hindi'
    UNION ALL SELECT 'TCH007', 'Hindi'
    UNION ALL SELECT 'TCH008', 'Science'
    UNION ALL SELECT 'TCH009', 'Science'
    UNION ALL SELECT 'TCH010', 'Social Science'
    UNION ALL SELECT 'TCH011', 'Social Science'
    UNION ALL SELECT 'TCH012', 'Second Language'
    UNION ALL SELECT 'TCH013', 'Computer Science'
    UNION ALL SELECT 'TCH014', 'Physical Education'
    UNION ALL SELECT 'TCH015', 'Art Education'
    UNION ALL SELECT 'TCH016', 'Library'
    UNION ALL SELECT 'TCH017', 'Work Education'
    UNION ALL SELECT 'TCH018', 'Physics'
    UNION ALL SELECT 'TCH019', 'Chemistry'
    UNION ALL SELECT 'TCH020', 'Biology'
    UNION ALL SELECT 'TCH021', 'Computer Science'
    UNION ALL SELECT 'TCH022', 'Physical Education'
    UNION ALL SELECT 'TCH023', 'Physics Lab'
    UNION ALL SELECT 'TCH024', 'Chemistry Lab'
    UNION ALL SELECT 'TCH025', 'Biology Lab'
) req
JOIN teacher t ON t.employee_code = req.employee_code
JOIN subject s ON s.subject_name = req.subject_name
WHERE NOT EXISTS (
    SELECT 1
    FROM teacher_subject_mapping existing
    WHERE existing.teacher_id = t.id
      AND existing.subject_id = s.id
);

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

INSERT INTO teacher_availability (teacher_id, working_day_id, period_id, is_available)
SELECT t.id, wd.id, p.id, true
FROM teacher t
CROSS JOIN working_day wd
CROSS JOIN period p
WHERE t.employee_code IN (
        'TCH001', 'TCH002', 'TCH003', 'TCH004', 'TCH005', 'TCH006',
        'TCH007', 'TCH008', 'TCH009', 'TCH010', 'TCH011', 'TCH012',
        'TCH013', 'TCH014', 'TCH015', 'TCH016', 'TCH017', 'TCH018',
        'TCH019', 'TCH020', 'TCH021', 'TCH022', 'TCH023', 'TCH024',
        'TCH025'
)
  AND wd.day_name IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday')
  AND p.period_number BETWEEN 1 AND 7
  AND NOT EXISTS (
        SELECT 1
        FROM teacher_availability existing
        WHERE existing.teacher_id = t.id
          AND existing.working_day_id = wd.id
          AND existing.period_id = p.id
  );
