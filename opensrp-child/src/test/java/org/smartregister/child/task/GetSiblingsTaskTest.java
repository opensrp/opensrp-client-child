package org.smartregister.child.task;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.contract.IGetSiblings;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;

public class GetSiblingsTaskTest extends BaseUnitTest {

    private GetSiblingsTask getSiblingsTask;

    @Mock
    private IGetSiblings getSiblings;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        HashMap<String, String>  details = new HashMap<>();
        details.put(DBConstants.KEY.ZEIR_ID, "123123");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("baseEntityId", details, Constants.KEY.CHILD);
        getSiblingsTask = new GetSiblingsTask(commonPersonObjectClient, getSiblings);
    }

    @Test
    public void testConstructorNotNull() {
        Assert.assertNotNull(getSiblingsTask);
    }

    @Test
    public void testConstructWhereClauseFiltersDeadChild() {
        String whereClause = ReflectionHelpers.callInstanceMethod(getSiblingsTask, "constructWhereClause", ReflectionHelpers.ClassParameter.from(String.class, "123"));
        assertEquals(" WHERE ec_child_details.relational_id IN ('123') AND ec_client.date_removed IS NULL  AND ec_client.dod IS NULL AND ec_client.is_closed = 0", whereClause);
    }

}
