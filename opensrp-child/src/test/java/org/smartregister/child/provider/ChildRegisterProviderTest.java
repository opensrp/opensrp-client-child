package org.smartregister.child.provider;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.AlertService;

import java.util.HashMap;
import java.util.HashSet;
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
        registerProvider = new ChildRegisterProvider(RuntimeEnvironment.application, new RepositoryHolder(),
                new HashSet(), onClickListener, onClickListener, alertService);

    }

    @Test
    public void testGetViewShouldPopulateRegisterViewHolderIfVisibleColumnsIsEmpty() {

        ChildRegisterProvider registerProviderSpy = Mockito.spy(registerProvider);

        Mockito.doNothing().when(registerProviderSpy).updateImageViewWithPicture(Mockito.anyString(), Mockito.any(ImageView.class));

        Mockito.doNothing().when(registerProviderSpy).initiateViewUpdateTasks(Mockito.any(RegisterActionParams.class));

        String baseEntityId = "2323-sdse2323-2";
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        TextView txtPatientName = new TextView(RuntimeEnvironment.application);
        txtPatientName.setId(R.id.child_name);
        linearLayout.addView(txtPatientName);
        TextView childOpensrpID = new TextView(RuntimeEnvironment.application);
        childOpensrpID.setId(R.id.child_zeir_id);
        linearLayout.addView(childOpensrpID);
        TextView childMotherName = new TextView(RuntimeEnvironment.application);
        childMotherName.setId(R.id.child_mothername);
        linearLayout.addView(childMotherName);
        TextView childAge = new TextView(RuntimeEnvironment.application);
        childAge.setId(R.id.child_age);
        linearLayout.addView(childAge);
        TextView childCardNumber = new TextView(RuntimeEnvironment.application);
        childCardNumber.setId(R.id.child_card_number);
        linearLayout.addView(childCardNumber);
        ImageView imageView = new ImageView(RuntimeEnvironment.application);
        imageView.setId(R.id.child_profilepic);
        linearLayout.addView(imageView);
        View childProfileInfoLayout = new View(RuntimeEnvironment.application);
        childProfileInfoLayout.setId(R.id.child_profile_info_layout);
        linearLayout.addView(childProfileInfoLayout);
        View recordGrowth = new View(RuntimeEnvironment.application);
        recordGrowth.setId(R.id.record_growth);
        linearLayout.addView(recordGrowth);
        View recordVaccination = new View(RuntimeEnvironment.application);
        recordVaccination.setId(R.id.record_vaccination);
        linearLayout.addView(recordVaccination);
        View registerColumns = new View(RuntimeEnvironment.application);
        registerColumns.setId(R.id.register_columns);
        linearLayout.addView(registerColumns);

        Mockito.doReturn(opensrpContext).when(childLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        ReflectionHelpers.setStaticField(SyncStatusBroadcastReceiver.class, "singleton", syncStatusBroadcastReceiver);

        Mockito.doReturn(RuntimeEnvironment.application).when(opensrpContext).applicationContext();
        Mockito.doReturn(opensrpContext).when(coreLibrary).context();
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        ChildRegisterProvider.RegisterViewHolder registerViewHolder = new ChildRegisterProvider.RegisterViewHolder(linearLayout);
        Map<String, String> details = new HashMap<>();
        details.put(Constants.KEY.FIRST_NAME, "John");
        details.put(Constants.KEY.LAST_NAME, "Doe");
        details.put(Constants.KEY.ZEIR_ID, "2120");
        details.put(Constants.KEY.DOB, "2020-09-09");

        details.put(Constants.KEY.MOTHER_FIRST_NAME, "Jane");
        details.put(Constants.KEY.MOTHER_LAST_NAME, "Doe");


        CommonPersonObjectClient client = new CommonPersonObjectClient(baseEntityId, details, "John Doe");
        client.setColumnmaps(details);
        registerProviderSpy.getView(cursor, client, registerViewHolder);

        Mockito.verify(registerProviderSpy, Mockito.times(1)).initiateViewUpdateTasks(Mockito.any(RegisterActionParams.class));
        Mockito.verify(registerProviderSpy, Mockito.times(1)).updateImageViewWithPicture(Mockito.anyString(), Mockito.any(ImageView.class));

        Assert.assertNotNull(childAge.getText().toString());
        Assert.assertEquals(details.get(Constants.KEY.ZEIR_ID), childOpensrpID.getText().toString());
        Assert.assertEquals(String.format(opensrpContext.applicationContext().getString(R.string.mother_name), details.get(Constants.KEY.MOTHER_FIRST_NAME) + " " + details.get(Constants.KEY.MOTHER_LAST_NAME)), childMotherName.getText().toString());
        Assert.assertEquals(details.get(Constants.KEY.FIRST_NAME) + " " + details.get(Constants.KEY.LAST_NAME), txtPatientName.getText().toString());
    }
}