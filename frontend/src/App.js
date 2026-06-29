import React, { useCallback, useEffect, useMemo, useState } from 'react';

const NAV_ITEMS = [
  { id: 'dashboard', label: 'Dashboard', symbol: 'D' },
  { id: 'generation', label: 'School Generation', symbol: 'G' },
  { id: 'curriculum', label: 'Curriculum', symbol: 'C' },
  { id: 'planning', label: 'Planning', symbol: 'P' },
  { id: 'feasibility', label: 'Feasibility', symbol: 'F' },
  { id: 'timetable', label: 'Timetable Viewer', symbol: 'T' },
  { id: 'analytics', label: 'Analytics', symbol: 'A' },
  { id: 'settings', label: 'Settings', symbol: 'S' },
];

const POLICY_OPTIONS = [
  { value: 'balanced', label: 'Balanced' },
  { value: 'curriculum', label: 'Curriculum Priority' },
  { value: 'teacher-scarcity', label: 'Teacher Scarcity' },
];

const DEFAULT_SETTINGS = {
  schedulerPolicy: 'balanced',
  academicYearId: '',
  workingDayIds: [],
};

async function api(path, options = {}) {
  const response = await fetch(path, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options,
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed with status ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

function entityLabel(entity, fallback = 'Untitled') {
  return entity?.yearName
    || entity?.className
    || entity?.sectionName
    || entity?.subjectName
    || entity?.teacherName
    || entity?.categoryName
    || entity?.curriculumName
    || entity?.dayName
    || fallback;
}

function classSectionLabel(section) {
  const className = section?.classMaster?.className || section?.className || 'Class';
  return `${className} ${section?.sectionName || ''}`.trim();
}

function useLocalSettings() {
  const [settings, setSettings] = useState(() => {
    try {
      return { ...DEFAULT_SETTINGS, ...JSON.parse(localStorage.getItem('timetableSettings') || '{}') };
    } catch {
      return DEFAULT_SETTINGS;
    }
  });

  const saveSettings = (next) => {
    const merged = { ...settings, ...next };
    setSettings(merged);
    localStorage.setItem('timetableSettings', JSON.stringify(merged));
  };

  return [settings, saveSettings];
}

function useBootstrapData() {
  const [data, setData] = useState({
    academicYears: [],
    classes: [],
    sections: [],
    teachers: [],
    subjects: [],
    workingDays: [],
    periods: [],
    assignments: [],
    curricula: [],
    categories: [],
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const refresh = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [
        academicYears,
        classes,
        sections,
        teachers,
        subjects,
        workingDays,
        periods,
        assignments,
        curricula,
        categories,
      ] = await Promise.all([
        api('/api/academic-years'),
        api('/api/classes'),
        api('/api/sections'),
        api('/api/teachers'),
        api('/api/subjects'),
        api('/api/working-days'),
        api('/api/periods'),
        api('/api/timetable/assignments'),
        api('/api/curricula'),
        api('/api/subject-categories'),
      ]);

      setData({
        academicYears,
        classes,
        sections,
        teachers,
        subjects,
        workingDays,
        periods,
        assignments,
        curricula,
        categories,
      });
    } catch (err) {
      setError('Unable to load school data. Start the backend and refresh.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  return { ...data, loading, error, refresh };
}

function App() {
  const [activePage, setActivePage] = useState('dashboard');
  const [settings, saveSettings] = useLocalSettings();
  const [lastGeneration, setLastGeneration] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('lastGeneration') || 'null');
    } catch {
      return null;
    }
  });
  const bootstrap = useBootstrapData();

  const pageTitle = NAV_ITEMS.find((item) => item.id === activePage)?.label || 'Dashboard';

  const rememberGeneration = (report) => {
    const stamped = { ...report, generatedAt: new Date().toISOString() };
    setLastGeneration(stamped);
    localStorage.setItem('lastGeneration', JSON.stringify(stamped));
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-block">
          <div className="brand-mark">ST</div>
          <div>
            <div className="brand-title">School Timetable</div>
            <div className="brand-subtitle">Management System</div>
          </div>
        </div>
        <nav className="nav-list">
          {NAV_ITEMS.map((item) => (
            <button
              key={item.id}
              className={`nav-item ${activePage === item.id ? 'active' : ''}`}
              onClick={() => setActivePage(item.id)}
              type="button"
            >
              <span className="nav-symbol">{item.symbol}</span>
              <span>{item.label}</span>
            </button>
          ))}
        </nav>
      </aside>

      <main className="main-panel">
        <header className="topbar">
          <div>
            <h1>{pageTitle}</h1>
            <p>{currentYearLabel(bootstrap.academicYears)}</p>
          </div>
          <button className="icon-button" type="button" onClick={bootstrap.refresh} title="Refresh data">
            R
          </button>
        </header>

        {bootstrap.error && <Alert type="error" title="Data unavailable" message={bootstrap.error} />}
        {bootstrap.loading ? (
          <LoadingPanel />
        ) : (
          <PageRouter
            activePage={activePage}
            bootstrap={bootstrap}
            settings={settings}
            saveSettings={saveSettings}
            lastGeneration={lastGeneration}
            rememberGeneration={rememberGeneration}
          />
        )}
      </main>
    </div>
  );
}

function PageRouter({ activePage, bootstrap, settings, saveSettings, lastGeneration, rememberGeneration }) {
  if (activePage === 'generation') {
    return (
      <SchoolGenerationPage
        data={bootstrap}
        settings={settings}
        saveSettings={saveSettings}
        onGenerated={rememberGeneration}
        refresh={bootstrap.refresh}
      />
    );
  }
  if (activePage === 'curriculum') return <CurriculumPage data={bootstrap} refresh={bootstrap.refresh} />;
  if (activePage === 'planning') return <PlanningPage data={bootstrap} />;
  if (activePage === 'feasibility') return <FeasibilityPage data={bootstrap} />;
  if (activePage === 'timetable') return <TimetableViewer data={bootstrap} />;
  if (activePage === 'analytics') return <AnalyticsPage data={bootstrap} />;
  if (activePage === 'settings') {
    return <SettingsPage data={bootstrap} settings={settings} saveSettings={saveSettings} />;
  }
  return <Dashboard data={bootstrap} lastGeneration={lastGeneration} />;
}

function Dashboard({ data, lastGeneration }) {
  const academicYear = selectedAcademicYear(data.academicYears);
  const yearId = academicYear?.id;
  const yearClasses = classesForAcademicYear(data, yearId);
  const yearSections = sectionsForAcademicYear(data.sections, yearId);
  const yearAssignments = assignmentsForAcademicYear(data.assignments, yearId);
  const currentYear = academicYear?.yearName || 'Academic year not selected';
  const assignmentTotal = yearAssignments.length;
  const uniqueSections = new Set(yearAssignments.map((item) => item.section?.id).filter(Boolean)).size;

  return (
    <div className="page-stack">
      <section className="metric-grid">
        <MetricCard label="Academic Year" value={currentYear} tone="teal" />
        <MetricCard label="Total Classes" value={yearClasses.length} tone="coral" />
        <MetricCard label="Total Sections" value={yearSections.length} tone="amber" />
        <MetricCard label="Teachers" value={data.teachers.length} tone="green" />
        <MetricCard label="Subjects" value={data.subjects.length} tone="blue" />
        <MetricCard label="Generation Status" value={lastGeneration?.status || 'Not generated'} tone="purple" />
        <MetricCard label="Last Generated" value={formatDateTime(lastGeneration?.generatedAt)} tone="gray" />
        <MetricCard label="Assignments" value={`${assignmentTotal} across ${uniqueSections || 0} sections`} tone="teal" />
      </section>

      <section className="content-grid two-columns">
        <Panel title="Recent generation">
          {lastGeneration ? (
            <div className="report-list">
              <ReportLine label="Session" value={lastGeneration.sessionId} />
              <ReportLine label="Status" value={<StatusBadge status={lastGeneration.status} />} />
              <ReportLine label="Sections" value={lastGeneration.sectionsProcessed} />
              <ReportLine label="Assignments" value={lastGeneration.assignmentsGenerated} />
              <ReportLine label="Duration" value={`${lastGeneration.executionDurationMillis || 0} ms`} />
            </div>
          ) : (
            <EmptyState title="No generation run recorded" />
          )}
        </Panel>
        <Panel title="School data coverage">
          <DataTable
            columns={[
              { key: 'className', label: 'Class' },
              { key: 'sections', label: 'Sections' },
              { key: 'assignments', label: 'Assignments' },
            ]}
            rows={yearClasses.map((classItem) => {
              const sections = yearSections.filter((section) => section.classMaster?.id === classItem.id);
              const sectionIds = new Set(sections.map((section) => section.id));
              return {
                id: classItem.id,
                className: classItem.className,
                sections: sections.length,
                assignments: yearAssignments.filter((assignment) => sectionIds.has(assignment.section?.id)).length,
              };
            })}
          />
        </Panel>
      </section>
    </div>
  );
}

function SchoolGenerationPage({ data, settings, saveSettings, onGenerated, refresh }) {
  const defaultYearId = validAcademicYearId(data.academicYears, settings.academicYearId);
  const defaultClasses = classesForAcademicYear(data, defaultYearId);
  const defaultWorkingDays = settings.workingDayIds?.length
    ? validIds(settings.workingDayIds, data.workingDays)
    : data.workingDays.filter((day) => day.working !== false).slice(0, 5).map((item) => item.id);
  const [form, setForm] = useState({
    academicYearId: defaultYearId || '',
    classIds: defaultClasses.map((item) => item.id),
    workingDayIds: defaultWorkingDays,
    schedulerPolicy: settings.schedulerPolicy || 'balanced',
  });
  const [loading, setLoading] = useState(false);
  const [report, setReport] = useState(null);
  const [error, setError] = useState('');
  const availableClasses = classesForAcademicYear(data, form.academicYearId);
  const availableClassIds = availableClasses.map((item) => item.id).join(',');

  useEffect(() => {
    setForm((current) => {
      const validClassIds = availableClasses.map((item) => item.id);
      const selected = current.classIds.filter((id) => validClassIds.includes(id));
      return {
        ...current,
        classIds: selected.length ? selected : validClassIds,
      };
    });
  }, [form.academicYearId, availableClassIds]);

  const updateMulti = (name, id) => {
    setForm((current) => {
      const exists = current[name].includes(id);
      return {
        ...current,
        [name]: exists ? current[name].filter((item) => item !== id) : [...current[name], id].sort((a, b) => a - b),
      };
    });
  };

  const generate = async (event) => {
    event.preventDefault();
    setError('');
    setLoading(true);
    setReport(null);
    saveSettings({
      schedulerPolicy: form.schedulerPolicy,
      academicYearId: form.academicYearId,
      workingDayIds: form.workingDayIds,
    });

    try {
      const payload = {
        academicYearId: Number(form.academicYearId),
        classIds: form.classIds.map(Number),
        workingDayIds: form.workingDayIds.map(Number),
      };
      const result = await api('/api/timetable/generate-school', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      setReport(result);
      onGenerated(result);
      await refresh();
    } catch (err) {
      setError('School generation failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-stack">
      <Panel title="School generation">
        <form className="form-grid" onSubmit={generate}>
          <SelectField
            label="Academic Year"
            value={form.academicYearId}
            onChange={(value) => setForm({ ...form, academicYearId: Number(value) })}
            options={data.academicYears.map((item) => ({ value: item.id, label: item.yearName }))}
          />
          <SelectField
            label="Scheduler Policy"
            value={form.schedulerPolicy}
            onChange={(value) => setForm({ ...form, schedulerPolicy: value })}
            options={POLICY_OPTIONS}
          />
          <CheckGroup
            label="Classes"
            items={availableClasses}
            selected={form.classIds}
            onToggle={(id) => updateMulti('classIds', id)}
            getLabel={(item) => item.className}
          />
          <CheckGroup
            label="Working Days"
            items={data.workingDays}
            selected={form.workingDayIds}
            onToggle={(id) => updateMulti('workingDayIds', id)}
            getLabel={(item) => item.dayName}
          />
          <div className="form-actions">
            <button className="primary-button" type="submit" disabled={loading}>
              {loading ? 'Generating' : 'Generate School'}
            </button>
          </div>
        </form>
      </Panel>

      {error && <Alert type="error" title="Generation failed" message={error} />}
      {report && <SchedulerReportPanel report={report} />}
    </div>
  );
}

function CurriculumPage({ data, refresh }) {
  const defaultYearId = selectedAcademicYear(data.academicYears)?.id || '';
  const defaultClasses = classesForAcademicYear(data, defaultYearId);
  const currentYearCurricula = curriculaForAcademicYear(data.curricula, defaultYearId);
  const [activeTab, setActiveTab] = useState('curricula');
  const [selectedCurriculumId, setSelectedCurriculumId] = useState(currentYearCurricula[0]?.id || data.curricula[0]?.id || '');
  const [curriculumSubjects, setCurriculumSubjects] = useState([]);
  const [message, setMessage] = useState('');
  const [editingCurriculumId, setEditingCurriculumId] = useState(null);
  const [editingSubjectId, setEditingSubjectId] = useState(null);
  const [editingCategoryId, setEditingCategoryId] = useState(null);
  const [curriculumForm, setCurriculumForm] = useState({
    curriculumName: '',
    classId: defaultClasses[0]?.id || '',
    academicYearId: defaultYearId,
    description: '',
    active: true,
  });
  const [subjectForm, setSubjectForm] = useState({
    subjectId: data.subjects[0]?.id || '',
    categoryId: data.categories[0]?.id || '',
    weeklyPeriods: 1,
    dailyPeriodLimit: 1,
    requirementType: 'CORE',
    optionalSubject: false,
    active: true,
  });
  const [categoryForm, setCategoryForm] = useState({ categoryName: '', description: '', active: true });
  const curriculumFormClasses = classesForAcademicYear(data, curriculumForm.academicYearId);

  const loadSubjects = useCallback(async (curriculumId) => {
    if (!curriculumId) {
      setCurriculumSubjects([]);
      return;
    }
    const subjects = await api(`/api/curricula/${curriculumId}/subjects`);
    setCurriculumSubjects(subjects || []);
  }, []);

  useEffect(() => {
    loadSubjects(selectedCurriculumId).catch(() => setCurriculumSubjects([]));
  }, [selectedCurriculumId, loadSubjects]);

  const submitCurriculum = async (event) => {
    event.preventDefault();
    await api(editingCurriculumId ? `/api/curricula/${editingCurriculumId}` : '/api/curricula', {
      method: editingCurriculumId ? 'PUT' : 'POST',
      body: JSON.stringify(normalizeIds(curriculumForm)),
    });
    setMessage('Curriculum saved.');
    setEditingCurriculumId(null);
    refresh();
  };

  const submitCurriculumSubject = async (event) => {
    event.preventDefault();
    await api(editingSubjectId ? `/api/curricula/subjects/${editingSubjectId}` : `/api/curricula/${selectedCurriculumId}/subjects`, {
      method: editingSubjectId ? 'PUT' : 'POST',
      body: JSON.stringify(normalizeIds(subjectForm)),
    });
    setMessage('Curriculum subject saved.');
    setEditingSubjectId(null);
    loadSubjects(selectedCurriculumId);
  };

  const submitCategory = async (event) => {
    event.preventDefault();
    await api(editingCategoryId ? `/api/subject-categories/${editingCategoryId}` : '/api/subject-categories', {
      method: editingCategoryId ? 'PUT' : 'POST',
      body: JSON.stringify(categoryForm),
    });
    setMessage('Subject category saved.');
    setEditingCategoryId(null);
    refresh();
  };

  const editCurriculum = (row) => {
    setEditingCurriculumId(row.id);
    setCurriculumForm({
      curriculumName: row.curriculumName || '',
      classId: row.classMaster?.id || row.classId || '',
      academicYearId: row.academicYear?.id || row.academicYearId || '',
      description: row.description || '',
      active: row.active !== false,
    });
  };

  const deleteCurriculum = async (id) => {
    await api(`/api/curricula/${id}`, { method: 'DELETE' });
    setMessage('Curriculum deleted.');
    refresh();
  };

  const editSubject = (row) => {
    setEditingSubjectId(row.id);
    setSubjectForm({
      subjectId: row.subject?.id || '',
      categoryId: row.category?.id || '',
      weeklyPeriods: row.weeklyPeriods ?? 0,
      dailyPeriodLimit: row.dailyPeriodLimit ?? 0,
      requirementType: row.requirementType || '',
      optionalSubject: row.optionalSubject === true,
      active: row.active !== false,
    });
  };

  const deleteSubject = async (id) => {
    await api(`/api/curricula/subjects/${id}`, { method: 'DELETE' });
    setMessage('Curriculum subject deleted.');
    loadSubjects(selectedCurriculumId);
  };

  const editCategory = (row) => {
    setEditingCategoryId(row.id);
    setCategoryForm({
      categoryName: row.categoryName || '',
      description: row.description || '',
      active: row.active !== false,
    });
  };

  const deleteCategory = async (id) => {
    await api(`/api/subject-categories/${id}`, { method: 'DELETE' });
    setMessage('Subject category deleted.');
    refresh();
  };

  return (
    <div className="page-stack">
      <TabBar
        active={activeTab}
        onChange={setActiveTab}
        tabs={[
          { id: 'curricula', label: 'Curricula' },
          { id: 'subjects', label: 'Curriculum Subjects' },
          { id: 'categories', label: 'Subject Categories' },
        ]}
      />
      {message && <Alert type="success" title="Saved" message={message} />}
      {activeTab === 'curricula' && (
        <section className="content-grid two-columns">
          <Panel title="Curricula">
            <DataTable
              columns={[
                { key: 'curriculumName', label: 'Curriculum' },
                { key: 'className', label: 'Class' },
                { key: 'year', label: 'Academic Year' },
                { key: 'active', label: 'Status', render: (row) => <StatusBadge status={row.active ? 'ACTIVE' : 'INACTIVE'} /> },
                { key: 'actions', label: 'Actions', render: (row) => <RowActions onEdit={() => editCurriculum(row)} onDelete={() => deleteCurriculum(row.id)} /> },
              ]}
              rows={data.curricula.map((item) => ({
                ...item,
                className: item.classMaster?.className,
                year: item.academicYear?.yearName,
              }))}
            />
          </Panel>
          <Panel title={editingCurriculumId ? 'Edit curriculum' : 'Add curriculum'}>
            <form className="form-stack" onSubmit={submitCurriculum}>
              <TextField label="Curriculum Name" value={curriculumForm.curriculumName} onChange={(value) => setCurriculumForm({ ...curriculumForm, curriculumName: value })} />
              <SelectField label="Academic Year" value={curriculumForm.academicYearId} onChange={(value) => {
                const nextYearId = Number(value);
                const nextClasses = classesForAcademicYear(data, nextYearId);
                setCurriculumForm({ ...curriculumForm, academicYearId: nextYearId, classId: nextClasses[0]?.id || '' });
              }} options={data.academicYears.map((item) => ({ value: item.id, label: item.yearName }))} />
              <SelectField label="Class" value={curriculumForm.classId} onChange={(value) => setCurriculumForm({ ...curriculumForm, classId: Number(value) })} options={curriculumFormClasses.map((item) => ({ value: item.id, label: item.className }))} />
              <TextField label="Description" value={curriculumForm.description} onChange={(value) => setCurriculumForm({ ...curriculumForm, description: value })} />
              <ToggleField label="Active" checked={curriculumForm.active} onChange={(value) => setCurriculumForm({ ...curriculumForm, active: value })} />
              <button className="primary-button" type="submit">Save Curriculum</button>
              {editingCurriculumId && <button className="secondary-button" type="button" onClick={() => setEditingCurriculumId(null)}>Cancel Edit</button>}
            </form>
          </Panel>
        </section>
      )}
      {activeTab === 'subjects' && (
        <section className="content-grid two-columns">
          <Panel title="Curriculum subjects">
            <SelectField
              label="Curriculum"
              value={selectedCurriculumId}
              onChange={setSelectedCurriculumId}
              options={data.curricula.map((item) => ({ value: item.id, label: `${item.curriculumName} (${item.classMaster?.className || ''})` }))}
            />
            <DataTable
              columns={[
                { key: 'subject', label: 'Subject' },
                { key: 'category', label: 'Category' },
                { key: 'weeklyPeriods', label: 'Weekly Periods' },
                { key: 'requirementType', label: 'Type' },
                { key: 'active', label: 'Status', render: (row) => <StatusBadge status={row.active ? 'ACTIVE' : 'INACTIVE'} /> },
                { key: 'actions', label: 'Actions', render: (row) => <RowActions onEdit={() => editSubject(row)} onDelete={() => deleteSubject(row.id)} /> },
              ]}
              rows={curriculumSubjects.map((item) => ({
                ...item,
                subject: item.subject?.subjectName,
                category: item.category?.categoryName,
              }))}
            />
          </Panel>
          <Panel title={editingSubjectId ? 'Edit subject requirement' : 'Add subject requirement'}>
            <form className="form-stack" onSubmit={submitCurriculumSubject}>
              <SelectField label="Subject" value={subjectForm.subjectId} onChange={(value) => setSubjectForm({ ...subjectForm, subjectId: value })} options={data.subjects.map((item) => ({ value: item.id, label: item.subjectName }))} />
              <SelectField label="Category" value={subjectForm.categoryId} onChange={(value) => setSubjectForm({ ...subjectForm, categoryId: value })} options={data.categories.map((item) => ({ value: item.id, label: item.categoryName }))} />
              <NumberField label="Weekly Periods" value={subjectForm.weeklyPeriods} onChange={(value) => setSubjectForm({ ...subjectForm, weeklyPeriods: value })} />
              <NumberField label="Daily Limit" value={subjectForm.dailyPeriodLimit} onChange={(value) => setSubjectForm({ ...subjectForm, dailyPeriodLimit: value })} />
              <TextField label="Requirement Type" value={subjectForm.requirementType} onChange={(value) => setSubjectForm({ ...subjectForm, requirementType: value })} />
              <ToggleField label="Optional" checked={subjectForm.optionalSubject} onChange={(value) => setSubjectForm({ ...subjectForm, optionalSubject: value })} />
              <ToggleField label="Active" checked={subjectForm.active} onChange={(value) => setSubjectForm({ ...subjectForm, active: value })} />
              <button className="primary-button" type="submit" disabled={!selectedCurriculumId}>Save Requirement</button>
              {editingSubjectId && <button className="secondary-button" type="button" onClick={() => setEditingSubjectId(null)}>Cancel Edit</button>}
            </form>
          </Panel>
        </section>
      )}
      {activeTab === 'categories' && (
        <section className="content-grid two-columns">
          <Panel title="Subject categories">
            <DataTable
              columns={[
                { key: 'categoryName', label: 'Category' },
                { key: 'description', label: 'Description' },
                { key: 'active', label: 'Status', render: (row) => <StatusBadge status={row.active ? 'ACTIVE' : 'INACTIVE'} /> },
                { key: 'actions', label: 'Actions', render: (row) => <RowActions onEdit={() => editCategory(row)} onDelete={() => deleteCategory(row.id)} /> },
              ]}
              rows={data.categories}
            />
          </Panel>
          <Panel title={editingCategoryId ? 'Edit category' : 'Add category'}>
            <form className="form-stack" onSubmit={submitCategory}>
              <TextField label="Category Name" value={categoryForm.categoryName} onChange={(value) => setCategoryForm({ ...categoryForm, categoryName: value })} />
              <TextField label="Description" value={categoryForm.description} onChange={(value) => setCategoryForm({ ...categoryForm, description: value })} />
              <ToggleField label="Active" checked={categoryForm.active} onChange={(value) => setCategoryForm({ ...categoryForm, active: value })} />
              <button className="primary-button" type="submit">Save Category</button>
              {editingCategoryId && <button className="secondary-button" type="button" onClick={() => setEditingCategoryId(null)}>Cancel Edit</button>}
            </form>
          </Panel>
        </section>
      )}
    </div>
  );
}

function PlanningPage({ data }) {
  const [academicYearId, setAcademicYearId] = useState(selectedAcademicYear(data.academicYears)?.id || '');
  const [classIds, setClassIds] = useState(classesForAcademicYear(data, selectedAcademicYear(data.academicYears)?.id).map((item) => item.id));
  const [plan, setPlan] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const availableClasses = classesForAcademicYear(data, academicYearId);
  const availableClassIds = availableClasses.map((item) => item.id).join(',');

  useEffect(() => {
    const validClassIds = availableClasses.map((item) => item.id);
    setClassIds((current) => {
      const selected = current.filter((id) => validClassIds.includes(id));
      return selected.length ? selected : validClassIds;
    });
  }, [academicYearId, availableClassIds]);

  const loadPlan = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const params = new URLSearchParams();
      if (academicYearId) params.set('academicYearId', academicYearId);
      classIds.forEach((id) => params.append('classIds', id));
      setPlan(await api(`/api/planning?${params.toString()}`));
    } catch {
      setError('Planning data could not be loaded.');
    } finally {
      setLoading(false);
    }
  }, [academicYearId, classIds]);

  useEffect(() => {
    loadPlan();
  }, [loadPlan]);

  return (
    <div className="page-stack">
      <Panel title="Planning filters">
        <div className="form-grid">
          <SelectField label="Academic Year" value={academicYearId} onChange={(value) => setAcademicYearId(Number(value))} options={data.academicYears.map((item) => ({ value: item.id, label: item.yearName }))} />
          <CheckGroup label="Classes" items={availableClasses} selected={classIds} onToggle={(id) => setClassIds((current) => current.includes(id) ? current.filter((item) => item !== id) : [...current, id])} getLabel={(item) => item.className} />
        </div>
      </Panel>
      {loading && <LoadingPanel compact />}
      {error && <Alert type="error" title="Planning unavailable" message={error} />}
      {plan && <PlanningReport plan={plan} />}
    </div>
  );
}

function PlanningReport({ plan }) {
  const summary = plan.summary || {};
  return (
    <div className="page-stack">
      <section className="metric-grid">
        <MetricCard label="Classes" value={summary.totalClasses ?? plan.classDemands?.length ?? 0} tone="teal" />
        <MetricCard label="Sections" value={summary.totalSections ?? 0} tone="coral" />
        <MetricCard label="Curriculum Periods" value={summary.totalCurriculumPeriods ?? 0} tone="amber" />
        <MetricCard label="Teachers" value={summary.totalTeachers ?? plan.teacherUtilizations?.length ?? 0} tone="green" />
        <MetricCard label="Shortages" value={summary.subjectsWithShortages ?? plan.teacherRequirements?.filter((item) => item.additionalTeachersNeeded > 0).length ?? 0} tone="purple" />
        <MetricCard label="Feasible" value={summary.generationFeasible ? 'Yes' : 'No'} tone={summary.generationFeasible ? 'green' : 'coral'} />
      </section>
      <section className="content-grid two-columns">
        <Panel title="Class demand">
          <DataTable
            columns={[
              { key: 'className', label: 'Class' },
              { key: 'sectionCount', label: 'Sections' },
              { key: 'weeklyPeriodsPerSection', label: 'Periods / Section' },
              { key: 'totalWeeklyPeriods', label: 'Total Periods' },
            ]}
            rows={(plan.classDemands || []).map((item) => ({ id: item.classId, ...item }))}
          />
        </Panel>
        <Panel title="Teacher utilization">
          <DataTable
            columns={[
              { key: 'teacherName', label: 'Teacher' },
              { key: 'subjectsText', label: 'Subjects' },
              { key: 'load', label: 'Load' },
              { key: 'utilization', label: 'Utilization' },
            ]}
            rows={(plan.teacherUtilizations || []).map((item) => ({
              ...item,
              subjectsText: item.subjects?.join(', '),
              load: `${item.assignedCurriculumLoad}/${item.maxWeeklyPeriods}`,
              utilization: `${Math.round(item.utilizationPercent || 0)}%`,
            }))}
          />
        </Panel>
        <Panel title="Teacher requirements">
          <DataTable
            columns={[
              { key: 'subjectName', label: 'Subject' },
              { key: 'requiredPeriods', label: 'Required' },
              { key: 'availableCapacity', label: 'Capacity' },
              { key: 'additionalTeachersNeeded', label: 'Needed' },
            ]}
            rows={plan.teacherRequirements || []}
          />
        </Panel>
      </section>
    </div>
  );
}

function FeasibilityPage({ data }) {
  const defaultYearId = selectedAcademicYear(data.academicYears)?.id || '';
  const [form, setForm] = useState({
    academicYearId: defaultYearId,
    classIds: classesForAcademicYear(data, defaultYearId).map((item) => item.id),
    workingDayIds: data.workingDays.slice(0, 5).map((item) => item.id),
  });
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const availableClasses = classesForAcademicYear(data, form.academicYearId);
  const availableClassIds = availableClasses.map((item) => item.id).join(',');

  useEffect(() => {
    const validClassIds = availableClasses.map((item) => item.id);
    setForm((current) => {
      const selected = current.classIds.filter((id) => validClassIds.includes(id));
      return { ...current, classIds: selected.length ? selected : validClassIds };
    });
  }, [form.academicYearId, availableClassIds]);

  const submit = async (event) => {
    event.preventDefault();
    setLoading(true);
    try {
      setReport(await api('/api/planning/feasibility', {
        method: 'POST',
        body: JSON.stringify({
          academicYearId: Number(form.academicYearId),
          classIds: form.classIds.map(Number),
          workingDayIds: form.workingDayIds.map(Number),
        }),
      }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-stack">
      <Panel title="Feasibility request">
        <form className="form-grid" onSubmit={submit}>
          <SelectField label="Academic Year" value={form.academicYearId} onChange={(value) => setForm({ ...form, academicYearId: Number(value) })} options={data.academicYears.map((item) => ({ value: item.id, label: item.yearName }))} />
          <CheckGroup label="Classes" items={availableClasses} selected={form.classIds} onToggle={(id) => setForm({ ...form, classIds: toggle(form.classIds, id) })} getLabel={(item) => item.className} />
          <CheckGroup label="Working Days" items={data.workingDays} selected={form.workingDayIds} onToggle={(id) => setForm({ ...form, workingDayIds: toggle(form.workingDayIds, id) })} getLabel={(item) => item.dayName} />
          <div className="form-actions">
            <button className="primary-button" type="submit" disabled={loading}>{loading ? 'Checking' : 'Run Check'}</button>
          </div>
        </form>
      </Panel>
      {report && <FeasibilityReportPanel report={report} />}
    </div>
  );
}

function TimetableViewer({ data }) {
  const [mode, setMode] = useState('section');
  const [academicYearId, setAcademicYearId] = useState(selectedAcademicYear(data.academicYears)?.id || '');
  const [targetId, setTargetId] = useState('');
  const yearClasses = classesForAcademicYear(data, academicYearId);
  const yearSections = sectionsForAcademicYear(data.sections, academicYearId);
  const yearAssignments = assignmentsForAcademicYear(data.assignments, academicYearId);

  const options = mode === 'teacher'
    ? data.teachers.map((item) => ({ value: item.id, label: item.teacherName }))
    : mode === 'class'
      ? yearClasses.map((item) => ({ value: item.id, label: item.className }))
      : yearSections.map((item) => ({ value: item.id, label: classSectionLabel(item) }));
  const optionIds = options.map((item) => item.value).join(',');

  useEffect(() => {
    setTargetId(options[0]?.value || '');
  }, [mode, academicYearId, optionIds]);

  const filtered = yearAssignments.filter((assignment) => {
    if (!targetId) return true;
    if (mode === 'teacher') return assignment.teacher?.id === Number(targetId);
    if (mode === 'class') return assignment.section?.classMaster?.id === Number(targetId);
    return assignment.section?.id === Number(targetId);
  });

  return (
    <div className="page-stack">
      <Panel title="Timetable filters">
        <div className="toolbar">
          <SegmentedControl value={mode} onChange={setMode} options={[
            { value: 'class', label: 'Class' },
            { value: 'section', label: 'Section' },
            { value: 'teacher', label: 'Teacher' },
          ]} />
          <SelectField label="Academic Year" value={academicYearId} onChange={(value) => setAcademicYearId(Number(value))} options={data.academicYears.map((item) => ({ value: item.id, label: item.yearName }))} />
          <SelectField label="View" value={targetId} onChange={setTargetId} options={options} />
        </div>
      </Panel>
      <TimetableGrid assignments={filtered} days={data.workingDays} periods={data.periods} mode={mode} />
    </div>
  );
}

function AnalyticsPage({ data }) {
  const academicYear = selectedAcademicYear(data.academicYears);
  const yearId = academicYear?.id;
  const yearCurricula = curriculaForAcademicYear(data.curricula, yearId);
  const yearAssignments = assignmentsForAcademicYear(data.assignments, yearId);
  const teacherLoads = data.teachers.map((teacher) => {
    const count = yearAssignments.filter((assignment) => assignment.teacher?.id === teacher.id).length;
    return { id: teacher.id, teacherName: teacher.teacherName, assignments: count };
  }).sort((a, b) => b.assignments - a.assignments);

  const subjectDistribution = data.subjects.map((subject) => {
    const count = yearAssignments.filter((assignment) => assignment.subject?.id === subject.id).length;
    return { id: subject.id, subjectName: subject.subjectName, assignments: count };
  }).sort((a, b) => b.assignments - a.assignments);

  return (
    <div className="page-stack">
      <section className="metric-grid">
        <MetricCard label="Assignments" value={yearAssignments.length} tone="teal" />
        <MetricCard label="Active Teachers" value={teacherLoads.filter((item) => item.assignments > 0).length} tone="green" />
        <MetricCard label="Subjects Scheduled" value={subjectDistribution.filter((item) => item.assignments > 0).length} tone="amber" />
        <MetricCard label="Curricula" value={yearCurricula.length} tone="coral" />
      </section>
      <section className="content-grid two-columns">
        <Panel title="Teacher workload">
          <DataTable columns={[{ key: 'teacherName', label: 'Teacher' }, { key: 'assignments', label: 'Assignments' }]} rows={teacherLoads} />
        </Panel>
        <Panel title="Subject distribution">
          <DataTable columns={[{ key: 'subjectName', label: 'Subject' }, { key: 'assignments', label: 'Assignments' }]} rows={subjectDistribution} />
        </Panel>
      </section>
    </div>
  );
}

function SettingsPage({ data, settings, saveSettings }) {
  const [draft, setDraft] = useState({
    ...settings,
    academicYearId: validAcademicYearId(data.academicYears, settings.academicYearId),
    workingDayIds: validIds(settings.workingDayIds || [], data.workingDays),
  });
  const [message, setMessage] = useState('');

  const save = (event) => {
    event.preventDefault();
    saveSettings(draft);
    setMessage('Settings saved.');
  };

  return (
    <div className="page-stack">
      {message && <Alert type="success" title="Saved" message={message} />}
      <Panel title="Generation defaults">
        <form className="form-grid" onSubmit={save}>
          <SelectField label="Scheduler Policy" value={draft.schedulerPolicy} onChange={(value) => setDraft({ ...draft, schedulerPolicy: value })} options={POLICY_OPTIONS} />
          <SelectField label="Academic Year" value={draft.academicYearId || selectedAcademicYear(data.academicYears)?.id || ''} onChange={(value) => setDraft({ ...draft, academicYearId: Number(value) })} options={data.academicYears.map((item) => ({ value: item.id, label: item.yearName }))} />
          <CheckGroup label="Working Days" items={data.workingDays} selected={draft.workingDayIds || []} onToggle={(id) => setDraft({ ...draft, workingDayIds: toggle(draft.workingDayIds || [], id) })} getLabel={(item) => item.dayName} />
          <div className="form-actions">
            <button className="primary-button" type="submit">Save Settings</button>
          </div>
        </form>
      </Panel>
    </div>
  );
}

function SchedulerReportPanel({ report }) {
  return (
    <Panel title="Generation report">
      <section className="metric-grid compact">
        <MetricCard label="Status" value={report.status} tone={report.status === 'COMPLETED' ? 'green' : 'coral'} />
        <MetricCard label="Classes" value={report.classesProcessed || 0} tone="teal" />
        <MetricCard label="Sections" value={report.sectionsProcessed || 0} tone="amber" />
        <MetricCard label="Assignments" value={report.assignmentsGenerated || 0} tone="blue" />
        <MetricCard label="Failed Sections" value={report.failedSections || 0} tone="coral" />
        <MetricCard label="Duration" value={`${report.executionDurationMillis || 0} ms`} tone="gray" />
      </section>
      <div className="report-list">
        <ReportLine label="Session" value={report.sessionId || '-'} />
      </div>
      {report.failedSectionLabels?.length > 0 && (
        <IssueList title="Failed sections" items={report.failedSectionLabels.map((message) => ({ message }))} type="error" />
      )}
      {report.warnings?.length > 0 && (
        <IssueList title="Warnings" items={report.warnings.map((message) => ({ message }))} type="warning" />
      )}
    </Panel>
  );
}

function FeasibilityReportPanel({ report }) {
  return (
    <Panel title="Feasibility result">
      <div className="status-row">
        <StatusBadge status={report.feasible ? 'FEASIBLE' : 'NOT FEASIBLE'} />
        <span>{report.summary?.passedChecks || 0} of {report.summary?.totalChecks || 0} checks passed</span>
      </div>
      {report.errors?.length > 0 && <IssueList title="Errors" items={report.errors} type="error" />}
      {report.warnings?.length > 0 && <IssueList title="Warnings" items={report.warnings} type="warning" />}
      {report.recommendations?.length > 0 && (
        <IssueList title="Recommendations" items={report.recommendations.map((message) => ({ message }))} type="info" />
      )}
      {!report.errors?.length && !report.warnings?.length && <EmptyState title="No issues found" />}
    </Panel>
  );
}

function TimetableGrid({ assignments, days, periods, mode }) {
  const teachingPeriods = periods
    .filter((period) => !period.breakPeriod)
    .sort((a, b) => (a.periodNumber || 0) - (b.periodNumber || 0));
  const workingDays = days.filter((day) => day.working !== false);
  const bySlot = new Map();
  assignments.forEach((assignment) => {
    bySlot.set(`${assignment.workingDay?.id}-${assignment.period?.id}`, assignment);
  });

  if (!assignments.length) {
    return <Panel title="Timetable"><EmptyState title="No assignments match the selected view" /></Panel>;
  }

  return (
    <Panel title="Timetable">
      <div className="timetable-grid" style={{ gridTemplateColumns: `140px repeat(${teachingPeriods.length}, minmax(130px, 1fr))` }}>
        <div className="grid-head">Day</div>
        {teachingPeriods.map((period) => <div className="grid-head" key={period.id}>P{period.periodNumber}</div>)}
        {workingDays.map((day) => (
          <React.Fragment key={day.id}>
            <div className="grid-day">{day.dayName}</div>
            {teachingPeriods.map((period) => {
              const assignment = bySlot.get(`${day.id}-${period.id}`);
              return (
                <div className="grid-cell" key={`${day.id}-${period.id}`}>
                  {assignment ? (
                    <>
                      <strong>{assignment.subject?.subjectName}</strong>
                      <span>{mode === 'teacher' ? classSectionLabel(assignment.section) : assignment.teacher?.teacherName}</span>
                    </>
                  ) : (
                    <span className="muted">Free</span>
                  )}
                </div>
              );
            })}
          </React.Fragment>
        ))}
      </div>
    </Panel>
  );
}

function MetricCard({ label, value, tone = 'gray' }) {
  return (
    <div className={`metric-card ${tone}`}>
      <span>{label}</span>
      <strong>{value ?? '-'}</strong>
    </div>
  );
}

function Panel({ title, children }) {
  return (
    <section className="panel">
      <div className="panel-header">
        <h2>{title}</h2>
      </div>
      {children}
    </section>
  );
}

function DataTable({ columns, rows }) {
  const [sortKey, setSortKey] = useState(columns[0]?.key);
  const [direction, setDirection] = useState('asc');
  const sorted = useMemo(() => {
    const next = [...(rows || [])];
    next.sort((a, b) => {
      const left = a[sortKey] ?? '';
      const right = b[sortKey] ?? '';
      const result = String(left).localeCompare(String(right), undefined, { numeric: true });
      return direction === 'asc' ? result : -result;
    });
    return next;
  }, [rows, sortKey, direction]);

  if (!rows?.length) {
    return <EmptyState title="No records available" />;
  }

  return (
    <div className="table-wrap">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.key}>
                <button
                  type="button"
                  onClick={() => {
                    setDirection(sortKey === column.key && direction === 'asc' ? 'desc' : 'asc');
                    setSortKey(column.key);
                  }}
                >
                  {column.label}
                </button>
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {sorted.map((row, index) => (
            <tr key={row.id || index}>
              {columns.map((column) => (
                <td key={column.key}>{column.render ? column.render(row) : row[column.key] ?? '-'}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function RowActions({ onEdit, onDelete }) {
  return (
    <div className="row-actions">
      <button className="small-button" type="button" onClick={onEdit}>Edit</button>
      <button className="small-button danger" type="button" onClick={onDelete}>Delete</button>
    </div>
  );
}

function SelectField({ label, value, onChange, options }) {
  return (
    <label className="field">
      <span>{label}</span>
      <select value={value || ''} onChange={(event) => onChange(event.target.value)}>
        {options.map((option) => (
          <option key={option.value} value={option.value}>{option.label}</option>
        ))}
      </select>
    </label>
  );
}

function TextField({ label, value, onChange }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input value={value || ''} onChange={(event) => onChange(event.target.value)} required />
    </label>
  );
}

function NumberField({ label, value, onChange }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type="number" min="0" value={value ?? 0} onChange={(event) => onChange(Number(event.target.value))} required />
    </label>
  );
}

function ToggleField({ label, checked, onChange }) {
  return (
    <label className="toggle-field">
      <input type="checkbox" checked={Boolean(checked)} onChange={(event) => onChange(event.target.checked)} />
      <span>{label}</span>
    </label>
  );
}

function CheckGroup({ label, items, selected, onToggle, getLabel }) {
  return (
    <fieldset className="check-group">
      <legend>{label}</legend>
      <div className="check-list">
        {items.map((item) => (
          <label key={item.id} className={`check-pill ${selected.includes(item.id) ? 'checked' : ''}`}>
            <input type="checkbox" checked={selected.includes(item.id)} onChange={() => onToggle(item.id)} />
            <span>{getLabel(item)}</span>
          </label>
        ))}
      </div>
    </fieldset>
  );
}

function SegmentedControl({ value, onChange, options }) {
  return (
    <div className="segmented">
      {options.map((option) => (
        <button key={option.value} className={value === option.value ? 'active' : ''} type="button" onClick={() => onChange(option.value)}>
          {option.label}
        </button>
      ))}
    </div>
  );
}

function StatusBadge({ status }) {
  const normalized = String(status || 'UNKNOWN').toLowerCase().replaceAll('_', '-');
  return <span className={`status-badge ${normalized}`}>{status}</span>;
}

function Alert({ type, title, message }) {
  return (
    <div className={`alert ${type}`}>
      <strong>{title}</strong>
      <span>{message}</span>
    </div>
  );
}

function IssueList({ title, items, type }) {
  return (
    <div className={`issue-list ${type}`}>
      <h3>{title}</h3>
      {items.map((item, index) => (
        <div className="issue-item" key={`${item.checkName || title}-${index}`}>
          <strong>{item.checkName || item.severity || type}</strong>
          <span>{item.message || item}</span>
          {item.recommendation && <em>{item.recommendation}</em>}
        </div>
      ))}
    </div>
  );
}

function EmptyState({ title }) {
  return <div className="empty-state">{title}</div>;
}

function LoadingPanel({ compact = false }) {
  return <div className={`loading-panel ${compact ? 'compact' : ''}`}><span /> Loading</div>;
}

function ReportLine({ label, value }) {
  return (
    <div className="report-line">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function TabBar({ tabs, active, onChange }) {
  return (
    <div className="tab-bar">
      {tabs.map((tab) => (
        <button key={tab.id} className={active === tab.id ? 'active' : ''} type="button" onClick={() => onChange(tab.id)}>
          {tab.label}
        </button>
      ))}
    </div>
  );
}

function currentYearLabel(academicYears) {
  const current = selectedAcademicYear(academicYears);
  return current?.yearName || 'Academic year not selected';
}

function selectedAcademicYear(academicYears, requestedId) {
  if (!academicYears?.length) return null;
  if (requestedId) {
    const requested = academicYears.find((year) => year.id === Number(requestedId));
    if (requested) return requested;
  }
  return academicYears.find((year) => year.current === true || year.isCurrent === true)
    || academicYears[0];
}

function validAcademicYearId(academicYears, requestedId) {
  return selectedAcademicYear(academicYears, requestedId)?.id || '';
}

function classesForAcademicYear(data, academicYearId) {
  const yearId = Number(academicYearId);
  if (!yearId) return [];

  const byId = new Map();
  data.sections
    .filter((section) => entityAcademicYearId(section) === yearId)
    .forEach((section) => {
      if (section.classMaster?.id) {
        byId.set(section.classMaster.id, section.classMaster);
      }
    });

  data.curricula
    .filter((curriculum) => entityAcademicYearId(curriculum) === yearId)
    .forEach((curriculum) => {
      if (curriculum.classMaster?.id) {
        byId.set(curriculum.classMaster.id, curriculum.classMaster);
      }
    });

  return Array.from(byId.values())
    .sort((a, b) => String(a.className || '').localeCompare(String(b.className || ''), undefined, { numeric: true }));
}

function sectionsForAcademicYear(sections, academicYearId) {
  const yearId = Number(academicYearId);
  return sections
    .filter((section) => !yearId || entityAcademicYearId(section) === yearId)
    .sort((a, b) => {
      const classCompare = String(a.classMaster?.className || '').localeCompare(String(b.classMaster?.className || ''), undefined, { numeric: true });
      if (classCompare !== 0) return classCompare;
      return String(a.sectionName || '').localeCompare(String(b.sectionName || ''), undefined, { numeric: true });
    });
}

function curriculaForAcademicYear(curricula, academicYearId) {
  const yearId = Number(academicYearId);
  return curricula.filter((curriculum) => !yearId || entityAcademicYearId(curriculum) === yearId);
}

function assignmentsForAcademicYear(assignments, academicYearId) {
  const yearId = Number(academicYearId);
  return assignments.filter((assignment) => !yearId || entityAcademicYearId(assignment.section) === yearId);
}

function entityAcademicYearId(entity) {
  return entity?.academicYear?.id
    || entity?.academicYearId
    || entity?.section?.academicYear?.id
    || entity?.section?.academicYearId
    || null;
}

function validIds(ids, entities) {
  const valid = new Set(entities.map((entity) => entity.id));
  return (ids || []).map(Number).filter((id) => valid.has(id));
}

function formatDateTime(value) {
  if (!value) return 'Not available';
  return new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
}

function toggle(values, id) {
  return values.includes(id) ? values.filter((item) => item !== id) : [...values, id].sort((a, b) => a - b);
}

function normalizeIds(form) {
  const normalized = { ...form };
  ['academicYearId', 'classId', 'subjectId', 'categoryId'].forEach((key) => {
    if (normalized[key] !== '' && normalized[key] !== null && normalized[key] !== undefined) {
      normalized[key] = Number(normalized[key]);
    }
  });
  return normalized;
}

export default App;
