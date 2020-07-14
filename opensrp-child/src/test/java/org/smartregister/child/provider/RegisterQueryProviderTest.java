package org.smartregister.child.provider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RegisterQueryProviderTest extends BaseUnitTest {

    private RegisterQueryProvider queryProvider;

    private String expectedQuery = "select ec_client.object_id from ec_client_search ec_client  " +
            "join ec_child_details on ec_client.object_id =  ec_child_details.id " +
            "left join ec_child_details_search on ec_client.object_id =  ec_child_details_search.object_id ";

    private String registerQuery = "select %s from ec_child_details " +
            "join ec_mother_details on ec_child_details.relational_id = ec_mother_details.base_entity_id " +
            "join ec_client on ec_client.base_entity_id = ec_child_details.base_entity_id " +
            "join ec_client mother on mother.base_entity_id = ec_mother_details.base_entity_id";

    private String registerQuerySelect = "ec_client.id as _id,ec_client.relationalid,ec_client.zeir_id," +
            "ec_child_details.relational_id,ec_client.gender,ec_client.base_entity_id,ec_client.first_name," +
            "ec_client.last_name,mother.first_name as mother_first_name,mother.last_name as mother_last_name," +
            "ec_client.dob,mother.dob as mother_dob,ec_mother_details.nrc_number as mother_nrc_number," +
            "ec_mother_details.father_name,ec_mother_details.epi_card_number,ec_client.client_reg_date," +
            "ec_child_details.pmtct_status,ec_client.last_interacted_with,ec_child_details.inactive," +
            "ec_child_details.lost_to_follow_up,ec_child_details.mother_guardian_phone_number,ec_client.address1";

    private String countQuery = "select count(ec_client.object_id) from ec_client_search ec_client  " +
            "join ec_child_details on ec_client.object_id =  ec_child_details.id " +
            "left join ec_child_details_search on ec_client.object_id =  ec_child_details_search.object_id ";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        queryProvider = new RegisterQueryProvider();
    }

    @Test
    public void testGetFilterWithEmptyFiltersShouldReturnEmptyString() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = RegisterQueryProvider.class.getDeclaredMethod("getFilter", String.class);
        method.setAccessible(true);

        RegisterQueryProvider provider = Mockito.spy(queryProvider);

        String filters = "";
        Assert.assertEquals("", method.invoke(provider, filters));
    }

    @Test
    public void testGetFilterWithNonEmptyFilterShouldReturnFormattedFilterCondition() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = RegisterQueryProvider.class.getDeclaredMethod("getFilter", String.class);
        method.setAccessible(true);

        RegisterQueryProvider provider = Mockito.spy(queryProvider);

        String filters = "something";
        Assert.assertEquals(" AND ec_client.phrase MATCH '*something*'", method.invoke(provider, filters));
    }

    @Test
    public void testGetMainConditionWithEmptyConditionShouldReturnEmptyString() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = RegisterQueryProvider.class.getDeclaredMethod("getMainCondition", String.class);
        method.setAccessible(true);

        RegisterQueryProvider provider = Mockito.spy(queryProvider);

        String condition = "";
        Assert.assertEquals("", method.invoke(provider, condition));
    }

    @Test
    public void testGetMainConditionWithNonEmptyConditionShouldReturnFormattedWhereClause() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = RegisterQueryProvider.class.getDeclaredMethod("getMainCondition", String.class);
        method.setAccessible(true);

        RegisterQueryProvider provider = Mockito.spy(queryProvider);

        String condition = "condition = 'condition_filter'";
        Assert.assertEquals(" where condition = 'condition_filter'", method.invoke(provider, condition));
    }

    @Test
    public void testGetObjectIdsQueryWithEmptyConditionAndFilterReturnsQueryWithoutWhereClause() {
        String query = queryProvider.getObjectIdsQuery("", "");

        Assert.assertEquals(expectedQuery, query);
    }

    @Test
    public void testGetObjectIdsQueryWithNonEmptyConditionAndEmptyFilterReturnsQueryWithWhereClause() {
        expectedQuery += " where condition = 'condition_value'";

        String query = queryProvider.getObjectIdsQuery("condition = 'condition_value'", "");

        Assert.assertEquals(expectedQuery, query);
    }

    @Test
    public void testGetObjectIdsQueryWithEmptyConditionAndNonEmptyFilterReturnsQueryWithWhereClause() {
        expectedQuery += " where ec_client.phrase MATCH '*123*'";

        String query = queryProvider.getObjectIdsQuery("", "123");

        Assert.assertEquals(expectedQuery, query);
    }

    @Test
    public void testMainRegisterQueryNoParamsReturnsQueryWithAllSelectFields() {
        String expectedQuery = String.format(registerQuery, registerQuerySelect);

        String query = queryProvider.mainRegisterQuery();

        Assert.assertEquals(expectedQuery, query);
    }

    @Test
    public void testMainRegisterQueryWithBlankSelectParamReturnsQueryWithAllSelectFields() {
        String expectedQuery = String.format(registerQuery, registerQuerySelect);

        String query = queryProvider.mainRegisterQuery("");

        Assert.assertEquals(expectedQuery, query);
    }

    @Test
    public void testMainRegisterQueryWithSelectParamReturnsQueryWithSpecifiedSelectFields() {
        String selectOptions = "ec_client.id as _id,ec_client.relationalid";
        String expectedQuery = String.format(registerQuery, selectOptions);

        String query = queryProvider.mainRegisterQuery(selectOptions);

        Assert.assertEquals(expectedQuery, query);
    }

    @Test
    public void testGetCountExecuteQueryWithEmptyConditionAndFiltersReturnsCountQueryWithoutWhereFilter() {
        String query = queryProvider.getCountExecuteQuery("", "");

        Assert.assertEquals(countQuery, query);
    }

    @Test
    public void testGetCountExecuteQueryWithNonEmptyConditionAndEmptyFiltersReturnsCountQueryWithoutWhereClause() {
        countQuery += " where condition = 'condition_value'";

        String query = queryProvider.getCountExecuteQuery("condition = 'condition_value'", "");

        Assert.assertEquals(countQuery, query);
    }

    @Test
    public void testGetCountExecuteQueryWithEmptyConditionAndNonEmptyFiltersReturnsCountQueryWithWhereClause() {
        countQuery += " where ec_client.phrase MATCH '*4567*'";

        String query = queryProvider.getCountExecuteQuery("", "4567");

        Assert.assertEquals(countQuery, query);
    }

    @Test
    public void testGetCountExecuteQueryWithNonEmptyConditionAndFiltersReturnsCountQueryWithWhereClause() {
        countQuery += " where condition = 'condition_value' AND ec_client.phrase MATCH '*98765*'";

        String query = queryProvider.getCountExecuteQuery("condition = 'condition_value'", "98765");

        Assert.assertEquals(countQuery, query);
    }

}
