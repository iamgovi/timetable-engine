import React, { useState } from 'react';
import AllocationForm from './components/AllocationForm';
import SingleSectionTimetableGenerator from './components/SingleSectionTimetableGenerator';

function App() {
  const [activeTab, setActiveTab] = useState('generator');

  return (
    <div style={{ padding: 20, fontFamily: 'Arial, sans-serif' }}>
      <h1>Timetable Management System</h1>
      
      {/* Tab Navigation */}
      <div style={styles.tabNav}>
        <button
          onClick={() => setActiveTab('generator')}
          style={{
            ...styles.tabButton,
            borderBottom: activeTab === 'generator' ? '3px solid #007bff' : 'none',
            color: activeTab === 'generator' ? '#007bff' : '#666',
            fontWeight: activeTab === 'generator' ? 'bold' : 'normal',
          }}
        >
          📅 Single Section Generator (Phase 1)
        </button>
        <button
          onClick={() => setActiveTab('allocation')}
          style={{
            ...styles.tabButton,
            borderBottom: activeTab === 'allocation' ? '3px solid #007bff' : 'none',
            color: activeTab === 'allocation' ? '#007bff' : '#666',
            fontWeight: activeTab === 'allocation' ? 'bold' : 'normal',
          }}
        >
          ✏️ Manual Allocation
        </button>
      </div>

      {/* Tab Content */}
      <div style={styles.tabContent}>
        {activeTab === 'generator' && <SingleSectionTimetableGenerator />}
        {activeTab === 'allocation' && <AllocationForm />}
      </div>
    </div>
  );
}

const styles = {
  tabNav: {
    display: 'flex',
    borderBottom: '2px solid #eee',
    marginBottom: '20px',
    gap: '10px',
  },
  tabButton: {
    padding: '12px 20px',
    border: 'none',
    background: 'none',
    cursor: 'pointer',
    fontSize: '16px',
    transition: 'all 0.3s ease',
  },
  tabContent: {
    backgroundColor: '#fafafa',
    padding: '20px',
    borderRadius: '4px',
  },
};

export default App;
