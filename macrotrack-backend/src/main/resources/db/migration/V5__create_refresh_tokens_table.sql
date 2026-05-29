create table refresh_tokens (
    id bigserial primary key,
    token varchar(1000) not null,
    user_id bigint not null references users(id) on delete cascade,
    expiry_date timestamp not null,
    revoked boolean not null default false
);
