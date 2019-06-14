package org.smartregister.child.cursor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;

/**
 * Created by ndegwamartin on 2019-06-10.
 */
public class AdvancedMatrixCursorTest extends BaseUnitTest {
    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAdvancedMatrixCursorInstantiatesCorrectly() {

        AdvancedMatrixCursor advancedMatrixCursor = new AdvancedMatrixCursor(new String[] {"column1", "column2"});
        Assert.assertNotNull(advancedMatrixCursor);

    }

}
