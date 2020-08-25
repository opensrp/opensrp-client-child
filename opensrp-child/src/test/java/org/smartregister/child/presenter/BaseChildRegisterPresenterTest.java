package org.smartregister.child.presenter;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.impl.activity.TestBaseChildRegisterActivity;
import org.smartregister.child.interactor.ChildRegisterInteractor;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-07-09.
 */
public class BaseChildRegisterPresenterTest extends BaseUnitTest {

    @Mock
    protected WeakReference<ChildRegisterContract.View> viewReference;

    @Mock
    protected ChildRegisterContract.Model model;

    private BaseChildRegisterPresenter baseChildRegisterPresenter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        baseChildRegisterPresenter = new BaseChildRegisterPresenter(viewReference.get(), model);
    }

    @Test
    public void testBaseChildRegisterPresenterInstantiatesCorrectly() {

        Assert.assertNotNull(baseChildRegisterPresenter);

    }

    @Test
    public void testRegisterViewConfigurationsInvokesModelMethodWithCorrectParameters() {
        List<String> viewIds = new ArrayList<>();
        viewIds.add("123");
        viewIds.add("456");

        baseChildRegisterPresenter.registerViewConfigurations(viewIds);

        Mockito.verify(model).registerViewConfigurations(viewIds);

    }

    @Test
    public void testUnRegisterViewConfigurationsInvokesModelMethodWithCorrectParameters() {
        List<String> viewIds = new ArrayList<>();
        viewIds.add("123");
        viewIds.add("456");

        baseChildRegisterPresenter.unregisterViewConfiguration(viewIds);

        Mockito.verify(model).unregisterViewConfiguration(viewIds);

    }

    @Test
    public void testSetInteractorFunctionsCorrectly() {

        ChildRegisterContract.Interactor interactor = new ChildRegisterInteractor();
        Assert.assertNotNull(interactor);
        Assert.assertNotNull(baseChildRegisterPresenter);
        baseChildRegisterPresenter.setInteractor(interactor);
        Assert.assertNotNull(baseChildRegisterPresenter.interactor);

    }

    @Test
    public void testOnDestroyShouldNullifyInteractorIfIsChangingConfigurationIsFalse() {
        ChildRegisterContract.Interactor interactorSpy = Mockito.spy(new ChildRegisterInteractor());
        ReflectionHelpers.setField(baseChildRegisterPresenter, "interactor", interactorSpy);
        baseChildRegisterPresenter.onDestroy(false);
        Mockito.verify(interactorSpy, Mockito.times(1)).onDestroy(false);
        Assert.assertNull(ReflectionHelpers.getField(baseChildRegisterPresenter, "viewReference"));
        Assert.assertNull(ReflectionHelpers.getField(baseChildRegisterPresenter, "interactor"));
        Assert.assertNull(ReflectionHelpers.getField(baseChildRegisterPresenter, "model"));
    }

    @Test
    public void testOnDestroyShouldNotNullifyInteractorIfIsChangingConfigurationIsTrue() {
        ChildRegisterContract.Interactor interactorSpy = Mockito.spy(new ChildRegisterInteractor());
        ReflectionHelpers.setField(baseChildRegisterPresenter, "interactor", interactorSpy);
        baseChildRegisterPresenter.onDestroy(true);
        Mockito.verify(interactorSpy, Mockito.times(1)).onDestroy(true);
        Assert.assertNull(ReflectionHelpers.getField(baseChildRegisterPresenter, "viewReference"));
        Assert.assertNotNull(ReflectionHelpers.getField(baseChildRegisterPresenter, "interactor"));
        Assert.assertNotNull(ReflectionHelpers.getField(baseChildRegisterPresenter, "model"));
    }

    @Test
    public void testUpdateInitialsShouldCallExpectedMethods() {
        String initials = "JD";
        ChildRegisterContract.Model modelSpy = Mockito.spy(new BaseChildRegisterModel());
        ReflectionHelpers.setField(baseChildRegisterPresenter, "model", modelSpy);
        Mockito.doReturn(initials).when(modelSpy).getInitials();
        ChildRegisterContract.View view = Mockito.spy(new TestBaseChildRegisterActivity());
        Mockito.doReturn(view).when(viewReference).get();
        ReflectionHelpers.setField(baseChildRegisterPresenter, "viewReference", viewReference);
        baseChildRegisterPresenter.updateInitials();
        Mockito.verify(modelSpy, Mockito.times(1)).getInitials();
        Mockito.verify(view).updateInitialsText(initials);
    }

    @Test
    public void testStartFormShouldRequestForEntityIdIfBlank() throws Exception {
        String formName = "test_form";
        String entityId = "";
        String metadata = "";
        String currentLocationId = "09453-ert35";
        Map<String, String> metadataMap = new HashMap<>();
        ChildRegisterContract.Interactor interactorSpy = Mockito.spy(new ChildRegisterInteractor());
        ReflectionHelpers.setField(baseChildRegisterPresenter, "interactor", interactorSpy);
        baseChildRegisterPresenter.startForm(formName, entityId, metadata, currentLocationId);
        Mockito.verify(interactorSpy, Mockito.times(1)).getNextUniqueId(Triple.of(formName, metadataMap, currentLocationId), baseChildRegisterPresenter);
    }

    @Test
    public void testStartFormShouldCallStartFormActivity() throws Exception {
        String formName = "test_form";
        String entityId = "345435-re34535-34";
        String metadata = "";
        String currentLocationId = "09453-ert35";
        Map<String, String> metadataMap = new HashMap<>();
        ChildRegisterContract.Model modelSpy = Mockito.spy(new BaseChildRegisterModel());
        ReflectionHelpers.setField(baseChildRegisterPresenter, "model", modelSpy);
        Mockito.doReturn(new JSONObject()).when(modelSpy).getFormAsJson(formName, entityId, currentLocationId, metadataMap);

        ChildRegisterContract.View view = Mockito.spy(new TestBaseChildRegisterActivity());
        Mockito.doReturn(view).when(viewReference).get();
        Mockito.doNothing().when(view).startFormActivity(Mockito.any(JSONObject.class));
        ReflectionHelpers.setField(baseChildRegisterPresenter, "viewReference", viewReference);

        baseChildRegisterPresenter.startForm(formName, entityId, metadata, currentLocationId);
        Mockito.verify(modelSpy, Mockito.times(1)).getFormAsJson(formName, entityId, currentLocationId, metadataMap);
        Mockito.verify(view).startFormActivity(Mockito.any(JSONObject.class));
    }

    @Test
    public void testSavedFormShouldCallSaveRegistration() {
        ChildRegisterContract.Interactor interactorSpy = Mockito.spy(new ChildRegisterInteractor());
        ReflectionHelpers.setField(baseChildRegisterPresenter, "interactor", interactorSpy);

        ChildRegisterContract.Model modelSpy = Mockito.spy(new BaseChildRegisterModel());
        ReflectionHelpers.setField(baseChildRegisterPresenter, "model", modelSpy);

        String jsonString = new JSONObject().toString();
        UpdateRegisterParams updateRegisterParams = new UpdateRegisterParams();

        List<ChildEventClient> childEventClientList = new ArrayList<>();
        childEventClientList.add(new ChildEventClient(new Client("s"), new Event()));
        childEventClientList.add(new ChildEventClient(new Client("si"), new Event()));

        Mockito.doReturn(childEventClientList).when(modelSpy)
                .processRegistration(jsonString, updateRegisterParams.getFormTag());

        baseChildRegisterPresenter.saveForm(jsonString, updateRegisterParams);

        Mockito.verify(interactorSpy, Mockito.times(1))
                .saveRegistration(Mockito.<ChildEventClient>anyList(), Mockito.eq(jsonString), Mockito.eq(updateRegisterParams), Mockito.eq(baseChildRegisterPresenter));
    }
}
