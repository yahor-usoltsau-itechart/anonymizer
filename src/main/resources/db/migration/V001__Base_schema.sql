create type replacement_type as enum ('DOMAIN', 'EMAIL', 'ID');

create table replacements
(
    type   replacement_type not null,
    source text             not null,
    target text             not null,
    primary key (type, source)
);
