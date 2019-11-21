package org.smartregister.child;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-21
 */

public class JsonFormAssetsUtils {

    public static String childEnrollmentJsonForm = "{\n" +
            "  \"count\": \"1\",\n" +
            "  \"encounter_type\": \"Birth Registration\",\n" +
            "  \"mother\": {\n" +
            "    \"encounter_type\": \"New Woman Registration\"\n" +
            "  },\n" +
            "  \"entity_id\": \"\",\n" +
            "  \"relational_id\": \"\",\n" +
            "  \"metadata\": {\n" +
            "    \"start\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"start\",\n" +
            "      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"end\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"end\",\n" +
            "      \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"today\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"encounter\",\n" +
            "      \"openmrs_entity_id\": \"encounter_date\"\n" +
            "    },\n" +
            "    \"deviceid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"deviceid\",\n" +
            "      \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"subscriberid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"subscriberid\",\n" +
            "      \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"simserial\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"simserial\",\n" +
            "      \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"phonenumber\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"phonenumber\",\n" +
            "      \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"encounter_location\": \"\",\n" +
            "    \"look_up\": {\n" +
            "      \"entity_id\": \"\",\n" +
            "      \"value\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"step1\": {\n" +
            "    \"title\": \"Birth Registration\",\n" +
            "    \"fields\": [\n" +
            "      {\n" +
            "        \"key\": \"Child_Photo\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"type\": \"choose_image\",\n" +
            "        \"uploadButtonText\": \"Take a photo of the child\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Home_Facility\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"openmrs_data_type\": \"text\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Child's home health facility \",\n" +
            "        \"tree\": [],\n" +
            "        \"v_required\": {\n" +
            "          \"value\": true,\n" +
            "          \"err\": \"Please enter the child's home facility\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"ZEIR_ID\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_identifier\",\n" +
            "        \"openmrs_entity_id\": \"ZEIR_ID\",\n" +
            "        \"type\": \"barcode\",\n" +
            "        \"barcode_type\": \"qrcode\",\n" +
            "        \"hint\": \"Child's ZEIR ID \",\n" +
            "        \"scanButtonText\": \"Scan QR Code\",\n" +
            "        \"value\": \"0\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter a valid ID\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the Child's ZEIR ID\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Child_Register_Card_Number\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"Child_Register_Card_Number\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Child's register card number\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Child_Birth_Certificate\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"Child_Birth_Certificate\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Child's birth certificate number\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"First_Name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"first_name\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"First name\",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z\\\\s\\.\\-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Last_Name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"last_name\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Last name \",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the last name\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z\\\\s\\.\\-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Sex\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"gender\",\n" +
            "        \"type\": \"spinner\",\n" +
            "        \"hint\": \"Sex \",\n" +
            "        \"values\": [\n" +
            "          \"Male\",\n" +
            "          \"Female\"\n" +
            "        ],\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the sex\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Date_Birth\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"birthdate\",\n" +
            "        \"type\": \"date_picker\",\n" +
            "        \"hint\": \"Child's DOB \",\n" +
            "        \"expanded\": false,\n" +
            "        \"duration\": {\n" +
            "          \"label\": \"Age\"\n" +
            "        },\n" +
            "        \"min_date\": \"today-5y\",\n" +
            "        \"max_date\": \"today\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the date of birth\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"First_Health_Facility_Contact\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_data_type\": \"date\",\n" +
            "        \"type\": \"date_picker\",\n" +
            "        \"hint\": \"Date first seen \",\n" +
            "        \"expanded\": false,\n" +
            "        \"min_date\": \"today-5y\",\n" +
            "        \"max_date\": \"today\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Enter the date that the child was first seen at a health facility for immunization services\"\n" +
            "        },\n" +
            "        \"constraints\": [\n" +
            "          {\n" +
            "            \"type\": \"date\",\n" +
            "            \"ex\": \"greaterThanEqualTo(., step1:Date_Birth)\",\n" +
            "            \"err\": \"Date first seen can't occur before date of birth\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Birth_Weight\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_data_type\": \"text\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Birth weight (kg) \",\n" +
            "        \"value\": \"3.3\",\n" +
            "        \"v_min\": {\n" +
            "          \"value\": \"0.1\",\n" +
            "          \"err\": \"Weight must be greater than 0\"\n" +
            "        },\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Enter a valid weight\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Enter the child's birth weight\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Birth_Height\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_data_type\": \"text\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Birth Height (cm)\",\n" +
            "        \"v_min\": {\n" +
            "          \"value\": \"0.1\",\n" +
            "          \"err\": \"Height must be greater than 0\"\n" +
            "        },\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Enter a valid height\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Enter the child's birth height\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Mother_Guardian_First_Name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"first_name\",\n" +
            "        \"entity_id\": \"mother\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Mother/guardian first name \",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"look_up\": \"true\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the mother/guardian's first name\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z\\\\s\\.\\-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Mother_Guardian_Last_Name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"last_name\",\n" +
            "        \"entity_id\": \"mother\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Mother/guardian last name \",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"look_up\": \"true\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the mother/guardian's last name\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z\\\\s\\.\\-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Mother_Guardian_Date_Birth\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"birthdate\",\n" +
            "        \"entity_id\": \"mother\",\n" +
            "        \"type\": \"date_picker\",\n" +
            "        \"hint\": \"Mother/guardian DOB\",\n" +
            "        \"look_up\": \"true\",\n" +
            "        \"expanded\": false,\n" +
            "        \"duration\": {\n" +
            "          \"label\": \"Age\"\n" +
            "        },\n" +
            "        \"min_date\": \"01-01-1900\",\n" +
            "        \"max_date\": \"today-10y\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the mother/guardian's DOB\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Mother_Guardian_NRC\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"NRC_Number\",\n" +
            "        \"entity_id\": \"mother\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Mother/guardian NRC number\",\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"([0-9]{6}/[0-9]{2}/[0-9])|\\s*\",\n" +
            "          \"err\": \"Number must take the format of ######/##/#\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Mother_Guardian_Number\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Mother/guardian phone number\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"(09[5-7][0-9]{7})|\\s*\",\n" +
            "          \"err\": \"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Father_Guardian_Name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_data_type\": \"text\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Father/guardian full name\",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z\\\\s\\.\\-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Father_Guardian_NRC\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"Father_NRC_Number\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Father/guardian NRC number\",\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"([0-9]{6}/[0-9]{2}/[0-9])|\\s*\",\n" +
            "          \"err\": \"Number must take the format of ######/##/#\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Place_Birth\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_data_type\": \"select one\",\n" +
            "        \"type\": \"spinner\",\n" +
            "        \"hint\": \"Place of birth \",\n" +
            "        \"values\": [\n" +
            "          \"Health facility\",\n" +
            "          \"Home\"\n" +
            "        ],\n" +
            "        \"openmrs_choice_ids\": {\n" +
            "          \"Health facility\": \"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"Home\": \"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": true,\n" +
            "          \"err\": \"Please enter the place of birth\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Birth_Facility_Name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_data_type\": \"text\",\n" +
            "        \"type\": \"tree\",\n" +
            "        \"hint\": \"Which health facility was the child born in? \",\n" +
            "        \"tree\": [],\n" +
            "        \"v_required\": {\n" +
            "          \"value\": true,\n" +
            "          \"err\": \"Please enter the birth facility name\"\n" +
            "        },\n" +
            "        \"relevance\": {\n" +
            "          \"step1:Place_Birth\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"ex\": \"equalTo(., \\\"Health facility\\\")\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Birth_Facility_Name_Other\",\n" +
            "        \"openmrs_entity_parent\": \"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Other health facility \",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": true,\n" +
            "          \"err\": \"Please specify the health facility the child was born in\"\n" +
            "        },\n" +
            "        \"relevance\": {\n" +
            "          \"step1:Birth_Facility_Name\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"ex\": \"equalTo(., \\\"[\\\"Other\\\"]\\\")\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Residential_Area\",\n" +
            "        \"openmrs_entity_parent\": \"usual_residence\",\n" +
            "        \"openmrs_entity\": \"person_address\",\n" +
            "        \"openmrs_entity_id\": \"address3\",\n" +
            "        \"openmrs_data_type\": \"text\",\n" +
            "        \"type\": \"tree\",\n" +
            "        \"hint\": \"Child's residential area \",\n" +
            "        \"tree\": [],\n" +
            "        \"v_required\": {\n" +
            "          \"value\": false,\n" +
            "          \"err\": \"Please enter the child's residential area\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Residential_Area_Other\",\n" +
            "        \"openmrs_entity_parent\": \"usual_residence\",\n" +
            "        \"openmrs_entity\": \"person_address\",\n" +
            "        \"openmrs_entity_id\": \"address5\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Other residential area \",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": true,\n" +
            "          \"err\": \"Please specify the residential area\"\n" +
            "        },\n" +
            "        \"relevance\": {\n" +
            "          \"step1:Residential_Area\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"ex\": \"equalTo(., \\\"[\\\"Other\\\"]\\\")\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Residential_Address\",\n" +
            "        \"openmrs_entity_parent\": \"usual_residence\",\n" +
            "        \"openmrs_entity\": \"person_address\",\n" +
            "        \"openmrs_entity_id\": \"address2\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Home address \",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": true,\n" +
            "          \"err\": \"Please enter the home address\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Physical_Landmark\",\n" +
            "        \"openmrs_entity_parent\": \"usual_residence\",\n" +
            "        \"openmrs_entity\": \"person_address\",\n" +
            "        \"openmrs_entity_id\": \"address1\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Landmark\",\n" +
            "        \"edit_type\": \"name\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"CHW_Name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"CHW_Name\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"CHW name\",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z\\\\s\\.\\-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"CHW_Phone_Number\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"CHW_Phone_Number\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"CHW phone number\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"(09[5-7][0-9]{7})|\\s*\",\n" +
            "          \"err\": \"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"PMTCT_Status\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"type\": \"spinner\",\n" +
            "        \"hint\": \"HIV exposure\",\n" +
            "        \"values\": [\n" +
            "          \"CE\",\n" +
            "          \"MSU\",\n" +
            "          \"CNE\"\n" +
            "        ],\n" +
            "        \"openmrs_choice_ids\": {\n" +
            "          \"CE\": \"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"MSU\": \"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"CNE\": \"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
}
