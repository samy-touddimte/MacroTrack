INSERT INTO weight_entries (user_id, date, weight_kg, created_at)
SELECT id, CAST(created_at AS date), initial_weight, created_at
FROM users
WHERE initial_weight IS NOT NULL;

ALTER TABLE users DROP COLUMN initial_weight;
