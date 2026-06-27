create table if not exists riders (
    id bigserial primary key,
    name varchar(100) not null,
    phone_number varchar(20) not null,
    city varchar(80) not null,
    vehicle_type varchar(50) not null,
    rider_status varchar(50) not null default 'OFFLINE',
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

alter table riders
    add column if not exists phone_number varchar(20);

alter table riders
    add column if not exists vehicle_type varchar(50);

alter table riders
    add column if not exists rider_status varchar(50);

alter table riders
    add column if not exists created_at timestamp with time zone;

alter table riders
    add column if not exists updated_at timestamp with time zone;

update riders
set rider_status = 'OFFLINE'
where rider_status is null;

update riders
set created_at = now()
where created_at is null;

update riders
set updated_at = now()
where updated_at is null;

alter table riders
    alter column phone_number set not null,
    alter column vehicle_type set not null,
    alter column rider_status set default 'OFFLINE',
    alter column rider_status set not null,
    alter column created_at set not null,
    alter column updated_at set not null;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'uk_riders_phone_number'
    ) then
        alter table riders
            add constraint uk_riders_phone_number unique (phone_number);
    end if;
end
$$;

