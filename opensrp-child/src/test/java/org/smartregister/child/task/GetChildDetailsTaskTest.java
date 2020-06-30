package org.smartregister.child.task;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.activity.BaseActivity;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.lang.reflect.Method;
import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class})
public class GetChildDetailsTaskTest {
    @Mock
    private View view;

    @Mock
    private TextView textView;

    @Mock
    private ImageView imageView;

    @Mock
    private OpenSRPImageLoader openSRPImageLoader;

    @Mock
    private BaseActivity  baseActivity;

    private GetChildDetailsTask getChildDetailsTask;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        getChildDetailsTask = new GetChildDetailsTask(baseActivity, "e34343-343434-67", view);
    }

    @Test
    public void testProcessChildNames() throws Exception {
        Method processChildNames = GetChildDetailsTask.class.getDeclaredMethod("processChildNames", BaseActivity.class, CommonPersonObjectClient.class);
        processChildNames.setAccessible(true);

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.FIRST_NAME, "John");
        childDetails.put(Constants.KEY.LAST_NAME, "Doe");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("e34343-343434-67", new HashMap<String, String>(), "Roja");
        commonPersonObjectClient.setColumnmaps(childDetails);

        Whitebox.setInternalState(getChildDetailsTask, "initials", textView);

        processChildNames.invoke(getChildDetailsTask, baseActivity, commonPersonObjectClient);
        verify(textView).setVisibility(View.GONE);
    }

    @Test
    public void testProcessChildNamesWithoutProfilePic() throws Exception {
        Method processChildNames = GetChildDetailsTask.class.getDeclaredMethod("processChildNames", BaseActivity.class, CommonPersonObjectClient.class);
        processChildNames.setAccessible(true);

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.FIRST_NAME, "John");
        childDetails.put(Constants.KEY.LAST_NAME, "Doe");
        childDetails.put(Constants.KEY.HAS_PROFILE_IMAGE, "false");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("e34343-343434-67", new HashMap<String, String>(), "Roja");
        commonPersonObjectClient.setColumnmaps(childDetails);

        Whitebox.setInternalState(getChildDetailsTask, "initials", textView);

        processChildNames.invoke(getChildDetailsTask, baseActivity, commonPersonObjectClient);
        verify(textView).setVisibility(View.VISIBLE);
    }

    @Test
    public void testUpdatePicture() throws Exception {
        Method updatePicture = GetChildDetailsTask.class.getDeclaredMethod("updatePicture", BaseActivity.class, String.class, CommonPersonObjectClient.class);
        updatePicture.setAccessible(true);

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.FIRST_NAME, "John");
        childDetails.put(Constants.KEY.LAST_NAME, "Doe");
        childDetails.put(Constants.KEY.GENDER, "female");
        childDetails.put(Constants.KEY.HAS_PROFILE_IMAGE, "false");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("e34343-343434-67", new HashMap<String, String>(), "Roja");
        commonPersonObjectClient.setColumnmaps(childDetails);

        Whitebox.setInternalState(getChildDetailsTask, "profilePhoto", imageView);
        Whitebox.setInternalState(getChildDetailsTask, "initials", textView);

        Resources resources = Mockito.mock(Resources.class);
        when(baseActivity.getResources()).thenReturn(resources);

        updatePicture.invoke(getChildDetailsTask, baseActivity, "e34343-343434-67", commonPersonObjectClient);
        verify(imageView).setVisibility(View.GONE);
    }

    @Test
    public void testUpdatePictureWithHasProfImage() throws Exception {
        Method updatePicture = GetChildDetailsTask.class.getDeclaredMethod("updatePicture", BaseActivity.class, String.class, CommonPersonObjectClient.class);
        updatePicture.setAccessible(true);

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.FIRST_NAME, "John");
        childDetails.put(Constants.KEY.LAST_NAME, "Doe");
        childDetails.put(Constants.KEY.GENDER, "male");
        childDetails.put(Constants.KEY.HAS_PROFILE_IMAGE, "true");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("e34343-343434-67", new HashMap<String, String>(), "Roja");
        commonPersonObjectClient.setColumnmaps(childDetails);

        Whitebox.setInternalState(getChildDetailsTask, "profilePhoto", imageView);
        Whitebox.setInternalState(getChildDetailsTask, "initials", textView);

        Resources resources = Mockito.mock(Resources.class);
        when(baseActivity.getResources()).thenReturn(resources);

        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getCachedImageLoaderInstance()).thenReturn(openSRPImageLoader);

        updatePicture.invoke(getChildDetailsTask, baseActivity, "e34343-343434-67", commonPersonObjectClient);
        verify(imageView).setVisibility(View.VISIBLE);
    }

}
