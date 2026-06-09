Timetable Frontend

This is a minimal React frontend to interact with the Timetable Engine backend.

Prerequisites
- Node.js (16+ recommended)
- npm

Quick start
1. Install dependencies:

```bash
cd frontend
npm install
```

2. Start the dev server:

```bash
npm start
```

The app will open at http://localhost:3000 and proxy requests to http://localhost:8080.

Endpoints used
- GET /api/teachers
- GET /api/subjects
- GET /api/sections
- GET /api/working-days
- GET /api/periods
- POST /api/timetable/allocate
- GET /api/timetable/assignments


Notes
- Backend must be running on http://localhost:8080.
- CORS is already configured on the backend for http://localhost:3000.
