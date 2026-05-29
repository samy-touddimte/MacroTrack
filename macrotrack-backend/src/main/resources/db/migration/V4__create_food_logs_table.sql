create table food_logs (
    id bigserial primary key,
    user_id bigint references users(id) on delete cascade,
    date date not null,
    food_name varchar(255),
    calories float not null,
    protein_g float,
    carbs_g float,
    fat_g float,
    created_at timestamp default now()
);
