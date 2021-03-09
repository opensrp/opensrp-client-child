package org.smartregister.child.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.IMConstants;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.AlertService;
import org.smartregister.util.AppProperties;
import org.smartregister.view.LocationPickerView;

import java.util.Arrays;

/**
 * Created by ndegwamartin on 03/11/2020.
 */

@PrepareForTest({ImmunizationLibrary.class, Utils.class})
public class BaseChildRegisterFragmentTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private ImmunizationLibrary immunizationLibrary;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private Context context;

    @Mock
    private AlertService alertService;

    @Mock
    private ViewGroup container;

    @Mock
    private Bundle savedInstanceState;

    @Mock
    private FragmentActivity activity;

    @Mock
    private Window window;

    @Mock
    private View view;

    @Mock
    private EditText editText;

    @Mock
    private RecyclerViewPaginatedAdapter clientAdapter;

    @Mock
    private ChildMetadata childMetadata;

    @Mock
    private RegisterQueryProvider registerQueryProvider;

    @Mock
    private TextView overdueCountTextView;

    @Mock
    private AdvancedMatrixCursor advancedMatrixCursor;

    @Mock
    private LocationHelper locationHelper;
    @Mock
    private LocationPickerView clinicSelection;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    protected View filterSection;

    private final String TEST_ID = "unique-identifier";
    private final String TEST_LOCATION_ID = "some-test-location";
    private final String TEST_LOCATION = "Some Test Location";
    private BaseChildRegisterFragment baseChildRegisterFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        baseChildRegisterFragment = Mockito.mock(BaseChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        mockImmunizationLibrary(immunizationLibrary, context, vaccineRepository, alertService);
        Mockito.doReturn(VaccineRepo.Vaccine.values()).when(immunizationLibrary).getVaccines(IMConstants.VACCINE_TYPE.CHILD);
        Whitebox.setInternalState(baseChildRegisterFragment, "mainCondition", "is_closed IS NOT 1");
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }

    @Test
    public void testOnCreateView() {
        LayoutInflater inflater = Mockito.spy(LayoutInflater.class);

        Assert.assertNotNull(baseChildRegisterFragment);

        Mockito.doReturn(window).when(activity).getWindow();
        Mockito.doReturn(activity).when(baseChildRegisterFragment).getActivity();
        Mockito.doNothing().when(baseChildRegisterFragment).onResumption();

        Mockito.doReturn(view).when(inflater).inflate(R.layout.smart_register_activity_customized, container, false);
        Mockito.doNothing().when(baseChildRegisterFragment).setupViews(view);

        View view = baseChildRegisterFragment.onCreateView(inflater, container, savedInstanceState);

        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(window).setSoftInputMode(integerArgumentCaptor.capture());
        Integer capturedParam = integerArgumentCaptor.getValue();
        Assert.assertEquals(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN, capturedParam.intValue());
        Mockito.verify(baseChildRegisterFragment).setupViews(view);
    }

    @Test
    public void testSetUniqueID() {
        Mockito.doReturn(editText).when(baseChildRegisterFragment).getSearchView();

        baseChildRegisterFragment.setUniqueID(TEST_ID);

        ArgumentCaptor<String> integerArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(editText).setText(integerArgumentCaptor.capture());

        String capturedParam = integerArgumentCaptor.getValue();
        Assert.assertEquals(TEST_ID, capturedParam);

    }

    @Test
    public void testUpdateLocationText() {

        Assert.assertNotNull(baseChildRegisterFragment);

        Mockito.doReturn(TEST_LOCATION).when(clinicSelection).getSelectedItem();
        Whitebox.setInternalState(baseChildRegisterFragment, "clinicSelection", clinicSelection);

        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        Mockito.doReturn(TEST_LOCATION).when(locationHelper).getOpenMrsReadableName(ArgumentMatchers.anyString());
        Mockito.doReturn(TEST_LOCATION_ID).when(locationHelper).getOpenMrsLocationId(ArgumentMatchers.anyString());
        Mockito.doReturn(context).when(baseChildRegisterFragment).getOpenSRPContext();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();

        baseChildRegisterFragment.updateLocationText();

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(allSharedPreferences).savePreference(ArgumentMatchers.eq(Constants.CURRENT_LOCATION_ID), argumentCaptor.capture());

        String capturedLocationId = argumentCaptor.getValue();
        Assert.assertEquals(TEST_LOCATION_ID, capturedLocationId);
    }

    @Test
    public void testUpdateDueOverdueCountTextWhenOverdueCountGreaterThanZero() {

        Assert.assertNotNull(baseChildRegisterFragment);

        Whitebox.setInternalState(baseChildRegisterFragment, "overdueCountTV", overdueCountTextView);
        Whitebox.setInternalState(baseChildRegisterFragment, "overDueCount", 7);
        baseChildRegisterFragment.updateDueOverdueCountText();

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(overdueCountTextView).setText(argumentCaptor.capture());
        String captorValue = argumentCaptor.getValue();
        Assert.assertEquals("7", captorValue);

        Mockito.verify(overdueCountTextView).setVisibility(View.VISIBLE);
        Mockito.verify(overdueCountTextView).setClickable(true);

    }

    @Test
    public void testUpdateDueOverdueCountTextWhenOverdueCountEqualToZero() {

        Assert.assertNotNull(baseChildRegisterFragment);

        Whitebox.setInternalState(baseChildRegisterFragment, "overdueCountTV", overdueCountTextView);
        Whitebox.setInternalState(baseChildRegisterFragment, "overDueCount", 0);
        baseChildRegisterFragment.updateDueOverdueCountText();

        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(overdueCountTextView).setVisibility(argumentCaptor.capture());
        Integer captorValue = argumentCaptor.getValue();
        Assert.assertEquals(View.GONE, captorValue.intValue());


        ArgumentCaptor<Boolean> boolArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);

        Mockito.verify(overdueCountTextView).setClickable(boolArgumentCaptor.capture());
        Boolean boolCaptorValue = boolArgumentCaptor.getValue();
        Assert.assertEquals(false, boolCaptorValue);

    }

    @Test
    public void testRecalculatePagination() {

        Assert.assertNotNull(baseChildRegisterFragment);

        Whitebox.setInternalState(baseChildRegisterFragment, "clientAdapter", clientAdapter);

        int TEST_TOTAL_COUNT = 24;
        Mockito.doReturn(TEST_TOTAL_COUNT).when(advancedMatrixCursor).getCount();
        Mockito.doReturn(TEST_TOTAL_COUNT).when(clientAdapter).getTotalcount();

        baseChildRegisterFragment.recalculatePagination(advancedMatrixCursor);

        Mockito.verify(clientAdapter).setTotalcount(TEST_TOTAL_COUNT);
        Mockito.verify(clientAdapter).setCurrentlimit(20);
        Mockito.verify(clientAdapter).setCurrentlimit(24);
        Mockito.verify(clientAdapter).setCurrentoffset(0);
    }

    @Test
    public void testCountExecute() {

        Assert.assertNotNull(baseChildRegisterFragment);

        Whitebox.setInternalState(baseChildRegisterFragment, "clientAdapter", clientAdapter);
        Whitebox.setInternalState(baseChildRegisterFragment, "mainCondition", "");
        Whitebox.setInternalState(baseChildRegisterFragment, "filters", "");

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.metadata()).thenReturn(childMetadata);
        Mockito.doReturn(commonRepository).when(baseChildRegisterFragment).commonRepository();
        Mockito.doReturn(registerQueryProvider).when(childMetadata).getRegisterQueryProvider();

        String TEST_SQL = "Select count(*) from Table where id = 3";
        Mockito.doReturn(TEST_SQL).when(registerQueryProvider).getCountExecuteQuery(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        Mockito.doReturn(5).when(commonRepository).countSearchIds(TEST_SQL);

        baseChildRegisterFragment.countExecute();

        Mockito.verify(clientAdapter).setTotalcount(5);
        Mockito.verify(clientAdapter).setCurrentlimit(20);
        Mockito.verify(clientAdapter).setCurrentoffset(0);

    }


    @Test
    public void testOnLocationChange() {

        Assert.assertNotNull(baseChildRegisterFragment);

        Mockito.doReturn(activity).when(baseChildRegisterFragment).getActivity();

        PowerMockito.mockStatic(Utils.class);

        baseChildRegisterFragment.onLocationChange(TEST_LOCATION_ID);

        PowerMockito.verifyStatic(Utils.class);
        Utils.refreshDataCaptureStrategyBanner(activity, TEST_LOCATION_ID);
    }

    @Test
    public void testFilterAndSortQueryForValidFilterForFts() {

        Assert.assertNotNull(baseChildRegisterFragment);

        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);
        Mockito.doReturn(commonRepository).when(baseChildRegisterFragment).commonRepository();

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        Mockito.doReturn(true).when(baseChildRegisterFragment).isValidFilterForFts(commonRepository);

        // Mockito.doReturn(20).when(clientAdapter).getTotalcount();
        Mockito.doReturn(20).when(clientAdapter).getCurrentlimit();
        Mockito.doReturn(0).when(clientAdapter).getCurrentoffset();
        Mockito.doReturn(Arrays.asList("6", "9", "12")).when(commonRepository).findSearchIds(ArgumentMatchers.anyString());

        String searchText = "some random search text";

        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, null, true);
        metadata.updateChildRegister("test", "test",
                "test", "ChildRegister",
                "test", "test",
                "test",
                "test", "test");

        Mockito.when(Utils.metadata()).thenReturn(metadata);

        Whitebox.setInternalState(baseChildRegisterFragment, "clientAdapter", clientAdapter);
        Whitebox.setInternalState(baseChildRegisterFragment, "mainSelect", "SELECT * FROM ec_clients");
        Whitebox.setInternalState(baseChildRegisterFragment, "mainCondition", "WHERE id = 5");
        Whitebox.setInternalState(baseChildRegisterFragment, "Sortqueries", "SORT BY ID DESC");
        Whitebox.setInternalState(baseChildRegisterFragment, "filters", searchText);

        String expectedQuery = "select ec_client.id as _id,ec_client.relationalid,ec_client.zeir_id,ec_child_details.relational_id,ec_client.gender,ec_client.base_entity_id,ec_client.first_name,ec_client.last_name,mother.first_name as mother_first_name,mother.last_name as mother_last_name,ec_client.dob,mother.dob as mother_dob,ec_mother_details.nrc_number as mother_nrc_number,ec_mother_details.father_name,ec_mother_details.epi_card_number,ec_client.client_reg_date,ec_child_details.pmtct_status,ec_client.last_interacted_with,ec_child_details.inactive,ec_child_details.lost_to_follow_up,ec_child_details.mother_guardian_phone_number,ec_client.address1 from ec_child_details join ec_mother_details on ec_child_details.relational_id = ec_mother_details.base_entity_id join ec_client on ec_client.base_entity_id = ec_child_details.base_entity_id join ec_client mother on mother.base_entity_id = ec_mother_details.base_entity_id where _id IN ('6','9','12')";

        String result = baseChildRegisterFragment.filterAndSortQuery();

        Assert.assertEquals(expectedQuery, result);

    }


    @Test
    public void testFilterAndSortQueryForInvalidFilterForFts() {
        Assert.assertNotNull(baseChildRegisterFragment);

        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);
        Mockito.doReturn(commonRepository).when(baseChildRegisterFragment).commonRepository();

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        Mockito.doReturn(false).when(baseChildRegisterFragment).isValidFilterForFts(commonRepository);

        Mockito.doReturn(20).when(clientAdapter).getCurrentlimit();
        Mockito.doReturn(0).when(clientAdapter).getCurrentoffset();

        String searchText = "some random search text";

        Whitebox.setInternalState(baseChildRegisterFragment, "clientAdapter", clientAdapter);
        Whitebox.setInternalState(baseChildRegisterFragment, "mainSelect", "SELECT * FROM ec_clients");
        Whitebox.setInternalState(baseChildRegisterFragment, "filters", searchText);
        Whitebox.setInternalState(baseChildRegisterFragment, "Sortqueries", "SORT BY ID DESC");

        String expectedQuery = "SELECT * FROM ec_clients some random search text ORDER BY SORT BY ID DESC  LIMIT 0,20;";

        String result = baseChildRegisterFragment.filterAndSortQuery();

        Assert.assertEquals(expectedQuery, result);

    }


    @Test
    public void testToggleFilterSelectionWithNullTag() {
        Assert.assertNotNull(baseChildRegisterFragment);

        Whitebox.setInternalState(baseChildRegisterFragment, "filterSection", filterSection);

        Mockito.doReturn("ID = 8").when(baseChildRegisterFragment).filterSelectionCondition(false);
        Mockito.doNothing().when(baseChildRegisterFragment).filter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());

        ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> bgResourceCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> filterStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> joinTableStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> filterSelectConditionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> qrCodeCaptor = ArgumentCaptor.forClass(Boolean.class);

        baseChildRegisterFragment.toggleFilterSelection();

        Mockito.verify(baseChildRegisterFragment).filter(filterStringCaptor.capture(), joinTableStringCaptor.capture(), filterSelectConditionCaptor.capture(), qrCodeCaptor.capture());
        Mockito.verify(filterSection).setTag(tagCaptor.capture());
        Mockito.verify(filterSection).setBackgroundResource(bgResourceCaptor.capture());

        String capturedTag = tagCaptor.getValue();
        Integer resIdTag = bgResourceCaptor.getValue();

        Assert.assertEquals("PRESSED", capturedTag);
        Assert.assertEquals(new Integer(R.drawable.transparent_clicked_background), resIdTag);

        String filterString = filterStringCaptor.getValue();
        String joinTable = joinTableStringCaptor.getValue();
        String filterSelect = filterSelectConditionCaptor.getValue();
        boolean qrCodeCaptorValue = qrCodeCaptor.getValue();

        Assert.assertEquals("", filterString);
        Assert.assertEquals("", joinTable);
        Assert.assertEquals("ID = 8", filterSelect);
        Assert.assertEquals(false, qrCodeCaptorValue);

    }

    @Test
    public void testToggleFilterSelectionWithTag() {
        Assert.assertNotNull(baseChildRegisterFragment);

        Whitebox.setInternalState(baseChildRegisterFragment, "filterSection", filterSection);

        Mockito.doReturn("is_closed IS NOT 1").when(baseChildRegisterFragment).getMainCondition();
        Mockito.doReturn("PRESSED").when(filterSection).getTag();
        Mockito.doNothing().when(baseChildRegisterFragment).filter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());

        ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> bgResourceCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> filterStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> joinTableStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> mainConditionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> qrCodeCaptor = ArgumentCaptor.forClass(Boolean.class);

        baseChildRegisterFragment.toggleFilterSelection();

        Mockito.verify(baseChildRegisterFragment).filter(filterStringCaptor.capture(), joinTableStringCaptor.capture(), mainConditionCaptor.capture(), qrCodeCaptor.capture());
        Mockito.verify(filterSection).setTag(tagCaptor.capture());
        Mockito.verify(filterSection).setBackgroundResource(bgResourceCaptor.capture());

        String capturedTag = tagCaptor.getValue();
        Integer resIdTag = bgResourceCaptor.getValue();

        Assert.assertNull(capturedTag);
        Assert.assertEquals(new Integer(R.drawable.transparent_gray_background), resIdTag);

        String filterString = filterStringCaptor.getValue();
        String joinTable = joinTableStringCaptor.getValue();
        String filterSelect = mainConditionCaptor.getValue();
        boolean qrCodeCaptorValue = qrCodeCaptor.getValue();

        Assert.assertEquals("", filterString);
        Assert.assertEquals("", joinTable);
        Assert.assertEquals("is_closed IS NOT 1", filterSelect);
        Assert.assertFalse(qrCodeCaptorValue);
    }
    
    @Test
    public void testQRCodeButtonView() throws Exception {
        LayoutInflater inflater = Mockito.spy(LayoutInflater.class);

        Assert.assertNotNull(baseChildRegisterFragment);

        Mockito.doReturn(window).when(activity).getWindow();
        Mockito.doReturn(activity).when(baseChildRegisterFragment).getActivity();

        Mockito.doReturn(view).when(inflater).inflate(R.layout.smart_register_activity_customized, container, false);
        Mockito.doNothing().when(baseChildRegisterFragment).setupViews(view);
        Mockito.doNothing().when(baseChildRegisterFragment).onResumption();

        AppProperties appProperties = Mockito.mock(AppProperties.class);
        Mockito.doReturn(true).when(appProperties).hasProperty(Constants.PROPERTY.HOME_TOOLBAR_SCAN_QR_ENABLED);
        Mockito.doReturn(true).when(appProperties).getPropertyBoolean(Constants.PROPERTY.HOME_TOOLBAR_SCAN_QR_ENABLED);
        Mockito.doReturn(true).when(appProperties).hasProperty(Constants.PROPERTY.FEATURE_SCAN_QR_ENABLED);
        Mockito.doReturn(true).when(appProperties).getPropertyBoolean(Constants.PROPERTY.FEATURE_SCAN_QR_ENABLED);

        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        FrameLayout frameLayout = Mockito.mock(FrameLayout.class);
        frameLayout.setId(R.id.scan_qr_code);
        Mockito.doReturn(frameLayout).when(view).findViewById(R.id.scan_qr_code);

        View view = baseChildRegisterFragment.onCreateView(inflater, container, savedInstanceState);
        Whitebox.invokeMethod(baseChildRegisterFragment, "setUpQRCodeScanButtonView", view);

        Mockito.verify(frameLayout).setOnClickListener(baseChildRegisterFragment);

    }
}