create table IF NOT EXISTS audit_log
(
    id            uuid        not null primary key,
    timestamp     timestamptz not null default now(),
    input         text        not null default '',
    output        text        not null default '',
    status_code   text        not null default '',
    success       boolean     not null default false,
    version       bigint      not null default 0,
    created       timestamptz not null default now(),
    last_modified timestamptz not null default now()
)
