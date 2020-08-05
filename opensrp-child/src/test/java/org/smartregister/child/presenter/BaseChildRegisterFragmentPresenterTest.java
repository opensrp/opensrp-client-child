package org.smartregister.child.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.model.BaseChildRegisterFragmentModel;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Utils;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

/**
 * Created by ndegwamartin on 14/07/2020.
 */
@PrepareForTest({Utils.class})
public class BaseChildRegisterFragmentPresenterTest extends BasePowerMockUnitTest {

    @Mock
    private ChildRegisterFragmentContract.View view;

    private ChildRegisterFragmentContract.Model model;

    @Mock
    private RegisterConfiguration registerConfiguration;

    @Mock
    private ViewConfiguration viewConfiguration;

    @Mock
    private ChildMetadata metadata;

    private RegisterQueryProvider registerQueryProvider;

    @Mock
    private AdvancedMatrixCursor advancedMatrixCursor;

    @Mock
    private List<Field> filterList;

    @Mock
    private Field sortField;

    private WeakReference<ChildRegisterFragmentContract.View> viewWeakReference;

    @Mock
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private String NULL_STRING = null;

    private static String VIEW_CONFIGURATION_IDENTIFIER = "LOGINVIEW";
    private static String SEARCH_BAR_TEXT = "search text";
    private static String DEMOGRAPHICS_TABLE_NAME = "ec_client";
    private static String SQL_QUERY_FILTER = "id > 1";
    private static final String EXPECTED_MAIN_COUNT_QUERY = "select count(1) from ec_child_details join ec_mother_details " +
            "on ec_child_details.relational_id = ec_mother_details.base_entity_id join ec_client on ec_client.base_entity_id = ec_child_details.base_entity_id " +
            "join ec_client mother on mother.base_entity_id = ec_mother_details.base_entity_id where id > 1";

    private static final String EXPECTED_MAIN_SELECT_QUERY = "select ec_client.id as _id,ec_client.relationalid,ec_client.zeir_id,ec_child_details.relational_id,ec_client.gender," +
            "ec_client.base_entity_id,ec_client.first_name,ec_client.last_name,mother.first_name as mother_first_name,mother.last_name as mother_last_name,ec_client.dob,mother.dob as mother_dob," +
            "ec_mother_details.nrc_number as mother_nrc_number,ec_mother_details.father_name,ec_mother_details.epi_card_number,ec_client.client_reg_date,ec_child_details.pmtct_status," +
            "ec_client.last_interacted_with,ec_child_details.inactive,ec_child_details.lost_to_follow_up,ec_child_details.mother_guardian_phone_number,ec_client.address1 " +
            "from ec_child_details join ec_mother_details on ec_child_details.relational_id = ec_mother_details.base_entity_id join ec_client on ec_client.base_entity_id = ec_child_details.base_entity_id " +
            "join ec_client mother on mother.base_entity_id = ec_mother_details.base_entity_id where id > 1";

    private static final String EXPECTED_FILTER_TEXT = "<font color=#727272>id > 1</font> <font color=#f0ab41>(0)</font>";
    private static final String EXPECTED_SORT_TEXT = "Sort by id";

    private BaseChildRegisterFragmentPresenter presenter;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        registerQueryProvider = new RegisterQueryProvider();
        model = Mockito.mock(BaseChildRegisterFragmentModel.class, Mockito.CALLS_REAL_METHODS);

        viewWeakReference = Mockito.spy(new WeakReference<>(view));

        Mockito.doReturn(viewConfiguration).when(model).getViewConfiguration(VIEW_CONFIGURATION_IDENTIFIER);
        Mockito.doReturn(visibleColumns).when(model).getRegisterActiveColumns(VIEW_CONFIGURATION_IDENTIFIER);

        presenter = Mockito.mock(BaseChildRegisterFragmentPresenter.class, Mockito.CALLS_REAL_METHODS);
        presenter.setModel(model);
        presenter.setMatrixCursor(advancedMatrixCursor);

        Whitebox.setInternalState(presenter, "viewReference", viewWeakReference);
        Whitebox.setInternalState(presenter, "viewConfigurationIdentifier", VIEW_CONFIGURATION_IDENTIFIER);
        Whitebox.setInternalState(presenter, "config", registerConfiguration);
        Whitebox.invokeMethod(presenter, "setVisibleColumns", visibleColumns);
    }

    @Test
    public void testProcessViewConfigurationsReturnsEarlyIfViewConfigurationIdentifierIsBlank() {

        Mockito.doReturn(registerConfiguration).when(viewConfiguration).getMetadata();

        Whitebox.setInternalState(presenter, "viewConfigurationIdentifier", NULL_STRING);

        presenter.processViewConfigurations();

        Mockito.verify(model, Mockito.never()).getViewConfiguration(null);
    }


    @Test
    public void testProcessViewConfigurationsRegistersCorrectVisibleColumns() {

        Mockito.doReturn(view).when(presenter).getView();
        Mockito.doReturn(viewConfiguration).when(model).getViewConfiguration(VIEW_CONFIGURATION_IDENTIFIER);
        Mockito.doReturn(registerConfiguration).when(viewConfiguration).getMetadata();

        presenter.processViewConfigurations();

        Mockito.verify(model, Mockito.times(1)).getRegisterActiveColumns(VIEW_CONFIGURATION_IDENTIFIER);
    }

    @Test
    public void testProcessViewConfigurationsUpdateSearchBarHintWithCorrectValue() {

        Mockito.doReturn(SEARCH_BAR_TEXT).when(registerConfiguration).getSearchBarText();
        Mockito.doReturn(registerConfiguration).when(viewConfiguration).getMetadata();
        Mockito.doReturn(view).when(presenter).getView();

        presenter.processViewConfigurations();

        Mockito.verify(view, Mockito.times(1)).updateSearchBarHint(SEARCH_BAR_TEXT);
    }

    @Test
    public void testInitializeQueriesWithCorrectValues() {

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.metadata()).thenReturn(metadata);

        Mockito.doReturn(view).when(presenter).getView();
        Mockito.doReturn(registerQueryProvider).when(metadata).getRegisterQueryProvider();

        presenter.initializeQueries(SQL_QUERY_FILTER);

        ArgumentCaptor<String> tableNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> countSelectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> mainSelectCaptor = ArgumentCaptor.forClass(String.class);


        Mockito.verify(view).initializeQueryParams(tableNameCaptor.capture(), countSelectCaptor.capture(), mainSelectCaptor.capture());
        Assert.assertEquals(DEMOGRAPHICS_TABLE_NAME, tableNameCaptor.getValue());
        Assert.assertEquals(EXPECTED_MAIN_SELECT_QUERY, mainSelectCaptor.getValue());
        Assert.assertEquals(EXPECTED_MAIN_COUNT_QUERY, countSelectCaptor.getValue());

    }

    @Test
    public void testInitializeQueriesInitializesAdapterWithVisibleColumns() {

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.metadata()).thenReturn(metadata);

        Mockito.doReturn(view).when(presenter).getView();
        Mockito.doReturn(registerQueryProvider).when(metadata).getRegisterQueryProvider();

        presenter.initializeQueries(SQL_QUERY_FILTER);

        Mockito.verify(view).initializeAdapter(ArgumentMatchers.any(Set.class));
    }

    @Test
    public void testInitializeQueriesInvokesViewCountExecute() {

        PowerMockito.mockStatic(Utils.class);

        PowerMockito.when(Utils.metadata()).thenReturn(metadata);

        Mockito.doReturn(registerQueryProvider).when(metadata).getRegisterQueryProvider();

        presenter.initializeQueries(SQL_QUERY_FILTER);

        Mockito.verify(view).countExecute();
    }

    @Test
    public void testInitializeQueriesInvokesViewFilterandSortInInitializeQueries() {

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.metadata()).thenReturn(metadata);

        Mockito.doReturn(registerQueryProvider).when(metadata).getRegisterQueryProvider();

        presenter.initializeQueries(SQL_QUERY_FILTER);
        Mockito.verify(view).filterandSortInInitializeQueries();
    }

    @Test
    public void testGetViewInvokesGetMethodOfWeakReference() {

        Whitebox.setInternalState(presenter, "viewReference", viewWeakReference);

        presenter.getView();

        Mockito.verify(viewWeakReference).get();
    }

    @Test
    public void testUpdateSortAndFilterInvokesUpdateFilterAndFilterStatusWithCorrectParams() {

        Mockito.doReturn(SQL_QUERY_FILTER).when(view).getString(ArgumentMatchers.anyInt());
        Mockito.doReturn(EXPECTED_SORT_TEXT).when(model).getSortText(sortField);

        presenter.updateSortAndFilter(filterList, sortField);

        Mockito.verify(model).getFilterText(filterList, SQL_QUERY_FILTER);
        Mockito.verify(model).getSortText(sortField);

        Mockito.verify(view).updateFilterAndFilterStatus(EXPECTED_FILTER_TEXT, EXPECTED_SORT_TEXT);
    }

}