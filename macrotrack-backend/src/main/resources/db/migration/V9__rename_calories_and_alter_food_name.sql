ALTER TABLE food_logs RENAME COLUMN calories TO calories_kcal;
ALTER TABLE food_logs ALTER COLUMN food_name TYPE varchar(200);
