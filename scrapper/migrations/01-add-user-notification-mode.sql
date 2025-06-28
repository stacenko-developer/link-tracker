alter table tr_chat add column notification_mode text not null default 'IMMEDIATE';

comment on column tr_chat.notification_mode is 'Режим отправки уведомлений';
