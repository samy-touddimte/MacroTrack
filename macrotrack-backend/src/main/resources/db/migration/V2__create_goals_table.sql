create table goals (
    id bigserial primary key,
    user_id bigint references users(id) on delete cascade,
    target_weight_kg float not null,
    weekly_loss_rate float not null,
    start_date date not null,
    is_active boolean default true
);
