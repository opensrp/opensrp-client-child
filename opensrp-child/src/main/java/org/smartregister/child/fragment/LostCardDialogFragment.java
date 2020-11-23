package org.smartregister.child.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import org.smartregister.child.R;

public class LostCardDialogFragment extends AlertDialog {

    private final View.OnClickListener onClickListener;

    public LostCardDialogFragment(Context context, View.OnClickListener onClickListener) {
        super(context);
        this.onClickListener = onClickListener;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.report_lost_card_dialog_layout, null);
        setView(view);
        view.findViewById(R.id.cancelButton).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.reportLostCardButton).setOnClickListener(onClickListener);
        setCancelable(true);
    }

    public void launch() {
        show();
        if (getWindow() != null) {
            getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}
