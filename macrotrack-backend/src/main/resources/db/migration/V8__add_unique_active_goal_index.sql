CREATE UNIQUE INDEX idx_unique_active_goal 
ON goals (user_id) 
WHERE is_active = true;
