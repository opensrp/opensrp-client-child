
Select ec_client.id as _id , ec_client.relationalid , ec_client.zeir_id , ec_child_details.relational_id , ec_client.first_name , ec_client.last_name , ec_client.gender,
ec_client.base_entity_id , ec_mother_details.nrc_number as mother_nrc_number , ec_mother_details.father_name , ec_client.dob , ec_mother_details.epi_card_number ,
ec_client.contact_phone_number , ec_child_details.pmtct_status, ec_client.client_reg_date , ec_client.last_interacted_with , ec_child_details.inactive , ec_child_details.lost_to_follow_up
FROM ec_client join ec_child_details ON  ec_client.id join ec_mother_details on  ec_client.id =  ec_mother_details.base_entity_id WHERE  ec_client.date_removed is null


select ec_client.id as _id, ec_client.relationalid, ec_client.zeir_id, child_details.relational_id, ec_client.gender, ec_client.base_entity_id, ec_client.first_name, ec_client.last_name, mother.first_name
as mother_first_name, mother.last_name as mother_last_name,
ec_client.dob, mother.dob as mother_dob, mother_details.nrc_number as mother_nrc_number, mother_details.father_name, mother_details.epi_card_number,ec_client.client_reg_date,child_details.pmtct_status, ec_client.last_interacted_with,
 child_details.inactive, child_details.lost_to_follow_up, child_details.contact_phone_number from ec_child_details child_details
join ec_mother_details mother_details on child_details.relational_id = mother_details.base_entity_id join ec_client ec_client on ec_client.base_entity_id=child_details.base_entity_id join ec_client mother
on mother.base_entity_id = mother_details.base_entity_id where ec_child_detail.relational_id IN ()