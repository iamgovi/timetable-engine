import React, { useState } from 'react';

/**
 * SingleSectionTimetableGenerator Component
 * 
 * Generates a timetable for a single section on a single working day.
 * Phase 1 of phased timetable generation.
 * 
 * Features:
 * - Simple form to select section and working day
 * - Calls /api/timetable/generate/single-section endpoint
 * - Displays generated timetable in human-readable format
 * - Shows loading state and error handling
 * - Validates rule compliance
 */
export default function SingleSectionTimetableGenerator() {
  const [classId, setClassId] = useState('');
  const [sectionId, setSectionId] = useState('');
  const [workingDayId, setWorkingDayId] = useState('');
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleGenerate = async (e) => {
    e.preventDefault();
    
    if ((!classId && !sectionId) || !workingDayId) {
      setError('Please provide a class ID or section ID, and select a working day');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess(false);

    const payload = {
      workingDayId: parseInt(workingDayId),
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
        // Sort by section ID and period number for display
        const sorted = [...data].sort((a, b) => {
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
    text += `Working Day: ${workingDayId}\n\n`;
    assignments.forEach((a) => {
      text += `P${a.period.periodNumber}: ${a.subject.subjectName} (${a.teacher.name})\n`;
    });
    return text;
  };

  return (
    <div style={styles.container}>
      <h1>Class / Section Timetable Generator</h1>
      <p style={styles.subtitle}>Phase 1: Generate Monday timetable for one class or one section</p>

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
          <label htmlFor="workingDayId">Working Day ID:</label>
          <input
            id="workingDayId"
            type="number"
            value={workingDayId}
            onChange={(e) => setWorkingDayId(e.target.value)}
            placeholder="e.g., 1"
            min="1"
            required
          />
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
                  <th style={styles.th}>Period</th>
                  <th style={styles.th}>Section</th>
                  <th style={styles.th}>Subject</th>
                  <th style={styles.th}>Teacher</th>
                </tr>
              </thead>
              <tbody>
                {assignments.map((assignment, index) => (
                  <tr key={assignment.id} style={index % 2 === 0 ? styles.tableRowEven : styles.tableRowOdd}>
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
              a.download = `timetable_section_${sectionId}.txt`;
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
          <li>Enter a section ID (e.g., 4 for class 8-A)</li>
          <li>Enter a working day ID (e.g., 1 for Monday)</li>
          <li>The system will generate a timetable for all periods</li>
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
    alignItems: 'center',
    gap: '10px',
  },
  formGroup: {
    marginBottom: '15px',
    display: 'flex',
    gap: '15px',
    alignItems: 'center',
    flexWrap: 'wrap',
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
