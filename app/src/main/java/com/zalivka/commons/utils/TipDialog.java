package com.zalivka.commons.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.zalivka.commons.R;

public abstract class TipDialog extends DialogFragment {

    public TipDialog() {super();}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(getResources().getString(R.string.tip_of_day));
        View root = LayoutInflater.from(getActivity()).inflate(getLayoutId(), null);
        b.setView(root);
        b.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onClose();
            }
        });
        if (root.findViewById(R.id.tip_text) != null) {
            TextView tw = (TextView) root.findViewById(R.id.tip_text);
            tw.setTypeface(Fonts.getTypeface(Fonts.MEDIUM));
        }

        Dialog dialog = b.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onClose();
            }
        });
        return b.create();
    }

    public abstract int getLayoutId();

    public void onClose() {};
}
