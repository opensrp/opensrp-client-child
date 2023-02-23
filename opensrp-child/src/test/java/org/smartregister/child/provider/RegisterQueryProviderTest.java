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

    private String expectedQuery = "SELECT ec_child_details.id FROM ec_child_details ec_child_details " +
            "LEFT JOIN ec_client ON ec_child_details.id = ec_client.base_entity_id ";

    private String registerQuery = "SELECT %s FROM ec_child_details " +
            "JOIN ec_mother_details ON ec_child_details.relational_id = ec_mother_details.base_entity_id " +
            "JOIN ec_client ON ec_client.base_entity_id = ec_child_details.base_entity_id " +
            "JOIN ec_client mother ON mother.base_entity_id = ec_mother_details.base_entity_id";

    private String registerQuerySelect = "ec_client.id as _id,ec_client.relationalid,ec_client.zeir_id," +
            "ec_child_details.relational_id,ec_client.gender,ec_client.base_entity_id,ec_client.first_name," +
            "ec_client.last_name,mother.first_name as mother_first_name,mother.last_name as mother_last_name," +
            "ec_client.dob,mother.dob as mother_dob,ec_mother_details.nrc_number as mother_nrc_number," +
            "ec_mother_details.father_name,ec_mother_details.epi_card_number,ec_client.client_reg_date," +
            "ec_child_details.pmtct_status,ec_client.last_interacted_with,ec_child_details.inactive," +
            "ec_child_details.lost_to_follow_up,ec_child_details.mother_guardian_phone_number,ec_client.address1";

    private String countQuery = "SELECT count(ec_child_details.id) FROM ec_child_details ec_child_details " +
            "LEFT JOIN ec_client ON ec_child_details.id = ec_client.base_entity_id ";

    private final String activeChildrenIdsQuery = "SELECT ec_child_details.id FROM ec_child_details " +
            "INNER JOIN ec_client ON ec_child_details.id = ec_client.id " +
            "WHERE (ec_child_details.date_removed IS NULL AND (ec_child_details.inactive is NOT true " +
            "OR ec_child_details.inactive is NULL) AND ec_child_details.is_closed IS NOT '1') " +
            "ORDER BY ec_client.last_interacted_with DESC ";

    private final String countActiveChildrenIdsQuery = "SELECT count(id) FROM ec_child_details " +
            "WHERE (date_removed IS NULL  AND (ec_child_details.inactive is NOT true " +
            "OR ec_child_details.inactive is NULL)  AND is_closed IS NOT '1')";

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
        Assert.assertEquals(" AND (ec_client.first_name LIKE '%something%' OR ec_client.last_name LIKE '%something%')", method.invoke(provider, filters));
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
        Assert.assertEquals(" WHERE condition = 'condition_filter'", method.invoke(provider, condition));
    }

    @Test
    public void testGetObjectIdsQueryWithEmptyConditionAndFilterReturnsQueryWithoutWhereClause() {
        String query = queryProvider.getObjectIdsQuery("", "");

        Assert.assertEquals(expectedQuery, query);
    }

    @Test
    public void testGetObjectIdsQueryWithNonEmptyConditionAndEmptyFilterReturnsQueryWithWhereClause() {
        expectedQuery += " WHERE condition = 'condition_value'";

        String query = queryProvider.getObjectIdsQuery("condition = 'condition_value'", "");

        Assert.assertEquals(expectedQuery, query);
    }

    @Test
    public void testGetObjectIdsQueryWithEmptyConditionAndNonEmptyFilterReturnsQueryWithWhereClause() {
        expectedQuery += " WHERE (ec_client.first_name LIKE '%123%' OR ec_client.last_name LIKE '%123%')";

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
        countQuery += " WHERE condition = 'condition_value'";

        String query = queryProvider.getCountExecuteQuery("condition = 'condition_value'", "");

        Assert.assertEquals(countQuery, query);
    }

    @Test
    public void testGetCountExecuteQueryWithEmptyConditionAndNonEmptyFiltersReturnsCountQueryWithWhereClause() {
        countQuery += " WHERE (ec_client.first_name LIKE '%4567%' OR ec_client.last_name LIKE '%4567%')";

        String query = queryProvider.getCountExecuteQuery("", "4567");

        Assert.assertEquals(countQuery, query);
    }

    @Test
    public void testGetCountExecuteQueryWithNonEmptyConditionAndFiltersReturnsCountQueryWithWhereClause() {
        countQuery += " WHERE condition = 'condition_value' AND (ec_client.first_name LIKE '%98765%' OR ec_client.last_name LIKE '%98765%')";

        String query = queryProvider.getCountExecuteQuery("condition = 'condition_value'", "98765");

        Assert.assertEquals(countQuery, query);
    }

    @Test
    public void testGetActiveChildrenIdsShouldReturnQueryToCountActiveChildrenIds() {
        String query = queryProvider.getActiveChildrenIds();
        Assert.assertEquals(activeChildrenIdsQuery, query);
    }

    @Test
    public void testGetActiveChildrenQueryCountsActiveChildrenIds() {
        String query = queryProvider.getActiveChildrenQuery();
        Assert.assertEquals(countActiveChildrenIdsQuery, query);
    }

}
