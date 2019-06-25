package org.smartregister.child.adapter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.domain.KeyValueItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndegwamartin on 2019-06-19.
 */
public class ChildRegistrationDataAdapterTest extends BaseUnitTest {

    private ChildRegistrationDataAdapter adapter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        List<KeyValueItem> keyValueItemList = new ArrayList<>();

        KeyValueItem keyValueItem = new KeyValueItem("first_name", "Bit");
        KeyValueItem keyValueItem2 = new KeyValueItem("last_name", "Alchemist");

        keyValueItemList.add(keyValueItem);
        keyValueItemList.add(keyValueItem2);

        adapter = new ChildRegistrationDataAdapter(keyValueItemList);
    }

    @Test
    public void testAdapterInstantiatesCorrectly() {

        Assert.assertNotNull(adapter);

    }

    @Test
    public void testAdapterGetsCorrectItemCountCorrectly() {

        Assert.assertEquals(2, adapter.getItemCount());

    }

}
