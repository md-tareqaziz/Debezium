CREATE TABLE public.user
(
    id searial NOT NULL,
    name character varying,
    type integer,
    photo bytea,
    CONSTRAINT user_pkey PRIMARY KEY (id)
);
ALTER SYSTEM SET wal_level = logical;
show wal_level;
alter table public.user REPLICA IDENTITY FULL;

insert into public.user (name, type) values ('tareq',12);
update public."user" set name='saba' where id=1;