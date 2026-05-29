const fs = require('fs');

const mappings = [
  {
    toolFile: '/home/samy/.gemini/tmp/macrotrack/tool-outputs/session-30484f9e-38f4-4f8b-866d-4df9852de4a2/read_file_read_file_1778927302470_1_bgho4.txt',
    target: '/home/samy/macrotrack/macrotrack-frontend/src/pages/DashboardPage.tsx'
  },
  {
    toolFile: '/home/samy/.gemini/tmp/macrotrack/tool-outputs/session-30484f9e-38f4-4f8b-866d-4df9852de4a2/read_file_read_file_1778927302693_2_n3xert.txt',
    target: '/home/samy/macrotrack/macrotrack-frontend/src/pages/AnalyticsPage.tsx'
  },
  {
    toolFile: '/home/samy/.gemini/tmp/macrotrack/tool-outputs/session-30484f9e-38f4-4f8b-866d-4df9852de4a2/read_file_read_file_1778927302811_3_61dwo.txt',
    target: '/home/samy/macrotrack/macrotrack-frontend/src/pages/Objectif/ObjectifPage.tsx'
  },
  {
    toolFile: '/home/samy/.gemini/tmp/macrotrack/tool-outputs/session-30484f9e-38f4-4f8b-866d-4df9852de4a2/read_file_read_file_1778927302811_4_vnk9r.txt',
    target: '/home/samy/macrotrack/macrotrack-frontend/src/hooks/useAnalyticsByPeriod.ts'
  },
  {
    toolFile: '/home/samy/.gemini/tmp/macrotrack/tool-outputs/session-30484f9e-38f4-4f8b-866d-4df9852de4a2/read_file_read_file_1778926715934_4_wh79ut.txt',
    target: '/home/samy/macrotrack/macrotrack-backend/src/main/java/com/macrotrack/service/TdeeAlgorithmService.java'
  },
  {
    toolFile: '/home/samy/.gemini/tmp/macrotrack/tool-outputs/session-30484f9e-38f4-4f8b-866d-4df9852de4a2/read_file_read_file_1778927651028_1_i0yzbe.txt',
    target: '/home/samy/macrotrack/macrotrack-backend/src/main/java/com/macrotrack/service/AnalyticsService.java'
  }
];

mappings.forEach(m => {
  if (fs.existsSync(m.toolFile)) {
    const raw = fs.readFileSync(m.toolFile, 'utf8');
    const parsed = JSON.parse(raw);
    let content = parsed.output;
    // read_file prepends truncation notice if file > 2000 lines. But none of these are > 2000 lines, so parsed.output is the exact file.
    // wait, if they are exactly the file, I can just write it.
    if (content.startsWith('\nIMPORTANT: The file content has been truncated.')) {
      console.log(`WARNING: Tool output was truncated for ${m.target}`);
      // This means I can't fully restore from here.
    } else {
      fs.writeFileSync(m.target, content);
      console.log(`Restored ${m.target} from tool output.`);
    }
  } else {
    console.log(`Could not find tool file ${m.toolFile}`);
  }
});

// For ProfilPage, BottomNav, authApi, CalendarLog, I only used replace.
// I will manually revert them since they were 1-line changes.
