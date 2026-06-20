# Quick Start: Phase 1 Single Section Timetable Generation

## 🚀 Start Here

This is your 5-minute quick start guide to getting Phase 1 working.

## Prerequisites

- ✓ Backend running on port 8080
- ✓ Database with sample data
- ✓ At least 1 Section (e.g., ID 4)
- ✓ At least 1 WorkingDay (e.g., ID 1)
- ✓ Multiple Teachers with subject mappings
- ✓ Multiple Periods (e.g., 7 periods)

## Step 1: Verify Database

Open your database client and run:

```sql
-- Check sections
SELECT id, name FROM section LIMIT 5;

-- Check working days
SELECT id, day_name FROM working_day;

-- Check periods
SELECT id, period_number FROM period ORDER BY period_number;

-- Check teachers
SELECT id, name FROM teacher LIMIT 5;

-- Verify teacher-subject mappings
SELECT COUNT(*) FROM teacher_subject_mapping;
```

**Expected Results**:
- At least 1 section exists (note its ID)
- At least 1 working day exists (note its ID)
- 6-7 periods exist
- At least 5-10 teachers exist
- Teacher-subject mappings exist

## Step 2: Test API

### Option A: Using curl

```bash
# Replace 4 and 1 with your actual IDs
curl -X POST http://localhost:8080/api/timetable/generate/single-section \
  -H "Content-Type: application/json" \
  -d '{"sectionId": 4, "workingDayId": 1}'
```

**Expected Response**: Array of assignments (should not be empty)

### Option B: Using Postman

1. Create new POST request
2. URL: `http://localhost:8080/api/timetable/generate/single-section`
3. Body (JSON):
   ```json
   {
     "sectionId": 4,
     "workingDayId": 1
   }
   ```
4. Click Send

**Expected Response**: Status 200 with array of assignments

### Option C: Using Frontend

1. Open http://localhost:3000 (if frontend running)
2. Click "Single Section Generator (Phase 1)" tab
3. Enter Section ID: 4
4. Enter Working Day ID: 1
5. Click "Generate Timetable"
6. View results

## Step 3: Verify Results

Your response should look like:

```json
[
  {
    "id": 1,
    "teacher": {
      "id": 5,
      "name": "Teacher Name",
      "email": "teacher@school.edu"
    },
    "section": {
      "id": 4,
      "name": "8-A"
    },
    "subject": {
      "id": 2,
      "name": "Mathematics"
    },
    "workingDay": {
      "id": 1,
      "dayName": "Monday"
    },
    "period": {
      "id": 1,
      "periodNumber": 1
    }
  }
  // ... more assignments
]
```

**Check**:
- ✓ Array is not empty
- ✓ All periods are represented (or mostly)
- ✓ Different teachers for different periods
- ✓ Different subjects for different periods

## Step 4: Validate Quality

### Print Timetable

```bash
# Get section ID and working day ID first, then:
curl -X POST http://localhost:8080/api/timetable/generate/single-section \
  -H "Content-Type: application/json" \
  -d '{"sectionId": 4, "workingDayId": 1}' | \
  jq '.[] | "P\(.period.periodNumber): \(.subject.subjectName) - \(.teacher.name)"'
```

**Expected Output**:
```
"P1: Mathematics - Mr. John Smith"
"P2: English - Ms. Sarah Johnson"
"P3: Science - Dr. Michael Brown"
"P4: Social Studies - Ms. Emily White"
"P5: Computer Science - Mr. David Lee"
"P6: Mathematics - Mr. James Wilson"
"P7: Physical Education - Ms. Rachel Green"
```

### Check Quality

- ✓ All periods filled (or justified if skipped)
- ✓ No same teacher twice in a row (ideally)
- ✓ Good subject distribution
- ✓ Realistic schedule

## Troubleshooting

### Got Empty Response `[]`

**Most Common Causes** (in order):

1. **Invalid IDs** - Section/WorkingDay don't exist
   ```sql
   SELECT id FROM section WHERE id = 4;
   SELECT id FROM working_day WHERE id = 1;
   ```

2. **No Teachers** - No teachers in system
   ```sql
   SELECT COUNT(*) FROM teacher;
   ```

3. **No Periods** - No periods configured
   ```sql
   SELECT COUNT(*) FROM period;
   ```

4. **No Subject Mappings** - Teachers not mapped to subjects
   ```sql
   SELECT COUNT(*) FROM teacher_subject_mapping;
   ```

**Solution**: Check logs and database, fix missing data

### Got Error Response

**Check Backend Logs**:
```bash
# Look for error messages
tail -100 logs/timetable.log | grep -i error

# Look for rule failures
tail -100 logs/timetable.log | grep "Rule Failed"
```

**Common Errors**:
- `NullPointerException` → Invalid IDs
- `Rule Failed` → Constraint violation
- `No teachers available` → No eligible teachers

### Got Weird Results

**Check Consistency**:
```sql
-- Verify all assignments created
SELECT COUNT(*) FROM timetable_assignment 
WHERE section_id = 4 AND working_day_id = 1;

-- Clear old assignments for re-testing
DELETE FROM timetable_assignment 
WHERE section_id = 4 AND working_day_id = 1;
```

## Next Steps

### Immediate
1. ✅ **Test the API** (you just did this!)
2. ✅ **Verify results quality**
3. ✅ **Check logs for warnings**

### Short Term
1. **Run multiple times** - Results should be consistent
2. **Try different sections** - Test scalability
3. **Monitor performance** - Should be < 1 second

### Next Phase
1. **Phase 2**: Multiple sections + Monday
2. **Phase 3**: Multiple sections + multiple days
3. **Phase 4**: Full weekly optimization

## Quick Commands Reference

### Compile Backend
```bash
cd /path/to/timetable_engine
mvn clean compile
```

### Run Backend
```bash
mvn spring-boot:run
```

### Run Frontend
```bash
cd frontend
npm start
```

### Test API
```bash
curl -X POST http://localhost:8080/api/timetable/generate/single-section \
  -H "Content-Type: application/json" \
  -d '{"sectionId": 4, "workingDayId": 1}'
```

### View Logs
```bash
# Real-time logs
tail -f target/timetable.log

# Search for errors
grep -i error target/timetable.log

# Search for rule failures
grep "Rule Failed" target/timetable.log
```

### Database Checks
```sql
-- Get a section ID
SELECT id, name FROM section LIMIT 1;

-- Get a working day ID
SELECT id, day_name FROM working_day LIMIT 1;

-- Count periods
SELECT COUNT(*) FROM period;

-- Count teachers
SELECT COUNT(*) FROM teacher;
```

## Common Scenarios

### Scenario 1: First Time Setup
1. Verify database has data (run SQL checks)
2. Get valid section and working day IDs
3. Call API with those IDs
4. Check response isn't empty

### Scenario 2: Testing Algorithm
1. Run generation 3-4 times
2. Results should be similar (deterministic)
3. Check teacher rotation
4. Check rule compliance

### Scenario 3: Testing Constraints
1. Temporarily disable a rule
2. Run generation
3. Observe difference
4. Re-enable rule

## Success Indicators

✅ **You're good if**:
- API returns non-empty list
- All periods are filled
- Teachers vary across periods
- Subjects vary across periods
- No error messages in logs
- Response time < 1 second

❌ **Something's wrong if**:
- API returns empty list
- Error response (HTTP 500)
- Same teacher in consecutive periods (consistently)
- Rule failure messages in logs
- Generation takes > 5 seconds

## Need Help?

1. **Check logs**: `tail -f logs/timetable.log`
2. **Run SQL verification**: See database checks above
3. **Review documentation**: See `TESTING_GUIDE.md`
4. **Inspect code**: See service implementation
5. **Test manually**: Use Postman or curl

## Done! 🎉

You now have Phase 1 working. Next step is Phase 2: multiple sections.

---

**Time to complete**: 5-10 minutes  
**Success rate**: ~95% if database is set up  
**Next**: Phase 2 - Multiple Sections + Monday
