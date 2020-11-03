package org.smartregister.child.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

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
import org.smartregister.Context;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.R;
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
import org.smartregister.view.LocationPickerView;

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
    private LayoutInflater inflater;
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
    private View filterSection;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private LocationPickerView clinicSelection;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    private String TEST_ID = "unique-identifier";
    private String TEST_LOCATION_ID = "some-test-location";
    private String TEST_LOCATION = "Some Test Location";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockImmunizationLibrary(immunizationLibrary, context, vaccineRepository, alertService);
        Mockito.doReturn(VaccineRepo.Vaccine.values()).when(immunizationLibrary).getVaccines(IMConstants.VACCINE_TYPE.CHILD);
    }

    @Test
    public void testOnCreateView() {

        BaseChildRegisterFragment baseChildRegisterFragment = Mockito.mock(BaseChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
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
        BaseChildRegisterFragment baseChildRegisterFragment = Mockito.mock(BaseChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        Mockito.doReturn(editText).when(baseChildRegisterFragment).getSearchView();

        baseChildRegisterFragment.setUniqueID(TEST_ID);


        ArgumentCaptor<String> integerArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(editText).setText(integerArgumentCaptor.capture());

        String capturedParam = integerArgumentCaptor.getValue();
        Assert.assertEquals(TEST_ID, capturedParam);

    }

    @Test
    public void testUpdateLocationText() {

        BaseChildRegisterFragment baseChildRegisterFragment = Mockito.mock(BaseChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        Assert.assertNotNull(baseChildRegisterFragment);

        Whitebox.setInternalState(baseChildRegisterFragment, "clinicSelection", overdueCountTextView);

        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        Mockito.doReturn(TEST_LOCATION).when(locationHelper).getOpenMrsReadableName(ArgumentMatchers.anyString());
        Mockito.doReturn(TEST_LOCATION_ID).when(locationHelper).getOpenMrsLocationId(ArgumentMatchers.anyString());
        Mockito.doReturn(context).when(baseChildRegisterFragment).getOpenSRPContext();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();

        baseChildRegisterFragment.updateLocationText();


        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(allSharedPreferences).savePreference(Constants.CURRENT_LOCATION_ID, argumentCaptor.capture());

        String capturedLocationId = argumentCaptor.getValue();
        Assert.assertEquals(TEST_LOCATION_ID, capturedLocationId);

    }

    @Test
    public void testUpdateDueOverdueCountTextWhenOverdueCountGreaterThanZero() {

        BaseChildRegisterFragment baseChildRegisterFragment = Mockito.mock(BaseChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        Assert.assertNotNull(baseChildRegisterFragment);

        int totalCount = 7;

        Whitebox.setInternalState(baseChildRegisterFragment, "overdueCountTV", overdueCountTextView);
        baseChildRegisterFragment.updateDueOverdueCountText(totalCount);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(overdueCountTextView).setText(argumentCaptor.capture());
        String captorValue = argumentCaptor.getValue();
        Assert.assertEquals("7", captorValue);

        Mockito.verify(overdueCountTextView).setVisibility(View.VISIBLE);
        Mockito.verify(overdueCountTextView).setClickable(true);

    }

    @Test
    public void testUpdateDueOverdueCountTextWhenOverdueCountEqualToZero() {


        BaseChildRegisterFragment baseChildRegisterFragment = Mockito.mock(BaseChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        Assert.assertNotNull(baseChildRegisterFragment);

        int totalCount = 0;


        Whitebox.setInternalState(baseChildRegisterFragment, "overdueCountTV", overdueCountTextView);
        baseChildRegisterFragment.updateDueOverdueCountText(totalCount);

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

        BaseChildRegisterFragment baseChildRegisterFragment = Mockito.mock(BaseChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
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

        BaseChildRegisterFragment baseChildRegisterFragment = Mockito.mock(BaseChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
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

        BaseChildRegisterFragment baseChildRegisterFragment = Mockito.mock(BaseChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        Assert.assertNotNull(baseChildRegisterFragment);

        Mockito.doReturn(activity).when(baseChildRegisterFragment).getActivity();

        PowerMockito.mockStatic(Utils.class);

        baseChildRegisterFragment.onLocationChange(TEST_LOCATION_ID);

        PowerMockito.verifyStatic(Utils.class);
        Utils.refreshDataCaptureStrategyBanner(activity, TEST_LOCATION_ID);
    }
}