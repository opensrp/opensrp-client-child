package org.smartregister.child.presenter;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildTabbedDetailsContract;
import org.smartregister.repository.EventClientRepository;

public class BaseChildDetailsPresenterTest {

    private BaseChildDetailsPresenter childDetailsPresenter;

    @Mock
    private ChildTabbedDetailsContract.View view;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private EventClientRepository eventClientRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        Mockito.doReturn(eventClientRepository).when(childLibrary).eventClientRepository();
        childDetailsPresenter = Mockito.spy(new BaseChildDetailsPresenter(view));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }

    @Test
    public void testGetView() {
        Assert.assertNotNull(childDetailsPresenter.getView());
        Assert.assertTrue(childDetailsPresenter.getView() instanceof ChildTabbedDetailsContract.View);
    }

    @Test
    public void testReportLostCard() throws JSONException {
        String baseEntityId = "f50b1b66-6d71-4f7d-8c7c-70bec269412e";
        String sampleClientJson = "{\"type\":\"Client\",\"dateCreated\":\"2020-11-24T14:15:03.498+01:00\"," +
                "\"serverVersion\":1600329284628,\"baseEntityId\":\"f50b1b66-6d71-4f7d-8c7c-70bec269412e\"," +
                "\"identifiers\":{\"M_ZEIR_ID\":\"183317-7\"},\"addresses\":[{\"addressType\":\"\"," +
                "\"addressFields\":{\"address1\":\"vvvv\",\"address2\":\"dddd\"}}],\"attributes\":{\"first_birth\":\"yes\",\"card_status_date\":\"2020-11-24T16:55:42.748Z\"" +
                ",\"mother_tdv_doses\":\"2_plus_tdv_doses\",\"rubella_serology\":\"no\",\"mother_nationality\":\"unknown\"," +
                "\"registration_location_id\":\"e2b4a441-21b5-4d03-816b-09d45b17cad7\",\"registration_location_name\":\"CSB Hopital Bouficha\"}," +
                "\"firstName\":\"Liz\",\"lastName\":\"White\",\"birthdate\":\"1993-10-01T13:00:00.000+01:00\"," +
                "\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"_id\":\"fb5c2429-29bc-4417-88f7-938dce30100f\",\"_rev\":\"v1\"}";
        JSONObject client = new JSONObject(sampleClientJson);
        Mockito.doReturn(client).when(eventClientRepository).getClientByBaseEntityId(baseEntityId);
        Mockito.doNothing().when(eventClientRepository).addorUpdateClient(baseEntityId, client);

        childDetailsPresenter.reportLostCard(baseEntityId);

        Mockito.verify(view, Mockito.atMost(1)).notifyLostCardReported(Mockito.anyString());

    }
}