package org.smartregister.child.provider;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.AlertService;
import org.smartregister.util.AppProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class ChildRegisterProviderTest extends BaseUnitTest {

    @Mock
    private ChildLibrary childLibrary;

    private ChildRegisterProvider registerProvider;

    @Mock
    private SyncStatusBroadcastReceiver syncStatusBroadcastReceiver;

    @Mock
    private View.OnClickListener onClickListener;

    @Mock
    private AlertService alertService;

    @Mock
    private Cursor cursor;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private Context opensrpContext;

    @Mock
    private CoreLibrary coreLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        registerProvider = new ChildRegisterProvider(ApplicationProvider.getApplicationContext(), new RepositoryHolder(),
                new HashSet(), onClickListener, onClickListener, alertService);

    }

    @Test
    public void testGetViewShouldPopulateRegisterViewHolderIfVisibleColumnsIsEmpty() {

        ChildRegisterProvider registerProviderSpy = Mockito.spy(registerProvider);

        Mockito.doNothing().when(registerProviderSpy).updateImageViewWithPicture(Mockito.anyString(), Mockito.any(ImageView.class), Mockito.any(Integer.class));

        Mockito.doNothing().when(registerProviderSpy).initiateViewUpdateTasks(Mockito.any(RegisterActionParams.class));

        String baseEntityId = "2323-sdse2323-2";
        LinearLayout linearLayout = new LinearLayout(ApplicationProvider.getApplicationContext());
        TextView txtPatientName = new TextView(ApplicationProvider.getApplicationContext());
        txtPatientName.setId(R.id.child_name);
        linearLayout.addView(txtPatientName);
        TextView childOpensrpID = new TextView(ApplicationProvider.getApplicationContext());
        childOpensrpID.setId(R.id.child_zeir_id);
        linearLayout.addView(childOpensrpID);
        TextView childMotherName = new TextView(ApplicationProvider.getApplicationContext());
        childMotherName.setId(R.id.child_mothername);
        linearLayout.addView(childMotherName);
        TextView childAge = new TextView(ApplicationProvider.getApplicationContext());
        childAge.setId(R.id.child_age);
        linearLayout.addView(childAge);
        TextView childCardNumber = new TextView(ApplicationProvider.getApplicationContext());
        childCardNumber.setId(R.id.child_card_number);
        linearLayout.addView(childCardNumber);
        ImageView imageView = new ImageView(ApplicationProvider.getApplicationContext());
        imageView.setId(R.id.child_profilepic);
        linearLayout.addView(imageView);
        View childProfileInfoLayout = new View(ApplicationProvider.getApplicationContext());
        childProfileInfoLayout.setId(R.id.child_profile_info_layout);
        linearLayout.addView(childProfileInfoLayout);
        View recordGrowth = new View(ApplicationProvider.getApplicationContext());
        recordGrowth.setId(R.id.record_growth);
        linearLayout.addView(recordGrowth);
        View recordVaccination = new View(ApplicationProvider.getApplicationContext());
        recordVaccination.setId(R.id.record_vaccination);
        linearLayout.addView(recordVaccination);
        View showCompliance = new View(ApplicationProvider.getApplicationContext());
        showCompliance.setId(R.id.ll_compliance);
        linearLayout.addView(showCompliance);
        View registerColumns = new View(ApplicationProvider.getApplicationContext());
        registerColumns.setId(R.id.register_columns);
        TextView systemOfRegistration = new TextView(ApplicationProvider.getApplicationContext());
        systemOfRegistration.setId(R.id.system_of_registration);
        linearLayout.addView(systemOfRegistration);

        Mockito.doReturn(opensrpContext).when(childLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        ReflectionHelpers.setStaticField(SyncStatusBroadcastReceiver.class, "singleton", syncStatusBroadcastReceiver);

        Mockito.doReturn(ApplicationProvider.getApplicationContext()).when(opensrpContext).applicationContext();
        Mockito.doReturn(opensrpContext).when(coreLibrary).context();
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);


        AppProperties appProperties = Mockito.mock(AppProperties.class);
        Mockito.doReturn(true).when(appProperties).hasProperty(ChildAppProperties.KEY.NOVEL.OUT_OF_CATCHMENT);

        Mockito.doReturn(appProperties).when(childLibrary).getProperties();

        ChildRegisterProvider.RegisterViewHolder registerViewHolder = new ChildRegisterProvider.RegisterViewHolder(linearLayout);
        Map<String, String> details = new HashMap<>();
        details.put(Constants.KEY.FIRST_NAME, "John");
        details.put(Constants.KEY.LAST_NAME, "Doe");
        details.put(Constants.KEY.ZEIR_ID, "2120");
        details.put(Constants.KEY.DOB, "2020-09-09");
        details.put(Constants.Client.SYSTEM_OF_REGISTRATION, "MVACC");
        details.put(Constants.KEY.MOTHER_FIRST_NAME, "Jane");
        details.put(Constants.KEY.MOTHER_LAST_NAME, "Doe");


        CommonPersonObjectClient client = new CommonPersonObjectClient(baseEntityId, details, "John Doe");
        client.setColumnmaps(details);
        registerProviderSpy.getView(cursor, client, registerViewHolder);

        Mockito.verify(registerProviderSpy, Mockito.times(1)).initiateViewUpdateTasks(Mockito.any(RegisterActionParams.class));
        Mockito.verify(registerProviderSpy, Mockito.times(1)).updateImageViewWithPicture(Mockito.anyString(), Mockito.any(ImageView.class), Mockito.any(Integer.class));

        Assert.assertNotNull(childAge.getText().toString());
        Assert.assertEquals(details.get(Constants.KEY.ZEIR_ID), childOpensrpID.getText().toString());
        Assert.assertEquals(String.format(opensrpContext.applicationContext().getString(R.string.mother_name), details.get(Constants.KEY.MOTHER_FIRST_NAME) + " " + details.get(Constants.KEY.MOTHER_LAST_NAME)), childMotherName.getText().toString());
        Assert.assertEquals(details.get(Constants.KEY.FIRST_NAME) + " " + details.get(Constants.KEY.LAST_NAME), txtPatientName.getText().toString());
        Assert.assertEquals(View.VISIBLE, systemOfRegistration.getVisibility());
        Assert.assertEquals(details.get(Constants.Client.SYSTEM_OF_REGISTRATION), systemOfRegistration.getText());

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);

        ReflectionHelpers.setStaticField(SyncStatusBroadcastReceiver.class, "singleton", null);
    }

    @Test
    public void testGetFooterViewShouldFillViewsCorrectlyForOnePage() {
        int currentPageCount = 1;
        int totalPageCount = 1;
        ChildRegisterProvider.FooterViewHolder footerViewHolder = Mockito.mock(ChildRegisterProvider.FooterViewHolder.class);

        TextView pageInfoView = new TextView(ApplicationProvider.getApplicationContext());

        Button nextPageView = new Button(ApplicationProvider.getApplicationContext());

        Button previousPageView = new Button(ApplicationProvider.getApplicationContext());

        ReflectionHelpers.setField(footerViewHolder, "pageInfoView", pageInfoView);

        ReflectionHelpers.setField(footerViewHolder, "nextPageView", nextPageView);

        ReflectionHelpers.setField(footerViewHolder, "previousPageView", previousPageView);

        registerProvider.getFooterView(footerViewHolder, currentPageCount, totalPageCount,
                false, true);

        Assert.assertEquals(View.INVISIBLE, nextPageView.getVisibility());

        Assert.assertEquals(View.VISIBLE, previousPageView.getVisibility());

        Assert.assertTrue(previousPageView.hasOnClickListeners());

        Assert.assertTrue(nextPageView.hasOnClickListeners());

        Assert.assertEquals(String.format(Locale.ENGLISH, pageInfoView.getContext().getString(R.string.str_page_info), currentPageCount, totalPageCount), pageInfoView.getText());
    }

    @Test
    public void testCreateViewHolderShouldShowOrHideViewsCorrectly() {
        ViewGroup viewGroup = new LinearLayout(ApplicationProvider.getApplicationContext());
        LayoutInflater layoutInflater = Mockito.mock(LayoutInflater.class);

        LinearLayout linearLayout = new LinearLayout(ApplicationProvider.getApplicationContext());
        TextView recordWeightWrapper = new TextView(ApplicationProvider.getApplicationContext());
        recordWeightWrapper.setId(R.id.record_weight_wrapper);
        TextView childNextAppointmentWrapper = new TextView(ApplicationProvider.getApplicationContext());
        childNextAppointmentWrapper.setId(R.id.child_next_appointment_wrapper);
        TextView zeirIdWrapper = new TextView(ApplicationProvider.getApplicationContext());
        zeirIdWrapper.setId(R.id.zeir_id_wrapper);
        linearLayout.addView(recordWeightWrapper);
        linearLayout.addView(childNextAppointmentWrapper);
        linearLayout.addView(zeirIdWrapper);

        Mockito.doReturn(linearLayout).when(layoutInflater)
                .inflate(Mockito.eq(R.layout.child_register_list_row), Mockito.eq(viewGroup), Mockito.eq(false));
        ReflectionHelpers.setField(registerProvider, "inflater", layoutInflater);

        AppProperties appProperties = Mockito.mock(AppProperties.class);
        Mockito.doReturn(true).when(appProperties).hasProperty(Constants.PROPERTY.HOME_RECORD_WEIGHT_ENABLED);
        Mockito.doReturn(true).when(appProperties).getPropertyBoolean(Constants.PROPERTY.HOME_RECORD_WEIGHT_ENABLED);

        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ChildRegisterProvider.RegisterViewHolder viewHolder = registerProvider.createViewHolder(viewGroup);

        Assert.assertEquals(View.VISIBLE, viewHolder.itemView.findViewById(R.id.record_weight_wrapper).getVisibility());
        Assert.assertEquals(View.GONE, viewHolder.itemView.findViewById(R.id.child_next_appointment_wrapper).getVisibility());
        Assert.assertEquals(View.GONE, viewHolder.itemView.findViewById(R.id.zeir_id_wrapper).getVisibility());
    }

    @Test
    public void testCreateViewHolderShouldShowZEeirIdColumn() {
        ViewGroup viewGroup = new LinearLayout(ApplicationProvider.getApplicationContext());
        LayoutInflater layoutInflater = Mockito.mock(LayoutInflater.class);

        LinearLayout linearLayout = new LinearLayout(ApplicationProvider.getApplicationContext());
        TextView recordWeightWrapper = new TextView(ApplicationProvider.getApplicationContext());
        recordWeightWrapper.setId(R.id.record_weight_wrapper);
        TextView childNextAppointmentWrapper = new TextView(ApplicationProvider.getApplicationContext());
        childNextAppointmentWrapper.setId(R.id.child_next_appointment_wrapper);
        TextView zeirIdWrapper = new TextView(ApplicationProvider.getApplicationContext());
        zeirIdWrapper.setId(R.id.zeir_id_wrapper);
        linearLayout.addView(recordWeightWrapper);
        linearLayout.addView(childNextAppointmentWrapper);
        linearLayout.addView(zeirIdWrapper);

        Mockito.doReturn(linearLayout).when(layoutInflater)
                .inflate(Mockito.eq(R.layout.child_register_list_row), Mockito.eq(viewGroup), Mockito.eq(false));
        ReflectionHelpers.setField(registerProvider, "inflater", layoutInflater);

        AppProperties appProperties = Mockito.mock(AppProperties.class);
        Mockito.doReturn(true).when(appProperties).hasProperty(Constants.PROPERTY.HOME_RECORD_WEIGHT_ENABLED);
        Mockito.doReturn(true).when(appProperties).getPropertyBoolean(Constants.PROPERTY.HOME_RECORD_WEIGHT_ENABLED);
        Mockito.doReturn(true).when(appProperties).hasProperty(Constants.PROPERTY.HOME_ZEIR_ID_COL_ENABLED);
        Mockito.doReturn(true).when(appProperties).getPropertyBoolean(Constants.PROPERTY.HOME_ZEIR_ID_COL_ENABLED);

        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ChildRegisterProvider.RegisterViewHolder viewHolder = registerProvider.createViewHolder(viewGroup);

        Assert.assertEquals(View.VISIBLE, viewHolder.itemView.findViewById(R.id.record_weight_wrapper).getVisibility());
        Assert.assertEquals(View.GONE, viewHolder.itemView.findViewById(R.id.child_next_appointment_wrapper).getVisibility());
        Assert.assertEquals(View.VISIBLE, viewHolder.itemView.findViewById(R.id.zeir_id_wrapper).getVisibility());
    }

}