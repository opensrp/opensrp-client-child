package org.smartregister.child.activity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Utils;
import org.smartregister.view.activity.BaseProfileActivity;

import java.lang.reflect.Method;
import java.util.HashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, ChildMetadata.class, ChildLibrary.class})
public class BaseChildFormActivityTest {

    private BaseChildFormActivity childFormActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        childFormActivity = new BaseChildFormActivity();
    }

    @Test
    public void testLookUpQueryShouldReturnSelectQueryGivenATableNameAndFilterParametersMap() throws Exception {
        String query = "Select distinct(ec_client.id) as _id , ec_client.relationalid , ec_client.details , zeir_id , first_name , last_name , ec_client.gender , " +
                "dob , nrc_number , mother_guardian_phone_number , ec_mother_details.is_consented , ec_mother_details.preferred_language , ec_client.residential_area , " +
                "ec_client.residential_area_other , ec_client.residential_address , ec_client.base_entity_id " +
                "FROM ec_client  join ec_child_details on ec_child_details.relational_id=ec_mother_details.base_entity_id " +
                "join ec_mother_details on ec_mother_details.base_entity_id = ec_client.base_entity_id " +
                "WHERE  last_name Like '%lisa%' AND first_name Like '%mona%' ;";

        HashMap<String, String> map = new HashMap<>();
        map.put("first_name", "mona");
        map.put("last_name", "lisa");

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(ChildLibrary.class);
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, true, new RegisterQueryProvider());

        String tableName = metadata.getRegisterQueryProvider().getDemographicTable();

        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, true);
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);

        String actualQuery = childFormActivity.lookUpQuery(map, tableName);

        Assert.assertEquals(actualQuery, query);
    }

    @Test
    public void testIsValidDateShouldReturnTrueIfStringIsValidDate() throws Exception {
        Method dateMethod = BaseChildFormActivity.class.getDeclaredMethod("isDate", String.class);
        dateMethod.setAccessible(true);

        BaseChildFormActivity childActivity = Mockito.spy(childFormActivity);

        String date = "2000-09-01";
        Assert.assertTrue((boolean) dateMethod.invoke(childActivity, date));
    }

    @Test
    public void testIsValidDateShouldReturnFalseIfStringIsInvalidDate() throws Exception {
        Method dateMethod = BaseChildFormActivity.class.getDeclaredMethod("isDate", String.class);
        dateMethod.setAccessible(true);

        BaseChildFormActivity childActivity = Mockito.spy(childFormActivity);

        String date = "9/1/2000";
        Assert.assertFalse((boolean) dateMethod.invoke(childActivity, date));
    }
}
