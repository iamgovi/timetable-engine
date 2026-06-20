# Testing Guide: Single Section Timetable Generation

## Quick Start

### 1. Ensure Prerequisites
- Backend is running: `http://localhost:8080`
- Database has sample data configured
- Required entities exist:
  - At least one Section (e.g., Section 4 for "8-A")
  - At least one WorkingDay (e.g., WorkingDay 1 for "Monday")
  - Multiple Teachers with subject mappings
  - Multiple Periods

### 2. Send Test Request

**Endpoint**: `POST /api/timetable/generate/single-section`

**Example Request** (using curl):
```bash
curl -X POST http://localhost:8080/api/timetable/generate/single-section \
  -H "Content-Type: application/json" \
  -d '{"sectionId": 4, "workingDayId": 1}'
```

**Example Request** (JSON file - save as `request.json`):
```json
{
  "sectionId": 4,
  "workingDayId": 1
}
```

Then use:
```bash
curl -X POST http://localhost:8080/api/timetable/generate/single-section \
  -H "Content-Type: application/json" \
  -d @request.json
```

### 3. Interpret Response

**Success Response**:
```json
[
  {
    "id": 101,
    "teacher": {
      "id": 5,
      "name": "Mr. John Smith",
      "email": "john@school.edu"
    },
    "section": {
      "id": 4,
      "name": "8-A",
      "className": "Class 8"
    },
    "subject": {
      "id": 2,
      "subjectName": "Mathematics"
    },
    "workingDay": {
      "id": 1,
      "dayName": "Monday"
    },
    "period": {
      "id": 1,
      "periodNumber": 1,
      "periodName": "P1"
    }
  },
  {
    "id": 102,
    "teacher": {
      "id": 8,
      "name": "Ms. Sarah Johnson",
      "email": "sarah@school.edu"
    },
    "section": {
      "id": 4,
      "name": "8-A"
    },
    "subject": {
      "id": 3,
      "subjectName": "English"
    },
    "workingDay": {
      "id": 1,
      "dayName": "Monday"
    },
    "period": {
      "id": 2,
      "periodNumber": 2,
      "periodName": "P2"
    }
  }
]
```

**Empty Response** `[]` means:
- Invalid section or working day
- No teachers available
- No periods configured
- Check server logs for warnings

### 4. Verify Results

Print the timetable in human-readable format:

```bash
curl -X POST http://localhost:8080/api/timetable/generate/single-section \
  -H "Content-Type: application/json" \
  -d '{"sectionId": 4, "workingDayId": 1}' | \
  jq '.[] | "P\(.period.periodNumber): \(.subject.subjectName) (\(.teacher.name))"'
```

Expected output:
```
"P1: Mathematics (Mr. John Smith)"
"P2: English (Ms. Sarah Johnson)"
"P3: Science (Dr. Michael Brown)"
"P4: Social Studies (Ms. Emily White)"
"P5: Computer Science (Mr. David Lee)"
"P6: Mathematics (Mr. John Smith)"
"P7: Physical Education (Mr. James Wilson)"
```

## Validation Checks

### ✓ Expected Behavior

1. **Each period has exactly one assignment** (or is skipped if impossible)
2. **No teacher assigned twice in same period** (across all sections)
3. **All rules are satisfied**:
   - Teacher is available
   - No teacher conflicts
   - No section conflicts
   - Teacher doesn't exceed max periods
4. **Teacher rotation**: Same teacher ideally not used consecutively
5. **Subject distribution**: Subjects properly distributed

### ✗ Problems to Watch For

| Issue | Cause | Fix |
|-------|-------|-----|
| Empty response | Invalid IDs, no teachers, no periods | Check IDs in database |
| Log warning: "Could not assign teacher" | No valid teacher for period | Add more teachers or adjust rules |
| Same teacher in consecutive periods | Not enough teachers | Add more teachers or enable flexibility |
| Same subject in consecutive periods | Limited subject variety | Improve subject diversity in teacher mappings |

## Debugging Steps

### 1. Check Server Logs
```bash
# Look for warnings about unassigned periods
tail -f logs/timetable.log | grep "Could not assign"

# Look for rule failures
tail -f logs/timetable.log | grep "Rule Failed"
```

### 2. Verify Database State
```sql
-- Check if section exists
SELECT id, name FROM section WHERE id = 4;

-- Check if working day exists
SELECT id, day_name FROM working_day WHERE id = 1;

-- Check if periods exist
SELECT id, period_number FROM period ORDER BY period_number;

-- Check teacher-subject mappings
SELECT t.id, t.name, s.subject_name 
FROM teacher t
JOIN teacher_subject_mapping tsm ON t.id = tsm.teacher_id
JOIN subject s ON tsm.subject_id = s.id;

-- Check existing assignments (should be empty before first run)
SELECT * FROM timetable_assignment 
WHERE section_id = 4 AND working_day_id = 1;
```

### 3. Check Rule Implementations
```bash
# View rule files
ls -la src/main/java/org/ideoholic/timetable/engine/rules/
```

## Performance Notes

- **Expected time**: < 1 second for standard schedule (6-7 periods, 10+ teachers)
- **Database queries**: ~20-30 queries per generation
- **Memory usage**: Negligible

## Next Test: Verify Quality

After successful generation:

1. **Check subject distribution**: Are subjects well-spread?
2. **Check teacher rotation**: Are different teachers used?
3. **Check rules compliance**: Are all assignments valid?
4. **Compare with manual schedule**: Does it look realistic?

## Cleanup Between Tests

To regenerate, clear previous assignments:

```sql
DELETE FROM timetable_assignment 
WHERE section_id = 4 AND working_day_id = 1;
```

Then run the generation again to verify idempotency and compare results.

## Integration with Frontend

### React/JavaScript Component Example

```javascript
async function generateSectionTimetable(sectionId, workingDayId) {
  try {
    const response = await fetch('/api/timetable/generate/single-section', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sectionId, workingDayId })
    });

    const assignments = await response.json();
    
    // Sort by period number
    assignments.sort((a, b) => a.period.periodNumber - b.period.periodNumber);
    
    // Display timetable
    assignments.forEach(a => {
      console.log(
        `Period ${a.period.periodNumber}: ${a.subject.subjectName} - ${a.teacher.name}`
      );
    });

    return assignments;
  } catch (error) {
    console.error('Generation failed:', error);
  }
}

// Usage
generateSectionTimetable(4, 1);
```

## Troubleshooting

**Q: Always getting empty response**
- A: Check that section and working day IDs exist in database

**Q: Getting same teacher multiple times**
- A: Normal if only few teachers available; add more teachers

**Q: Getting same subject repeatedly**
- A: No subject diversity algorithm yet; add custom rule if needed

**Q: Some periods are blank**
- A: No valid teacher for that period; check rules and constraints

**Q: Generation takes too long**
- A: Too many teachers with conflicts; optimize rule engine or add caching

## Success Criteria

- ✓ All periods assigned (or properly skipped with warning)
- ✓ No rule violations
- ✓ Teachers properly distributed
- ✓ Subjects well-distributed
- ✓ Realistic schedule for the section
- ✓ Consistent, reproducible results
