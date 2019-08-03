create table league_bridge
(
  id              serial  not null
    constraint league_bridge_pkey
    primary key,
  rapid_league_id integer not null,
  leon_league_id  bigint  not null
);

alter table league_bridge
  owner to postgres;

create unique index league_bridge_id_uindex
  on league_bridge (id);
