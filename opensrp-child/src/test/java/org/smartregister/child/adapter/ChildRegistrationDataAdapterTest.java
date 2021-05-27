package org.smartregister.child.adapter;

import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.domain.KeyValueItem;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by ndegwamartin on 2019-06-19.
 */
public class ChildRegistrationDataAdapterTest extends BaseUnitTest {

    private ChildRegistrationDataAdapter adapter;
    private final List<KeyValueItem> keyValueItemList = new ArrayList<>();

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

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

    @Test
    public void testOnBindViewHolderUpdatesViewHolderTextViewsWithCorrectValues() {
        TextView mKeyText = mock(TextView.class);
        TextView mValueText = mock(TextView.class);
        ChildRegistrationDataAdapter.ViewHolder viewHolder = mock(ChildRegistrationDataAdapter.ViewHolder.class);
        viewHolder.keyText = mKeyText;
        viewHolder.valueText = mValueText;

        adapter.onBindViewHolder(viewHolder, 0);

        verify(mKeyText).setText(keyValueItemList.get(0).getKey());
        verify(mValueText).setText(keyValueItemList.get(0).getValue());
    }

}
