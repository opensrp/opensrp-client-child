package org.smartregister.child.task;

import android.content.Context;
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
import org.smartregister.child.R;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.child.wrapper.GrowthMonitoringViewRecordUpdateWrapper;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.repository.Repository;

import java.lang.reflect.Method;

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

    private GrowthMonitoringAsyncTask growthMonitoringAsyncTask;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        GrowthMonitoringLibrary.init(Mockito.mock(org.smartregister.Context.class), Mockito.mock(Repository.class), 1, 1);
        growthMonitoringAsyncTask = new GrowthMonitoringAsyncTask(registerActionParams, commonRepository, weightRepository, heightRepository, context);
    }

    @Test
    public void testGetGrowthMonitoringValues() throws Exception {
        Method getGrowthMonitoringValues = GrowthMonitoringAsyncTask.class.getDeclaredMethod("getGrowthMonitoringValues", String.class, String.class);
        getGrowthMonitoringValues.setAccessible(true);

        String val = (String) getGrowthMonitoringValues.invoke(growthMonitoringAsyncTask, "50", "10");

        Assert.assertEquals("10, 50", val);
    }

    @Test
    public void testUpdateRecordWeight() throws Exception {
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
}

