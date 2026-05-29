const fs = require('fs');
const path = require('path');

const historyDir = '/home/samy/.vscode-server/data/User/History';
const targetFiles = [
  'macrotrack-frontend/src/api/authApi.ts',
  'macrotrack-frontend/src/components/BottomNav/BottomNav.tsx',
  'macrotrack-frontend/src/hooks/useAnalyticsByPeriod.ts',
  'macrotrack-frontend/src/pages/Profil/ProfilPage.tsx',
  'macrotrack-frontend/src/pages/AnalyticsPage.tsx',
  'macrotrack-frontend/src/pages/DashboardPage.tsx',
  'macrotrack-frontend/src/pages/Objectif/ObjectifPage.tsx',
  'macrotrack-frontend/src/components/CalendarLog/CalendarLog.tsx',
  'macrotrack-backend/src/main/java/com/macrotrack/service/AnalyticsService.java',
  'macrotrack-backend/src/main/java/com/macrotrack/service/TdeeAlgorithmService.java',
  'macrotrack-frontend/src/types/index.ts'
];

const folders = fs.readdirSync(historyDir);

for (const folder of folders) {
  const folderPath = path.join(historyDir, folder);
  if (!fs.statSync(folderPath).isDirectory()) continue;
  
  const entriesPath = path.join(folderPath, 'entries.json');
  if (!fs.existsSync(entriesPath)) continue;

  const entriesData = JSON.parse(fs.readFileSync(entriesPath, 'utf8'));
  const fileId = entriesData.resource;

  for (const target of targetFiles) {
    if (fileId && fileId.endsWith(target)) {
      // Find the oldest entry (the original one before my changes)
      const oldestEntry = entriesData.entries[0];
      if (oldestEntry) {
        const originalContentPath = path.join(folderPath, oldestEntry.id);
        const currentContentPath = path.join('/home/samy/macrotrack', target);
        
        console.log(`Restoring ${target}...`);
        fs.copyFileSync(originalContentPath, currentContentPath);
      }
    }
  }
}

console.log('Restoration attempt finished.');
