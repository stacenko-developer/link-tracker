create table tr_link(
    id bigint generated always as identity,
    url text unique not null,
    last_updated_at bigint not null,
    last_tracked_at bigint,
    primary key (id)
);

comment on table tr_link is 'Ссылка';
comment on column tr_link.id is 'Идентификатор ссылки';
comment on column tr_link.url is 'URL-адрес ресурса';
comment on column tr_link.last_updated_at is 'Время последнего обновления ресурса (в формате Unix Timestamp)';
comment on column tr_link.last_tracked_at is 'Время последнего отслеживания ресурса (в формате Unix Timestamp)';

create table tr_chat(
    id bigint,
    primary key (id)
);

comment on table tr_chat is 'Чат';
comment on column tr_chat.id is 'Идентификатор чата';

create table tr_tag(
    id bigint generated always as identity,
    name text unique not null,
    primary key (id)
);

comment on table tr_tag is 'Тэг';
comment on column tr_tag.id is 'Идентификатор тэга';
comment on column tr_tag.name is 'Название тэга';

create table tr_filter(
    id bigint generated always as identity,
    key text not null,
    value text not null,
    constraint unique_filter_key_value unique (key, value),
    primary key (id)
);

comment on table tr_filter is 'Фильтр';
comment on column tr_filter.id is 'Идентификатор фильтра';
comment on column tr_filter.key is 'Ключ фильтра';
comment on column tr_filter.value is 'Значение фильтра';

create table tr_chat_link_filter(
    chat_id bigint references tr_chat (id) on delete cascade,
    link_id bigint references tr_link (id) on delete cascade,
    filter_id bigint references tr_filter (id) on delete cascade,
    primary key (chat_id, link_id, filter_id)
);

comment on table tr_chat_link_filter is 'Связь чата, ссылки и фильтра';
comment on column tr_chat_link_filter.chat_id is 'Идентификатор чата';
comment on column tr_chat_link_filter.link_id is 'Идентификатор ссылки';
comment on column tr_chat_link_filter.filter_id is 'Идентификатор фильтра';

create table tr_chat_link_tag(
    chat_id bigint references tr_chat (id) on delete cascade,
    link_id bigint references tr_link (id) on delete cascade,
    tag_id bigint references tr_tag (id) on delete cascade,
    primary key (chat_id, link_id, tag_id)
);

comment on table tr_chat_link_tag is 'Связь чата, ссылки и тэга';
comment on column tr_chat_link_tag.chat_id is 'Идентификатор чата';
comment on column tr_chat_link_tag.link_id is 'Идентификатор ссылки';
comment on column tr_chat_link_tag.tag_id is 'Идентификатор тэга';

create table tr_chat_link(
    chat_id bigint references tr_chat (id) on delete cascade,
    link_id bigint references tr_link (id) on delete cascade,
    primary key (chat_id, link_id)
);

comment on table tr_chat_link is 'Связь чата и ссылки';
comment on column tr_chat_link.chat_id is 'Идентификатор чата';
comment on column tr_chat_link.link_id is 'Идентификатор ссылки';

create index idx_link_last_tracked_at on tr_link(last_tracked_at);
create index idx_chat_link_link_id on tr_chat_link(link_id);
create index idx_chat_link_filter_composite on tr_chat_link_filter(link_id, chat_id);
create index idx_chat_link_tag_composite on tr_chat_link_tag(link_id, chat_id);

