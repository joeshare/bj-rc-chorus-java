use chorus_black_card;

create table tmp_t_bi_crm_cust like t_bi_crm_cust;
alter table tmp_t_bi_crm_cust set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_bi_crm_cust SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_bi_crm_cust_2017-09-05_1504601799907' INTO TABLE tmp_t_bi_crm_cust  partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_bi_crm_cust PARTITION (p_date='2017-09-07') select cust_guid, shop_guid, cust_code, cust_type, cust_name, sex, birth_day, rx_inserttime, rx_updatetime FROM tmp_t_bi_crm_cust;
DROP TABLE tmp_t_bi_crm_cust PURGE;

create table tmp_t_goods like t_goods;
alter table tmp_t_goods set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_goods SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_goods_2017-09-05_1504600801069'  INTO TABLE  tmp_t_goods partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_goods PARTITION (p_date='2017-09-07') select  goods_guid, shop_guid, goods_code, goods_name, use_yn, outer_goods_code, outer_goods_name, tax_rate, sale_pric, brnd_guid, no_accounting_yn  FROM tmp_t_goods;
DROP TABLE tmp_t_goods PURGE;

create table tmp_t_goods_sku like t_goods_sku;
alter table tmp_t_goods_sku set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_goods_sku SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_goods_sku_2017-09-05_1504600876689'  INTO TABLE  tmp_t_goods_sku partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_goods_sku PARTITION (p_date='2017-09-07') select  goods_sku_guid, shop_guid, goods_guid, goods_sku_code, goods_sku_name, sale_pric, outer_goods_sku_code, purc_pric, use_yn, goods_sku_no, outer_goods_sku_name, box_up_num, counter_price, gram_weight  FROM tmp_t_goods_sku;
DROP TABLE tmp_t_goods_sku PURGE;

create table tmp_t_order like t_order;
alter table tmp_t_order set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_order SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_order_2017-09-05_1504596502483'  INTO TABLE  tmp_t_order partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_order PARTITION (p_date='2017-09-07') select  order_guid, order_code, shop_guid, online_shop_guid, order_time, cust_guid, order_type_guid, order_sour_guid, order_stus, allot_type, trad_stus, trad_code, pay_mode_guid, tb_up_stus, flag_guid, tb_flag_code, lgst_guid, lgst_fee, lgst_cost, check_weight, stor_guid, stor_type, sales, sales_guid, cust_addr_guid, cons, cons_tel, cons_mobile, cons_addr, cons_post_code, cons_yn, prov_guid, city_guid, dist_guid, area_coding, tot_goods_qty, tot_weight, order_money, order_disc, serv_fee, paid_fee, paid_date, error_stus, lock_yn, lock_type, lock_pern_guid, lock_time, lock_remark, cancel_yn, cancel_pern_guid, cancel_time, cancel_remark, uncancel_pern_guid, uncancel_time, splt_comb_stus, splt_comb_pern_guid, splt_comb_time, forder_guid, cust_appr_guid, cust_appr_time, cust_appr_cancel_guid, cust_appr_cacel_time, fina_appr_guid, fina_appr_time, fina_appr_cancel_guid, fina_appr_cacel_time, allot_appr_guid, allot_appr_time, lgst_no, lgst_prnt_yn, lgst_prnt_pern_guid, lgst_prnt_time, lgst_prnt_times, ship_prnt_yn, pt_guid, ship_prnt_pern_guid, ship_prnt_time, ship_prnt_times, ship_tot_weight, check_yn, check_time, check_pern_guid, ship_stus, ship_pern_guid, ship_time, ship_type, close_pern_guid, close_time, sour_refund_stus, archive_stus, cust_remark, sales_remark, archives_guid, remark, point_money, ticket_money, unlock_pern_guid, unlock_time, oper_stus, lgst_arrive_yn, order_error_yn, goods_error_yn, fnc_error_yn, lgst_error_yn, stock_error_yn, order_error_remark, goods_error_remark, fnc_error_remark, lgst_error_remark, stock_error_remark, goods_modify_yn, inserted_time, outer_sys_yn, promotion_error_yn, promotion_error_remark, trad_upship_time, automa_time, have_note_yn, rx_updatetime, rx_inserttime, invoice_yn, issued_yn, invoice_title, item_goods_yn, invoice_item, invoice_type_guid, ori_order_money, buyer_payment, promt_stus, promt_time, flgst_mess_time, llgst_mess_time, lgst_mess_yn  FROM tmp_t_order;
DROP TABLE tmp_t_order PURGE;



create table tmp_t_order_goods like t_order_goods;
alter table tmp_t_order_goods set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_order_goods SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_order_goods_2017-09-05_1504600503351'  INTO TABLE  tmp_t_order_goods partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_order_goods PARTITION (p_date='2017-09-07') select  guid, shop_guid, online_shop_guid, order_guid, goods_guid, goods_sku_guid, qty, shiped_qty, stan_unit_pric, disc, fact_unit_pric, goods_score, weight, orig_goods_name, orig_sku_name, sour_goods_code, sour_goods_sku_code, gived_virt_qty, stus, remark, oid, gifts_yn, virt_yn, discount_fee, adjust_fee, sour_outer_goods_code, sour_outer_goods_sku_code, goods_sku_cost, insert_time, rx_updatetime, rx_inserttime, sys_stan_unit_pric, o2o_yn, o2o_ok_qty, activity_id, divide_goods_fee, tid  FROM tmp_t_order_goods;
DROP TABLE tmp_t_order_goods PURGE;

create table tmp_t_refund_addfund like t_refund_addfund;
alter table tmp_t_refund_addfund set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_refund_addfund SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_refund_addfund_2017-09-05_1504599138902'  INTO TABLE  tmp_t_refund_addfund partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_refund_addfund PARTITION (p_date='2017-09-07') select rfnd_addfund_guid, rfnd_addfund_code, comp_guid, shop_guid, sour_type, sour_code_guid, rfnd_addfund_type, pay_mode_guid, cust_guid, cust_account, rfnd_addfund_money, oper_pern_guid, oper_date, duty_pern_guid, appr_pern_guid, appr_date, unappr_pern_guid, unappr_date, cancel_yn, cancel_pern_guid, cancel_date, rfnd_addfund_stus, remark, from_type, trad_code, from_fund_code, over_time, rfnd_addfund_time, rfnd_addfund_pern, rfnd_addfund_reason, reject_pern_guid, reject_pern_time, reject_pern_reason, from_rfnd_addfund_stus, flag_guid, old_rfnd_addfund_stus, asp_guid, good_status, trade_status, good_return_yn, oid, lgst_name, lgst_no, type_guid, account_type, file_location1, file_location2, file_location3, diff_yn, chan_type, trad_create_time, trad_modify_time, timeout, rx_updatetime, rx_inserttime, online_yn, refund_reason_class_guid, after_sale_yn FROM tmp_t_refund_addfund;
DROP TABLE tmp_t_refund_addfund PURGE;

create table tmp_t_refund_addfund_dtls like t_refund_addfund_dtls;
alter table tmp_t_refund_addfund_dtls set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_refund_addfund_dtls SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_refund_addfund_dtls_2017-09-05_1504600600046'  INTO TABLE  tmp_t_refund_addfund_dtls partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_refund_addfund_dtls PARTITION (p_date='2017-09-07') select  guid, shop_guid, rfnd_addfund_guid, goods_sku_guid, qty, stan_unit_pric, fact_unit_pric, sour_outer_goods_name, sour_outer_goods_sku_name, sour_outer_goods_code, sour_outer_goods_sku_code, oid, remark, sour_guid, rx_updatetime, rx_inserttime FROM tmp_t_refund_addfund_dtls;
DROP TABLE tmp_t_refund_addfund_dtls PURGE;

create table tmp_t_rtrn_exch like t_rtrn_exch;
alter table tmp_t_rtrn_exch set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_rtrn_exch SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_rtrn_exch_2017-09-05_1504599089855'  INTO TABLE  tmp_t_rtrn_exch partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_rtrn_exch PARTITION (p_date='2017-09-07') select  rtrn_exch_guid, rtrn_exch_code, comp_guid, shop_guid, online_shop_guid, rtrn_exch_date, plan_fini_date, sour_type, code_guid, rtrn_exch_type, cust_supp_guid, stor_guid, stor_type, out_stor_guid, out_stor_type, stor_loca_guid, oper_pern_guid, oper_time, appr_guid, appr_time, fina_appr_guid, fina_appr_time, lgst_fee, lgst_guid, lgst_no, rtrn_lgst_guid, rtrn_lgst_no, flag_guid, lgst_prnt_yn, lgst_prnt_pern_guid, lgst_prnt_time, lgst_prnt_times, pt_guid, ship_prnt_yn, ship_prnt_pern_guid, ship_prnt_time, ship_prnt_times, ac_pern_guid, ac_time, rtrn_exch_stus, rtrn_yn, rtrn_pern_guid, rtrn_time, exch_yn, exch_pern_guid, exch_time, check_yn, check_time, check_pern_guid, close_pern_guid, close_time, close_type, close_remark, cons, cons_tel, cons_mobile, cons_addr, post_code, area_coding, shipper, shipper_tel, shipper_mobile, shipper_addr, shipper_post_code, remark, return_reason_code, return_reason, options, contact_express_mode, asp_guid, outer_take_yn, type_guid, purc_rtrn_mode, plan_exch_stockin_time, plan_exch_stockout_time, rtrn_next_type, cancel_yn, cancel_pern_guid, cancel_time, cancel_remark, auto_create_yn, rx_updatetime, rx_inserttime, flgst_mess_time, llgst_mess_time, lgst_mess_yn  FROM tmp_t_rtrn_exch;
DROP TABLE tmp_t_rtrn_exch PURGE;

create table tmp_t_rtrnexch_exch like t_rtrnexch_exch;
alter table tmp_t_rtrnexch_exch set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_rtrnexch_exch SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_rtrnexch_exch_2017-09-05_1504600620372'  INTO TABLE  tmp_t_rtrnexch_exch partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_rtrnexch_exch PARTITION (p_date='2017-09-07') select  exch_guid, comp_guid, shop_guid, online_shop_guid, rtrn_exch_guid, goods_guid, goods_sku_guid, qty, stan_unit_pric, disc, fact_unit_pric, exch_score, weight, remark, goods_sku_cost, rx_updatetime, rx_inserttime FROM tmp_t_rtrnexch_exch;
DROP TABLE tmp_t_rtrnexch_exch PURGE;

create table tmp_t_rtrnexch_fact_rtrn like t_rtrnexch_fact_rtrn;
alter table tmp_t_rtrnexch_fact_rtrn set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_rtrnexch_fact_rtrn SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_rtrnexch_fact_rtrn_2017-09-05_1504600631962'  INTO TABLE  tmp_t_rtrnexch_fact_rtrn partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_rtrnexch_fact_rtrn PARTITION (p_date='2017-09-07') select  rtrn_guid,shop_guid,online_shop_guid,rtrn_exch_guid,goods_sku_guid,qty,damage_qty,stan_unit_pric,fact_unit_pric,weight,remark,goods_sku_cost,order_goods_guid,rx_updatetime,rx_inserttime,divide_goods_fee,fact_snbatch_no  FROM tmp_t_rtrnexch_fact_rtrn;
DROP TABLE tmp_t_rtrnexch_fact_rtrn PURGE;

create table tmp_t_stockout like t_stockout;
alter table tmp_t_stockout set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_stockout SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_stockout_2017-09-05_1504598904624'  INTO TABLE  tmp_t_stockout partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_stockout PARTITION (p_date='2017-09-07') select  stockout_guid,stockout_code,comp_guid,shop_guid,sour_code_guid,stockout_type,out_time,stor_guid,stor_loca_guid,oper_pern_guid,oper_time,stockout_stus,trad_stockout_stus,appr_guid,appr_time,cons,cons_tel,cons_mobile,cons_addr,cons_post_code,cons_yn,area_coding,lgst_guid,lgst_no,lgst_prnt_yn,lgst_prnt_pern_guid,lgst_prnt_time,lgst_prnt_times,weight,ship_prnt_yn,pt_guid,ship_prnt_pern_guid,ship_prnt_time,ship_prnt_times,ship_tot_weight,check_yn,check_time,check_pern_guid,cancel_stus,cancel_rqst_pern,cancel_rqst_time,cancel_rqst_remark,cancel_appr_pern,cancel_appr_time,cancel_appr_remark,remark,holding_yn,sales_remark,archive_stus,flag_guid,lock_yn,lock_remark,lock_type,lock_time,lock_rqst_pern,lock_rqst_time,lock_rqst_remark,lock_appr_pern,lock_appr_time,lock_appr_remark,cancel_type,modify_time,outer_take_yn,outer_cancel_take_yn,outer_lock_take_yn,oper_area,orig_lgst_guid,orig_lgst_no,up_lgst_guid,up_lgst_no,package_weight,weight_time,weight_pern_guid,other_type_guid,cancel_new_yn,supp_remark,ac_modified_yn,trad_upship_time,create_pern_guid,create_time,rx_updatetime,rx_inserttime,invoice_guid,outer_take_count,outer_cancel_take_count,outer_lock_take_count FROM tmp_t_stockout;
DROP TABLE tmp_t_stockout PURGE;

create table tmp_t_stockout_dtls like t_stockout_dtls;
alter table tmp_t_stockout_dtls set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_stockout_dtls SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_stockout_dtls_2017-09-05_1504600658659'  INTO TABLE  tmp_t_stockout_dtls partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_stockout_dtls PARTITION (p_date='2017-09-07') select  stockout_dtls_guid,comp_guid,shop_guid,stockout_guid,goods_guid,goods_sku_guid,qty,scanned_qty,stan_unit_pric,disc,fact_unit_pric,remark,               order_dtls_guid,lock_yn,lock_remark,goods_sku_cost,rx_updatetime,rx_inserttime,sys_stan_unit_pric,exch_card_no,sn  FROM tmp_t_stockout_dtls;
DROP TABLE tmp_t_stockout_dtls PURGE;

create table tmp_t_stockout_request like t_stockout_request;
alter table tmp_t_stockout_request set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_stockout_request SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_stockout_request_2017-09-05_1504596875299'  INTO TABLE  tmp_t_stockout_request partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_stockout_request PARTITION (p_date='2017-09-07') select  stockout_request_guid,stockout_request_code,comp_guid,shop_guid,online_shop_guid,order_type_guid,sour_code_guid,sour_code,paid_date,trad_code,          trad_sour_guid,stockout_type,stor_guid,stor_type,stor_loca_guid,oper_pern_guid,oper_time,stockout_request_stus,lgst_guid,cancel_stus,                   cancel_rqst_pern,cancel_rqst_time,cancel_rqst_remark,cancel_appr_pern,cancel_appr_time,cancel_appr_remark,cons,cons_tel,cons_mobile,cons_addr,          cons_post_code,area_coding,paid_fee,order_money,cust_guid,remark,cust_remark,sour_remark,sales_remark,archive_stus,rx_updatetime,rx_inserttime,         invoice_guid  FROM tmp_t_stockout_request;
DROP TABLE tmp_t_stockout_request PURGE;

create table tmp_t_top_interface_promotiondetail like t_top_interface_promotiondetail;
alter table tmp_t_top_interface_promotiondetail set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_top_interface_promotiondetail SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_top_interface_promotiondetail_2017-09-05_1504600714586'  INTO TABLE  tmp_t_top_interface_promotiondetail partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_top_interface_promotiondetail PARTITION (p_date='2017-09-07') select  top_promotiondetail_guid,nick,id,tid,promotion_name,discount_fee,gift_item_name,rx_inserttime,rx_updatetime,gift_item_id,gift_item_num,promotion_desc,promotion_id,activity_id  FROM tmp_t_top_interface_promotiondetail;
DROP TABLE tmp_t_top_interface_promotiondetail PURGE;

create table tmp_t_top_interface_tradeaccountdetail like t_top_interface_tradeaccountdetail;
alter table tmp_t_top_interface_tradeaccountdetail set tblproperties ("skip.header.line.count"="1");
ALTER TABLE tmp_t_top_interface_tradeaccountdetail SET SERDEPROPERTIES ('field.delim' = ',');
LOAD DATA INPATH '/chorus/project/black_card/hdfs/erp/t_top_interface_tradeaccountdetail_2017-09-05_1504595614946'  INTO TABLE  tmp_t_top_interface_tradeaccountdetail partition (p_date='2017-09-07');
INSERT OVERWRITE TABLE t_top_interface_tradeaccountdetail PARTITION (p_date='2017-09-07') select  top_tradeaccountdetail_guid , nick                        , taobao_tid                  , alipay_tid                  ,                                alipay_date                 , account_balance             , income                      , expense                     ,                                 trade_partner               , trade_locale                , item_name                   , type                        ,                                 memo                        , payment_type_code           , merchant_order_no           , id                          ,                                 self_user_id                , opt_user_id                 , business_type               , old_taobao_tid              ,                                 rx_inserttime               , rx_updatetime from tmp_t_top_interface_tradeaccountdetail;
DROP TABLE tmp_t_top_interface_tradeaccountdetail PURGE;


