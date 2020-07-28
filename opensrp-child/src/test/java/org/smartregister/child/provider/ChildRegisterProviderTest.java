package org.smartregister.child.provider;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.child.R;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.service.AlertService;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class})
public class ChildRegisterProviderTest {

    private ChildRegisterProvider queryProvider;

    @Mock
    private Context context;

    @Mock
    private LayoutInflater layoutInflater;

    @Captor
    private ArgumentCaptor<TextView> viewArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Mock
    private Cursor cursor;

    @Mock
    private ChildRegisterProvider.RegisterViewHolder registerViewHolder;

    @Mock
    private View.OnClickListener onClickListener;

    @Mock
    private View.OnClickListener paginationClickListener;

    @Mock
    private RepositoryHolder repositoryHolder;

    @Mock
    private TextView childOpensrpID;

    @Mock
    private TextView patientName;

    @Mock
    private TextView childMotherName;

    @Mock
    private TextView childAge;

    @Mock
    private TextView childCardNumber;

    @Mock
    private ImageView imageView;

    @Mock
    private OpenSRPImageLoader openSRPImageLoader;

    @Mock
    private View childProfileInfoLayout;

    @Mock
    private View recordGrowth;

    @Mock
    private View recordVaccination;

    @Mock
    private Resources resources;

    @Mock
    private AlertService alertService;

    @Mock
    private Set visibleColumns;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        queryProvider = new ChildRegisterProvider(this.context, repositoryHolder, visibleColumns, onClickListener, paginationClickListener, alertService);
    }

    @Test
    public void testGetViewShouldUpdateViews() {
        Mockito.when(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(layoutInflater);

        Map<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.FIRST_NAME, "Janet");
        childDetails.put(Constants.KEY.LAST_NAME, "Doe");
        childDetails.put(Constants.KEY.BASE_ENTITY_ID, "12345");
        childDetails.put(Constants.KEY.ZEIR_ID, "12");
        childDetails.put(Constants.KEY.MOTHER_FIRST_NAME, "Mom");
        childDetails.put(Constants.KEY.GENDER, "male");
        childDetails.put(Constants.KEY.DOB, "2018-10-12T00:00:00+03:00");
        childDetails.put(Constants.KEY.EPI_CARD_NUMBER, "1234567890");

        Set visibleColumns = Mockito.mock(Set.class);
        Mockito.doReturn(true).when(visibleColumns).isEmpty();

        RepositoryHolder repositoryHolder = Mockito.mock(RepositoryHolder.class);

        Mockito.doReturn("M/G: %1$s").when(context).getString(R.string.mother_name);

        ChildRegisterProvider childRegisterProvider = Mockito.spy(new ChildRegisterProvider(context, repositoryHolder, visibleColumns, null, null, alertService));

        CommonPersonObjectClient smartRegisterClient = new CommonPersonObjectClient(childDetails.get(Constants.KEY.BASE_ENTITY_ID), childDetails, "child");
        smartRegisterClient.setColumnmaps(childDetails);

        View itemView = Mockito.mock(View.class);
        Mockito.doReturn(patientName).when(itemView).findViewById(R.id.child_name);
        Mockito.doReturn(childOpensrpID).when(itemView).findViewById(R.id.child_zeir_id);
        Mockito.doReturn(childMotherName).when(itemView).findViewById(R.id.child_mothername);
        Mockito.doReturn(imageView).when(itemView).findViewById(R.id.child_profilepic);
        Mockito.doReturn(childProfileInfoLayout).when(itemView).findViewById(R.id.child_profile_info_layout);
        Mockito.doReturn(recordGrowth).when(itemView).findViewById(R.id.record_growth);
        Mockito.doReturn(recordVaccination).when(itemView).findViewById(R.id.record_vaccination);
        Mockito.doReturn(childAge).when(itemView).findViewById(R.id.child_age);
        Mockito.doReturn(childCardNumber).when(itemView).findViewById(R.id.child_card_number);

        Mockito.when(context.getResources()).thenReturn(resources);

        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getCachedImageLoaderInstance()).thenReturn(openSRPImageLoader);

        registerViewHolder = new ChildRegisterProvider.RegisterViewHolder(itemView);

        childRegisterProvider.getView(cursor, smartRegisterClient, registerViewHolder);

        Mockito.verify(childRegisterProvider, Mockito.atLeastOnce()).fillValue(viewArgumentCaptor.capture(), stringArgumentCaptor.capture());
        Assert.assertEquals("12", stringArgumentCaptor.getAllValues().get(0));
        Assert.assertEquals("Janet Doe", stringArgumentCaptor.getAllValues().get(1));
        Assert.assertEquals("M/G: Mom", stringArgumentCaptor.getAllValues().get(2));
        Assert.assertEquals("", stringArgumentCaptor.getAllValues().get(3));
        Assert.assertEquals("1234567890", stringArgumentCaptor.getAllValues().get(4));
    }

}
