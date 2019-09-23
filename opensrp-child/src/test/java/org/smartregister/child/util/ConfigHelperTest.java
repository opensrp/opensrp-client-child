package org.smartregister.child.util;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.configurableviews.model.RegisterConfiguration;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConfigHelper.class)
public class ConfigHelperTest {

    @Mock
    private Context context;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDefaultRegisterConfigurationWithContextNull(){
        PowerMockito.spy(ConfigHelper.class);
        Assert.assertNull(ConfigHelper.defaultRegisterConfiguration(null));
    }

    @Test
    public void testDefaultRegisterConfigurationWithContext(){
        RegisterConfiguration config = new RegisterConfiguration();
        config.setEnableAdvancedSearch(false);
        config.setEnableFilterList(false);
        config.setEnableSortList(false);
        Assert.assertFalse(ConfigHelper.defaultRegisterConfiguration(context).isEnableSortList());
    }
}
