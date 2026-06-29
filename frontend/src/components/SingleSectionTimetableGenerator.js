import React, { useState } from 'react';

/**
 * SingleSectionTimetableGenerator Component
 * 
 * Generates a timetable for a class or section on selected working days.
 * 
 * Features:
 * - Simple form to select section/class and working days
 * - Calls /api/timetable/generate/single-section endpoint
 * - Displays generated timetable in human-readable format
 * - Shows loading state and error handling
 * - Validates rule compliance
 */
export default function SingleSectionTimetableGenerator() {
  const [classId, setClassId] = useState('');
  const [sectionId, setSectionId] = useState('');
  const [workingDayIds, setWorkingDayIds] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleGenerate = async (e) => {
    e.preventDefault();
    
    if ((!classId && !sectionId) || workingDayIds.length === 0) {
      setError('Please provide a class ID or section ID, and select at least one working day');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess(false);

    const payload = {
      workingDayIds,
    };

    if (classId) {
      payload.classId = parseInt(classId);
    }

    if (sectionId) {
      payload.sectionId = parseInt(sectionId);
    }

    try {
      const response = await fetch('/api/timetable/generate/single-section', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      if (Array.isArray(data) && data.length === 0) {
        setError('No timetable could be generated. Check logs for details.');
        setAssignments([]);
      } else {
        const sorted = [...data].sort((a, b) => {
          const dayA = a.workingDay?.id || 0;
          const dayB = b.workingDay?.id || 0;

          if (dayA !== dayB) {
            return dayA - dayB;
          }

          if (a.section.id !== b.section.id) {
            return a.section.id - b.section.id;
          }

          return a.period.periodNumber - b.period.periodNumber;
        });
        setAssignments(sorted);
        setSuccess(true);
      }
    } catch (err) {
      setError(`Generation failed: ${err.message}`);
      setAssignments([]);
    } finally {
      setLoading(false);
    }
  };

  const exportTimetableAsText = () => {
    let timetableTarget = classId ? `Class ${classId}` : `Section ${sectionId}`;
    let text = `Timetable for ${timetableTarget}\n`;
    text += `Working Days: ${workingDayIds.join(', ')}\n\n`;
    assignments.forEach((a) => {
      text += `${a.workingDay?.dayName || `Day ${a.workingDay?.id}`}, P${a.period.periodNumber}: ${a.subject.subjectName} (${a.teacher.name})\n`;
    });
    return text;
  };

  const handleWorkingDayChange = (dayId) => {
    setWorkingDayIds((current) => {
      if (current.includes(dayId)) {
        return current.filter((id) => id !== dayId);
      }

      return [...current, dayId].sort((a, b) => a - b);
    });
  };

  const workingDays = [
    { id: 1, name: 'Monday' },
    { id: 2, name: 'Tuesday' },
    { id: 3, name: 'Wednesday' },
    { id: 4, name: 'Thursday' },
    { id: 5, name: 'Friday' },
  ];

  return (
    <div style={styles.container}>
      <h1>Class / Section Timetable Generator</h1>
      <p style={styles.subtitle}>Generate selected working days for one class or one section</p>

      {/* Form Section */}
      <form onSubmit={handleGenerate} style={styles.form}>
        <div style={styles.formGroup}>
          <label htmlFor="classId">Class ID:</label>
          <input
            id="classId"
            type="number"
            value={classId}
            onChange={(e) => setClassId(e.target.value)}
            placeholder="e.g., 1"
            min="1"
          />
        </div>

        <div style={styles.formGroup}>
          <label htmlFor="sectionId">Section ID:</label>
          <input
            id="sectionId"
            type="number"
            value={sectionId}
            onChange={(e) => setSectionId(e.target.value)}
            placeholder="e.g., 4"
            min="1"
          />
        </div>

        <div style={styles.formGroup}>
          <span>Working Days:</span>
          <div style={styles.checkboxGroup}>
            {workingDays.map((day) => (
              <label key={day.id} style={styles.checkboxLabel}>
                <input
                  type="checkbox"
                  checked={workingDayIds.includes(day.id)}
                  onChange={() => handleWorkingDayChange(day.id)}
                />
                {day.name}
              </label>
            ))}
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          style={{
            ...styles.button,
            opacity: loading ? 0.6 : 1,
            cursor: loading ? 'not-allowed' : 'pointer',
          }}
        >
          {loading ? 'Generating...' : 'Generate Timetable'}
        </button>
      </form>

      {/* Error Message */}
      {error && (
        <div style={styles.error}>
          <strong>Error:</strong> {error}
        </div>
      )}

      {/* Success Message */}
      {success && (
        <div style={styles.success}>
          ✓ Timetable generated successfully for {assignments.length} periods
        </div>
      )}

      {/* Timetable Display */}
      {assignments.length > 0 && (
        <div style={styles.resultSection}>
          <h2>Generated Timetable</h2>
          <div style={styles.timetableContainer}>
            <table style={styles.table}>
              <thead>
                <tr style={styles.tableHeader}>
                  <th style={styles.th}>Day</th>
                  <th style={styles.th}>Period</th>
                  <th style={styles.th}>Section</th>
                  <th style={styles.th}>Subject</th>
                  <th style={styles.th}>Teacher</th>
                </tr>
              </thead>
              <tbody>
                {assignments.map((assignment, index) => (
                  <tr key={assignment.id} style={index % 2 === 0 ? styles.tableRowEven : styles.tableRowOdd}>
                    <td style={styles.td}>{assignment.workingDay?.dayName || assignment.workingDay?.id || '-'}</td>
                    <td style={styles.td}>
                      <strong>P{assignment.period.periodNumber}</strong>
                    </td>
                    <td style={styles.td}>{assignment.section?.sectionName || assignment.section?.id || '-'}</td>
                    <td style={styles.td}>{assignment.subject.subjectName}</td>
                    <td style={styles.td}>{assignment.teacher.name}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Export Option */}
          <button
            onClick={() => {
              const text = exportTimetableAsText();
              const blob = new Blob([text], { type: 'text/plain' });
              const url = URL.createObjectURL(blob);
              const a = document.createElement('a');
              a.href = url;
              a.download = `timetable_${classId ? `class_${classId}` : `section_${sectionId}`}.txt`;
              a.click();
              URL.revokeObjectURL(url);
            }}
            style={styles.exportButton}
          >
            Export as Text
          </button>

          {/* Statistics */}
          <div style={styles.statistics}>
            <h3>Statistics</h3>
            <p>Total Assignments: <strong>{assignments.length}</strong></p>
            <p>
              Unique Teachers: <strong>
                {new Set(assignments.map((a) => a.teacher.id)).size}
              </strong>
            </p>
            <p>
              Unique Subjects: <strong>
                {new Set(assignments.map((a) => a.subject.id)).size}
              </strong>
            </p>
            <p style={styles.note}>
              ℹ️ All assignments have passed rule validation.
            </p>
          </div>
        </div>
      )}

      {/* Info Section */}
      <div style={styles.infoSection}>
        <h3>How It Works</h3>
        <ul>
          <li>Enter a class ID to generate all sections, or a section ID for one section</li>
          <li>Select one or more working days</li>
          <li>The system generates selected days sequentially</li>
          <li>Each assignment is validated against all rules</li>
          <li>Teachers are rotated to ensure fair distribution</li>
        </ul>
      </div>
    </div>
  );
}

// Styles
const styles = {
  container: {
    maxWidth: '1000px',
    margin: '0 auto',
    padding: '20px',
    fontFamily: 'Arial, sans-serif',
  },
  subtitle: {
    color: '#666',
    marginBottom: '30px',
  },
  form: {
    backgroundColor: '#f5f5f5',
    padding: '20px',
    borderRadius: '8px',
    marginBottom: '30px',
  },
  formGroup: {
    marginBottom: '15px',
    display: 'flex',
    gap: '15px',
    alignItems: 'center',
    flexWrap: 'wrap',
  },
  checkboxGroup: {
    display: 'flex',
    gap: '12px',
    flexWrap: 'wrap',
  },
  checkboxLabel: {
    display: 'flex',
    alignItems: 'center',
    gap: '6px',
  },
  button: {
    padding: '10px 20px',
    fontSize: '16px',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginTop: '10px',
  },
  exportButton: {
    padding: '8px 16px',
    fontSize: '14px',
    backgroundColor: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginTop: '15px',
  },
  error: {
    backgroundColor: '#f8d7da',
    color: '#721c24',
    padding: '12px',
    borderRadius: '4px',
    marginBottom: '20px',
    border: '1px solid #f5c6cb',
  },
  success: {
    backgroundColor: '#d4edda',
    color: '#155724',
    padding: '12px',
    borderRadius: '4px',
    marginBottom: '20px',
    border: '1px solid #c3e6cb',
  },
  resultSection: {
    marginBottom: '30px',
  },
  timetableContainer: {
    overflowX: 'auto',
    marginBottom: '20px',
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
    border: '1px solid #ddd',
  },
  tableHeader: {
    backgroundColor: '#007bff',
    color: 'white',
  },
  th: {
    padding: '12px',
    textAlign: 'left',
    fontWeight: 'bold',
    borderBottom: '2px solid #ddd',
  },
  td: {
    padding: '10px',
    borderBottom: '1px solid #ddd',
  },
  tableRowEven: {
    backgroundColor: '#f9f9f9',
  },
  tableRowOdd: {
    backgroundColor: '#ffffff',
  },
  statistics: {
    backgroundColor: '#e7f3ff',
    padding: '15px',
    borderRadius: '4px',
    border: '1px solid #b3d9ff',
  },
  note: {
    fontSize: '14px',
    color: '#666',
    marginTop: '10px',
  },
  infoSection: {
    backgroundColor: '#f0f0f0',
    padding: '15px',
    borderRadius: '4px',
    marginTop: '20px',
  },
};
