package org.smartregister.child.provider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.LayoutInflater;

import org.junit.Test;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.R;

public class AdvancedSearchClientsProviderTest extends BaseUnitTest {

    @Test
    public void testInflateLayoutForCursorAdapterInflatesCorrectView() {
        AdvancedSearchClientsProvider provider = mock(AdvancedSearchClientsProvider.class);
        LayoutInflater inflater = mock(LayoutInflater.class);
        doReturn(inflater).when(provider).inflater();
        when(provider.inflatelayoutForCursorAdapter()).thenCallRealMethod();
        provider.inflatelayoutForCursorAdapter();
        verify(inflater).inflate(eq(R.layout.advanced_search_client), any());
    }

}
