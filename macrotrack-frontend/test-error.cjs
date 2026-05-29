const puppeteer = require('puppeteer');
(async () => {
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  page.on('console', msg => console.log('BROWSER CONSOLE:', msg.text()));
  page.on('pageerror', err => console.error('BROWSER ERROR:', err.toString()));
  await page.goto('http://localhost:5173/dashboard');
  await page.waitForTimeout(3000);
  try {
    await page.click('button.w-12.h-12.rounded-full.bg-white');
    await page.waitForTimeout(1000);
    await page.click('text=MODIFIER LA JOURNÉE');
    await page.waitForTimeout(2000);
  } catch(e) {
    console.log("Could not click:", e.message);
  }
  await browser.close();
})();
