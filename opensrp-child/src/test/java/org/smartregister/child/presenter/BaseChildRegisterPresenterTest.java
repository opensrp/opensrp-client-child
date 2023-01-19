package org.smartregister.child.presenter;

import androidx.test.core.app.ApplicationProvider;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.R;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.impl.activity.TestBaseChildRegisterActivity;
import org.smartregister.child.interactor.ChildRegisterInteractor;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;
import org.smartregister.util.AppProperties;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-07-09.
 */
public class BaseChildRegisterPresenterTest extends BaseUnitTest {

    protected WeakReference<ChildRegisterContract.View> viewReference;

    @Mock
    protected ChildRegisterContract.Model model;

    @Mock
    private ChildRegisterContract.View view;

    @Mock
    private Context opensrpContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private UserService userService;

    @Spy
    private AppProperties appProperties;

    private BaseChildRegisterPresenter baseChildRegisterPresenter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        viewReference = Mockito.spy(new WeakReference<>(view));
        Mockito.doReturn(ApplicationProvider.getApplicationContext()).when(opensrpContext).applicationContext();
        Mockito.doReturn(appProperties).when(opensrpContext).getAppProperties();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn(userService).when(opensrpContext).userService();
        Mockito.doReturn(allSharedPreferences).when(userService).getAllSharedPreferences();
        Mockito.doReturn("testuser").when(allSharedPreferences).fetchRegisteredANM();

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
        Mockito.doNothing().when(interactorSpy).getNextUniqueId(Triple.of(formName, metadataMap, currentLocationId), baseChildRegisterPresenter);
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
                .processRegistration(jsonString, updateRegisterParams.getFormTag(), false);

        baseChildRegisterPresenter.saveForm(jsonString, updateRegisterParams);

        Mockito.verify(interactorSpy, Mockito.times(1))
                .saveRegistration(Mockito.<ChildEventClient>anyList(), Mockito.eq(jsonString), Mockito.eq(updateRegisterParams), Mockito.eq(baseChildRegisterPresenter));
    }

    @Test
    public void testCloseChildRecordShouldCallRemoveChildFromRegisterMethod() {
        String jsonString = new JSONObject().toString();
        ChildRegisterContract.View view = Mockito.spy(new TestBaseChildRegisterActivity());
        Mockito.doReturn(ApplicationProvider.getApplicationContext()).when(view).getContext();
        Mockito.doReturn(view).when(viewReference).get();
        ReflectionHelpers.setField(baseChildRegisterPresenter, "viewReference", viewReference);

        ChildRegisterContract.Interactor interactorSpy = Mockito.spy(new ChildRegisterInteractor());
        ReflectionHelpers.setField(baseChildRegisterPresenter, "interactor", interactorSpy);

        CoreLibrary.init(opensrpContext);

        baseChildRegisterPresenter.closeChildRecord(jsonString);

        Mockito.verify(interactorSpy, Mockito.times(1)).removeChildFromRegister(Mockito.eq(jsonString), Mockito.nullable(String.class));
    }

    @Test
    public void testOnRegistrationSavedShouldCallExpectedMethods() {
        ChildRegisterContract.View view = Mockito.spy(new TestBaseChildRegisterActivity());
        Mockito.doReturn(view).when(viewReference).get();
        ReflectionHelpers.setField(baseChildRegisterPresenter, "viewReference", viewReference);
        Mockito.doNothing().when(view).refreshList(FetchStatus.fetched);
        baseChildRegisterPresenter.onRegistrationSaved(true);
        Mockito.verify(view, Mockito.times(1)).refreshList(Mockito.eq(FetchStatus.fetched));
        Mockito.verify(view, Mockito.times(1)).hideProgressDialog();
    }

    @Test
    public void testSaveLanguage() {

        BaseChildRegisterPresenter baseChildRegisterPresenter = Mockito.spy(new BaseChildRegisterPresenter(view, null));
        baseChildRegisterPresenter.setModel(model);
        Assert.assertNotNull(baseChildRegisterPresenter);

        String swahiliLang = "swa";
        baseChildRegisterPresenter.saveLanguage(swahiliLang);

        ArgumentCaptor<String> selectLanguageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(model).saveLanguage(selectLanguageCaptor.capture());

        String capturedLang = selectLanguageCaptor.getValue();

        Assert.assertNotNull(capturedLang);
        Assert.assertEquals(swahiliLang, capturedLang);

        ArgumentCaptor<String> displayToastCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(view).displayToast(displayToastCaptor.capture());

        String capturedToastMessage = displayToastCaptor.getValue();

        Assert.assertNotNull(capturedToastMessage);
        Assert.assertEquals("swa selected", capturedToastMessage);
    }

    @Test
    public void testOnNoUniqueIdReturnsCorrectMessage() {

        BaseChildRegisterPresenter baseChildRegisterPresenter = Mockito.spy(new BaseChildRegisterPresenter(view, null));
        baseChildRegisterPresenter.setModel(model);
        Assert.assertNotNull(baseChildRegisterPresenter);

        baseChildRegisterPresenter.onNoUniqueId();

        ArgumentCaptor<Integer> stringMessageResourceIdCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(view).displayShortToast(stringMessageResourceIdCaptor.capture());

        Integer capturedResourceId = stringMessageResourceIdCaptor.getValue();

        Assert.assertNotNull(capturedResourceId);
        Assert.assertEquals(R.string.no_unique_id, capturedResourceId.intValue());
    }
}
