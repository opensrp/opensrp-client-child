package org.smartregister.child.interactor;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.growthmonitoring.BuildConfig;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.repository.AllSharedPreferences;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-21
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.O_MR1)
public class ChildRegisterInteractorTest {

    private ChildRegisterInteractor interactor;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        interactor = new ChildRegisterInteractor(Mockito.mock(AppExecutors.class));
    }

    @Test
    public void processWeightShouldNotProcessWeightIfIdentifierContainsMzeirId() throws JSONException {

        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put("M_ZEIR_ID", "9029393");

        UpdateRegisterParams updateRegisterParam = new UpdateRegisterParams();
        updateRegisterParam.setEditMode(false);

        FormTag formTag = new FormTag();
        formTag.providerId = "provider-id";
        formTag.appVersion = 21;
        formTag.databaseVersion = 3;

        updateRegisterParam.setFormTag(formTag);


        JSONObject clientJson = Mockito.spy(new JSONObject(womanRegistrationClient));
        interactor.processWeight(identifiers, jsonEnrollmentForm, updateRegisterParam, clientJson);
        Mockito.verify(clientJson, Mockito.times(0))
                .getString(Mockito.eq(FormEntityConstants.Person.gender.name()));
    }


    @Test
    public void processWeightShouldProcessWeightIfIdentifierDoesNotContainsMzeirId() throws Exception {
        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put("ZEIR_ID", "9029393");

        UpdateRegisterParams updateRegisterParam = new UpdateRegisterParams();
        updateRegisterParam.setEditMode(false);


        FormTag formTag = new FormTag();
        formTag.providerId = "provider-id";
        formTag.appVersion = 21;
        formTag.databaseVersion = 3;

        updateRegisterParam.setFormTag(formTag);
        GrowthMonitoringLibrary growthMonitoringLibrary = Mockito.mock(GrowthMonitoringLibrary.class);


        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", growthMonitoringLibrary);
        Mockito.doReturn(Mockito.mock(WeightRepository.class)).when(growthMonitoringLibrary).weightRepository();

        // Mock this call ChildLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM()
        ChildLibrary childLibrary = Mockito.mock(ChildLibrary.class);
        Context context = Mockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        Mockito.doReturn(context).when(childLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();
        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();

        JSONObject clientJson = Mockito.spy(new JSONObject(childRegistrationClient));
        interactor.processWeight(identifiers, jsonEnrollmentForm, updateRegisterParam, clientJson);
        Mockito.verify(clientJson, Mockito.times(1))
                .getString(Mockito.eq(FormEntityConstants.Person.gender.name()));
    }

    @Test
    public void isClientMotherShouldReturnFalseIfIdentifiersDoNotContainMzeirId() {
        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put("ZEIR_ID", "9029393");

        Assert.assertFalse(interactor.isClientMother(identifiers));
    }

    @Test
    public void isClientMotherShouldReturnTrueIfIdentifiersContainsMzeirId() {
        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put("M_ZEIR_ID", "9029393");

        Assert.assertTrue(interactor.isClientMother(identifiers));
    }

    private String jsonEnrollmentForm = "{\n" +
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

    private String womanRegistrationClient = "{\"firstName\":\"Mary\",\"lastName\":\"Janostri\",\"birthdate\":\"2009-06-20T02:00:00.000+02:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"baseEntityId\":\"f2f5dfb6-5110-42f6-88bb-951a070f5df2\",\"identifiers\":{\"M_ZEIR_ID\":\"14656508_mother\"},\"addresses\":[],\"attributes\":{},\"dateCreated\":\"2019-06-24T12:45:44.100+02:00\",\"dateEdited\":\"2019-06-25T10:23:10.491+02:00\",\"serverVersion\":1561451012837,\"type\":\"Client\",\"id\":\"703652b4-3516-49a2-80f8-2ace440e4fad\",\"revision\":\"v3\"}";
    private String childRegistrationClient = "{\"firstName\":\"Doe\",\"middleName\":\"Jane\",\"lastName\":\"Jane\",\"birthdate\":\"2019-07-02T02:00:00.000+02:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{\"mother\":[\"bdf50ebc-c352-421c-985d-9e9880d9ec58\",\"bdf50ebc-c352-421c-985d-9e9880d9ec58\"]},\"baseEntityId\":\"c4badbf0-89d4-40b9-8c37-68b0371797ed\",\"identifiers\":{\"zeir_id\":\"14750004\"},\"addresses\":[{\"addressType\":\"usual_residence\",\"addressFields\":{\"address5\":\"Not sure\"}}],\"attributes\":{\"age\":\"0.0\",\"Birth_Certificate\":\"ADG\\/23652432\\/1234\",\"second_phone_number\":\"0972343243\"},\"dateCreated\":\"2019-07-02T15:42:57.838+02:00\",\"serverVersion\":1562074977828,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":1,\"type\":\"Client\",\"id\":\"b8798571-dee6-43b5-a289-fc75ab703792\",\"revision\":\"v1\"}";
}