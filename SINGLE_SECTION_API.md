# Single Section Timetable Generation API

## Overview

The simplified timetable generation system focuses on generating a high-quality timetable for a **single section on a single working day**.

This is **Phase 1** of the phased timetable generation strategy:
- **Phase 1**: Single Section + Monday (CURRENT)
- **Phase 2**: Multiple Sections + Monday
- **Phase 3**: Multiple Sections + Multiple Days
- **Phase 4**: Weekly Timetable Generation

## Endpoint

### POST `/api/timetable/generate/single-section`

Generates a timetable for a single section on a single working day, validating all rules for each assignment.

### Request

```json
{
  "sectionId": 4,
  "workingDayId": 1
}
```

**Parameters:**
- `sectionId` (Long, required): The ID of the section (e.g., 8-A)
- `workingDayId` (Long, required): The ID of the working day (e.g., 1 for Monday)

### Response

```json
[
  {
    "id": 1,
    "teacher": { "id": 5, "name": "Mr. John" },
    "section": { "id": 4, "name": "8-A" },
    "subject": { "id": 2, "name": "Mathematics" },
    "workingDay": { "id": 1, "dayName": "Monday" },
    "period": { "id": 1, "periodNumber": 1, "periodName": "P1" }
  },
  {
    "id": 2,
    "teacher": { "id": 8, "name": "Ms. Sarah" },
    "section": { "id": 4, "name": "8-A" },
    "subject": { "id": 3, "name": "English" },
    "workingDay": { "id": 1, "dayName": "Monday" },
    "period": { "id": 2, "periodNumber": 2, "periodName": "P2" }
  }
]
```

Returns a list of `TimetableAssignment` objects representing the generated timetable.

## Generation Algorithm

For each period in the working day:

1. **Identify Candidates**: Get all teachers not already assigned in this period
2. **Sort Candidates**: Prioritize by:
   - **Primary**: Teachers not yet used in this section (encourage rotation)
   - **Secondary**: Teachers with lowest usage count in this section (fair distribution)
   - **Tertiary**: Teacher ID (for deterministic ordering)
3. **Validate**: For each candidate (in order), attempt allocation via `TimetableAllocationService`
4. **Apply Rules**: All configured rules are validated:
   - `TeacherAvailabilityRule`: Teacher is available in this period
   - `TeacherConflictRule`: Teacher is not already assigned in this period elsewhere
   - `SectionConflictRule`: Section doesn't have conflict in this period
   - `MaxTeacherPeriodsRule`: Teacher hasn't exceeded max periods for the day
   - Additional rules as configured
5. **Persist**: If validation passes, assignment is persisted to database
6. **Continue**: Move to next period

## Key Features

✅ **Single Section Focus**: Generates only for the specified section  
✅ **Single Day Focus**: Generates only for the specified working day  
✅ **Complete Rule Validation**: Applies all configured rules to each assignment  
✅ **Fair Teacher Distribution**: Ensures teachers are rotated within the section  
✅ **Quality Over Quantity**: Prioritizes correctness and quality over speed  
✅ **Idempotent Allocation**: Rejects conflicting assignments, ensures valid state  

## Example Usage

### Using curl:

```bash
curl -X POST http://localhost:8080/api/timetable/generate/single-section \
  -H "Content-Type: application/json" \
  -d '{
    "sectionId": 4,
    "workingDayId": 1
  }'
```

### Using JavaScript/TypeScript:

```javascript
const response = await fetch('/api/timetable/generate/single-section', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    sectionId: 4,
    workingDayId: 1
  })
});

const assignments = await response.json();
console.log(assignments);
```

### Using Python:

```python
import requests

response = requests.post(
    'http://localhost:8080/api/timetable/generate/single-section',
    json={
        'sectionId': 4,
        'workingDayId': 1
    }
)

assignments = response.json()
for assignment in assignments:
    print(f"P{assignment['period']['periodNumber']}: {assignment['subject']['name']}")
```

## Validation & Error Handling

- **Invalid sectionId**: Returns empty list
- **Invalid workingDayId**: Returns empty list
- **No periods configured**: Returns empty list
- **No teachers available**: Returns empty list
- **Period cannot be filled**: Logs warning, continues to next period

## Monitoring & Debugging

When a period cannot be assigned, the service logs:
```
Warning: Could not assign teacher for section=4, workingDay=1, period=3
```

Check logs for these warnings to identify scheduling conflicts or constraint issues.

## Next Steps

After validating Phase 1 (Single Section + Monday):

1. **Analyze Results**: 
   - Check subject distribution quality
   - Verify teacher rotation
   - Review rule violations (if any)

2. **Improve Algorithm**: 
   - Adjust sorting priorities if needed
   - Fine-tune rule thresholds
   - Add subject diversity logic

3. **Scale to Phase 2**:
   - Accept list of sectionIds
   - Generate for multiple sections simultaneously
   - Manage cross-section teacher conflicts

## Related Files

- **DTO**: `SimpleTimetableGenerationRequest.java`
- **Service**: `TimetableGenerationService.generateSingleSectionTimetable()`
- **Controller**: `TimetableController.generateSingleSection()`
- **Rule Validation**: `RuleEvaluationService.validate()`
- **Allocation**: `TimetableAllocationService.allocate()`
