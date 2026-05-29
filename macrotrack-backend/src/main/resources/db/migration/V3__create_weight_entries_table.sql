create table weight_entries (
    id bigserial primary key,
    user_id bigint references users(id) on delete cascade,
    date date not null,
    weight_kg float not null,
    created_at timestamp default now(),
    unique(user_id, date)
);
