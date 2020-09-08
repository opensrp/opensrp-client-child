package org.smartregister.child.adapter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.activity.BaseActivity;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SiblingPictureAdapterTest extends BaseUnitTest {

    private SiblingPictureAdapter adapter;

    @Mock
    private BaseActivity baseActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ArrayList<String> siblingIds = new ArrayList<>();
        siblingIds.add("12345");
        siblingIds.add("123456");

        adapter = new SiblingPictureAdapter(baseActivity, siblingIds);
    }

    @Test
    public void testAdapterInstantiatesCorrectly() {
        assertNotNull(adapter);
    }

    @Test
    public void testGetItemIdReturnsCorrectId() {
        assertEquals(4224, adapter.getItemId(1));
    }

    @Test
    public void testGetItemCountReturnsTotalItemsInList() {
        assertEquals(2, adapter.getItemCount());
    }
}
