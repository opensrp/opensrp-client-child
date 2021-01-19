package org.smartregister.child.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class BCGNotificationDialogTest {

    private BCGNotificationDialog bcgNotificationDialog;

    @Mock
    private DialogInterface.OnClickListener positiveDialogListener;

    @Mock
    private DialogInterface.OnClickListener negativeDialogListener;

    private final Activity activity = Robolectric.setupActivity(AppCompatActivity.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bcgNotificationDialog = new BCGNotificationDialog(activity, positiveDialogListener, negativeDialogListener);
    }

    @Test
    public void testThatAlertDialogIsShown() {
        bcgNotificationDialog.show();
        AlertDialog alertDialog = bcgNotificationDialog.getAlertDialog();
        Assert.assertNotNull(alertDialog);
        final Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        final Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        Assert.assertEquals(positiveButton.getText().toString(), "OK");
        Assert.assertEquals(String.valueOf(positiveButton.getTextSize()), String.valueOf(20f));
        Assert.assertEquals(negativeButton.getText().toString(), "DISMISS");
        Assert.assertEquals(String.valueOf(negativeButton.getTextSize()), String.valueOf(20f));
    }
}