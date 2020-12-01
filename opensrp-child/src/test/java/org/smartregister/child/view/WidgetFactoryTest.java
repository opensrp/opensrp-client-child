package org.smartregister.child.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.util.Pair;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 23/07/2020.
 */
public class WidgetFactoryTest extends BaseUnitTest {

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private View.OnClickListener listener;

    @Mock
    private LinearLayout tableLayout;

    @Spy
    private View rowsView;

    @Mock
    private TextView label;

    @Mock
    private TextView value;

    @Mock
    private Button editButton;

    private List<Boolean> editModeList;

    @Mock
    private LinearLayout fragmentContainer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }


    @Test
    public void testCreateHeightWidgetUpdatesTableRowViewChildStatesWithCorrectValues() {

        WidgetFactory widgetFactory = new WidgetFactory();
        Assert.assertNotNull(widgetFactory);

        String labelText = "height-label-xxxxx";
        String valueText = "height-value-xxxxx";
        Map<Long, Pair<String, String>> lastFiveHeightMap = new HashMap<>();
        lastFiveHeightMap.put(1l, new Pair<>(labelText, valueText));

        Mockito.doReturn(rowsView).when(layoutInflater).inflate(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(tableLayout), ArgumentMatchers.eq(false));
        Mockito.doReturn(label).when(rowsView).findViewById(R.id.label);
        Mockito.doReturn(value).when(rowsView).findViewById(R.id.value);
        Mockito.doReturn(editButton).when(rowsView).findViewById(R.id.edit);

        Mockito.doReturn(tableLayout).when(fragmentContainer).findViewById(R.id.heightvalues);

        //set up listeners
        List<View.OnClickListener> listeners = new ArrayList<>();
        listeners.add(listener);

        //set up edit mode list
        editModeList = new ArrayList<>();
        editModeList.add(true);

        widgetFactory.createHeightWidget(layoutInflater, fragmentContainer, lastFiveHeightMap, listeners, editModeList);

        ArgumentCaptor<View> viewCaptor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(tableLayout, Mockito.times(1)).addView(viewCaptor.capture());

        View capturedView = viewCaptor.getValue();
        Assert.assertNotNull(capturedView);
        Assert.assertEquals(rowsView, capturedView);

        Mockito.verify(rowsView, Mockito.times(1)).findViewById(R.id.label);
        Mockito.verify(rowsView, Mockito.times(1)).findViewById(R.id.value);
        Mockito.verify(rowsView, Mockito.times(1)).findViewById(R.id.edit);

        ArgumentCaptor<String> labelArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueArgCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(label).setText(labelArgCaptor.capture());
        Mockito.verify(value).setText(valueArgCaptor.capture());

        Assert.assertEquals(labelText, labelArgCaptor.getValue());
        Assert.assertEquals(valueText, valueArgCaptor.getValue());

        Mockito.verify(editButton).setOnClickListener(listener);
        Mockito.verify(editButton).setVisibility(View.VISIBLE);

        //Test sets edit button to invisible if edit mode is false
        editModeList = new ArrayList<>();
        editModeList.add(false);

        widgetFactory.createHeightWidget(layoutInflater, fragmentContainer, lastFiveHeightMap, listeners, editModeList);
        Mockito.verify(editButton).setVisibility(View.INVISIBLE);

    }

    @Test
    public void testCreateWeightWidgetUpdatesTableRowViewChildStatesWithCorrectValues() {
        WidgetFactory widgetFactory = new WidgetFactory();
        Assert.assertNotNull(widgetFactory);

        String labelText = "label-weight";
        String valueText = "value-weight";
        Map<Long, Pair<String, String>> lastFiveWeightMap = new HashMap<>();
        lastFiveWeightMap.put(2l, new Pair<>(labelText, valueText));

        Mockito.doReturn(rowsView).when(layoutInflater).inflate(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(tableLayout), ArgumentMatchers.eq(false));
        Mockito.doReturn(label).when(rowsView).findViewById(R.id.label);
        Mockito.doReturn(value).when(rowsView).findViewById(R.id.value);
        Mockito.doReturn(editButton).when(rowsView).findViewById(R.id.edit);

        Mockito.doReturn(tableLayout).when(fragmentContainer).findViewById(R.id.weightvalues);

        //set up listeners
        List<View.OnClickListener> listeners = new ArrayList<>();
        listeners.add(listener);

        //set up edit mode list
        editModeList = new ArrayList<>();
        editModeList.add(true);

        widgetFactory.createWeightWidget(layoutInflater, fragmentContainer, lastFiveWeightMap, listeners, editModeList);

        ArgumentCaptor<View> viewCaptor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(tableLayout, Mockito.times(1)).addView(viewCaptor.capture());

        View capturedView = viewCaptor.getValue();
        Assert.assertNotNull(capturedView);
        Assert.assertEquals(rowsView, capturedView);

        Mockito.verify(rowsView, Mockito.times(1)).findViewById(R.id.label);
        Mockito.verify(rowsView, Mockito.times(1)).findViewById(R.id.value);
        Mockito.verify(rowsView, Mockito.times(1)).findViewById(R.id.edit);

        ArgumentCaptor<String> labelArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueArgCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(label).setText(labelArgCaptor.capture());
        Mockito.verify(value).setText(valueArgCaptor.capture());

        Assert.assertEquals(labelText, labelArgCaptor.getValue());
        Assert.assertEquals(valueText, valueArgCaptor.getValue());

        Mockito.verify(editButton).setOnClickListener(listener);
        Mockito.verify(editButton).setVisibility(View.VISIBLE);

        //Test sets edit button to invisible if edit mode is false
        editModeList = new ArrayList<>();
        editModeList.add(false);

        widgetFactory.createWeightWidget(layoutInflater, fragmentContainer, lastFiveWeightMap, listeners, editModeList);
        Mockito.verify(editButton).setVisibility(View.INVISIBLE);

    }
}