--alter table Ad drop column year;
alter table Ad add column year integer;
alter table Ad add column url varchar(255);
--alter table User drop column wantNotifications;
alter table User add column wantNotifications boolean;