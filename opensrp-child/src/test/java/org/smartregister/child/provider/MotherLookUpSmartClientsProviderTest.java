package org.smartregister.child.provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.R;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MotherLookUpSmartClientsProviderTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Mock
    private LayoutInflater layoutInflater;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetViewShouldUpdateRequiredTextViews() {
        Mockito.when(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(layoutInflater);
        MotherLookUpSmartClientsProvider motherLookUpSmartClientsProvider = new MotherLookUpSmartClientsProvider(context);
        HashMap<String, String> motherDetails = new HashMap<>();
        motherDetails.put(Constants.KEY.FIRST_NAME, "Jane");
        motherDetails.put(Constants.KEY.LAST_NAME, "Doe");
        motherDetails.put(Constants.KEY.DOB, "2010-02-02");
        CommonPersonObject commonPersonObject = new CommonPersonObject("2323-awdtye", "12", motherDetails, "mother");
        commonPersonObject.setColumnmaps(motherDetails);

        HashMap<String, String> childDetails = new HashMap<>();
        motherDetails.put(Constants.KEY.FIRST_NAME, "John");
        motherDetails.put(Constants.KEY.LAST_NAME, "Doe");
        CommonPersonObject commonPersonObjectChild = new CommonPersonObject("5423-awewe", "12", childDetails, "child");
        commonPersonObjectChild.setColumnmaps(motherDetails);
        List<CommonPersonObject> children = new ArrayList<>();
        children.add(commonPersonObjectChild);

        LinearLayout view = new LinearLayout(RuntimeEnvironment.application);
        TextView textViewName = new TextView(RuntimeEnvironment.application);
        textViewName.setId(R.id.name);
        TextView textViewDetails = new TextView(RuntimeEnvironment.application);
        textViewDetails.setId(R.id.details);
        view.addView(textViewName);
        view.addView(textViewDetails);
        motherLookUpSmartClientsProvider.getView(commonPersonObject, children, view);
        Assert.assertEquals("John Doe", textViewName.getText());
        Assert.assertEquals("02/02/10 - John Doe", textViewDetails.getText());
    }
}