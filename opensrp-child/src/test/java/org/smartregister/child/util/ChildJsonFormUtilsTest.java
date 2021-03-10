package org.smartregister.child.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.FormLocationTree;
import org.smartregister.child.domain.MoveToCatchmentEvent;
import org.smartregister.child.model.ChildMotherDetailModel;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.UniqueId;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.ImageRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.CloudantDataHandler;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AppProperties;
import org.smartregister.util.CredentialsHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import id.zelory.compressor.Compressor;

import static org.smartregister.util.JsonFormUtils.FIELDS;
import static org.smartregister.util.JsonFormUtils.STEP1;

@PrepareForTest({CredentialsHelper.class})
public class ChildJsonFormUtilsTest extends BaseUnitTest {

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private org.smartregister.Context openSrpContext;

    @Mock
    private Context context;

    private JSONObject jsonObject;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private ImmunizationLibrary immunizationLibrary;

    @Mock
    private CoreLibrary coreLibrary;

    @Captor
    private ArgumentCaptor addClientCaptor;

    @Captor
    private ArgumentCaptor ecSyncHelperAddEventCaptor;

    @Mock
    private DristhiConfiguration dristhiConfiguration;

    @Mock
    private HTTPAgent httpAgent;

    @Mock
    private ECSyncHelper ecSyncHelper;

    private final String registrationForm = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Birth Registration\",\"fields\":[{\"key\":\"Child_Photo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"},{\"key\":\"Home_Facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"hint\":\"Child's home health facility \",\"tree\":[],\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home facility\"}},{\"key\":\"ZEIR_ID\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"ZEIR_ID\",\"type\":\"barcode\",\"render_type\":\"ID\",\"barcode_type\":\"qrcode\",\"hint\":\"Child's ZEIR ID \",\"scanButtonText\":\"Scan QR Code\",\"value\":\"0\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's ZEIR ID\"}},{\"key\":\"First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\"},{\"key\":\"Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Last name \",\"edit_type\":\"name\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the last name\"}},{\"key\":\"Sex\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"spinner\",\"hint\":\"Sex \",\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the sex\"}},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"date_picker\",\"hint\":\"Child's DOB \",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"}},{\"key\":\"First_Health_Facility_Contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"date\",\"type\":\"date_picker\",\"hint\":\"Date first seen \",\"expanded\":false,\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date that the child was first seen at a health facility for immunization services\"},\"constraints\":[{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date first seen can't occur before date of birth\"}]},{\"key\":\"Birth_Weight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth weight (kg) \",\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth weight\"}},{\"key\":\"Birth_Height\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth Height (cm)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Height must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid height\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth height\"}},{\"key\":\"Mother_Guardian_First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian first name \",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's first name\"}},{\"key\":\"Mother_Guardian_Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian last name \",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's last name\"}},{\"key\":\"Mother_Guardian_Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"entity_id\":\"mother\",\"type\":\"date_picker\",\"hint\":\"Mother/Guardian DOB\",\"look_up\":\"true\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"01-01-1900\",\"max_date\":\"today-10y\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's DOB\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child-enrollment-relevance.yml\"}}}},{\"key\":\"Mother_Guardian_Date_Birth_Unknown\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdateApprox\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"check_box\",\"label\":\"\",\"options\":[{\"key\":\"Mother_Guardian_Date_Birth_Unknown\",\"text\":\"DOB unknown?\",\"text_size\":\"18px\",\"value\":\"false\"}]},{\"key\":\"Mother_Guardian_Age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"mother_age\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/Guardian Age\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a number\"},\"v_min\":{\"value\":\"0\",\"err\":\"Age must be equal or greater than 0\"},\"v_max\":{\"value\":\"99\",\"err\":\"Age must be equal or less than 99\"},\"v_regex\":{\"value\":\"^$|([0-9]+)\",\"err\":\"The number must be valid\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child-enrollment-relevance.yml\"}}},\"v_required\":{\"value\":true,\"err\":\"Please enter the age\"}},{\"key\":\"Mother_Guardian_NRC\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"NRC_Number\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian NRC number\"},{\"key\":\"Mother_Guardian_Phone_Number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian phone number\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length\"}},{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Place of birth \",\"values\":[\"Health facility\",\"Home\"],\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":true,\"err\":\"Please enter the place of birth\"}},{\"key\":\"Birth_Facility_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Which health facility was the child born in? \",\"tree\":[],\"v_required\":{\"value\":true,\"err\":\"Please enter the birth facility name\"},\"relevance\":{\"step1:Place_Birth\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"Health facility\\\")\"}}},{\"key\":\"Birth_Facility_Name_Other\",\"openmrs_entity_parent\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Other health facility \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the health facility the child was born in\"},\"relevance\":{\"step1:Birth_Facility_Name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}}},{\"key\":\"Residential_Area_Other\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address5\",\"type\":\"edit_text\",\"hint\":\"Other residential area \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the residential area\"},\"relevance\":{\"step1:Residential_Area\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}}},{\"key\":\"Residential_Address\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address2\",\"type\":\"edit_text\",\"hint\":\"Home address \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please enter the home address\"}}]}}";

    private final String jsonForm = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2019-11-02 14:02:17\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2019-11-02 14:03:03\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"02-11-2019\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"99d01128-51cd-42de-84c4-432b3ac56532\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Birth Registration\",\"fields\":[{\"key\":\"photo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"},{\"key\":\"zeir_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"zeir_id\",\"type\":\"edit_text\",\"hint\":\"Child's MER ID\",\"label_info_text\":\"Write this number down on the child's health passport.\",\"scanButtonText\":\"Scan QR Code\",\"read_only\":true,\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's MER ID\"},\"value\":\"16449043\"},{\"key\":\"birth_registration_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"Birth_Certificate\",\"type\":\"edit_text\",\"hint\":\"Child's NRB birth registration number\",\"label_info_text\":\"If the child was registered in vital registration, enter the registration number here.\",\"edit_type\":\"name\",\"v_required\":{\"value\":false,\"err\":\"Please enter the Birth Registration Number\"},\"v_regex\":{\"value\":\"([A-Z]{2,3}/[0-9]{8}/[0-9]{4})|\\\\s*\",\"err\":\"Number must take the format of ###/########/####\"},\"value\":\"\"},{\"key\":\"last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"hint\":\"Last name\",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please enter the last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"Mona\"},{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"v_required\":{\"value\":true,\"err\":\"Please enter a first name\"},\"value\":\"Saida\"},{\"key\":\"middle_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"middle_name\",\"type\":\"edit_text\",\"hint\":\"Middle name\",\"edit_type\":\"name\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"v_required\":{\"value\":false,\"err\":\"Please enter the child's middle name\"},\"value\":\"Mui\"},{\"key\":\"Sex\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"spinner\",\"hint\":\"Sex\",\"options\":[{\"key\":\"Male\",\"value\":\"Male\"},{\"key\":\"Female\",\"value\":\"Female\"}],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Gender of the child\"},\"value\":\"Male\"},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"date_picker\",\"hint\":\"Child's DOB\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"},\"step\":\"step1\",\"is-rule-check\":true,\"value\":\"02-11-2019\"},{\"key\":\"age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"age\",\"type\":\"hidden\",\"value\":\"0.0\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_calculation_rules.yml\"}}},\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"home_address\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"tree\":[{\"key\":\"Malawi\",\"level\":\"\",\"name\":\"Malawi\",\"nodes\":[{\"key\":\"Central West Zone\",\"level\":\"\",\"name\":\"Central West Zone\",\"nodes\":[{\"key\":\"Lizulu Health Centre\",\"level\":\"\",\"name\":\"Lizulu Health Centre\",\"nodes\":[{\"key\":\"Chibonga Outreach Clinic (Lizulu)\",\"level\":\"\",\"name\":\"Chibonga Outreach Clinic (Lizulu)\"},{\"key\":\"Chilobwe Outreach Clinic (Lizulu)\",\"level\":\"\",\"name\":\"Chilobwe Outreach Clinic (Lizulu)\"},{\"key\":\"Chilobwe Village Clinic (Lizulu)\",\"level\":\"\",\"name\":\"Chilobwe Village Clinic (Lizulu)\"}]}]}]},{\"key\":\"Other\",\"level\":\"\",\"name\":\"Other\"}],\"value\":\"Lombwa Outreach\",\"hint\":\"Address/Location\",\"v_required\":{\"value\":false,\"err\":\"Please enter the Child's Home Address\"},\"default\":[\"Malawi\",\"Central West Zone\",\"Lizulu Health Centre\"]},{\"key\":\"village\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address1\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"label_info_text\":\"Indicate the village where the child comes from.\",\"hint\":\"Child's Village\",\"value\":\"\"},{\"key\":\"traditional_authority\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"traditional_authority\",\"hint\":\"Traditional Authority\",\"label_info_text\":\"Indicate the name of the Traditional Authority for the child.\",\"type\":\"edit_text\",\"value\":\"\"},{\"key\":\"Birth_Weight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"label_info_text\":\"The weight as measured when the child is born\",\"hint\":\"Birth weight (kg)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth weight\"},\"value\":\"23\"},{\"key\":\"Birth_Height\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth Height (cm)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Height must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid height\"},\"v_required\":{\"value\":false,\"err\":\"Enter the child's birth height\"},\"value\":\"\"},{\"key\":\"Mother_Guardian_First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/Guardian first name\",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's first name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"esther\"},{\"key\":\"Mother_Guardian_Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian last name\",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"heung\"},{\"key\":\"Mother_Guardian_Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"entity_id\":\"mother\",\"type\":\"date_picker\",\"hint\":\"Mother/guardian DOB\",\"look_up\":\"true\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"default\":\"01-01-1960\",\"min_date\":\"01-01-1960\",\"max_date\":\"today-10y\",\"value\":\"\"},{\"key\":\"nrc_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"nrc_number\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian NRB Identification number\",\"look_up\":\"true\",\"v_regex\":{\"value\":\"([A-Za-z0-9]{1,11})|\\\\s*\",\"err\":\"ID should be at-most 11 characters in length\"},\"value\":\"\"},{\"key\":\"mother_guardian_phone_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian phone number\",\"v_regex\":{\"value\":\"([0][0-9]{9})|\\\\s*\",\"err\":\"Number must begin with 0 and must be a total of 10 digits in length\"},\"value\":\"\"},{\"key\":\"second_phone_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"second_phone_number\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Alternative phone number\",\"v_regex\":{\"value\":\"([0][0-9]{9})|\\\\s*\",\"err\":\"Number must begin with 0 and must be a total of 10 digits in length\"},\"value\":\"\"},{\"key\":\"protected_at_birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"164826AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"spinner\",\"label_info_text\":\"Whether the child's mother received 2+ doses of Td.\",\"hint\":\"Protected at birth (PAB)\",\"entity_id\":\"mother\",\"v_required\":{\"value\":true,\"err\":\"Please choose an option\"},\"values\":[\"Yes\",\"No\",\"Don't Know\"],\"openmrs_choice_ids\":{\"Yes\":\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"No\":\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Don't Know\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_relevance_rules.yml\"}}},\"is_visible\":true,\"value\":\"No\"},{\"key\":\"mother_hiv_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"type\":\"spinner\",\"hint\":\"HIV Status of the Child's Mother\",\"values\":[\"Positive\",\"Negative\",\"Unknown\"],\"openmrs_choice_ids\":{\"Positive\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Negative\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Unknown\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"step\":\"step1\",\"is-rule-check\":true,\"value\":\"HIV Status of the Child's Mother\"},{\"key\":\"child_hiv_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5303AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"spinner\",\"hint\":\"HIV Status of the Child\",\"values\":[\"Positive\",\"Negative\",\"Unknown\",\"Exposed\"],\"openmrs_choice_ids\":{\"Positive\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Negative\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Unknown\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Exposed\":\"822AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_relevance_rules.yml\"}}},\"is_visible\":false,\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"child_treatment\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"162240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"spinner\",\"label_info_text\":\"Indicate whether the child is on CPT and/or ART.\",\"hint\":\"Child's treatment\",\"values\":[\"CPT\",\"ART\",\"None\"],\"openmrs_choice_ids\":{\"CPT\":\"160434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"ART\":\"160119AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"None\":\"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_relevance_rules.yml\"}}},\"is_visible\":false},{\"key\":\"lost_to_follow_up\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"lost_to_follow_up\",\"type\":\"hidden\",\"value\":\"\"},{\"key\":\"inactive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"inactive\",\"type\":\"hidden\",\"value\":\"\"}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.6.59-SNAPSHOT\",\"formVersion\":\"\"}}";

    @Mock
    private EventClientRepository eventClientRepository;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private Repository repository;

    @Captor
    private ArgumentCaptor<JSONObject> eventClientAddOrUpdateClient;

    @Captor
    private ArgumentCaptor<ContentValues> dbUpdateDateOfRemoval;

    @Captor
    private ArgumentCaptor<ContentValues> allCommonsRepoUpdate;

    @Mock
    private AllCommonsRepository allCommonsRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private ClientProcessorForJava clientProcessorForJava;

    @Mock
    private UniqueIdRepository uniqueIdRepository;

    @Mock
    private UniqueId uniqueId;

    @Mock
    private DrishtiApplication drishtiApplication;

    @Mock
    private Compressor compressor;

    @Mock
    private Bitmap bitmap;

    @Mock
    private File file;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private org.smartregister.Context mContext;

    @Spy
    private AppProperties appProperties;

    @Mock
    private CloudantDataHandler cloudantDataHandler;

    private static final String TEST_BASE_URL = "https://abc.def";

    private final String[] baseEntityIds = new String[]{"fb8ce328-04d9", "bf1043a2-cb00"};

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jsonObject = new JSONObject();
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
    }

    @Test
    public void testGetFormAsJsonShouldReturnNullForEmptyForm() throws Exception {
        Assert.assertNull(ChildJsonFormUtils.getFormAsJson(null, "", "", "", null));
    }

    @Test
    public void testGetFormAsJsonForChildRegistrationShouldPopulateFormWithMetadata() throws Exception {
        JSONObject form = JsonFormUtils.toJSONObject(jsonForm);
        String formName = "Child Form";
        String entityId = "d9fc11c2-d70f-4b1f-86d4-617977d18661";
        String currentLocationId = "0fcaa0aa-33e9-4056-a42e-cc6481bf87fd";
        Map<String, String> metadata = new HashMap<>();
        metadata.put("FIRST_NAME", "Janet");
        metadata.put("LAST_NAME", "Doe");
        metadata.put("Sex", "Male");
        metadata.put("BIRTH_WEIGHT", "2.1");

        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add("HF 1");

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Facility");

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        ProfileImage image = Mockito.mock(ProfileImage.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(openSrpContext.imageRepository()).thenReturn(imageRepository);
        Mockito.when(imageRepository.findByEntityId("")).thenReturn(image);

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        Mockito.when(childLibrary.context()).thenReturn(openSrpContext);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, null, true);
        childMetadata.updateChildRegister(
                formName,
                "childTable",
                "guardianTable",
                "Birth Registration",
                "Birth Registration",
                "Immunization",
                "none",
                "12345",
                "Out of Catchment");
        childMetadata.setFieldsWithLocationHierarchy(new HashSet<>(Arrays.asList("Facility")));
        childMetadata.setHealthFacilityLevels(healthFacilities);
        childMetadata.setLocationLevels(allowedLevels);
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);

        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        JSONObject populatedForm = ChildJsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, metadata);
        Assert.assertNotNull(populatedForm);
        Assert.assertNotNull(populatedForm.getJSONObject(JsonFormConstants.STEP1));
        Assert.assertNotNull(populatedForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS));

        JSONArray fields = populatedForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
        Assert.assertEquals(26, fields.length());
        Assert.assertEquals(entityId.replace("-", ""), JsonFormUtils.getFieldValue(populatedForm.toString(), Constants.KEY.ZEIR_ID));
        Assert.assertEquals("Janet", JsonFormUtils.getFieldValue(fields, "first_name"));
        Assert.assertEquals("Doe", JsonFormUtils.getFieldValue(fields, "last_name"));
        Assert.assertEquals("Male", JsonFormUtils.getFieldValue(fields, "Sex"));
        Assert.assertEquals("2.1", JsonFormUtils.getFieldValue(fields, "Birth_Weight"));
    }

    @Test
    public void testGetFormAsJsonForOutOfCatchmentShouldPopulateZeirIdWhenVaccinesNotConfigured() throws Exception {
        JSONObject form = JsonFormUtils.toJSONObject(jsonForm);
        String formName = "Out of Catchment Form";
        String entityId = "d9fc11c2-d70f-4b1f-86d4-617977d18661";

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", immunizationLibrary);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(childLibrary.context()).thenReturn(openSrpContext);
        Mockito.doReturn(appProperties).when(openSrpContext).getAppProperties();
        Mockito.doReturn("false").when(appProperties).getProperty(ChildAppProperties.KEY.SHOW_OUT_OF_CATCHMENT_RECURRING_SERVICES);
        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, null, true);
        childMetadata.updateChildRegister(
                "",
                "childTable",
                "guardianTable",
                "Birth Registration",
                "Birth Registration",
                "Immunization",
                "none",
                "12345",
                "Out of Catchment Form");
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);

        JSONObject populatedForm = ChildJsonFormUtils.getFormAsJson(form, formName, entityId, "", null);
        Assert.assertNotNull(populatedForm);
        Assert.assertNotNull(populatedForm.getJSONObject(JsonFormConstants.STEP1));
        Assert.assertNotNull(populatedForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS));
        Assert.assertEquals(26, populatedForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).length());
        Assert.assertEquals(entityId.replace("-", ""), JsonFormUtils.getFieldValue(populatedForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS), "zeir_id"));
    }

    @Test
    public void isDateApproxWithNonNumberTest() throws Exception {
        boolean isApproximate = Whitebox.invokeMethod(ChildJsonFormUtils.class, "isDateApprox", "1500208620000L");
        Assert.assertFalse(isApproximate);
    }

    @Test
    public void isDateApproxWithNumberTest() throws Exception {
        boolean isApproximate = Whitebox.invokeMethod(ChildJsonFormUtils.class, "isDateApprox", "1");
        Assert.assertTrue(isApproximate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void processAgeWithWrongDateFormatTest() throws Exception {
        JSONObject result = Whitebox.invokeMethod(ChildJsonFormUtils.class, "processAge", "7/19/19", jsonObject);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.has(ChildJsonFormUtils.VALUE));
        Assert.assertNotNull(result.get(ChildJsonFormUtils.VALUE));
    }

    @Test
    public void processAgeTest() throws Exception {
        String TEST_DOB = "2017-01-01";
        LocalDate date = LocalDate.parse(TEST_DOB);
        Integer expectedDifferenceInYears = Period.between(date, LocalDate.now()).getYears();

        Whitebox.invokeMethod(ChildJsonFormUtils.class, "processAge", TEST_DOB, jsonObject);
        Assert.assertTrue(jsonObject.has(ChildJsonFormUtils.VALUE));
        Assert.assertNotNull(jsonObject.get(ChildJsonFormUtils.VALUE));
        Assert.assertEquals(expectedDifferenceInYears, jsonObject.get(ChildJsonFormUtils.VALUE));
    }

    @Test
    public void getChildLocationIdShouldReturnNullWhenCurrentLocalityIsNull() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        Assert.assertNull(ChildJsonFormUtils.getChildLocationId("98349797-489834", allSharedPreferences));
    }

    @Test
    public void getChildLocationIdShouldReturnCurrentLocalityIdWhenCurrentLocalityIsDifferentFromDefaultLocality() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        String currentLocality = "Kilimani";
        String currentLocalityId = "9943-43534-2dsfs";

        Mockito.doReturn(currentLocality)
                .when(allSharedPreferences)
                .fetchCurrentLocality();

        LocationHelper locationHelper = Mockito.mock(LocationHelper.class);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        Mockito.doReturn(currentLocalityId).when(locationHelper).getOpenMrsLocationId(Mockito.eq(currentLocality));

        Assert.assertEquals(currentLocalityId, ChildJsonFormUtils.getChildLocationId("98349797-489834", allSharedPreferences));
    }

    @Test
    public void mergeAndSaveClient() throws Exception {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("first_name", "John");
        Mockito.when(ecSyncHelper.getClient("234")).thenReturn(jsonObject);
        Mockito.when(childLibrary.getEcSyncHelper()).thenReturn(ecSyncHelper);

        Client client = new Client("234");
        ChildJsonFormUtils.mergeAndSaveClient(client);
        Mockito.verify(ecSyncHelper, Mockito.times(1))
                .addClient((String) addClientCaptor.capture(), (JSONObject) addClientCaptor.capture());

        JSONObject expected = new JSONObject();
        expected.put("baseEntityId", "234");
        expected.put("type", "Client");
        expected.put("first_name", "John");
        Assert.assertEquals("234", addClientCaptor.getAllValues().get(0));
        Assert.assertEquals(expected.optString("first_name"), ((JSONObject) addClientCaptor.getAllValues().get(1)).optString("first_name"));
    }

    @Test
    public void createBCGScarEvent() throws Exception {
        Context context = Mockito.mock(Context.class);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(openSrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        String providerId = "providerId";
        String teamName = "teamA";
        String teamId = "24234-234";
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(providerId);
        Mockito.when(allSharedPreferences.fetchDefaultTeam(providerId)).thenReturn(teamName);
        Mockito.when(allSharedPreferences.fetchDefaultTeamId(providerId)).thenReturn(teamId);
        Mockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn(null);

        ReflectionHelpers.setStaticField(ECSyncHelper.class, "instance", ecSyncHelper);
        ChildJsonFormUtils jsonFormUtils = new ChildJsonFormUtils();
        Whitebox.invokeMethod(jsonFormUtils,
                "createBCGScarEvent", context,
                "3434-234", providerId, "locationId");
        Mockito.verify(ecSyncHelper).addEvent((String) ecSyncHelperAddEventCaptor.capture(),
                (JSONObject) ecSyncHelperAddEventCaptor.capture());

        String baseEntityId = (String) ecSyncHelperAddEventCaptor.getAllValues().get(0);
        JSONObject eventJson = (JSONObject) ecSyncHelperAddEventCaptor.getAllValues().get(1);
        JSONObject obsJsonObject = eventJson.optJSONArray("obs").optJSONObject(0);
        Assert.assertEquals("3434-234", baseEntityId);
        Assert.assertEquals("bcg_scar", obsJsonObject.optString("formSubmissionField"));
        Assert.assertEquals("concept", obsJsonObject.optString("fieldType"));

        Assert.assertEquals("160265AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", obsJsonObject.optString("fieldCode"));
        Assert.assertEquals("select one", obsJsonObject.optString("fieldDataType"));
        Assert.assertEquals("Yes", obsJsonObject.optJSONArray("humanReadableValues").optString(0));
        Assert.assertEquals("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", obsJsonObject.optJSONArray("values").optString(0));

        Assert.assertEquals(ChildJsonFormUtils.BCG_SCAR_EVENT, eventJson.optString("eventType"));
        Assert.assertEquals(teamName, eventJson.optString("team"));
        Assert.assertEquals("child", eventJson.optString("entityType"));
        Assert.assertEquals(teamId, eventJson.optString("teamId"));
    }

    @Test
    public void testMotherDobUnknownUpdateFromAgeCalculatesDOBCorrectly() throws JSONException {

        JSONArray array = new JSONArray();

        JSONObject isBirthdateApproximate = new JSONObject();
        isBirthdateApproximate.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH_UNKNOWN);
        isBirthdateApproximate.put(Constants.OPENMRS.ENTITY, Constants.OPENMRS_ENTITY.PERSON);
        isBirthdateApproximate.put(Constants.OPENMRS.ENTITY_ID, FormEntityConstants.Person.birthdate_estimated);

        JSONObject dobOptions = new JSONObject();
        dobOptions.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH_UNKNOWN);
        dobOptions.put(Constants.KEY.VALUE, "true");

        JSONArray optArray = new JSONArray();
        optArray.put(dobOptions);

        isBirthdateApproximate.put(Constants.JSON_FORM_KEY.OPTIONS, optArray);
        array.put(isBirthdateApproximate);

        JSONObject ageJson = new JSONObject();
        ageJson.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_AGE);
        ageJson.put(Constants.KEY.VALUE, "21");
        ageJson.put(Constants.OPENMRS.ENTITY, "person_attribute");
        ageJson.put(Constants.OPENMRS.ENTITY_ID, JsonFormConstants.EDIT_TEXT);
        array.put(ageJson);

        JSONObject dobJson = new JSONObject();
        dobJson.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH);
        dobJson.put(Constants.KEY.VALUE, "");
        dobJson.put(Constants.OPENMRS.ENTITY, Constants.OPENMRS_ENTITY.PERSON);
        dobJson.put(Constants.OPENMRS.ENTITY_ID, FormEntityConstants.Person.birthdate);
        array.put(dobJson);

        JSONObject dobDateObject = ChildJsonFormUtils.getFieldJSONObject(array, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH);

        ChildJsonFormUtils.dobUnknownUpdateFromAge(array, Constants.KEY.MOTHER);

        Assert.assertNotNull(dobDateObject.getString(Constants.KEY.VALUE));

        Calendar cal = Calendar.getInstance();
        cal.setTime(Calendar.getInstance().getTime());
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.YEAR, -21);
        String expectedDate = new SimpleDateFormat(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN).format(cal.getTime());

        Assert.assertEquals(expectedDate, dobDateObject.get(Constants.KEY.VALUE));
    }

    @Test
    public void testMotherDobUnknownUpdateFromAgeSetsToFalseDOBUknownCheckbox() throws JSONException {
        JSONArray array = new JSONArray();

        JSONObject isBirthdateApproximate = new JSONObject();
        isBirthdateApproximate.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH_UNKNOWN);
        isBirthdateApproximate.put(Constants.OPENMRS.ENTITY, Constants.OPENMRS_ENTITY.PERSON);
        isBirthdateApproximate.put(Constants.OPENMRS.ENTITY_ID, FormEntityConstants.Person.birthdate_estimated);

        JSONObject dobOptions = new JSONObject();
        dobOptions.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH_UNKNOWN);
        dobOptions.put(Constants.KEY.VALUE, "false");
        JSONArray optArray = new JSONArray();
        optArray.put(dobOptions);

        isBirthdateApproximate.put(Constants.JSON_FORM_KEY.OPTIONS, optArray);
        array.put(isBirthdateApproximate);

        JSONObject ageJson = new JSONObject();
        ageJson.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_AGE);
        ageJson.put(Constants.KEY.VALUE, "21");
        ageJson.put(Constants.OPENMRS.ENTITY, "person_attribute");
        ageJson.put(Constants.OPENMRS.ENTITY_ID, JsonFormConstants.EDIT_TEXT);
        array.put(ageJson);

        JSONObject dobJson = new JSONObject();
        dobJson.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH);
        dobJson.put(Constants.KEY.VALUE, "");
        dobJson.put(Constants.OPENMRS.ENTITY, Constants.OPENMRS_ENTITY.PERSON);
        dobJson.put(Constants.OPENMRS.ENTITY_ID, FormEntityConstants.Person.birthdate);
        array.put(dobJson);

        JSONObject dobUnknownObject = ChildJsonFormUtils.getFieldJSONObject(array, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH_UNKNOWN);

        ChildJsonFormUtils.dobUnknownUpdateFromAge(array, Constants.KEY.MOTHER);
        Assert.assertEquals(dobUnknownObject.getJSONArray(Constants.KEY.VALUE).getJSONObject(0).get("value"), "false");
    }

    @Test
    public void testAddChildRegLocHierarchyQuestionsShouldFillFieldsWithLocationHierarchy() throws Exception {
        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add("Country");
        healthFacilities.add("Province");
        String entity = "232-432-3232";
        List<String> entityHierarchy = new ArrayList<>();
        entityHierarchy.add("Kenya");
        entityHierarchy.add("Central");
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, null, true, new RegisterQueryProvider());
        Mockito.when(childLibrary.metadata()).thenReturn(metadata);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        Mockito.doReturn("locationA").when(locationHelper).getOpenMrsLocationId(entity);
        Mockito.doReturn(entityHierarchy).when(locationHelper).getOpenMrsLocationHierarchy("locationA", false);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, BaseChildRegisterActivity.class,
                true);
        childMetadata.setFieldsWithLocationHierarchy(new HashSet<String>(Arrays.asList("Home_Facility")));
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);
        childMetadata.setHealthFacilityLevels(healthFacilities);
        List<FormLocation> entireTree = new ArrayList<>();
        FormLocation formLocationCountry = new FormLocation();
        formLocationCountry.level = "Country";
        formLocationCountry.name = "Kenya";
        formLocationCountry.key = "0";
        FormLocation formLocationProvince = new FormLocation();
        formLocationProvince.level = "Province";
        formLocationProvince.name = "Central";
        formLocationProvince.key = "1";
        List<FormLocation> entireTreeCountryNode = new ArrayList<>();
        entireTreeCountryNode.add(formLocationProvince);
        formLocationCountry.nodes = entireTreeCountryNode;
        entireTree.add(formLocationCountry);
        ArrayList<String> allLevels = Utils.metadata().getLocationLevels();
        Mockito.doReturn(entireTree).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(healthFacilities));
        Mockito.doReturn(entireTree).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(allLevels));
        Mockito.doReturn(healthFacilities).when(locationHelper).generateDefaultLocationHierarchy(ArgumentMatchers.eq(healthFacilities));
        Mockito.doReturn(allLevels).when(locationHelper).generateDefaultLocationHierarchy(ArgumentMatchers.eq(allLevels));
        JSONObject form = new JSONObject(registrationForm);
        ChildJsonFormUtils.addRegistrationFormLocationHierarchyQuestions(form);
        JSONArray fields = FormUtils.getMultiStepFormFields(form);
        JSONObject homeFacility = ChildJsonFormUtils.getFieldJSONObject(fields, Constants.HOME_FACILITY);
        JSONArray resultTreeObject = new JSONArray(homeFacility.optString(JsonFormConstants.TREE));
        Assert.assertTrue(resultTreeObject.optJSONObject(0).has("nodes"));
        Assert.assertEquals("Kenya", resultTreeObject.optJSONObject(0).optString("name"));
        Assert.assertEquals("Country", resultTreeObject.optJSONObject(0).optString("level"));
        Assert.assertEquals("0", resultTreeObject.optJSONObject(0).optString("key"));
        Assert.assertEquals("Central", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("name"));
        Assert.assertEquals("1", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("key"));
        Assert.assertEquals("Province", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("level"));
    }

    @Test
    public void testUpdateLocationStringShouldPopulateTreeAndDefaultAttributeUsingLocationHierarchyTree() throws Exception {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, null, true);
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);
        childMetadata.setFieldsWithLocationHierarchy(new HashSet<>(Arrays.asList("village")));

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.KEY, "village");
        jsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.TREE);
        jsonArray.put(jsonObject);
        String hierarchyString = "[\"Kenya\",\"Central\"]";
        String entireTreeString = "[{\"nodes\":[{\"level\":\"Province\",\"name\":\"Central\",\"key\":\"1\"}],\"level\":\"Country\",\"name\":\"Kenya\",\"key\":\"0\"}]";
        ArrayList<String> allLevels = Utils.metadata().getLocationLevels();
        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add("Country");
        healthFacilities.add("Province");

        List<FormLocation> entireTree = new ArrayList<>();
        FormLocation formLocationCountry = new FormLocation();
        formLocationCountry.level = "Country";
        formLocationCountry.name = "Kenya";
        formLocationCountry.key = "0";
        FormLocation formLocationProvince = new FormLocation();
        formLocationProvince.level = "Province";
        formLocationProvince.name = "Central";
        formLocationProvince.key = "1";

        List<FormLocation> entireTreeCountryNode = new ArrayList<>();
        entireTreeCountryNode.add(formLocationProvince);
        formLocationCountry.nodes = entireTreeCountryNode;
        entireTree.add(formLocationCountry);

        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        Mockito.doReturn(entireTree).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(healthFacilities));

        WhiteboxImpl.invokeMethod(ChildJsonFormUtils.class, "updateLocationTree", jsonArray, hierarchyString, entireTreeString, healthFacilities, allLevels);
        Assert.assertTrue(jsonObject.has(JsonFormConstants.TREE));
        Assert.assertTrue(jsonObject.has(JsonFormConstants.DEFAULT));
        Assert.assertEquals(hierarchyString, jsonObject.optString(JsonFormConstants.DEFAULT));
        JSONArray resultTreeObject = new JSONArray(jsonObject.optString(JsonFormConstants.TREE));
        Assert.assertTrue(resultTreeObject.optJSONObject(0).has("nodes"));
        Assert.assertEquals("Kenya", resultTreeObject.optJSONObject(0).optString("name"));
        Assert.assertEquals("Country", resultTreeObject.optJSONObject(0).optString("level"));
        Assert.assertEquals("0", resultTreeObject.optJSONObject(0).optString("key"));
        Assert.assertEquals("Central", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("name"));
        Assert.assertEquals("1", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("key"));
        Assert.assertEquals("Province", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("level"));
    }

    @Test
    public void testGenerateFormLocationTreeGeneratesCorrectFormTree() throws Exception {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        ArrayList<String> locations = new ArrayList<>();
        locations.add("Country");
        locations.add("County");
        locations.add("Village");

        ArrayList<String> filteredLocations = new ArrayList<>();
        filteredLocations.add("Country");
        filteredLocations.add("County");

        Mockito.doReturn(filteredLocations).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(filteredLocations));

        FormLocationTree formLocationTree = WhiteboxImpl.invokeMethod(ChildJsonFormUtils.class, "generateFormLocationTree", locations, false, "County");

        Assert.assertNotNull(formLocationTree);
        Assert.assertEquals("[\"Country\",\"County\"]", formLocationTree.getFormLocationString());
        List<FormLocation> formLocations = formLocationTree.getFormLocations();

        Assert.assertNotNull(formLocations);
        Assert.assertEquals(2, formLocations.size());
        Assert.assertEquals("Country", formLocations.get(0));
        Assert.assertEquals("County", formLocations.get(1));
    }

    @Test
    public void testSaveReportDeceasedShouldPassCorrectArguments() throws JSONException {
        String entityId = "b8798571-dee6-43b5-a289-fc75ab703792";
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, null, true, new RegisterQueryProvider());
        Mockito.when(childLibrary.metadata()).thenReturn(metadata);

        String childRegistrationClient = "{\"firstName\":\"Doe\",\"middleName\":\"Jane\",\"lastName\":\"Jane\",\"birthdate\":\"2019-07-02T02:00:00.000+02:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{\"mother\":[\"bdf50ebc-c352-421c-985d-9e9880d9ec58\",\"bdf50ebc-c352-421c-985d-9e9880d9ec58\"]},\"baseEntityId\":\"c4badbf0-89d4-40b9-8c37-68b0371797ed\",\"identifiers\":{\"zeir_id\":\"14750004\"},\"addresses\":[{\"addressType\":\"usual_residence\",\"addressFields\":{\"address5\":\"Not sure\"}}],\"attributes\":{\"age\":\"0.0\",\"Birth_Certificate\":\"ADG\\/23652432\\/1234\",\"second_phone_number\":\"0972343243\"},\"dateCreated\":\"2019-07-02T15:42:57.838+02:00\",\"serverVersion\":1562074977828,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":1,\"type\":\"Client\",\"id\":\"b8798571-dee6-43b5-a289-fc75ab703792\",\"revision\":\"v1\"}";
        JSONObject jsonClientObject = new JSONObject(childRegistrationClient);
        Mockito.when(eventClientRepository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(openSrpContext.allCommonsRepositoryobjects(metadata.getRegisterQueryProvider().getDemographicTable())).thenReturn(allCommonsRepository);
        Mockito.when(eventClientRepository.getClientByBaseEntityId(entityId)).thenReturn(jsonClientObject);
        Mockito.when(childLibrary.eventClientRepository()).thenReturn(eventClientRepository);
        Mockito.when(childLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(openSrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("demo");
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        String reportDeceasedForm = "{\"count\":\"1\",\"encounter_type\":\"Death\",\"entity_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-05-19 10:26:41\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-05-19 10:27:18\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"19-05-2020\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Report Deceased\",\"fields\":[{\"key\":\"Date_of_Death\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"date\",\"type\":\"date_picker\",\"hint\":\"Date of death \",\"expanded\":false,\"min_date\":\"19-05-2020\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Date cannot be past today's date\"},\"constraints\":[{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date of death can't occur before date of birth\"}],\"is-rule-check\":false,\"value\":\"19-05-2020\"},{\"key\":\"Cause_Death\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160218AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Suspected cause of death\",\"edit_type\":\"name\",\"value\":\"something terrible\"},{\"key\":\"Place_Death\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1541AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Where did the death occur? \",\"values\":[\"Health facility\",\"Home\"],\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please select one option\"},\"value\":\"Home\"},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"date_picker\",\"hint\":\"Child's DOB\",\"read_only\":true,\"hidden\":true,\"is_visible\":false,\"is-rule-check\":false,\"value\":\"19-05-2020\"}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.8.1-SNAPSHOT\",\"formVersion\":\"\"}}";
        ChildJsonFormUtils.saveReportDeceased(context, reportDeceasedForm, "434-2342", entityId);

        Mockito.verify(eventClientRepository).addorUpdateClient(Mockito.eq(entityId), eventClientAddOrUpdateClient.capture());

        Mockito.verify(eventClientRepository, Mockito.atLeastOnce())
                .addEvent(Mockito.eq(entityId), Mockito.any(JSONObject.class));

        Mockito.verify(allCommonsRepository)
                .update(Mockito.eq(metadata.getRegisterQueryProvider().getDemographicTable()), allCommonsRepoUpdate.capture(), Mockito.eq(entityId));

        Mockito.verify(sqLiteDatabase).update(Mockito.eq(metadata.getRegisterQueryProvider().getDemographicTable()),
                dbUpdateDateOfRemoval.capture(), Mockito.eq(Constants.KEY.BASE_ENTITY_ID + " = ?"), Mockito.eq(new String[]{entityId}));

        JSONObject resultEventAddOrUpdate = eventClientAddOrUpdateClient.getValue();
        Assert.assertNotNull(resultEventAddOrUpdate);
        Assert.assertEquals("2020-05-19T00:00:00.000Z", resultEventAddOrUpdate.optString("deathdate"));
        Assert.assertFalse(resultEventAddOrUpdate.optBoolean("deathdate_estimated"));

        ContentValues contentValues = allCommonsRepoUpdate.getValue();
        Assert.assertNotNull(contentValues);

        Assert.assertEquals("19-05-2020", contentValues.get(Constants.KEY.DOD));
        Assert.assertEquals(Utils.getTodaysDate(), contentValues.get(Constants.KEY.DATE_REMOVED));


        ContentValues contentValues1 = dbUpdateDateOfRemoval.getValue();
        Assert.assertNotNull(contentValues1);
        Assert.assertEquals(contentValues1.get(Constants.KEY.DATE_REMOVED), "2020-05-19T00:00:00.000Z");

    }

    @Test
    @PrepareForTest(ImmunizationLibrary.class)
    public void testAddAvailableVaccinesShouldPopulateForm() throws Exception {
        JSONObject formObject = new JSONObject(registrationForm);
        int initialSize = formObject.optJSONObject(JsonFormConstants.STEP1).optJSONArray(JsonFormConstants.FIELDS).length();
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", immunizationLibrary);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        Mockito.doReturn(appProperties).when(openSrpContext).getAppProperties();
        Mockito.doReturn("false").when(appProperties).getProperty(ChildAppProperties.KEY.SHOW_OUT_OF_CATCHMENT_RECURRING_SERVICES);
        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        Whitebox.invokeMethod(ChildJsonFormUtils.class, "addAvailableVaccines", ApplicationProvider.getApplicationContext(), formObject);
        JSONObject step1 = formObject.optJSONObject(JsonFormConstants.STEP1);
        JSONArray fields = step1.optJSONArray(JsonFormConstants.FIELDS);
        Assert.assertEquals((initialSize + 7), fields.length()); // 6 vaccine groups + a label
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.optJSONObject(i);
            if (field.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                JSONArray options = field.optJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                Set<String> keySet = getOptionKeys(options);
                if ("Birth".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("OPV 0"));
                    Assert.assertTrue(keySet.contains("BCG"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(2, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                } else if ("Six_Wks".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("OPV 1"));
                    Assert.assertTrue(keySet.contains("Penta 1"));
                    Assert.assertTrue(keySet.contains("PCV 1"));
                    Assert.assertTrue(keySet.contains("Rota 1"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(4, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                } else if ("Ten_Wks".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("OPV 2"));
                    Assert.assertTrue(keySet.contains("Penta 2"));
                    Assert.assertTrue(keySet.contains("Rota 2"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(4, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                } else if ("Fourteen_Weeks".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("OPV 3"));
                    Assert.assertTrue(keySet.contains("Penta 3"));
                    Assert.assertTrue(keySet.contains("PCV 3"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(3, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                } else if ("Nine_Months".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("Measles 1"));
                    Assert.assertTrue(keySet.contains("MR 1"));
                    Assert.assertTrue(keySet.contains("OPV 4"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(3, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                } else if ("Eighteen_Months".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("Measles 2"));
                    Assert.assertTrue(keySet.contains("MR 2"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(2, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                }
            }
        }
    }

    private Set<String> getOptionKeys(JSONArray options) {
        Set<String> keySet = new HashSet<>();
        for (int i = 0; i < options.length(); i++) {
            JSONObject obj = options.optJSONObject(i);
            keySet.add(obj.optString(JsonFormConstants.KEY));
        }
        return keySet;
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(CloudantDataHandler.class, "instance", null);
    }

    @Test
    public void testFormTagCreatesValidInstance() {

        String provider = "demo";
        String appVersionName = "1.0.3";

        Mockito.doReturn(provider).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(3).when(childLibrary).getApplicationVersion();
        Mockito.doReturn(appVersionName).when(childLibrary).getApplicationVersionName();
        Mockito.doReturn(5).when(childLibrary).getDatabaseVersion();

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        FormTag formTag = ChildJsonFormUtils.formTag(allSharedPreferences);

        Assert.assertNotNull(formTag);
        Assert.assertEquals(provider, formTag.providerId);
        Assert.assertEquals(appVersionName, formTag.appVersionName);
        Assert.assertEquals(Integer.valueOf(3), formTag.appVersion);
        Assert.assertEquals(Integer.valueOf(5), formTag.databaseVersion);
    }

    @Test
    public void testUpdateClientAttribute() throws Exception {
        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(repository).getReadableDatabase();
        Mockito.doReturn(repository).when(childLibrary).getRepository();
        Mockito.doReturn(openSrpContext).when(coreLibrary).context();
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        Mockito.doReturn(clientProcessorForJava).when(childLibrary).getClientProcessorForJava();
        Mockito.doReturn(ecSyncHelper).when(childLibrary).getEcSyncHelper();

        String attributeName = "Child_Status";
        String attributeValue = "Inactive";
        String oldAttributValue = "Lost to follow up";
        String baseEntityId = "b8798571-dee6-43b5-a289-fc75ab703792";
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null, null, null, true, new RegisterQueryProvider());
        Mockito.when(childLibrary.metadata()).thenReturn(metadata);

        JSONObject client = new JSONObject();
        client.put("baseEntityId", baseEntityId);
        JSONObject clientAttributes = new JSONObject();
        clientAttributes.put(attributeName, oldAttributValue);
        client.put(ChildJsonFormUtils.attributes, clientAttributes);

        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(allSharedPreferences).when(openSrpContext).allSharedPreferences();
        Mockito.doReturn(client).when(eventClientRepository).getClientByBaseEntityId(baseEntityId);
        Mockito.doReturn(eventClientRepository).when(openSrpContext).getEventClientRepository();
        Mockito.doReturn(eventClientRepository).when(childLibrary).eventClientRepository();
        Mockito.doNothing().when(eventClientRepository).addorUpdateClient(ArgumentMatchers.endsWith(baseEntityId), ArgumentMatchers.any(JSONObject.class));

        Mockito.doReturn("My Location").when(allSharedPreferences).fetchCurrentLocality();


        //Verify client details updated correctly on device
        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put("baseEntityId", baseEntityId);
        childDetails.put("first_name", "Alaine");
        childDetails.put("last_name", "Nasenyana");
        childDetails.put(attributeName, oldAttributValue);

        ArrayList<HashMap<String, String>> dbRecords = new ArrayList<>();
        dbRecords.add(childDetails);

        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(baseEntityId, childDetails, "child");
        commonPersonObjectClient.setColumnmaps(childDetails);

        Mockito.doReturn(dbRecords).when(eventClientRepository).rawQuery(ArgumentMatchers.any(SQLiteDatabase.class), ArgumentMatchers.anyString());

        ChildJsonFormUtils.updateClientAttribute(openSrpContext, commonPersonObjectClient, locationHelper, attributeName, attributeValue);

        ArgumentCaptor<String> tableArgumentCaptor = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<ContentValues> queryParamsArgumentCaptor = ArgumentCaptor.forClass(ContentValues.class);

        ArgumentCaptor<String> filterArgumentCaptor = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<String[]> filterArgumentsCaptor = ArgumentCaptor.forClass(String[].class);

        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(tableArgumentCaptor.capture(), queryParamsArgumentCaptor.capture(), filterArgumentCaptor.capture(), filterArgumentsCaptor.capture());

        String table = tableArgumentCaptor.getValue();

        Assert.assertNotNull(table);
        Assert.assertEquals("ec_child_details", table);

        String filterArgumentCaptorValue = filterArgumentCaptor.getValue();

        Assert.assertNotNull(filterArgumentCaptorValue);
        Assert.assertEquals("base_entity_id= ?", filterArgumentCaptorValue);

        String[] filterArgumentsCaptorValue = filterArgumentsCaptor.getValue();

        Assert.assertNotNull(filterArgumentsCaptorValue);
        Assert.assertEquals(1, filterArgumentsCaptorValue.length);
        Assert.assertEquals(baseEntityId, filterArgumentsCaptorValue[0]);

        ContentValues contentValues = queryParamsArgumentCaptor.getValue();

        Assert.assertNotNull(contentValues);
        Assert.assertEquals("child_status=Inactive", contentValues.toString());


        //Verify Client document is generated correctly
        ArgumentCaptor<JSONObject> updatedClientArgCaptor = ArgumentCaptor.forClass(JSONObject.class);
        ArgumentCaptor<String> baseEntityIdArgCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(eventClientRepository).addorUpdateClient(baseEntityIdArgCaptor.capture(), updatedClientArgCaptor.capture());

        JSONObject updatedClient = updatedClientArgCaptor.getValue();
        Assert.assertNotNull(updatedClient);
        Assert.assertEquals(baseEntityId, client.getString("baseEntityId"));
        Assert.assertTrue(updatedClient.has(ChildJsonFormUtils.attributes));
        Assert.assertEquals("{\"Child_Status\":\"Inactive\"}", updatedClient.getString(ChildJsonFormUtils.attributes));


        //Verify update Event document is generated correctly
        ArgumentCaptor<JSONObject> updatedEventArgCaptor = ArgumentCaptor.forClass(JSONObject.class);
        ArgumentCaptor<String> baseEntityIdArgCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(eventClientRepository).addEvent(baseEntityIdArgCaptor2.capture(), updatedEventArgCaptor.capture());

        JSONObject updatedEvent = updatedEventArgCaptor.getValue();
        Assert.assertNotNull(updatedEvent);
        Assert.assertEquals(baseEntityId, updatedEvent.getString("baseEntityId"));
        Assert.assertEquals("Event", updatedEvent.getString("type"));
        Assert.assertEquals("Update Birth Registration", updatedEvent.getString("eventType"));
        Assert.assertEquals("demo", updatedEvent.getString("providerId"));
    }

    @Test
    public void testProcessChildDetailsFormShouldPrepareClientAndEventObjects() {
        String provider = "Provider X";
        String appVersionName = "2.0.8";
        Mockito.doReturn(provider).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(3).when(childLibrary).getApplicationVersion();
        Mockito.doReturn(appVersionName).when(childLibrary).getApplicationVersionName();
        Mockito.doReturn(5).when(childLibrary).getDatabaseVersion();
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        FormTag formTag = ChildJsonFormUtils.formTag(allSharedPreferences);

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(openSrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);

        ChildEventClient client = ChildJsonFormUtils.processChildDetailsForm(jsonForm, formTag);
        Assert.assertNotNull(client);
        Assert.assertNotNull(client.getClient());
        Assert.assertNotNull(client.getClient().getBaseEntityId());
        Assert.assertEquals(1, client.getClient().getIdentifiers().size());
        Assert.assertEquals(1, client.getClient().getAttributes().size());
        Assert.assertEquals(appVersionName, client.getClient().getClientApplicationVersionName());
        Assert.assertEquals("Saida", client.getClient().getFirstName());
        Assert.assertEquals("Mui", client.getClient().getMiddleName());
        Assert.assertEquals("Mona", client.getClient().getLastName());
        Assert.assertEquals("Male", client.getClient().getGender());

        Assert.assertNotNull(client.getEvent());
        Assert.assertEquals(provider, client.getEvent().getProviderId());
        Assert.assertEquals(appVersionName, client.getEvent().getClientApplicationVersionName());
        Assert.assertNotNull(client.getEvent().getBaseEntityId());
        Assert.assertEquals(client.getClient().getBaseEntityId(), client.getEvent().getBaseEntityId());
        Assert.assertEquals("Birth Registration", client.getEvent().getEventType());
        Assert.assertEquals("child", client.getEvent().getEntityType());
    }

    @Test
    public void testFieldsReturnsJSONArrayForNonEmptyFormString() {
        JSONObject form = JsonFormUtils.toJSONObject(registrationForm);
        String step = "step1";

        JSONArray formFields = ChildJsonFormUtils.fields(form, step);
        Assert.assertNotNull(formFields);
        Assert.assertEquals(22, formFields.length());
    }

    @Test
    public void testFieldsReturnsNullForInvalidStep() {
        JSONObject form = JsonFormUtils.toJSONObject(registrationForm);
        String step = "step2";

        JSONArray formFields = ChildJsonFormUtils.fields(form, step);
        Assert.assertNull(formFields);
    }

    @Test
    public void testGetFieldValueReturnsNullForEmptyForm() {
        String value = ChildJsonFormUtils.getFieldValue("", "", "");
        Assert.assertNull(value);
    }

    @Test
    public void testGetFieldValueReturnsNullForFormWithoutFields() {
        String jsonFormString = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"step1\":{\"title\":\"Birth Registration\"},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.6.59-SNAPSHOT\",\"formVersion\":\"\"}}";
        String value = ChildJsonFormUtils.getFieldValue(jsonFormString, "step1", "last_name");
        Assert.assertNull(value);
    }

    @Test
    public void testGetFieldValueReturnsValidStringForCompleteForm() {
        String jsonFormString = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"step1\":{\"title\":\"Birth Registration\",\"fields\":[{\"key\":\"last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"hint\":\"Last name\",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please enter the last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"Mona\"},{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"v_required\":{\"value\":true,\"err\":\"Please enter a first name\"},\"value\":\"Saida\"}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.6.59-SNAPSHOT\",\"formVersion\":\"\"}}";

        String value = ChildJsonFormUtils.getFieldValue(jsonFormString, "step1", "last_name");
        Assert.assertNotNull(value);
        Assert.assertEquals("Mona", value);
    }

    @Test
    public void testProcessMotherRegistrationFormPopulatesEventClientCorrectly() {
        String relationalId = "relational-id";
        String entityId = "123456";
        String userLocalityId = "user-locality-id";
        String defaultLocalityId = "default-locality-id";
        String currentLocalityId = "current-locality-id";
        String openmrsLocalityId = "openmrs-locality-id";
        String defaultTeam = "default-team";
        String defaultTeamId = "default-team-id";
        String userId = "user-1";
        String openMrsId = "unique-id-1";

        List<EventClient> eventClients = new ArrayList<>();

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        Mockito.when(childLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(childLibrary.getDatabaseVersion()).thenReturn(1);
        Mockito.when(childLibrary.getApplicationVersion()).thenReturn(5);
        Mockito.when(childLibrary.getUniqueIdRepository()).thenReturn(uniqueIdRepository);
        Mockito.when(childLibrary.eventClientRepository()).thenReturn(eventClientRepository);
        Mockito.when(uniqueIdRepository.getNextUniqueId()).thenReturn(uniqueId);
        Mockito.when(uniqueId.getOpenmrsId()).thenReturn(openMrsId);
        Mockito.when(eventClientRepository.getEventsByBaseEntityIdsAndSyncStatus(ArgumentMatchers.anyString(), ArgumentMatchers.anyList())).thenReturn(eventClients);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        Mockito.when(openSrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(userId);

        Mockito.when(allSharedPreferences.fetchUserLocalityId(ArgumentMatchers.anyString())).thenReturn(userLocalityId);
        Mockito.when(allSharedPreferences.fetchDefaultLocalityId(ArgumentMatchers.anyString())).thenReturn(defaultLocalityId);
        Mockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn(currentLocalityId);
        Mockito.when(allSharedPreferences.fetchDefaultTeam(ArgumentMatchers.anyString())).thenReturn(defaultTeam);
        Mockito.when(allSharedPreferences.fetchDefaultTeamId(ArgumentMatchers.anyString())).thenReturn(defaultTeamId);

        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        Mockito.when(locationHelper.getOpenMrsLocationId(ArgumentMatchers.anyString())).thenReturn(openmrsLocalityId);

        ChildEventClient base = new ChildEventClient(new Client(entityId), new Event());

        ChildEventClient childEventClient = ChildJsonFormUtils.processMotherRegistrationForm(jsonForm, relationalId, base);

        Assert.assertNotNull(childEventClient);
        Assert.assertNotNull(childEventClient.getClient());
        Assert.assertNotNull(childEventClient.getEvent());

        Client client = childEventClient.getClient();
        Assert.assertEquals(relationalId, client.getBaseEntityId());
        Assert.assertEquals("esther", client.getFirstName());
        Assert.assertEquals("heung", client.getLastName());
        Assert.assertEquals("female", client.getGender());
        Assert.assertEquals(1, client.getIdentifiers().size());
        Assert.assertTrue(client.getIdentifiers().containsKey("M_ZEIR_ID"));
        Assert.assertEquals(openMrsId, client.getIdentifiers().get("M_ZEIR_ID"));

        Event event = childEventClient.getEvent();
        Assert.assertNotNull(event);
        Assert.assertEquals(relationalId, event.getBaseEntityId());
        Assert.assertEquals(userId, event.getProviderId());
        Assert.assertEquals("New Woman Registration", event.getEventType());
        Assert.assertEquals("mother", event.getEntityType());
        Assert.assertEquals(defaultTeam, event.getTeam());
        Assert.assertEquals(defaultTeamId, event.getTeamId());
        Assert.assertEquals(userLocalityId, event.getLocationId());
        Assert.assertEquals(openmrsLocalityId, event.getChildLocationId());
        Assert.assertEquals(5, (long) event.getClientApplicationVersion());
        Assert.assertEquals(1, (long) event.getClientDatabaseVersion());
    }

    @Test
    public void testProcessFatherRegistrationFormReturnsWhenNullWhenFatherDetailsAreMissing() {
        String relationalId = "relational-id";
        String entityId = "123456";

        ChildEventClient base = new ChildEventClient(new Client(entityId), new Event());

        ChildEventClient childEventClient = ChildJsonFormUtils.processFatherRegistrationForm(jsonForm, relationalId, base);
        Assert.assertNull(childEventClient);
    }

    @Test
    public void testSaveImageShouldSaveNewImageWithEntityIdAsFileName() throws IOException {
        String providerId = "a52e91ae-f1ab-4c81-a2b5-1c2d258f6afe";
        String entityId = "357fbff7-8073-4495-a980-528c613298e8";
        String imageLocation = "src/test/resources/test-image.JPEG";
        String appDir = "src/test/resources";

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        Mockito.when(childLibrary.getCompressor()).thenReturn(compressor);
        Mockito.when(compressor.compressToBitmap(ArgumentMatchers.any(File.class))).thenReturn(bitmap);

        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        Mockito.when(drishtiApplication.getApplicationContext()).thenReturn(context);
        Mockito.when(context.getDir(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(file);
        Mockito.when(DrishtiApplication.getAppDir()).thenReturn(appDir);

        Mockito.when(Utils.context()).thenReturn(mContext);
        Mockito.when(mContext.imageRepository()).thenReturn(imageRepository);

        ChildJsonFormUtils.saveImage(providerId, entityId, imageLocation);

        String newFilePath = appDir + "/" + entityId + ".JPEG";
        File newFile = new File(newFilePath);
        Assert.assertTrue(newFile.exists());
        newFile.delete();
    }

    @Test
    public void testGetMetadataForEditFormReturnsEmptyStringOnError() {
        String forEditForm = ChildJsonFormUtils.getMetadataForEditForm(context, new HashMap<String, String>());
        Assert.assertEquals("", forEditForm);
    }

    @Test
    public void testGetMetadataForEditFormPopulatesFormCorrectly() throws Exception {
        String entityId = "6497c0ab-0483-4441-b6ed-f30be8040ce0";

        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add("Karura Health Centre");

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Health Facility");

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(CloudantDataHandler.class, "instance", cloudantDataHandler);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        Mockito.when(context.getApplicationContext()).thenReturn(RuntimeEnvironment.application.getApplicationContext());
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(childLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(context.getResources()).thenReturn(RuntimeEnvironment.application.getResources());

        LocationPickerView view = Mockito.mock(LocationPickerView.class);
        Mockito.when(childLibrary.getLocationPickerView(ArgumentMatchers.any(Context.class))).thenReturn(view);
        Mockito.when(view.getSelectedItem()).thenReturn("Karura Health Centre");

        ChildMetadata childMetadata = new ChildMetadata(
                BaseChildFormActivity.class,
                BaseProfileActivity.class,
                BaseChildImmunizationActivity.class,
                null,
                true
        );
        childMetadata.updateChildRegister(
                "child_registration",
                "childTable",
                "guardianTable",
                "Birth Registration",
                "Birth Registration",
                "Immunization",
                "none",
                "12345",
                "Out of Catchment");
        childMetadata.setFieldsWithLocationHierarchy(new HashSet<>(allowedLevels));
        childMetadata.setHealthFacilityLevels(healthFacilities);
        childMetadata.setLocationLevels(allowedLevels);

        Mockito.when(Utils.metadata()).thenReturn(childMetadata);

        List<String> hierarchy = new ArrayList<>();
        hierarchy.add("Karura Health Centre");
        Mockito.when(locationHelper.generateDefaultLocationHierarchy(ArgumentMatchers.anyList())).thenReturn(hierarchy);
        Mockito.when(locationHelper.getOpenMrsLocationHierarchy(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(hierarchy);

        Map<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.ZEIR_ID, entityId);
        childDetails.put(Constants.KEY.FIRST_NAME, "Hames");
        childDetails.put(Constants.KEY.LAST_NAME, "Guez");
        childDetails.put(Constants.KEY.GENDER, "Male");
        childDetails.put(Constants.KEY.DOB, "2020-11-01");
        childDetails.put(Constants.KEY.FIRST_HEALTH_FACILITY_CONTACT, "2020-11-10");
        childDetails.put("birth_weight", "3");
        childDetails.put("birth_height", "12");
        childDetails.put("mother_first_name", "Loving");
        childDetails.put("mother_last_name", "Mom");
        childDetails.put("mother_dob", "2000-01-01");
        childDetails.put("mother_guardian_phone_number", "700123123");
        childDetails.put("birth_place", "Health Facility");
        childDetails.put("home_facility", "Karura Health Centre");
        childDetails.put("address2", "123 Kiamb");

        String forEditForm = ChildJsonFormUtils.getMetadataForEditForm(context, childDetails);
        Assert.assertNotNull(forEditForm);

        JSONObject populatedForm = JsonFormUtils.toJSONObject(forEditForm);

        Assert.assertEquals("Karura Health Centre", populatedForm.getJSONObject("metadata").getString("encounter_location"));

        Assert.assertNotNull(populatedForm);
        Assert.assertNotNull(populatedForm.getJSONObject(STEP1));
        Assert.assertNotNull(populatedForm.getJSONObject(STEP1).getJSONArray(FIELDS));

        JSONArray fields = populatedForm.getJSONObject(STEP1).getJSONArray(FIELDS);
        Assert.assertEquals(22, fields.length());
        Assert.assertEquals(entityId.replace("-", ""), JsonFormUtils.getFieldValue(fields, Constants.KEY.ZEIR_ID));
        Assert.assertEquals("Hames", JsonFormUtils.getFieldValue(fields, Constants.KEY.FIRST_NAME));
        Assert.assertEquals("Guez", JsonFormUtils.getFieldValue(fields, Constants.KEY.LAST_NAME));
        Assert.assertEquals("Male", JsonFormUtils.getFieldValue(fields, Constants.KEY.GENDER));
        Assert.assertEquals("01-11-2020", JsonFormUtils.getFieldValue(fields, "birth_date"));
        Assert.assertEquals("10-11-2020", JsonFormUtils.getFieldValue(fields, Constants.KEY.FIRST_HEALTH_FACILITY_CONTACT));
        Assert.assertEquals("3", JsonFormUtils.getFieldValue(fields, "birth_weight"));
        Assert.assertEquals("12", JsonFormUtils.getFieldValue(fields, "birth_height"));
        Assert.assertEquals("Loving", JsonFormUtils.getFieldValue(fields, "mother_guardian_first_name"));
        Assert.assertEquals("Mom", JsonFormUtils.getFieldValue(fields, "mother_guardian_last_name"));
        Assert.assertEquals("01-01-2000", JsonFormUtils.getFieldValue(fields, "mother_dob"));
        Assert.assertEquals("700123123", JsonFormUtils.getFieldValue(fields, "mother_guardian_phone_number"));
        Assert.assertEquals("Health Facility", JsonFormUtils.getFieldValue(fields, "birth_place"));
        Assert.assertEquals("[\"Karura Health Centre\"]", JsonFormUtils.getFieldValue(fields, "home_facility"));
        Assert.assertEquals("123 Kiamb", JsonFormUtils.getFieldValue(fields, "residential_address"));
    }

    @Test(expected = Exception.class)
    public void testProcessReturnedAdvanceSearchResultsThrowsExceptionWhenResponseIsNull() {
        List<ChildMotherDetailModel> models = ChildJsonFormUtils.processReturnedAdvanceSearchResults(null);
        Assert.assertNull(models);
    }

    @Test
    public void testProcessReturnedAdvanceSearchResultsReturnsEmptyListWhenResponseStatusIsFailure() {
        Response<String> response = new Response<>(ResponseStatus.failure, null);

        List<ChildMotherDetailModel> models = ChildJsonFormUtils.processReturnedAdvanceSearchResults(response);
        Assert.assertNotNull(models);
        Assert.assertEquals(0, models.size());
    }

    @Test(expected = Exception.class)
    public void testProcessReturnedAdvanceSearchResultsThrowsExceptionWhenResponseStatusIsSuccessAndPayloadIsNull() {
        Response<String> response = new Response<>(ResponseStatus.success, null);

        List<ChildMotherDetailModel> models = ChildJsonFormUtils.processReturnedAdvanceSearchResults(response);
        Assert.assertNull(models);
    }

    @Test
    public void testProcessReturnedAdvanceSearchResultsReturnsEmptyListWhenResponseStatusIsSuccessAndPayloadIsEmptyJSONArray() {
        Response<String> response = new Response<>(ResponseStatus.success, "[]");

        List<ChildMotherDetailModel> models = ChildJsonFormUtils.processReturnedAdvanceSearchResults(response);
        Assert.assertNotNull(models);
        Assert.assertEquals(0, models.size());
    }

    @Test
    public void testProcessReturnedAdvanceSearchResultsReturnsCorrectChildData() {
        String searchResponse = "[{\"type\":\"Client\",\"dateCreated\":\"2020-10-13T07:57:11.105+01:00\",\"serverVersion\":1600329284434,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":11,\"baseEntityId\":\"20770b0a-f8e9-4ed5-bf25-bb10c4a2cfbc\",\"identifiers\":{\"zeir_id\":\"2000000\"},\"addresses\":[{\"addressType\":\"\",\"addressFields\":{\"address1\":\"Small villa\",\"address2\":\"Illinois\"}}],\"attributes\":{\"age\":\"0.18\",\"child_reg\":\"19012990192\",\"ga_at_birth\":\"39\",\"sms_recipient\":\"mother\",\"place_of_birth\":\"hospital\",\"birth_registration_number\":\"2020/0809\",\"inactive\":\"false\",\"lost_to_follow_up\":\"true\"},\"firstName\":\"Benjamin\",\"lastName\":\"Franklin\",\"birthdate\":\"2020-08-09T13:00:00.000+01:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Male\",\"relationships\":{\"father\":[\"ef052dab-564e-47c6-8550-dbedc50ae06f\"],\"mother\":[\"29a28f93-779d-4936-aee2-1fdb02eee9b9\"]},\"teamId\":\"8c1112a5-7d17-41b3-b8fa-e1dafa87e9e4\",\"_id\":\"c0e7fd14-308a-4475-bc1b-1b651f5bf105\",\"_rev\":\"v1\"},{\"type\":\"Client\",\"dateCreated\":\"2020-10-16T07:57:11.105+01:00\",\"serverVersion\":1,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":11,\"baseEntityId\":\"4e7eec63-ba90-44b5-9eea-e2835c5582c3\",\"identifiers\":{\"zeir_id\":\"1000000\"},\"addresses\":[{\"addressType\":\"\",\"addressFields\":{\"address1\":\"Large Villa\",\"address2\":\"Nimesota\"}}],\"attributes\":{\"age\":\"0.5\",\"child_reg\":\"19013000000\",\"ga_at_birth\":\"30\",\"sms_recipient\":\"mother\",\"place_of_birth\":\"hospital\",\"birth_registration_number\":\"2020/0900\",\"inactive\":\"false\",\"lost_to_follow_up\":\"false\"},\"firstName\":\"Anto\",\"lastName\":\"Nio\",\"birthdate\":\"2020-05-05T00:00:00.000+01:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{\"father\":[\"ef052dab-564e-47c6-8550-dbedc50ae06f\"],\"mother\":[\"29a28f93-779d-4936-aee2-1fdb02eee9b9\"]},\"teamId\":\"6eac92bd-5886-436e-9a3b-ebec540a8cf5\",\"_id\":\"8e57d028-b112-4ff7-a19b-ea0b144c98d2\",\"_rev\":\"v1\"},{\"type\":\"Client\",\"dateCreated\":\"2020-10-13T07:57:11.144+01:00\",\"serverVersion\":1600329284435,\"baseEntityId\":\"29a28f93-779d-4936-aee2-1fdb02eee9b9\",\"identifiers\":{\"M_ZEIR_ID\":\"100003-3\"},\"addresses\":[{\"addressType\":\"\",\"addressFields\":{\"address1\":\"Small villa\",\"address2\":\"Illinois\"}}],\"attributes\":{\"first_birth\":\"yes\",\"mother_rubella\":\"yes\",\"mother_tdv_doses\":\"2_plus_tdv_doses\",\"rubella_serology\":\"yes\",\"serology_results\":\"negative\",\"mother_nationality\":\"other\",\"second_phone_number\":\"23233232\",\"mother_guardian_number\":\"07456566\",\"mother_nationality_other\":\"American\"},\"firstName\":\"Gates\",\"lastName\":\"Belinda\",\"birthdate\":\"1973-01-01T13:00:00.000+01:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"_id\":\"95c4951a-aadb-4b41-8e50-8f7566fea40b\",\"_rev\":\"v1\"}]";

        Response<String> response = new Response<>(ResponseStatus.success, searchResponse);

        List<ChildMotherDetailModel> models = ChildJsonFormUtils.processReturnedAdvanceSearchResults(response);

        Assert.assertNotNull(models);
        Assert.assertEquals(2, models.size());

        // child 1
        ChildMotherDetailModel model = models.get(0);

        Assert.assertEquals("c0e7fd14-308a-4475-bc1b-1b651f5bf105", model.getId());
        Assert.assertEquals("20770b0a-f8e9-4ed5-bf25-bb10c4a2cfbc", model.getChildBaseEntityId());
        Assert.assertEquals("Benjamin", model.getFirstName());
        Assert.assertEquals("Franklin", model.getLastName());
        Assert.assertEquals("Male", model.getGender());
        Assert.assertEquals("2020-08-09T13:00:00.000+01:00", model.getDateOfBirth());
        Assert.assertEquals("29a28f93-779d-4936-aee2-1fdb02eee9b9", model.getRelationalId());
        Assert.assertEquals("29a28f93-779d-4936-aee2-1fdb02eee9b9", model.getMotherBaseEntityId());
        Assert.assertEquals("ef052dab-564e-47c6-8550-dbedc50ae06f", model.getFatherBaseEntityId());
        Assert.assertEquals("2000000", model.getZeirId());
        Assert.assertEquals("false", model.getInActive());
        Assert.assertEquals("true", model.getLostFollowUp());
        Assert.assertEquals("Gates", model.getMotherFirstName());
        Assert.assertEquals("Belinda", model.getMotherLastName());

        // child 2
        model = models.get(1);

        Assert.assertEquals("8e57d028-b112-4ff7-a19b-ea0b144c98d2", model.getId());
        Assert.assertEquals("4e7eec63-ba90-44b5-9eea-e2835c5582c3", model.getChildBaseEntityId());
        Assert.assertEquals("Anto", model.getFirstName());
        Assert.assertEquals("Nio", model.getLastName());
        Assert.assertEquals("Female", model.getGender());
        Assert.assertEquals("2020-05-05T00:00:00.000+01:00", model.getDateOfBirth());
        Assert.assertEquals("29a28f93-779d-4936-aee2-1fdb02eee9b9", model.getRelationalId());
        Assert.assertEquals("29a28f93-779d-4936-aee2-1fdb02eee9b9", model.getMotherBaseEntityId());
        Assert.assertEquals("ef052dab-564e-47c6-8550-dbedc50ae06f", model.getFatherBaseEntityId());
        Assert.assertEquals("1000000", model.getZeirId());
        Assert.assertEquals("false", model.getInActive());
        Assert.assertEquals("false", model.getLostFollowUp());
        Assert.assertEquals("Gates", model.getMotherFirstName());
        Assert.assertEquals("Belinda", model.getMotherLastName());
    }

    @Test
    public void testGetRelationalIdByTypeReturnsNullWhenRelationshipsAreNotSet() throws JSONException {
        String entityId = "184a6d7f-760f-401c-8194-4fd6828094e1";
        String client = "{\"firstName\":\"Martha\",\"middleName\":\"Thomes\",\"lastName\":\"Athame\",\"birthdate\":\"2018-11-12T00:00:00.000+03:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"baseEntityId\":\"184a6d7f-760f-401c-8194-4fd6828094e1\",\"identifiers\":{\"zeir_id\":\"123456789\"},\"addresses\":[{\"addressType\":\"usual_residence\",\"addressFields\":{\"address5\":\"123 Home\"}}],\"attributes\":{\"age\":\"2.0\",\"Birth_Certificate\":\"ADG/01234/56789\",\"second_phone_number\":\"721234567\"},\"dateCreated\":\"2020-11-12T17:16:00.000+03:00\",\"serverVersion\":1605201360000,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":1,\"type\":\"Client\",\"id\":\"597dee4a-9568-4006-bca9-8efccbded2a0\",\"revision\":\"v1\"}";
        JSONObject clientJsonObject = new JSONObject(client);

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ChildMetadata metadata = new ChildMetadata(
                BaseChildFormActivity.class,
                null,
                null,
                null,
                true,
                new RegisterQueryProvider()
        );
        Mockito.when(childLibrary.metadata()).thenReturn(metadata);
        Mockito.when(childLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(childLibrary.eventClientRepository()).thenReturn(eventClientRepository);
        Mockito.when(eventClientRepository.getClientByBaseEntityId(entityId)).thenReturn(clientJsonObject);

        String relationalId = ChildJsonFormUtils.getRelationalIdByType(entityId, Constants.KEY.MOTHER);
        Assert.assertNull(relationalId);
    }

    @Test
    public void testGetRelationalIdByTypeReturnsBaseEntityIdValueWhenRelationshipsAreSet() throws JSONException {
        String entityId = "184a6d7f-760f-401c-8194-4fd6828094e1";
        String client = "{\"firstName\":\"Martha\",\"middleName\":\"Thomes\",\"lastName\":\"Athame\",\"birthdate\":\"2018-11-12T00:00:00.000+03:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{\"mother\":[\"082fbd1e-e4c8-4f7a-92dc-57abe81aee42\"]},\"baseEntityId\":\"184a6d7f-760f-401c-8194-4fd6828094e1\",\"identifiers\":{\"zeir_id\":\"123456789\"},\"addresses\":[{\"addressType\":\"usual_residence\",\"addressFields\":{\"address5\":\"123 Home\"}}],\"attributes\":{\"age\":\"2.0\",\"Birth_Certificate\":\"ADG/01234/56789\",\"second_phone_number\":\"721234567\"},\"dateCreated\":\"2020-11-12T17:16:00.000+03:00\",\"serverVersion\":1605201360000,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":1,\"type\":\"Client\",\"id\":\"597dee4a-9568-4006-bca9-8efccbded2a0\",\"revision\":\"v1\"}";
        JSONObject clientJsonObject = new JSONObject(client);

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ChildMetadata metadata = new ChildMetadata(
                BaseChildFormActivity.class,
                null,
                null,
                null,
                true,
                new RegisterQueryProvider()
        );
        Mockito.when(childLibrary.metadata()).thenReturn(metadata);
        Mockito.when(childLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(childLibrary.eventClientRepository()).thenReturn(eventClientRepository);
        Mockito.when(eventClientRepository.getClientByBaseEntityId(entityId)).thenReturn(clientJsonObject);

        String relationalId = ChildJsonFormUtils.getRelationalIdByType(entityId, Constants.KEY.MOTHER);
        Assert.assertNotNull(relationalId);
        Assert.assertEquals("184a6d7f-760f-401c-8194-4fd6828094e1", entityId);
    }

    @Test
    public void testProcessMoveToCatchmentReturnsFalseWhenBaseEntityIdsAreNotSet() throws JSONException {
        MoveToCatchmentEvent event = MoveToMyCatchmentUtils.createMoveToCatchmentEvent(null, false, false);
        Assert.assertNull(event);

        boolean moveToCatchment = ChildJsonFormUtils.processMoveToCatchment(openSrpContext, event);
        Assert.assertFalse(moveToCatchment);
    }

    @Test
    public void testProcessMoveToCatchmentReturnsFalseWhenEventsCountIsZero() throws JSONException {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(openSrpContext.applicationContext()).thenReturn(context);
        Mockito.when(openSrpContext.configuration()).thenReturn(dristhiConfiguration);
        Mockito.when(openSrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(dristhiConfiguration.dristhiBaseURL()).thenReturn("https://abc.def");
        Mockito.when(openSrpContext.getHttpAgent()).thenReturn(httpAgent);

        JSONObject jsonObject = Mockito.spy(JSONObject.class);
        jsonObject.put(Constants.NO_OF_EVENTS, 0);

        Response<String> response = new Response<>(ResponseStatus.success, jsonObject.toString()).withTotalRecords(0L);
        Mockito.when(httpAgent.fetch(ArgumentMatchers.anyString())).thenReturn(response);

        MoveToCatchmentEvent event = MoveToMyCatchmentUtils.createMoveToCatchmentEvent(Arrays.asList(baseEntityIds), true, true);
        Assert.assertNotNull(event);
        Assert.assertTrue(event.isPermanent());
        Assert.assertTrue(event.isCreateEvent());
        Assert.assertEquals(event.getJsonObject().length(), 1);
        Assert.assertNotNull(event.getJsonObject().has(Constants.NO_OF_EVENTS));
        Assert.assertEquals(event.getJsonObject().get(Constants.NO_OF_EVENTS), 0);

        boolean moveToCatchment = ChildJsonFormUtils.processMoveToCatchment(openSrpContext, event);
        Assert.assertFalse(moveToCatchment);
    }

    @Test
    public void testProcessMoveToCatchmentReturnsTrueWhenEventsSetAndMoveIsPermanent() throws JSONException {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(openSrpContext.applicationContext()).thenReturn(context);
        Mockito.when(openSrpContext.configuration()).thenReturn(dristhiConfiguration);
        Mockito.when(openSrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(dristhiConfiguration.dristhiBaseURL()).thenReturn("https://abc.def");
        Mockito.when(openSrpContext.getHttpAgent()).thenReturn(httpAgent);

        JSONObject jsonObject = Mockito.spy(JSONObject.class);
        jsonObject.put(Constants.EVENTS, getEvents());
        jsonObject.put(Constants.CLIENTS, getClients());
        jsonObject.put(Constants.NO_OF_EVENTS, 2);

        Response<String> response = new Response<>(ResponseStatus.success, jsonObject.toString()).withTotalRecords(2L);
        Mockito.when(httpAgent.fetch(ArgumentMatchers.anyString())).thenReturn(response);

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        Mockito.when(childLibrary.getEcSyncHelper()).thenReturn(ecSyncHelper);
        Mockito.when(childLibrary.getClientProcessorForJava()).thenReturn(clientProcessorForJava);

        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, null, true);
        childMetadata.updateChildRegister(
                "OoC",
                "childTable",
                "guardianTable",
                "Birth Registration",
                "Birth Registration",
                "Immunization",
                "none",
                "345",
                "Out of Catchment");
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);
        Mockito.when(childLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(openSrpContext.allCommonsRepositoryobjects(ArgumentMatchers.anyString())).thenReturn(allCommonsRepository);

        @Nullable
        MoveToCatchmentEvent event = MoveToMyCatchmentUtils.createMoveToCatchmentEvent(Arrays.asList(baseEntityIds), true, true);
        Assert.assertNotNull(event);
        Assert.assertTrue(event.isPermanent());
        Assert.assertTrue(event.isCreateEvent());
        Assert.assertNotNull(event.getJsonObject().has(Constants.NO_OF_EVENTS));
        Assert.assertEquals(event.getJsonObject().get(Constants.NO_OF_EVENTS), 2);

        boolean moveToCatchment = ChildJsonFormUtils.processMoveToCatchment(openSrpContext, event);
        Assert.assertTrue(moveToCatchment);
    }

    @Test
    public void testProcessMoveToCatchmentReturnsTrueWhenEventsSetAndMoveIsNotPermanent() throws JSONException {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(openSrpContext.applicationContext()).thenReturn(context);
        Mockito.when(openSrpContext.configuration()).thenReturn(dristhiConfiguration);
        Mockito.when(openSrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(dristhiConfiguration.dristhiBaseURL()).thenReturn(TEST_BASE_URL);
        Mockito.when(openSrpContext.getHttpAgent()).thenReturn(httpAgent);

        JSONObject jsonObject = Mockito.spy(JSONObject.class);
        jsonObject.put(Constants.EVENTS, getEvents());
        jsonObject.put(Constants.CLIENTS, getClients());
        jsonObject.put(Constants.NO_OF_EVENTS, 2);

        Response<String> response = new Response<>(ResponseStatus.success, jsonObject.toString()).withTotalRecords(1L);
        Mockito.when(httpAgent.fetch(ArgumentMatchers.anyString())).thenReturn(response);

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        Mockito.when(childLibrary.getEcSyncHelper()).thenReturn(ecSyncHelper);
        Mockito.when(childLibrary.getClientProcessorForJava()).thenReturn(clientProcessorForJava);

        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, null, true);
        childMetadata.updateChildRegister(
                "OoC",
                "childTable",
                "guardianTable",
                "Birth Registration",
                "Birth Registration",
                "Immunization",
                "none",
                "345",
                "Out of Catchment");
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);
        Mockito.when(childLibrary.context()).thenReturn(openSrpContext);
        Mockito.when(openSrpContext.allCommonsRepositoryobjects(ArgumentMatchers.anyString())).thenReturn(allCommonsRepository);

        MoveToCatchmentEvent event = MoveToMyCatchmentUtils.createMoveToCatchmentEvent(Arrays.asList(baseEntityIds), false, true);
        Assert.assertNotNull(event);
        Assert.assertFalse(event.isPermanent());
        Assert.assertTrue(event.isCreateEvent());
        Assert.assertEquals(event.getJsonObject().get(Constants.NO_OF_EVENTS), 2);

        boolean moveToCatchment = ChildJsonFormUtils.processMoveToCatchment(openSrpContext, event);
        Assert.assertTrue(moveToCatchment);
    }

    private JSONArray getEvents() throws JSONException {
        JSONArray events = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventId", "b1e71b7e-b4b3-412c-a760-149fbbad7648");
        jsonObject.put("baseEntityId", "7fcace2d-0c50-4baa-b851-52b81c8d975d");
        jsonObject.put("identifiers", new JSONObject());
        jsonObject.put("eventType", Constants.EventType.BITRH_REGISTRATION);
        jsonObject.put("formSubmissionId", "aeefe323-3781-4e0d-a8a0-9e5c98efe9d7");
        jsonObject.put("syncStatus", "unsynced");

        events.put(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("eventId", "f659b714-a9cf-4d44-a34a-25f74f93934c");
        jsonObject.put("baseEntityId", "8d2747fa-8072-4b47-b313-d835a7e51bac");
        jsonObject.put("identifiers", new JSONObject());
        jsonObject.put("eventType", Constants.EventType.BITRH_REGISTRATION);
        jsonObject.put("formSubmissionId", "2aa4797a-92c2-458c-93c4-0e8da2b30f62");
        jsonObject.put("syncStatus", "unsynced");

        events.put(jsonObject);

        return events;
    }

    private JSONArray getClients() throws JSONException {
        JSONArray clients = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("identifiers", new JSONObject());
        jsonObject.put("baseEntityId", 54321);
        jsonObject.put("firstName", "John");
        jsonObject.put("middleName", "Holmes");
        jsonObject.put("lastName", "Doe");
        jsonObject.put("gender", "MALE");
        jsonObject.put("relationships", new JSONArray());
        jsonObject.put("attributes", new JSONObject());
        jsonObject.put("relationalBaseEntityId", "5e8f5e72-189d-4c86-bd1e-260c28aa38ff");
        jsonObject.put("clientType", "");
        jsonObject.put("locationId", "aaf05413-d3d7-4e9d-9004-bcee1a27125e");
        jsonObject.put("teamId", "c591fa51-f129-4389-b887-ea43c6b9d12c");
        jsonObject.put("syncStatus", "unsynced");

        clients.put(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("identifiers", new JSONObject());
        jsonObject.put("baseEntityId", 65432);
        jsonObject.put("firstName", "Sam");
        jsonObject.put("middleName", "Wel");
        jsonObject.put("lastName", "Who");
        jsonObject.put("gender", "FEMALE");
        jsonObject.put("relationships", Arrays.asList(""));
        jsonObject.put("attributes", new JSONObject());
        jsonObject.put("relationalBaseEntityId", "a8ab2185-e02b-42c6-8d4c-b9aee1390c01");
        jsonObject.put("clientType", "");
        jsonObject.put("locationId", "a290480a-a773-4f51-bf49-c0227e1a3fa3");
        jsonObject.put("teamId", "60383788-d5bb-4092-8dcb-7d2b28baa7df");
        jsonObject.put("syncStatus", "unsynced");

        clients.put(jsonObject);

        return clients;
    }

    @Test
    public void testAddRecurringServices() throws JSONException {
        Mockito.doReturn("true").when(appProperties).getProperty(ChildAppProperties.KEY.SHOW_OUT_OF_CATCHMENT_RECURRING_SERVICES);
        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        Mockito.doReturn(openSrpContext).when(coreLibrary).context();
        Mockito.doReturn(ApplicationProvider.getApplicationContext()).when(openSrpContext).applicationContext();
        JSONObject form = new JSONObject("{\"count\":\"1\",\"encounter_type\":\"Out of Catchment Service\",\"entity_id\":\"\",\"step1\":{\"title\":\"Record out of catchment area service\",\"fields\":[]}}");
        ChildJsonFormUtils.addRecurringServices(ApplicationProvider.getApplicationContext(), form);
        JSONArray fields = form.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
        JSONArray options = fields.getJSONObject(0).getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        Assert.assertEquals(options.length(), 3);
        Assert.assertEquals(options.getJSONObject(0).getString(JsonFormConstants.KEY), "vit_a");
        Assert.assertEquals(options.getJSONObject(1).getString(JsonFormConstants.KEY), "deworming");
        Assert.assertEquals(options.getJSONObject(2).getString(JsonFormConstants.KEY), "itn");
    }
}
