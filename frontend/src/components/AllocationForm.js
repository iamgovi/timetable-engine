import React, { useEffect, useState } from 'react';
import axios from 'axios';

export default function AllocationForm() {
  const [teachers, setTeachers] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [sections, setSections] = useState([]);
  const [workingDays, setWorkingDays] = useState([]);
  const [periods, setPeriods] = useState([]);
  const [form, setForm] = useState({
    teacherId: '',
    subjectId: '',
    sectionId: '',
    workingDayId: '',
    periodId: ''
  });
  const [message, setMessage] = useState(null);
  const [assignments, setAssignments] = useState([]);

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const [tRes, sRes, secRes, wdRes, pRes, aRes] = await Promise.all([
          axios.get('/api/teachers'),
          axios.get('/api/subjects'),
          axios.get('/api/sections'),
          axios.get('/api/working-days'),
          axios.get('/api/periods'),
          axios.get('/api/timetable/assignments')
        ]);

        setTeachers(tRes.data || []);
        setSubjects(sRes.data || []);
        setSections(secRes.data || []);
        setWorkingDays(wdRes.data || []);
        setPeriods(pRes.data || []);
        setAssignments(aRes.data || []);
      } catch (err) {
        console.error(err);
        setMessage({ type: 'error', text: 'Failed to load master data. Is the backend running?' });
      }
    };

    fetchAll();
  }, []);

  const onChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const allocate = async (e) => {
    e.preventDefault();
    setMessage(null);
    try {
      const res = await axios.post('/api/timetable/allocate', {
        teacherId: Number(form.teacherId),
        subjectId: Number(form.subjectId),
        sectionId: Number(form.sectionId),
        workingDayId: Number(form.workingDayId),
        periodId: Number(form.periodId)
      });

      setMessage({ type: res.data.success ? 'success' : 'error', text: res.data.message });

      // refresh assignments on success
      if (res.data.success) {
        const aRes = await axios.get('/api/timetable/assignments');
        setAssignments(aRes.data || []);
      }
    } catch (err) {
      console.error(err);
      setMessage({ type: 'error', text: 'Allocation request failed' });
    }
  };

  return (
    <div>
      {message && (
        <div style={{ marginBottom: 10, color: message.type === 'success' ? 'green' : 'red' }}>
          {message.text}
        </div>
      )}

      <form onSubmit={allocate} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, maxWidth: 800 }}>
        <div>
          <label>Teacher</label>
          <select name="teacherId" value={form.teacherId} onChange={onChange} required>
            <option value="">-- select teacher --</option>
            {teachers.map(t => (
              <option key={t.id} value={t.id}>{t.teacherName}</option>
            ))}
          </select>
        </div>

        <div>
          <label>Subject</label>
          <select name="subjectId" value={form.subjectId} onChange={onChange} required>
            <option value="">-- select subject --</option>
            {subjects.map(s => (
              <option key={s.id} value={s.id}>{s.subjectName}</option>
            ))}
          </select>
        </div>

        <div>
          <label>Section</label>
          <select name="sectionId" value={form.sectionId} onChange={onChange} required>
            <option value="">-- select section --</option>
            {sections.map(s => (
              <option key={s.id} value={s.id}>{s.classMaster?.className} - {s.sectionName}</option>
            ))}
          </select>
        </div>

        <div>
          <label>Working Day</label>
          <select name="workingDayId" value={form.workingDayId} onChange={onChange} required>
            <option value="">-- select day --</option>
            {workingDays.map(w => (
              <option key={w.id} value={w.id}>{w.dayName}</option>
            ))}
          </select>
        </div>

        <div>
          <label>Period</label>
          <select name="periodId" value={form.periodId} onChange={onChange} required>
            <option value="">-- select period --</option>
            {periods.map(p => (
              <option key={p.id} value={p.id}>Period {p.periodNumber} ({p.startTime} - {p.endTime})</option>
            ))}
          </select>
        </div>

        <div style={{ gridColumn: '1 / -1' }}>
          <button type="submit">Allocate</button>
        </div>
      </form>

      <h2 style={{ marginTop: 20 }}>Existing Assignments</h2>
      <table border="1" cellPadding="6" style={{ borderCollapse: 'collapse', width: '100%', maxWidth: 1000 }}>
        <thead>
          <tr>
            <th>Teacher</th>
            <th>Subject</th>
            <th>Section</th>
            <th>Day</th>
            <th>Period</th>
          </tr>
        </thead>
        <tbody>
          {assignments.map(a => (
            <tr key={a.id}>
              <td>{a.teacher?.teacherName}</td>
              <td>{a.subject?.subjectName}</td>
              <td>{a.section?.classMaster?.className} - {a.section?.sectionName}</td>
              <td>{a.workingDay?.dayName}</td>
              <td>{a.period?.periodNumber}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
