package org.smartregister.child.model;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.UniqueId;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.AppProperties;
import org.smartregister.util.JsonFormUtils;

import java.util.List;

@PrepareForTest({CoreLibrary.class, LocationHelper.class, TextUtils.class, JsonFormUtils.class, Log.class})
public class BaseChildRegisterModelTest extends BasePowerMockUnitTest {

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private Context context;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private AppProperties appProperties;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
    }

    @Test
    public void processRegistrationWithNewWomanRegistration() throws JSONException {
        String anm = "providerId";
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        PowerMockito.when(locationHelper.getOpenMrsLocationId("locality")).thenReturn("ssd");
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);

        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(anm);
        PowerMockito.when(allSharedPreferences.fetchUserLocalityId(anm)).thenReturn("locality");
        PowerMockito.when(allSharedPreferences.fetchDefaultTeam(anm))
                .thenReturn("team");
        PowerMockito.when(allSharedPreferences.fetchDefaultTeamId(anm))
                .thenReturn("teamId");
        PowerMockito.when(context.allCommonsRepositoryobjects("ec_client")).thenReturn(Mockito.mock(AllCommonsRepository.class));
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(Mockito.<String>any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String s = invocation.getArgument(0);
                return s == null || s.length() == 0;
            }
        });
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null, null,
                null, true);
        metadata.updateChildRegister("test", "test",
                "test", "ChildRegister",
                "test", "test",
                "test",
                "test", "test");

        String jsonString = "{\n  \"count\": \"1\",\n  \"encounter_type\": \"Birth Registration\",\n  \"mother\": {\n    \"encounter_type\": \"New Woman Registration\"\n  },\n  \"entity_id\": \"\",\n  \"relational_id\": \"\",\n  \"metadata\": {\n    \"start\": {\n      \"openmrs_entity_parent\": \"\",\n      \"openmrs_entity\": \"concept\",\n      \"openmrs_data_type\": \"start\",\n      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n      \"value\": \"2019-11-02 14:02:17\"\n    },\n    \"end\": {\n      \"openmrs_entity_parent\": \"\",\n      \"openmrs_entity\": \"concept\",\n      \"openmrs_data_type\": \"end\",\n      \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n      \"value\": \"2019-11-02 14:03:03\"\n    },\n    \"today\": {\n      \"openmrs_entity_parent\": \"\",\n      \"openmrs_entity\": \"encounter\",\n      \"openmrs_entity_id\": \"encounter_date\",\n      \"value\": \"02-11-2019\"\n    },\n    \"deviceid\": {\n      \"openmrs_entity_parent\": \"\",\n      \"openmrs_entity\": \"concept\",\n      \"openmrs_data_type\": \"deviceid\",\n      \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n      \"value\": \"358240051111110\"\n    },\n    \"subscriberid\": {\n      \"openmrs_entity_parent\": \"\",\n      \"openmrs_entity\": \"concept\",\n      \"openmrs_data_type\": \"subscriberid\",\n      \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n      \"value\": \"310260000000000\"\n    },\n    \"simserial\": {\n      \"openmrs_entity_parent\": \"\",\n      \"openmrs_entity\": \"concept\",\n      \"openmrs_data_type\": \"simserial\",\n      \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n      \"value\": \"89014103211118510720\"\n    },\n    \"phonenumber\": {\n      \"openmrs_entity_parent\": \"\",\n      \"openmrs_entity\": \"concept\",\n      \"openmrs_data_type\": \"phonenumber\",\n      \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n      \"value\": \"+15555215554\"\n    },\n    \"encounter_location\": \"99d01128-51cd-42de-84c4-432b3ac56532\",\n    \"look_up\": {\n      \"entity_id\": \"\",\n      \"value\": \"\"\n    }\n  },\n  \"step1\": {\n    \"title\": \"Birth Registration\",\n    \"fields\": [\n      {\n        \"key\": \"photo\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"\",\n        \"openmrs_entity_id\": \"\",\n        \"type\": \"choose_image\",\n        \"uploadButtonText\": \"Take a photo of the child\"\n      },\n      {\n        \"key\": \"zeir_id\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person_identifier\",\n        \"openmrs_entity_id\": \"zeir_id\",\n        \"type\": \"edit_text\",\n        \"hint\": \"Child\'s MER ID\",\n        \"label_info_text\": \"Write this number down on the child\'s health passport.\",\n        \"scanButtonText\": \"Scan QR Code\",\n        \"read_only\": true,\n        \"v_numeric\": {\n          \"value\": \"true\",\n          \"err\": \"Please enter a valid ID\"\n        },\n        \"v_required\": {\n          \"value\": \"true\",\n          \"err\": \"Please enter the Child\'s MER ID\"\n        },\n        \"value\": \"16449043\"\n      },\n      {\n        \"key\": \"birth_registration_number\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person_attribute\",\n        \"openmrs_entity_id\": \"Birth_Certificate\",\n        \"type\": \"edit_text\",\n        \"hint\": \"Child\'s NRB birth registration number\",\n        \"label_info_text\": \"If the child was registered in vital registration, enter the registration number here.\",\n        \"edit_type\": \"name\",\n        \"v_required\": {\n          \"value\": false,\n          \"err\": \"Please enter the Birth Registration Number\"\n        },\n        \"v_regex\": {\n          \"value\": \"([A-Z]{2,3}/[0-9]{8}/[0-9]{4})|\\\\s*\",\n          \"err\": \"Number must take the format of ###/########/####\"\n        },\n        \"value\": \"\"\n      },\n      {\n        \"key\": \"last_name\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person\",\n        \"openmrs_entity_id\": \"last_name\",\n        \"type\": \"edit_text\",\n        \"hint\": \"Last name\",\n        \"edit_type\": \"name\",\n        \"v_required\": {\n          \"value\": true,\n          \"err\": \"Please enter the last name\"\n        },\n        \"v_regex\": {\n          \"value\": \"[A-Za-z\\\\s\\\\.\\\\-]*\",\n          \"err\": \"Please enter a valid name\"\n        },\n        \"value\": \"sad\"\n      },\n      {\n        \"key\": \"first_name\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person\",\n        \"openmrs_entity_id\": \"first_name\",\n        \"type\": \"edit_text\",\n        \"hint\": \"First name\",\n        \"edit_type\": \"name\",\n        \"v_regex\": {\n          \"value\": \"[A-Za-z\\\\s\\\\.\\\\-]*\",\n          \"err\": \"Please enter a valid name\"\n        },\n        \"v_required\": {\n          \"value\": true,\n          \"err\": \"Please enter a first name\"\n        },\n        \"value\": \"asd\"\n      },\n      {\n        \"key\": \"middle_name\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person\",\n        \"openmrs_entity_id\": \"middle_name\",\n        \"type\": \"edit_text\",\n        \"hint\": \"Middle name\",\n        \"edit_type\": \"name\",\n        \"v_regex\": {\n          \"value\": \"[A-Za-z\\\\s\\\\.\\\\-]*\",\n          \"err\": \"Please enter a valid name\"\n        },\n        \"v_required\": {\n          \"value\": false,\n          \"err\": \"Please enter the child\'s middle name\"\n        },\n        \"value\": \"\"\n      },\n      {\n        \"key\": \"Sex\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person\",\n        \"openmrs_entity_id\": \"gender\",\n        \"type\": \"spinner\",\n        \"hint\": \"Sex\",\n        \"values\": [\n          \"Male\",\n          \"Female\"\n        ],\n        \"v_required\": {\n          \"value\": \"true\",\n          \"err\": \"Please enter the Gender of the child\"\n        },\n        \"value\": \"Male\"\n      },\n      {\n        \"key\": \"Date_Birth\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person\",\n        \"openmrs_entity_id\": \"birthdate\",\n        \"type\": \"date_picker\",\n        \"hint\": \"Child\'s DOB\",\n        \"expanded\": false,\n        \"duration\": {\n          \"label\": \"Age\"\n        },\n        \"min_date\": \"today-5y\",\n        \"max_date\": \"today\",\n        \"v_required\": {\n          \"value\": \"true\",\n          \"err\": \"Please enter the date of birth\"\n        },\n        \"step\": \"step1\",\n        \"is-rule-check\": true,\n        \"value\": \"02-11-2019\"\n      },\n      {\n        \"key\": \"age\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person_attribute\",\n        \"openmrs_entity_id\": \"age\",\n        \"type\": \"hidden\",\n        \"value\": \"0.0\",\n        \"calculation\": {\n          \"rules-engine\": {\n            \"ex-rules\": {\n              \"rules-file\": \"child_register_registration_calculation_rules.yml\"\n            }\n          }\n        },\n        \"step\": \"step1\",\n        \"is-rule-check\": true\n      },\n      {\n        \"key\": \"home_address\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"\",\n        \"openmrs_entity_id\": \"\",\n        \"openmrs_data_type\": \"text\",\n        \"type\": \"tree\",\n        \"tree\": [\n          {\n            \"key\": \"Malawi\",\n            \"level\": \"\",\n            \"name\": \"Malawi\",\n            \"nodes\": [\n              {\n                \"key\": \"Central West Zone\",\n                \"level\": \"\",\n                \"name\": \"Central West Zone\",\n                \"nodes\": [\n                  {\n                    \"key\": \"Lizulu Health Centre\",\n                    \"level\": \"\",\n                    \"name\": \"Lizulu Health Centre\",\n                    \"nodes\": [\n                      {\n                        \"key\": \"Chibonga Outreach Clinic (Lizulu)\",\n                        \"level\": \"\",\n                        \"name\": \"Chibonga Outreach Clinic (Lizulu)\"\n                      },\n                      {\n                        \"key\": \"Chilobwe Outreach Clinic (Lizulu)\",\n                        \"level\": \"\",\n                        \"name\": \"Chilobwe Outreach Clinic (Lizulu)\"\n                      },\n                      {\n                        \"key\": \"Chilobwe Village Clinic (Lizulu)\",\n                        \"level\": \"\",\n                        \"name\": \"Chilobwe Village Clinic (Lizulu)\"\n                      }\n                    ]\n                  }\n                ]\n              }\n            ]\n          },\n          {\n            \"key\": \"Other\",\n            \"level\": \"\",\n            \"name\": \"Other\"\n          }\n        ],\n        \"value\": \"Lombwa Outreach\",\n        \"hint\": \"Address/Location\",\n        \"v_required\": {\n          \"value\": false,\n          \"err\": \"Please enter the Child\'s Home Address\"\n        },\n        \"default\": [\n          \"Malawi\",\n          \"Central West Zone\",\n          \"Lizulu Health Centre\"\n        ]\n      },\n      {\n        \"key\": \"village\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person_address\",\n        \"openmrs_entity_id\": \"address1\",\n        \"openmrs_data_type\": \"text\",\n        \"type\": \"edit_text\",\n        \"label_info_text\": \"Indicate the village where the child comes from.\",\n        \"hint\": \"Child\'s Village\",\n        \"value\": \"\"\n      },\n      {\n        \"key\": \"traditional_authority\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person_attribute\",\n        \"openmrs_entity_id\": \"traditional_authority\",\n        \"hint\": \"Traditional Authority\",\n        \"label_info_text\": \"Indicate the name of the Traditional Authority for the child.\",\n        \"type\": \"edit_text\",\n        \"value\": \"\"\n      },\n      {\n        \"key\": \"Birth_Weight\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"concept\",\n        \"openmrs_entity_id\": \"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n        \"openmrs_data_type\": \"text\",\n        \"type\": \"edit_text\",\n        \"label_info_text\": \"The weight as measured when the child is born\",\n        \"hint\": \"Birth weight (kg)\",\n        \"v_min\": {\n          \"value\": \"0.1\",\n          \"err\": \"Weight must be greater than 0\"\n        },\n        \"v_numeric\": {\n          \"value\": \"true\",\n          \"err\": \"Enter a valid weight\"\n        },\n        \"v_required\": {\n          \"value\": \"true\",\n          \"err\": \"Enter the child\'s birth weight\"\n        },\n        \"value\": \"23\"\n      },\n      {\n        \"key\": \"Birth_Height\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"concept\",\n        \"openmrs_entity_id\": \"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n        \"openmrs_data_type\": \"text\",\n        \"type\": \"edit_text\",\n        \"hint\": \"Birth Height (cm)\",\n        \"v_min\": {\n          \"value\": \"0.1\",\n          \"err\": \"Height must be greater than 0\"\n        },\n        \"v_numeric\": {\n          \"value\": \"true\",\n          \"err\": \"Enter a valid height\"\n        },\n        \"v_required\": {\n          \"value\": false,\n          \"err\": \"Enter the child\'s birth height\"\n        },\n        \"value\": \"\"\n      },\n      {\n        \"key\": \"Mother_Guardian_First_Name\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person\",\n        \"openmrs_entity_id\": \"first_name\",\n        \"entity_id\": \"mother\",\n        \"type\": \"edit_text\",\n        \"hint\": \"Mother/Guardian first name\",\n        \"edit_type\": \"name\",\n        \"look_up\": \"true\",\n        \"v_required\": {\n          \"value\": \"true\",\n          \"err\": \"Please enter the mother/guardian\'s first name\"\n        },\n        \"v_regex\": {\n          \"value\": \"[A-Za-z\\\\s\\\\.\\\\-]*\",\n          \"err\": \"Please enter a valid name\"\n        },\n        \"value\": \"wewe\"\n      },\n      {\n        \"key\": \"Mother_Guardian_Last_Name\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person\",\n        \"openmrs_entity_id\": \"last_name\",\n        \"entity_id\": \"mother\",\n        \"type\": \"edit_text\",\n        \"hint\": \"Mother/guardian last name\",\n        \"edit_type\": \"name\",\n        \"look_up\": \"true\",\n        \"v_required\": {\n          \"value\": \"true\",\n          \"err\": \"Please enter the mother/guardian\'s last name\"\n        },\n        \"v_regex\": {\n          \"value\": \"[A-Za-z\\\\s\\\\.\\\\-]*\",\n          \"err\": \"Please enter a valid name\"\n        },\n        \"value\": \"wewe\"\n      },\n      {\n        \"key\": \"Mother_Guardian_Date_Birth\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person\",\n        \"openmrs_entity_id\": \"birthdate\",\n        \"entity_id\": \"mother\",\n        \"type\": \"date_picker\",\n        \"hint\": \"Mother/guardian DOB\",\n        \"look_up\": \"true\",\n        \"expanded\": false,\n        \"duration\": {\n          \"label\": \"Age\"\n        },\n        \"default\": \"01-01-1960\",\n        \"min_date\": \"01-01-1960\",\n        \"max_date\": \"today-10y\",\n        \"value\": \"\"\n      },\n      {\n        \"key\": \"nrc_number\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person_attribute\",\n        \"openmrs_entity_id\": \"nrc_number\",\n        \"entity_id\": \"mother\",\n        \"type\": \"edit_text\",\n        \"hint\": \"Mother/guardian NRB Identification number\",\n        \"look_up\": \"true\",\n        \"v_regex\": {\n          \"value\": \"([A-Za-z0-9]{1,11})|\\\\s*\",\n          \"err\": \"ID should be at-most 11 characters in length\"\n        },\n        \"value\": \"\"\n      },\n      {\n        \"key\": \"mother_guardian_phone_number\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"concept\",\n        \"openmrs_entity_id\": \"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n        \"entity_id\": \"mother\",\n        \"type\": \"edit_text\",\n        \"hint\": \"Mother/guardian phone number\",\n        \"v_regex\": {\n          \"value\": \"([0][0-9]{9})|\\\\s*\",\n          \"err\": \"Number must begin with 0 and must be a total of 10 digits in length\"\n        },\n        \"value\": \"\"\n      },\n      {\n        \"key\": \"second_phone_number\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person_attribute\",\n        \"openmrs_entity_id\": \"second_phone_number\",\n        \"entity_id\": \"mother\",\n        \"type\": \"edit_text\",\n        \"hint\": \"Alternative phone number\",\n        \"v_regex\": {\n          \"value\": \"([0][0-9]{9})|\\\\s*\",\n          \"err\": \"Number must begin with 0 and must be a total of 10 digits in length\"\n        },\n        \"value\": \"\"\n      },\n      {\n        \"key\": \"protected_at_birth\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"concept\",\n        \"openmrs_entity_id\": \"164826AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n        \"type\": \"spinner\",\n        \"label_info_text\": \"Whether the child\'s mother received 2+ doses of Td.\",\n        \"hint\": \"Protected at birth (PAB)\",\n        \"entity_id\": \"mother\",\n        \"v_required\": {\n          \"value\": true,\n          \"err\": \"Please choose an option\"\n        },\n        \"values\": [\n          \"Yes\",\n          \"No\",\n          \"Don\'t Know\"\n        ],\n        \"openmrs_choice_ids\": {\n          \"Yes\": \"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n          \"No\": \"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n          \"Don\'t Know\": \"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n        },\n        \"relevance\": {\n          \"rules-engine\": {\n            \"ex-rules\": {\n              \"rules-file\": \"child_register_registration_relevance_rules.yml\"\n            }\n          }\n        },\n        \"is_visible\": true,\n        \"value\": \"No\"\n      },\n      {\n        \"key\": \"mother_hiv_status\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"concept\",\n        \"openmrs_entity_id\": \"1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n        \"entity_id\": \"mother\",\n        \"type\": \"spinner\",\n        \"hint\": \"HIV Status of the Child\'s Mother\",\n        \"values\": [\n          \"Positive\",\n          \"Negative\",\n          \"Unknown\"\n        ],\n        \"openmrs_choice_ids\": {\n          \"Positive\": \"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n          \"Negative\": \"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n          \"Unknown\": \"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n        },\n        \"step\": \"step1\",\n        \"is-rule-check\": true,\n        \"value\": \"HIV Status of the Child\'s Mother\"\n      },\n      {\n        \"key\": \"child_hiv_status\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"concept\",\n        \"openmrs_entity_id\": \"5303AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n        \"type\": \"spinner\",\n        \"hint\": \"HIV Status of the Child\",\n        \"values\": [\n          \"Positive\",\n          \"Negative\",\n          \"Unknown\",\n          \"Exposed\"\n        ],\n        \"openmrs_choice_ids\": {\n          \"Positive\": \"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n          \"Negative\": \"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n          \"Unknown\": \"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n          \"Exposed\": \"822AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n        },\n        \"relevance\": {\n          \"rules-engine\": {\n            \"ex-rules\": {\n              \"rules-file\": \"child_register_registration_relevance_rules.yml\"\n            }\n          }\n        },\n        \"is_visible\": false,\n        \"step\": \"step1\",\n        \"is-rule-check\": true\n      },\n      {\n        \"key\": \"child_treatment\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"concept\",\n        \"openmrs_entity_id\": \"162240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n        \"type\": \"spinner\",\n        \"label_info_text\": \"Indicate whether the child is on CPT and/or ART.\",\n        \"hint\": \"Child\'s treatment\",\n        \"values\": [\n          \"CPT\",\n          \"ART\",\n          \"None\"\n        ],\n        \"openmrs_choice_ids\": {\n          \"CPT\": \"160434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n          \"ART\": \"160119AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n          \"None\": \"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n        },\n        \"relevance\": {\n          \"rules-engine\": {\n            \"ex-rules\": {\n              \"rules-file\": \"child_register_registration_relevance_rules.yml\"\n            }\n          }\n        },\n        \"is_visible\": false\n      },\n      {\n        \"key\": \"lost_to_follow_up\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person_attribute\",\n        \"openmrs_entity_id\": \"lost_to_follow_up\",\n        \"type\": \"hidden\",\n        \"value\": \"\"\n      },\n      {\n        \"key\": \"inactive\",\n        \"openmrs_entity_parent\": \"\",\n        \"openmrs_entity\": \"person_attribute\",\n        \"openmrs_entity_id\": \"inactive\",\n        \"type\": \"hidden\",\n        \"value\": \"\"\n      }\n    ]\n  },\n  \"invisible_required_fields\": \"[]\",\n  \"details\": {\n    \"appVersionName\": \"1.6.59-SNAPSHOT\",\n    \"formVersion\": \"\"\n  }\n}";
        String clientString = "{\"firstName\":\"Ben\",\"middleName\":\"\",\"lastName\":\"Njoro\",\"birthdate\":\"2019-09-20T12:00:00.000Z\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Male\",\"relationships\":{\"mother\":[\"ce3ab60c-cf72-47e3-8b73-76ec5c3a58cd\",\"ce3ab60c-cf72-47e3-8b73-76ec5c3a58cd\"]},\"baseEntityId\":\"39de61e8-26e6-4511-9fdb-0d6a2cdfebf0\",\"identifiers\":{\"zeir_id\":\"104650\",\"OPENMRS_UUID\":\"e65dabbc-b87e-4931-a05c-c71a58c2d24c\"},\"addresses\":[],\"attributes\":{\"age\":\"0.33\",\"mother_tdv_doses\":\"TDV not received\"},\"dateCreated\":\"2020-01-20T12:01:42.752Z\",\"dateEdited\":\"2020-01-20T12:04:22.930Z\",\"serverVersion\":1579521702751,\"clientApplicationVersion\":12,\"clientDatabaseVersion\":9,\"type\":\"Client\",\"id\":\"924e0120-b105-45e7-887d-371360d5f2f8\",\"revision\":\"v2\"}";
        JSONObject clientJsonObject = new JSONObject(clientString);
        Mockito.doReturn(context).when(childLibrary).context();

        UniqueId uniqueId = new UniqueId();
        uniqueId.setOpenmrsId("23");
        UniqueIdRepository uniqueIdRepository = Mockito.mock(UniqueIdRepository.class);
        Mockito.doReturn(uniqueId).when(uniqueIdRepository).getNextUniqueId();
        Mockito.doReturn(uniqueIdRepository).when(childLibrary).getUniqueIdRepository();

        EventClientRepository eventClientRepository = Mockito.mock(EventClientRepository.class);
        Mockito.doReturn(clientJsonObject).when(eventClientRepository).getClientByBaseEntityId(Mockito.anyString());
        Mockito.doReturn(eventClientRepository).when(childLibrary).eventClientRepository();


        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);
        Mockito.doReturn(detailsRepository).when(context).detailsRepository();

        Mockito.doReturn(metadata).when(childLibrary).metadata();
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        BaseChildRegisterModel baseChildRegisterModel = new BaseChildRegisterModel();
        List<ChildEventClient> actualEvent = baseChildRegisterModel.processRegistration(jsonString, Mockito.mock(FormTag.class),false);
        //Expect child and Mother registration event
        Assert.assertEquals(2, actualEvent.size());
        Assert.assertEquals("Birth Registration", actualEvent.get(0).getEvent().getEventType());
        Assert.assertEquals("New Woman Registration", actualEvent.get(1).getEvent().getEventType());

    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }
}