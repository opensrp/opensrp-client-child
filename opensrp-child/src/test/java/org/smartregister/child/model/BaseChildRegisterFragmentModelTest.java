package org.smartregister.child.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.child.impl.model.TestBaseChildRegisterFragmentModel;
import org.smartregister.configurableviews.model.Field;

import java.util.ArrayList;

public class BaseChildRegisterFragmentModelTest {

    private TestBaseChildRegisterFragmentModel childRegisterFragmentModel;

    @Before
    public void setUp(){
        childRegisterFragmentModel = new TestBaseChildRegisterFragmentModel();
    }

    @Test
    public void getFilterText() {
        String expected = "<font color=#727272></font> <font color=#f0ab41>(0)</font>";
        String result = childRegisterFragmentModel.getFilterText(new ArrayList<Field>(),"");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void getSortText() {
        Field field = new Field();
        field.setDisplayName("display");
        String expected = "(Sort: display)";
        Assert.assertEquals(expected, childRegisterFragmentModel.getSortText(field));
    }

    @Test
    public void getJsonArray() {
    }
}