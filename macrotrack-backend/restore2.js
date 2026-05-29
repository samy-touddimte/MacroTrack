const fs = require('fs');
const path = require('path');

const historyDir = '/home/samy/.vscode-server/data/User/History';
const cutoffTime = new Date('2026-05-16T10:20:00.000Z').getTime();

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
      // Find the LATEST entry before the cutoff time
      let selectedEntry = null;
      for (let i = entriesData.entries.length - 1; i >= 0; i--) {
        const entry = entriesData.entries[i];
        if (entry.timestamp < cutoffTime) {
          selectedEntry = entry;
          break;
        }
      }
      
      if (selectedEntry) {
        const originalContentPath = path.join(folderPath, selectedEntry.id);
        const currentContentPath = path.join('/home/samy/macrotrack', target);
        
        console.log(`Restoring ${target} to version from ${new Date(selectedEntry.timestamp).toISOString()}...`);
        fs.copyFileSync(originalContentPath, currentContentPath);
      } else {
        console.log(`No valid entry found for ${target} before cutoff.`);
      }
    }
  }
}

console.log('Restoration attempt finished.');
