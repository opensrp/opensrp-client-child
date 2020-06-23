package org.smartregister.child.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.provider.RegisterQueryProvider;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ChildLibrary.class})
public class BaseChildAdvancedSearchPresenterTest {
    @Mock
    private ChildLibrary childLibrary;

    @Mock
    protected ChildAdvancedSearchContract.View view;

    @Mock
    protected ChildAdvancedSearchContract.Model model;

    @Mock
    ChildAdvancedSearchContract.Interactor interactor;

    private BaseChildAdvancedSearchPresenter baseChildAdvancedSearchPresenter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(ChildLibrary.class);
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, true, new RegisterQueryProvider());
        metadata.updateChildRegister("test", "ec_client",
                "ec_client", "test",
                "test", "test",
                "test",
                "test", "test");
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        PowerMockito.when(childLibrary.metadata()).thenReturn(metadata);
        baseChildAdvancedSearchPresenter = Mockito.mock(BaseChildAdvancedSearchPresenter.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testCleanMapForAdvancedSearch(){

        Map<String, String> map = new HashMap<>();
        map.put("testKey", "testVal");
        Map<String, String> res = baseChildAdvancedSearchPresenter.cleanMapForAdvancedSearch(map);
        Assert.assertEquals(map, res);

        map = new HashMap<>();
        map.put("start_date", "2020-01-01");
        map.put("end_date", "2020-06-23");
        res = baseChildAdvancedSearchPresenter.cleanMapForAdvancedSearch(map);
        Assert.assertFalse(res.containsKey("start_date"));
        Assert.assertFalse(res.containsKey("end_date"));
        Assert.assertTrue(res.containsKey("birth_date"));
    }

    @Test
    public void testGetDefaultSortQuery(){

        String res = baseChildAdvancedSearchPresenter.getDefaultSortQuery();
        Assert.assertEquals("ec_client.last_interacted_with DESC", res);
    }

    @Test
    public void testSearch() {

        PowerMockito.when(baseChildAdvancedSearchPresenter.getView()).thenReturn(view);
        Whitebox.setInternalState(baseChildAdvancedSearchPresenter, "model", model);
        Whitebox.setInternalState(baseChildAdvancedSearchPresenter, "interactor", interactor);

        Map<String, String> map = new HashMap<>();
        map.put("start_date", "2020-01-01");

        PowerMockito.when(model.createEditMap(ArgumentMatchers.<String, String>anyMap())).thenReturn(map);

        baseChildAdvancedSearchPresenter.search(map, true);
        verify(view).updateSearchCriteria("null<b>null</b> null");

        baseChildAdvancedSearchPresenter.search(map, false);
        verify(interactor).search(map, baseChildAdvancedSearchPresenter, null);
    }
}
