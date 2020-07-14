package org.smartregister.child.task;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.R;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.child.util.Constants;
import org.smartregister.child.wrapper.GrowthMonitoringViewRecordUpdateWrapper;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.repository.Repository;
import org.smartregister.view.contract.SmartRegisterClient;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class GrowthMonitoringAsyncTaskTest {

    @Mock
    private RegisterActionParams registerActionParams;

    @Mock
    private CommonRepository  commonRepository;

    @Mock
    private WeightRepository weightRepository;

    @Mock
    private HeightRepository heightRepository;

    @Mock
    private Context context;

    @Mock
    private GrowthMonitoringViewRecordUpdateWrapper growthMonitoringViewRecordUpdateWrapper;

    @Mock
    private View view;

    @Mock
    private TextView textView;

    @Mock
    private ImageView imageView;

    @Mock
    private Resources resources;

    private GrowthMonitoringAsyncTask growthMonitoringAsyncTask;

    private Height height;
    private Weight weight;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        GrowthMonitoringLibrary.init(Mockito.mock(org.smartregister.Context.class), Mockito.mock(Repository.class), 1, 1);
        growthMonitoringAsyncTask = new GrowthMonitoringAsyncTask(registerActionParams, commonRepository, weightRepository, heightRepository, context);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 3);
        height = new Height();
        height.setDate(cal.getTime());
        height.setAnmId("demo");
        height.setBaseEntityId("00ts-ime-hcla-0tib-0eht-ma0i");
        height.setChildLocationId("test");
        height.setCm(30.4f);

        weight = new Weight();
        cal.set(Calendar.MONTH, 3);
        weight.setDate(cal.getTime());
        weight.setAnmId("demo");
        weight.setBaseEntityId("00ts-ime-hcla-0tib-0eht-ma0i");
        weight.setChildLocationId("test");
        weight.setKg(3.4f);
    }

    @Test
    public void testGetGrowthMonitoringValues() throws Exception {
        Method getGrowthMonitoringValues = GrowthMonitoringAsyncTask.class.getDeclaredMethod("getGrowthMonitoringValues", String.class, String.class);
        getGrowthMonitoringValues.setAccessible(true);

        String val = (String) getGrowthMonitoringValues.invoke(growthMonitoringAsyncTask, "50", "10");

        Assert.assertEquals("10, 50", val);
    }

    @Test
    public void testUpdateRecordWeightWhenWeightAndHeightAreNull() throws Exception {
        Method updateRecordWeight = GrowthMonitoringAsyncTask.class.getDeclaredMethod("updateRecordWeight", GrowthMonitoringViewRecordUpdateWrapper.class, Boolean.class);
        updateRecordWeight.setAccessible(true);

        when(growthMonitoringViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(view.findViewById(R.id.record_growth)).thenReturn(view);
        when(view.findViewById(R.id.record_growth_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_growth_check)).thenReturn(imageView);
        when(context.getString(R.string.record_growth_with_nl)).thenReturn("test");

        when(growthMonitoringViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("True");
        when(growthMonitoringViewRecordUpdateWrapper.getInactive()).thenReturn("True");

        updateRecordWeight.invoke(growthMonitoringAsyncTask, growthMonitoringViewRecordUpdateWrapper, false);

        verify(imageView).setVisibility(View.GONE);
    }

    @Test
    public void testUpdateRecordWeightWhenWeightAndHeightAreNotNull() throws Exception {
        Method updateRecordWeight = GrowthMonitoringAsyncTask.class.getDeclaredMethod("updateRecordWeight", GrowthMonitoringViewRecordUpdateWrapper.class, Boolean.class);
        updateRecordWeight.setAccessible(true);

        when(growthMonitoringViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(view.findViewById(R.id.record_growth)).thenReturn(view);
        when(view.findViewById(R.id.record_growth_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_growth_check)).thenReturn(imageView);
        when(context.getString(R.string.record_growth_with_nl)).thenReturn("test");

        when(growthMonitoringViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("True");
        when(growthMonitoringViewRecordUpdateWrapper.getInactive()).thenReturn("True");

        when(context.getResources()).thenReturn(resources);

        when(growthMonitoringViewRecordUpdateWrapper.getHeight()).thenReturn(height);
        when(growthMonitoringViewRecordUpdateWrapper.getWeight()).thenReturn(weight);

        updateRecordWeight.invoke(growthMonitoringAsyncTask, growthMonitoringViewRecordUpdateWrapper, false);

        verify(textView).setText("3.4 kg");
        verify(imageView).setVisibility(View.VISIBLE);
    }

    @Test
    public void testUpdateRecordWeightWhenMonitorGrowthIsTrue() throws Exception {
        Method updateRecordWeight = GrowthMonitoringAsyncTask.class.getDeclaredMethod("updateRecordWeight", GrowthMonitoringViewRecordUpdateWrapper.class, Boolean.class);
        updateRecordWeight.setAccessible(true);

        when(growthMonitoringViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(view.findViewById(R.id.record_growth)).thenReturn(view);
        when(view.findViewById(R.id.record_growth_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_growth_check)).thenReturn(imageView);
        when(context.getString(R.string.record_growth_with_nl)).thenReturn("test");

        when(growthMonitoringViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("True");
        when(growthMonitoringViewRecordUpdateWrapper.getInactive()).thenReturn("True");

        when(context.getResources()).thenReturn(resources);

        when(growthMonitoringViewRecordUpdateWrapper.getHeight()).thenReturn(height);
        when(growthMonitoringViewRecordUpdateWrapper.getWeight()).thenReturn(weight);
        when(growthMonitoringViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("true");

        Whitebox.setInternalState(growthMonitoringAsyncTask, "hasProperty", true);
        Whitebox.setInternalState(growthMonitoringAsyncTask, "monitorGrowth", true);

        updateRecordWeight.invoke(growthMonitoringAsyncTask, growthMonitoringViewRecordUpdateWrapper, false);

        verify(textView).setText("3.4 kg, 30.4 cm");
        verify(imageView).setVisibility(View.VISIBLE);
    }

    @Test
    public void testUpdateViewsUpdatesCorrectly() throws Exception {
        Method updateViews = GrowthMonitoringAsyncTask.class.getDeclaredMethod("updateViews", View.class, SmartRegisterClient.class);
        updateViews.setAccessible(true);

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.move_to_catchment)).thenReturn(view);
        when(view.findViewById(R.id.child_profile_info_layout)).thenReturn(view);
        when(view.findViewById(R.id.record_growth_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_growth)).thenReturn(textView);
        when(context.getResources()).thenReturn(resources);

        Map<String, String> map = new HashMap<>();
        map.put(Constants.KEY.ZEIR_ID, "24127");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("00ts-ime-hcla-0tib-0eht-ma0i", new HashMap<String, String>(), "Roja");
        commonPersonObjectClient.setColumnmaps(map);

        updateViews.invoke(growthMonitoringAsyncTask, view, commonPersonObjectClient);

        verify(textView).setText(R.string.record_service);
    }
}

