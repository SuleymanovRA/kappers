create table IF NOT EXISTS kapper_info
(
	id serial not null,
	u_id integer not null
		constraint kapper_info_users_user_id_fk
		references users
		on delete cascade,
	tokens integer,
	bets integer,
	success_bets integer,
	fail_bets integer,
	blocked_tokens integer
)
;

create unique index kapper_info_id_uindex
	on kapper_info (id)
;

create unique index kapper_info_user_id_uindex
	on kapper_info (u_id)
;

