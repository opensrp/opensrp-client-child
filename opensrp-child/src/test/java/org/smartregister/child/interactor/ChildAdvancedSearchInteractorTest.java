package org.smartregister.child.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.Constants;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.service.HTTPAgent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreLibrary.class, ChildLibrary.class})
public class ChildAdvancedSearchInteractorTest {

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private DristhiConfiguration dristhiConfiguration;

    @Mock
    private Context context;

    @Mock
    private HTTPAgent httpAgent;

    private ChildAdvancedSearchInteractor childAdvancedSearchInteractor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        ChildAppProperties appProperties = new ChildAppProperties();
        PowerMockito.doReturn(appProperties).when(childLibrary).getProperties();
        childAdvancedSearchInteractor = new ChildAdvancedSearchInteractor(Mockito.mock(AppExecutors.class));
    }

    @Test
    public void testEnhanceStatusFilter() throws Exception {
        Method enhanceStatusFilter = ChildAdvancedSearchInteractor.class.getDeclaredMethod("enhanceStatusFilter", Map.class);
        enhanceStatusFilter.setAccessible(true);

        Map<String, String> map = new HashMap<>();
        map.put("key", "test");
        enhanceStatusFilter.invoke(childAdvancedSearchInteractor, map);

        Assert.assertEquals(map.get(Constants.CHILD_STATUS.ACTIVE), "false");

        Assert.assertEquals(map.get(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP), "false");
    }

    @Test
    public void testUrlEncode() throws Exception {
        Method urlEncodeMethod = ChildAdvancedSearchInteractor.class.getDeclaredMethod("urlEncode", String.class);
        urlEncodeMethod.setAccessible(true);

        Assert.assertEquals("key%3Dvalue", urlEncodeMethod.invoke(childAdvancedSearchInteractor, "key=value"));
    }

    @Test
    public void testGlobalSearch() throws Exception {
        Method globalSearchMethod = ChildAdvancedSearchInteractor.class.getDeclaredMethod("globalSearch", Map.class);
        globalSearchMethod.setAccessible(true);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.configuration()).thenReturn(dristhiConfiguration);

        ChildAdvancedSearchInteractor interactor = spy(ChildAdvancedSearchInteractor.class);
        PowerMockito.when(interactor.getHttpAgent()).thenReturn(httpAgent);

        Response<String> resp = new Response<>(ResponseStatus.success, "success");
        PowerMockito.when(httpAgent.fetch(anyString())).thenReturn(resp);

        Map<String, String> map = new HashMap<>();
        map.put("key", "test");
        globalSearchMethod.invoke(childAdvancedSearchInteractor, map);

        verify(interactor).getHttpAgent();
    }
}
