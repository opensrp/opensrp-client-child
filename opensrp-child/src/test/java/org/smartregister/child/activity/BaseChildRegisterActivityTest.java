package org.smartregister.child.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BuildConfig;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.impl.activity.TestBaseChildRegisterActivity;
import org.smartregister.child.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.BaseProfileActivity;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.child.util.ChildJsonFormUtils.REQUEST_CODE_GET_JSON;
import static org.smartregister.child.util.Constants.INTENT_KEY.JSON;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, CoreLibrary.class, TextUtils.class, ChildLibrary.class, ChildMetadata.class})
public class BaseChildRegisterActivityTest {

    private TestBaseChildRegisterActivity baseChildRegisterActivity;

    @Mock
    private Context context;

    @Mock
    private ChildRegisterContract.Presenter presenter;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private UpdateRegisterParams updateRegisterParam;

    @Mock
    private Intent androidIntent;

    private String registerChildJsonForm = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2019-11-02 14:02:17\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2019-11-02 14:03:03\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"02-11-2019\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"99d01128-51cd-42de-84c4-432b3ac56532\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Birth Registration\",\"fields\":[{\"key\":\"photo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"},{\"key\":\"zeir_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"zeir_id\",\"type\":\"edit_text\",\"hint\":\"Child's MER ID\",\"label_info_text\":\"Write this number down on the child's health passport.\",\"scanButtonText\":\"Scan QR Code\",\"read_only\":true,\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's MER ID\"},\"value\":\"16449043\"},{\"key\":\"birth_registration_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"Birth_Certificate\",\"type\":\"edit_text\",\"hint\":\"Child's NRB birth registration number\",\"label_info_text\":\"If the child was registered in vital registration, enter the registration number here.\",\"edit_type\":\"name\",\"v_required\":{\"value\":false,\"err\":\"Please enter the Birth Registration Number\"},\"v_regex\":{\"value\":\"([A-Z]{2,3}/[0-9]{8}/[0-9]{4})|\\\\s*\",\"err\":\"Number must take the format of ###/########/####\"},\"value\":\"\"},{\"key\":\"last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"hint\":\"Last name\",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please enter the last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"sad\"},{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"v_required\":{\"value\":true,\"err\":\"Please enter a first name\"},\"value\":\"asd\"},{\"key\":\"middle_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"middle_name\",\"type\":\"edit_text\",\"hint\":\"Middle name\",\"edit_type\":\"name\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"v_required\":{\"value\":false,\"err\":\"Please enter the child's middle name\"},\"value\":\"\"},{\"key\":\"Sex\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"spinner\",\"hint\":\"Sex\",\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Gender of the child\"},\"value\":\"Male\"},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"date_picker\",\"hint\":\"Child's DOB\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"},\"step\":\"step1\",\"is-rule-check\":true,\"value\":\"02-11-2019\"},{\"key\":\"age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"age\",\"type\":\"hidden\",\"value\":\"0.0\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_calculation_rules.yml\"}}},\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"home_address\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"tree\":[{\"key\":\"Malawi\",\"level\":\"\",\"name\":\"Malawi\",\"nodes\":[{\"key\":\"Central West Zone\",\"level\":\"\",\"name\":\"Central West Zone\",\"nodes\":[{\"key\":\"Lizulu Health Centre\",\"level\":\"\",\"name\":\"Lizulu Health Centre\",\"nodes\":[{\"key\":\"Chibonga Outreach Clinic (Lizulu)\",\"level\":\"\",\"name\":\"Chibonga Outreach Clinic (Lizulu)\"},{\"key\":\"Chilobwe Outreach Clinic (Lizulu)\",\"level\":\"\",\"name\":\"Chilobwe Outreach Clinic (Lizulu)\"},{\"key\":\"Chilobwe Village Clinic (Lizulu)\",\"level\":\"\",\"name\":\"Chilobwe Village Clinic (Lizulu)\"}]}]}]},{\"key\":\"Other\",\"level\":\"\",\"name\":\"Other\"}],\"value\":\"Lombwa Outreach\",\"hint\":\"Address/Location\",\"v_required\":{\"value\":false,\"err\":\"Please enter the Child's Home Address\"},\"default\":[\"Malawi\",\"Central West Zone\",\"Lizulu Health Centre\"]},{\"key\":\"village\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address1\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"label_info_text\":\"Indicate the village where the child comes from.\",\"hint\":\"Child's Village\",\"value\":\"\"},{\"key\":\"traditional_authority\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"traditional_authority\",\"hint\":\"Traditional Authority\",\"label_info_text\":\"Indicate the name of the Traditional Authority for the child.\",\"type\":\"edit_text\",\"value\":\"\"},{\"key\":\"Birth_Weight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"label_info_text\":\"The weight as measured when the child is born\",\"hint\":\"Birth weight (kg)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth weight\"},\"value\":\"23\"},{\"key\":\"Birth_Height\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth Height (cm)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Height must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid height\"},\"v_required\":{\"value\":false,\"err\":\"Enter the child's birth height\"},\"value\":\"\"},{\"key\":\"Mother_Guardian_First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/Guardian first name\",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's first name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"wewe\"},{\"key\":\"Mother_Guardian_Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian last name\",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"wewe\"},{\"key\":\"Mother_Guardian_Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"entity_id\":\"mother\",\"type\":\"date_picker\",\"hint\":\"Mother/guardian DOB\",\"look_up\":\"true\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"default\":\"01-01-1960\",\"min_date\":\"01-01-1960\",\"max_date\":\"today-10y\",\"value\":\"\"},{\"key\":\"nrc_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"nrc_number\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian NRB Identification number\",\"look_up\":\"true\",\"v_regex\":{\"value\":\"([A-Za-z0-9]{1,11})|\\\\s*\",\"err\":\"ID should be at-most 11 characters in length\"},\"value\":\"\"},{\"key\":\"mother_guardian_phone_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian phone number\",\"v_regex\":{\"value\":\"([0][0-9]{9})|\\\\s*\",\"err\":\"Number must begin with 0 and must be a total of 10 digits in length\"},\"value\":\"\"},{\"key\":\"second_phone_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"second_phone_number\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Alternative phone number\",\"v_regex\":{\"value\":\"([0][0-9]{9})|\\\\s*\",\"err\":\"Number must begin with 0 and must be a total of 10 digits in length\"},\"value\":\"\"},{\"key\":\"protected_at_birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"164826AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"spinner\",\"label_info_text\":\"Whether the child's mother received 2+ doses of Td.\",\"hint\":\"Protected at birth (PAB)\",\"entity_id\":\"mother\",\"v_required\":{\"value\":true,\"err\":\"Please choose an option\"},\"values\":[\"Yes\",\"No\",\"Don't Know\"],\"openmrs_choice_ids\":{\"Yes\":\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"No\":\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Don't Know\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_relevance_rules.yml\"}}},\"is_visible\":true,\"value\":\"No\"},{\"key\":\"mother_hiv_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"type\":\"spinner\",\"hint\":\"HIV Status of the Child's Mother\",\"values\":[\"Positive\",\"Negative\",\"Unknown\"],\"openmrs_choice_ids\":{\"Positive\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Negative\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Unknown\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"step\":\"step1\",\"is-rule-check\":true,\"value\":\"HIV Status of the Child's Mother\"},{\"key\":\"child_hiv_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5303AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"spinner\",\"hint\":\"HIV Status of the Child\",\"values\":[\"Positive\",\"Negative\",\"Unknown\",\"Exposed\"],\"openmrs_choice_ids\":{\"Positive\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Negative\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Unknown\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Exposed\":\"822AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_relevance_rules.yml\"}}},\"is_visible\":false,\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"child_treatment\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"162240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"spinner\",\"label_info_text\":\"Indicate whether the child is on CPT and/or ART.\",\"hint\":\"Child's treatment\",\"values\":[\"CPT\",\"ART\",\"None\"],\"openmrs_choice_ids\":{\"CPT\":\"160434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"ART\":\"160119AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"None\":\"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_relevance_rules.yml\"}}},\"is_visible\":false},{\"key\":\"lost_to_follow_up\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"lost_to_follow_up\",\"type\":\"hidden\",\"value\":\"\"},{\"key\":\"inactive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"inactive\",\"type\":\"hidden\",\"value\":\"\"}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.6.59-SNAPSHOT\",\"formVersion\":\"\"}}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        baseChildRegisterActivity = new TestBaseChildRegisterActivity();
    }

    @Test
    public void testOnCreate() {
        Assert.assertNotNull(baseChildRegisterActivity);
    }

    @Test
    public void testOnActivityResultExtendedShouldSaveForm() throws Exception {
        baseChildRegisterActivity = spy(baseChildRegisterActivity);

        when(baseChildRegisterActivity.presenter()).thenReturn(presenter);

        PowerMockito.whenNew(Intent.class).withArguments(String.class).thenReturn(androidIntent);
        when(androidIntent.getStringExtra(JSON)).thenReturn(registerChildJsonForm);

        doReturn(context).when(childLibrary).context();

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, true);
        childMetadata.updateChildRegister(
                "test",
                "test",
                "test",
                "Birth Registration",
                "test",
                "test",
                "test",
                "test",
                "test");
        ChildLibrary.init(context, Mockito.mock(Repository.class), childMetadata, BuildConfig.VERSION_CODE, 1);

        when(Utils.context().allSharedPreferences()).thenReturn(allSharedPreferences);
        when(Utils.metadata()).thenReturn(childMetadata);

        baseChildRegisterActivity.onActivityResultExtended(REQUEST_CODE_GET_JSON, Activity.RESULT_OK, androidIntent);
        verify(baseChildRegisterActivity).onActivityResultExtended(REQUEST_CODE_GET_JSON, Activity.RESULT_OK, androidIntent);

        presenter.saveForm(registerChildJsonForm, updateRegisterParam);
        verify(presenter).saveForm(registerChildJsonForm, updateRegisterParam);
    }
}
