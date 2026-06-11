import React, { useEffect, useState, useMemo } from 'react';
import axios from 'axios';

export default function AllocationForm() {
  const [teachers, setTeachers] = useState([]);
  const [sections, setSections] = useState([]);
  const [workingDays, setWorkingDays] = useState([]);
  const [form, setForm] = useState({
    teacherIds: [],
    classIds: [],
    sectionIds: [],
    workingDayId: ''
  });
  const [message, setMessage] = useState(null);
  const [assignments, setAssignments] = useState([]);

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const [tRes, secRes, wdRes, aRes] = await Promise.all([
          axios.get('/api/teachers'),
          axios.get('/api/sections'),
          axios.get('/api/working-days'),
          axios.get('/api/timetable/assignments')
        ]);

        setTeachers(tRes.data || []);
        setSections(secRes.data || []);
        setWorkingDays(wdRes.data || []);
        setAssignments(aRes.data || []);
      } catch (err) {
        console.error(err);
        setMessage({ type: 'error', text: 'Failed to load master data. Is the backend running?' });
      }
    };

    fetchAll();
  }, []);

  const onChange = (e) => {
    const { name, value, options, multiple } = e.target;

    if (multiple) {
      const vals = Array.from(options).filter(o => o.selected).map(o => Number(o.value));
      setForm({ ...form, [name]: vals });
      return;
    }

    setForm({ ...form, [name]: value });
  };

  const generateTimetable = async (e) => {
    e.preventDefault();
    setMessage(null);
    try {
      // build final sectionIds: include selected sections plus all sections of selected classes
      const selectedSectionIds = new Set((form.sectionIds || []).map(Number));
      if (form.classIds && form.classIds.length) {
        sections.forEach(s => {
          const classId = s.classMaster?.id || s.classMasterId || null;
          if (classId && form.classIds.includes(classId)) selectedSectionIds.add(s.id);
        });
      }

      const payload = {
        teacherIds: (form.teacherIds || []).map(Number),
        sectionIds: Array.from(selectedSectionIds),
        workingDayId: Number(form.workingDayId)
      };

      const res = await axios.post('/api/timetable/generate/monday', payload);

      setMessage({ type: res.data.success ? 'success' : 'error', text: res.data.message || 'Generation completed' });

      // refresh assignments and filter by selected working day
      const aRes = await axios.get('/api/timetable/assignments');
      const all = aRes.data || [];
      const filtered = all.filter(a => Number(a.workingDayId || a.workingDay?.id) === Number(form.workingDayId));
      setAssignments(filtered);
    } catch (err) {
      console.error(err);
      setMessage({ type: 'error', text: 'Timetable generation failed' });
    }
  };

  const classes = useMemo(() => {
    const m = new Map();
    sections.forEach(s => {
      const cm = s.classMaster || (s.classMasterId ? { id: s.classMasterId, className: s.className } : null);
      if (cm && !m.has(cm.id)) m.set(cm.id, { id: cm.id, className: cm.className });
    });
    return Array.from(m.values());
  }, [sections]);

  return (
    <div>
      {message && (
        <div style={{ marginBottom: 10, color: message.type === 'success' ? 'green' : 'red' }}>
          {message.text}
        </div>
      )}

      <form onSubmit={generateTimetable} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, maxWidth: 800 }}>
        <div>
          <label>Teacher (multi-select)</label>
          <select name="teacherIds" value={form.teacherIds} onChange={onChange} multiple size={6} required>
            {teachers.map(t => (
              <option key={t.id} value={t.id}>{t.teacherName}</option>
            ))}
          </select>
        </div>

        <div>
          <label>Class (multi-select)</label>
          <select name="classIds" value={form.classIds} onChange={onChange} multiple size={6}>
            {classes.map(c => (
              <option key={c.id} value={c.id}>{c.className}</option>
            ))}
          </select>
        </div>

        <div>
          <label>Section (multi-select)</label>
          <select name="sectionIds" value={form.sectionIds} onChange={onChange} multiple size={8}>
            {sections.map(s => (
              <option key={s.id} value={s.id}>{s.classMaster?.className || s.className} - {s.sectionName}</option>
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

        <div style={{ gridColumn: '1 / -1' }}>
          <button type="submit">{`Generate ${workingDays.find(w => Number(w.id) === Number(form.workingDayId))?.dayName || 'Timetable'}`}</button>
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
