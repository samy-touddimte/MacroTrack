async function test() {
  try {
        console.log("REGISTERING...");
        const regRes = await fetch('http://localhost:8080/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: 'samytest3@example.com', username: 'samytest3', password: 'password', heightCm: 180, birthDate: '1990-01-01', sex: 'Homme', activityLevel: 'SEDENTARY' })
        });
        const text = await regRes.text();
        console.log("REG STATUS:", regRes.status);
        console.log("REG DATA:", text);
        
        const token = JSON.parse(text).accessToken;
        if (!token) return;

        console.log("TOKEN:", token);

        const dashRes = await fetch('http://localhost:8080/api/analytics/dashboard', {
          headers: { Authorization: `Bearer ${token}` }
        });
        console.log("DASHBOARD STATUS:", dashRes.status);
        console.log(await dashRes.text());

        const projRes = await fetch('http://localhost:8080/api/analytics/projection', {
          headers: { Authorization: `Bearer ${token}` }
        });
        console.log("PROJECTION STATUS:", projRes.status);
        console.log(await projRes.text());
  } catch (e) {
    console.error("ERROR:", e);
  }
}

test();
