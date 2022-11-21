package org.smartregister.child.interactor;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
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
import org.smartregister.util.AppProperties;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Mock
    private AppProperties appProperties;

    private ChildAdvancedSearchInteractor childAdvancedSearchInteractor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        PowerMockito.doReturn(appProperties).when(childLibrary).getProperties();
        childAdvancedSearchInteractor = Mockito.spy(new ChildAdvancedSearchInteractor(Mockito.mock(AppExecutors.class)));
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
        Mockito.doReturn("https://www.test-opensrp.smartregister.org").when(dristhiConfiguration).dristhiBaseURL();

        ChildAdvancedSearchInteractor interactor = Mockito.spy(ChildAdvancedSearchInteractor.class);
        PowerMockito.when(interactor.getHttpAgent()).thenReturn(httpAgent);

        Response<String> resp = new Response<>(ResponseStatus.success, "success");
        PowerMockito.when(httpAgent.post(Mockito.anyString(), Mockito.anyString())).thenReturn(resp);

        Map<String, String> map = new HashMap<>();
        map.put("key", "test");
        globalSearchMethod.invoke(childAdvancedSearchInteractor, map);
        Mockito.verify(httpAgent).post(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(interactor).getHttpAgent();
    }

    @Test
    public void testGlobalSearchWhenUseNewAdvanceSearchApproachIsTrue() throws Exception {

        String searchUri = "https://www.test-opensrp.smartregister.org/rest/client/search";
        Method globalSearchMethod = ChildAdvancedSearchInteractor.class.getDeclaredMethod("globalSearch", Map.class);
        globalSearchMethod.setAccessible(true);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);

        Mockito.doReturn("https://www.test-opensrp.smartregister.org").when(dristhiConfiguration).dristhiBaseURL();
        PowerMockito.when(context.configuration()).thenReturn(dristhiConfiguration);

        ChildAdvancedSearchInteractor interactor = Mockito.spy(ChildAdvancedSearchInteractor.class);
        PowerMockito.when(interactor.getHttpAgent()).thenReturn(httpAgent);

        Response<String> motherResponse = new Response<>(ResponseStatus.success, "[{\"zeir_id\":\"M_F2103003KL\",\"last_name\":\"Madea\",\"first_name\":\"Momma\"}]");
        PowerMockito.when(httpAgent.post(Mockito.anyString(), Mockito.anyString())).thenReturn(motherResponse);

        Response<String> childResponse = new Response<>(ResponseStatus.success, "[{\"zeir_id\":\"F2103003KL\",\"last_name\":\"Mwanza\",\"first_name\":\"Sylvia\"},{\"zeir_id\":\"F2103003K\",\"last_name\":\"Banda\",\"first_name\":\"Sylvia\"}]");
        PowerMockito.when(httpAgent.post(Mockito.anyString(), Mockito.anyString())).thenReturn(childResponse);

        Mockito.doReturn(true).when(appProperties).isTrue(ChildAppProperties.KEY.USE_NEW_ADVANCE_SEARCH_APPROACH);

        Map<String, String> searchParametersMap = new HashMap<>();

        searchParametersMap.put(Constants.KEY.FIRST_NAME, "Sylvia");
        searchParametersMap.put(Constants.KEY.LAST_NAME, "Banda");
        searchParametersMap.put(Constants.KEY.MOTHER_FIRST_NAME, "Momma");
        searchParametersMap.put(Constants.KEY.MOTHER_LAST_NAME, "Madea");
        searchParametersMap.put(Constants.KEY.ZEIR_ID, "F2103003K");
        searchParametersMap.put(Constants.KEY.MOTHER_GUARDIAN_NUMBER, "+2546718880001");

        Response<String> response = (Response<String>) globalSearchMethod.invoke(childAdvancedSearchInteractor, searchParametersMap);

        //Verify crucial method invocations
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(httpAgent, Mockito.times(2)).post(Mockito.eq(searchUri), stringArgumentCaptor.capture());

        List<String> capturedParamValue = stringArgumentCaptor.getAllValues();
        Assert.assertNotNull(capturedParamValue);
        Assert.assertEquals(2, capturedParamValue.size());
        String expectedMotherClientSearchBody = "{\"searchRelationship\":\"mother\",\"name\":\"Momma\",\"attribute\":\"mother_guardian_number:+2546718880001\"}";
        Assert.assertEquals(expectedMotherClientSearchBody, capturedParamValue.get(0));
        String expectedChildClientSearchBody = "{\"identifier\":\"F2103003K\",\"relationships\":\"mother\"}";
        Assert.assertEquals(expectedChildClientSearchBody, capturedParamValue.get(1));

        //Assert payload
        Assert.assertNotNull(response);

        String payload = response.payload();
        Assert.assertNotNull(payload);

        JSONArray payloadArray = new JSONArray(payload);
//        Assert.assertEquals(3, payloadArray.length());

        Assert.assertEquals("Sylvia", payloadArray.getJSONObject(0).getString(Constants.KEY.FIRST_NAME));
        Assert.assertEquals("Mwanza", payloadArray.getJSONObject(0).getString(Constants.KEY.LAST_NAME));

        Assert.assertEquals("Sylvia", payloadArray.getJSONObject(1).getString(Constants.KEY.FIRST_NAME));
        Assert.assertEquals("Banda", payloadArray.getJSONObject(1).getString(Constants.KEY.LAST_NAME));

        //Mother
        Assert.assertEquals("Momma", payloadArray.getJSONObject(2).getString(Constants.KEY.FIRST_NAME));
        Assert.assertEquals("Madea", payloadArray.getJSONObject(2).getString(Constants.KEY.LAST_NAME));
    }

    @Test
    public void testGlobalSearchWhenUseNewAdvanceSearchApproachIsTrueAndOnlyChildParamsProvided() throws Exception {

        String expectedChildClientSearchUri = "https://www.test-opensrp.smartregister.org/rest/client/search?name=Sylvia&birthdate=2015-02-10:2021-02-25&attribute=lost_to_follow_up:true&relationships=mother";

        Method globalSearchMethod = ChildAdvancedSearchInteractor.class.getDeclaredMethod("globalSearch", Map.class);
        globalSearchMethod.setAccessible(true);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);

        Mockito.doReturn("https://www.test-opensrp.smartregister.org").when(dristhiConfiguration).dristhiBaseURL();
        PowerMockito.when(context.configuration()).thenReturn(dristhiConfiguration);

        ChildAdvancedSearchInteractor interactor = Mockito.spy(ChildAdvancedSearchInteractor.class);
        PowerMockito.when(interactor.getHttpAgent()).thenReturn(httpAgent);

        Response<String> childResponse = new Response<>(ResponseStatus.success, "[{\"zeir_id\":\"F2103003KL\",\"last_name\":\"Mwanza\",\"first_name\":\"Sylvia\"}]");
        PowerMockito.when(httpAgent.fetch(ArgumentMatchers.anyString())).thenReturn(childResponse);

        Mockito.doReturn(true).when(appProperties).isTrue(ChildAppProperties.KEY.USE_NEW_ADVANCE_SEARCH_APPROACH);

        Map<String, String> searchParametersMap = new HashMap<>();

        searchParametersMap.put(Constants.KEY.FIRST_NAME, "Sylvia");
        searchParametersMap.put(Constants.KEY.LAST_NAME, "Mwanza");
        searchParametersMap.put(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP, "true");

        searchParametersMap.put(Constants.KEY.BIRTH_DATE, "2015-02-10:2021-02-25");

        Response<String> response = (Response<String>) globalSearchMethod.invoke(childAdvancedSearchInteractor, searchParametersMap);

        //Verify crucial method invocations
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(httpAgent, Mockito.atMostOnce()).fetch(stringArgumentCaptor.capture());

        List<String> capturedParamValue = stringArgumentCaptor.getAllValues();
        Assert.assertNotNull(capturedParamValue);
        Assert.assertEquals(1, capturedParamValue.size());
        Assert.assertEquals(expectedChildClientSearchUri, capturedParamValue.get(0));

        //Assert payload
        Assert.assertNotNull(response);

        String payload = response.payload();
        Assert.assertNotNull(payload);

        JSONArray payloadArray = new JSONArray(payload);
        Assert.assertEquals(1, payloadArray.length());

        Assert.assertEquals("Sylvia", payloadArray.getJSONObject(0).getString(Constants.KEY.FIRST_NAME));
        Assert.assertEquals("Mwanza", payloadArray.getJSONObject(0).getString(Constants.KEY.LAST_NAME));
    }

    @Test
    public void testGlobalSearchWhenUseNewAdvanceSearchApproachIsTrueAndOnlyMotherParamsProvided() throws Exception {

        String expectedMotherClientSearchUri = "https://www.test-opensrp.smartregister.org/rest/client/search?name=Madea&attribute=mother_guardian_number:+2546718880001&searchRelationship=mother";

        Method globalSearchMethod = ChildAdvancedSearchInteractor.class.getDeclaredMethod("globalSearch", Map.class);
        globalSearchMethod.setAccessible(true);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);

        Mockito.doReturn("https://www.test-opensrp.smartregister.org").when(dristhiConfiguration).dristhiBaseURL();
        PowerMockito.when(context.configuration()).thenReturn(dristhiConfiguration);

        ChildAdvancedSearchInteractor interactor = Mockito.spy(ChildAdvancedSearchInteractor.class);
        PowerMockito.when(interactor.getHttpAgent()).thenReturn(httpAgent);

        Response<String> motherResponse = new Response<>(ResponseStatus.success, "[{\"zeir_id\":\"M_F2103003KL\",\"last_name\":\"Madea\",\"first_name\":\"Momma\"}]");
        PowerMockito.when(httpAgent.fetch(ArgumentMatchers.anyString())).thenReturn(motherResponse);

        Mockito.doReturn(true).when(appProperties).isTrue(ChildAppProperties.KEY.USE_NEW_ADVANCE_SEARCH_APPROACH);

        Map<String, String> searchParametersMap = new HashMap<>();

        searchParametersMap.put(Constants.KEY.MOTHER_LAST_NAME, "Madea");
        searchParametersMap.put(Constants.KEY.MOTHER_GUARDIAN_NUMBER, "+2546718880001");

        Response<String> response = (Response<String>) globalSearchMethod.invoke(childAdvancedSearchInteractor, searchParametersMap);

        //Verify crucial method invocations
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(httpAgent, Mockito.atMostOnce()).fetch(stringArgumentCaptor.capture());

        List<String> capturedParamValue = stringArgumentCaptor.getAllValues();
        Assert.assertNotNull(capturedParamValue);
        Assert.assertEquals(1, capturedParamValue.size());
        Assert.assertEquals(expectedMotherClientSearchUri, capturedParamValue.get(0));

        //Assert payload
        Assert.assertNotNull(response);

        String payload = response.payload();
        Assert.assertNotNull(payload);

        JSONArray payloadArray = new JSONArray(payload);
        Assert.assertEquals(1, payloadArray.length());

        //Mother
        Assert.assertEquals("Momma", payloadArray.getJSONObject(0).getString(Constants.KEY.FIRST_NAME));
        Assert.assertEquals("Madea", payloadArray.getJSONObject(0).getString(Constants.KEY.LAST_NAME));
    }
}
