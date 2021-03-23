package org.smartregister.child.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.customviews.CheckBox;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.util.AppProperties;
import org.smartregister.util.Utils;

import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ChildLibrary.class, org.smartregister.util.Utils.class})
public class BaseAdvancedSearchFragmentTest  extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private ViewGroup container;

    @Mock
    private View view;

    @Mock
    private CardView viewParent;

    @Mock
    private Button button;

    @Mock
    private RadioButton radioButton;

    @Mock
    private CheckBox checkBox;

    @Mock
    private EditText editText;

    @Mock
    private Resources resources;

    @Mock
    private Bundle savedInstanceState;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private AppProperties appProperties;

    @Mock
    private FragmentActivity fragmentActivity;

    private BaseAdvancedSearchFragment baseAdvancedSearchFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        baseAdvancedSearchFragment = Mockito.mock(BaseAdvancedSearchFragment.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testOnCreateView() {
        LayoutInflater inflater = Mockito.spy(LayoutInflater.class);

        Assert.assertNotNull(baseAdvancedSearchFragment);
        Mockito.doNothing().when(baseAdvancedSearchFragment).onResumption();

        Mockito.doReturn(view).when(inflater).inflate(R.layout.fragment_advanced_search, container, false);
        Mockito.doNothing().when(baseAdvancedSearchFragment).setupViews(view);

        View view = baseAdvancedSearchFragment.onCreateView(inflater, container, savedInstanceState);

        verify(baseAdvancedSearchFragment).setupViews(view);
    }

    @Test
    public void testPopulateFormViews() {
        Whitebox.setInternalState(baseAdvancedSearchFragment, "advancedSearchToolbarSearchButton", button);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "searchButton", button);

        Whitebox.setInternalState(baseAdvancedSearchFragment, "outsideInside", radioButton);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "myCatchment", radioButton);

        Mockito.doReturn(resources).when(baseAdvancedSearchFragment).getResources();
        Mockito.doReturn(view).when(view).findViewById(R.id.out_and_inside_layout);
        Mockito.doReturn(view).when(view).findViewById(R.id.my_catchment_layout);
        Mockito.doReturn(view).when(view).findViewById(R.id.active_layout);
        Mockito.doReturn(view).when(view).findViewById(R.id.inactive_layout);
        Mockito.doReturn(view).when(view).findViewById(R.id.lost_to_follow_up_layout);

        Mockito.doReturn(button).when(view).findViewById(R.id.qrCodeButton);
        Mockito.doReturn(checkBox).when(view).findViewById(R.id.active);
        Mockito.doReturn(checkBox).when(view).findViewById(R.id.inactive);
        Mockito.doReturn(checkBox).when(view).findViewById(R.id.lost_to_follow_up);

        Mockito.doReturn(editText).when(view).findViewById(R.id.start_date);
        Mockito.doReturn(editText).when(view).findViewById(R.id.end_date);

        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        Mockito.when(childLibrary.getProperties()).thenReturn(appProperties);

        baseAdvancedSearchFragment.populateFormViews(view);

        verify(editText).setTag(R.id.type, "start_date");
        verify(view).findViewById(R.id.start_date);
        verify(view).findViewById(R.id.active);
        verify(view).findViewById(R.id.inactive);
        verify(view).findViewById(R.id.lost_to_follow_up);
    }

    @Test
    public void testPopulateFormViewsWhenNfcCardIsEnabled() {
        Whitebox.setInternalState(baseAdvancedSearchFragment, "advancedSearchToolbarSearchButton", button);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "searchButton", button);

        Whitebox.setInternalState(baseAdvancedSearchFragment, "outsideInside", radioButton);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "myCatchment", radioButton);

        Mockito.doReturn(resources).when(baseAdvancedSearchFragment).getResources();
        Mockito.doReturn(view).when(view).findViewById(R.id.out_and_inside_layout);
        Mockito.doReturn(view).when(view).findViewById(R.id.my_catchment_layout);
        Mockito.doReturn(view).when(view).findViewById(R.id.active_layout);
        Mockito.doReturn(view).when(view).findViewById(R.id.inactive_layout);
        Mockito.doReturn(view).when(view).findViewById(R.id.lost_to_follow_up_layout);
        Mockito.doReturn(view).when(view).findViewById(R.id.card_id);
        Mockito.doReturn(viewParent).when(view).getParent();

        Mockito.doReturn(button).when(view).findViewById(R.id.qrCodeButton);
        Mockito.doReturn(button).when(view).findViewById(R.id.scanCardButton);
        Mockito.doReturn(checkBox).when(view).findViewById(R.id.active);
        Mockito.doReturn(checkBox).when(view).findViewById(R.id.inactive);
        Mockito.doReturn(checkBox).when(view).findViewById(R.id.lost_to_follow_up);

        Mockito.doReturn(editText).when(view).findViewById(R.id.start_date);
        Mockito.doReturn(editText).when(view).findViewById(R.id.end_date);

        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);

        Mockito.when(appProperties.getPropertyBoolean(ChildAppProperties.KEY.FEATURE_NFC_CARD_ENABLED)).thenReturn(true);
        Mockito.when(childLibrary.getProperties()).thenReturn(appProperties);

        baseAdvancedSearchFragment.populateFormViews(view);

        verify(button).setVisibility(View.VISIBLE);
        verify(view).findViewById(R.id.card_id);
    }

    @Test
    public void testSwitchViewsWhenShowListIsTrue() {
        Mockito.doReturn(fragmentActivity).when(baseAdvancedSearchFragment).requireActivity();

        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        ImageButton imageButton = Mockito.mock(ImageButton.class);
        TextView textView = Mockito.mock(TextView.class);

        Whitebox.setInternalState(baseAdvancedSearchFragment, "advancedSearchForm", view);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "listViewLayout", view);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "clientsView", recyclerView);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "backButton", imageButton);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "searchButton", button);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "advancedSearchToolbarSearchButton", button);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "titleLabelView", textView);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "matchingResults", textView);

        Mockito.doReturn("Test search results").when(baseAdvancedSearchFragment).getString(R.string.search_results);
        Mockito.doNothing().when(baseAdvancedSearchFragment).showProgressView();

        baseAdvancedSearchFragment.switchViews(true);

        verify(view).setVisibility(View.GONE);
        verify(recyclerView).setVisibility(View.VISIBLE);
        verify(textView).setText("Test search results");
    }

    @Test
    public void testSwitchViewsWhenShowListIsFalse() {
        Mockito.doReturn(fragmentActivity).when(baseAdvancedSearchFragment).requireActivity();

        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        ImageButton imageButton = Mockito.mock(ImageButton.class);
        TextView textView = Mockito.mock(TextView.class);

        Whitebox.setInternalState(baseAdvancedSearchFragment, "advancedSearchForm", view);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "listViewLayout", view);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "clientsView", recyclerView);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "backButton", imageButton);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "searchButton", button);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "advancedSearchToolbarSearchButton", button);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "titleLabelView", textView);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "searchCriteria", textView);

        Mockito.doReturn("Advanced Search").when(baseAdvancedSearchFragment).getString(R.string.advanced_search);

        baseAdvancedSearchFragment.switchViews(false);

        verify(view).setVisibility(View.VISIBLE);
        verify(textView).setText("Advanced Search");
    }

    @Test
    public void testSetUserVisibleHint(){
        PowerMockito.mockStatic(Utils.class);
        Mockito.doNothing().when(baseAdvancedSearchFragment).switchViews(false);
        Mockito.doReturn(fragmentActivity).when(baseAdvancedSearchFragment).requireActivity();

        Whitebox.setInternalState(baseAdvancedSearchFragment, "outsideInside", radioButton);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "myCatchment", radioButton);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "active", checkBox);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "inactive", checkBox);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "lostToFollowUp", checkBox);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "startDate", editText);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "endDate", editText);

        baseAdvancedSearchFragment.setUserVisibleHint(true);

        verify(checkBox, Mockito.times(1)).setChecked(true);
        verify(checkBox, Mockito.times(2)).setChecked(false);
        verify(editText, Mockito.times(2)).setText("");
    }

    @Test
    public void testSetUserVisibleHintWhenSearchCriteriaIsNotNullAndMatchingResultsIsNotNull(){
        PowerMockito.mockStatic(Utils.class);
        Mockito.doNothing().when(baseAdvancedSearchFragment).switchViews(false);
        Mockito.doReturn(fragmentActivity).when(baseAdvancedSearchFragment).requireActivity();

        Whitebox.setInternalState(baseAdvancedSearchFragment, "outsideInside", radioButton);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "myCatchment", radioButton);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "active", checkBox);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "inactive", checkBox);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "lostToFollowUp", checkBox);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "startDate", editText);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "endDate", editText);

        TextView textView = Mockito.mock(TextView.class);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "searchCriteria", textView);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "matchingResults", textView);

        baseAdvancedSearchFragment.setUserVisibleHint(true);

        verify(checkBox, Mockito.times(1)).setChecked(true);
        verify(checkBox, Mockito.times(2)).setChecked(false);
        verify(editText, Mockito.times(2)).setText("");
    }

    @Test
    public void testSetUserVisibleHintWhenIsConnectedToNetworkIsTrue(){
        PowerMockito.mockStatic(Utils.class);
        Mockito.doNothing().when(baseAdvancedSearchFragment).switchViews(false);
        Mockito.doReturn(fragmentActivity).when(baseAdvancedSearchFragment).requireActivity();

        Whitebox.setInternalState(baseAdvancedSearchFragment, "outsideInside", radioButton);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "myCatchment", radioButton);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "active", checkBox);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "inactive", checkBox);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "lostToFollowUp", checkBox);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "startDate", editText);
        Whitebox.setInternalState(baseAdvancedSearchFragment, "endDate", editText);

        Mockito.when(org.smartregister.child.util.Utils.isConnectedToNetwork(fragmentActivity)).thenReturn(true);

        baseAdvancedSearchFragment.setUserVisibleHint(true);

        verify(checkBox, Mockito.times(1)).setChecked(true);
        verify(checkBox, Mockito.times(2)).setChecked(false);
        verify(editText, Mockito.times(2)).setText("");
    }
}
