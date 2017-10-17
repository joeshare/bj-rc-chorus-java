ALTER TABLE chorus.table_info ADD atlas_guid varchar(50) NULL COMMENT 'Atlas Entity Id';
ALTER TABLE chorus.column_info ADD atlas_guid varchar(50) NULL COMMENT 'Atlas Entity Id';
-- migrate apply_detail_v2
insert into apply_detail_v2(apply_detail_id,apply_form_id,table_info_id, column_info_id,status_code)select * from
(select t0.apply_detail_id,t0.apply_form_id,t1.atlas_guid as table_info_id, t2.atlas_guid as column_info_id , t0.status_code
from apply_detail t0, table_info t1, column_info t2
where t0.table_info_id = t1.table_info_id and t0.column_info_id=t2.column_info_id
and t1.atlas_guid is not null
and t2.atlas_guid is not null
) as tt;

-- migrate apply_form_v2
insert apply_form_v2 (
`apply_form_id`, `project_id`, `table_info_id`,`table_name`, `end_date`, `apply_user_id`, `apply_time`, `reason`, `deal_user_id`,
  `deal_instruction`,  `deal_time`,  `status_code`,  `apply_user_name`
  )select * from (
  	select t0.apply_form_id,t1.project_id,t1.atlas_guid as table_info_id, t1.table_name, t0.end_date,t0.apply_user_id,
  	t0.apply_time,t0.reason, t0.deal_user_id,t0.deal_instruction,t0.deal_time,t0.status_code,t0.apply_user_name
  	from apply_form t0,table_info t1
  	where t0.table_info_id = t1.table_info_id
  	and t1.atlas_guid is not null
  )as tt
  ;

-- migarte table_authority_v2
insert into table_authority_v2(table_authority_id,table_info_id,column_info_id,user_id,end_date,project_id,table_name,column_name)
  select * from (
  	select t0.table_authority_id,t1.atlas_guid as table_info_id, t2.atlas_guid as column_info_id, t0.user_id, t0.end_date,t1.project_id,t1.table_name,t2.column_name
  	from table_authority t0, table_info t1, column_info t2
  	where t0.table_info_id = t1.table_info_id and t0.column_info_id=t2.column_info_id
  	and t1.atlas_guid is not null
	and t2.atlas_guid is not null
  ) as tt;

-- migrate table_monitor_v2
insert into table_monitor_v2(id,project_id,table_info_id,monitor_date,`rows`,storage_size,table_name)
  select * from (
  	select t0.id,t0.project_id,t1.atlas_guid as table_info_id, t0.monitor_date, t0.`rows`, t0.storage_size, t1.table_name
  	from table_monitor t0, table_info t1
  	where t0.table_info_id = t1.table_info_id
  	and t1.atlas_guid is not null
  ) as tt;