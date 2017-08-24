package com.app.jonathan.willimissbart.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.app.jonathan.willimissbart.R;

public class DeleteAlertDialog extends AlertDialog {
    public interface DeleteDataElemListener {
        void deleteDataElem(int index);
    }

    private AlertDialog alertDialog;

    public DeleteAlertDialog(final Context context,
                             final DeleteDataElemListener callback,
                             final int index) {
        super(context);
        alertDialog = new Builder(new ContextThemeWrapper(context, R.style.AlertDialogTheme))
                .setTitle(R.string.deleting_data)
                .setMessage(context.getString(R.string.sure_to_delete))
                .setPositiveButton("Yes", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.deleteDataElem(index);
                        dismiss();
                    }
                })
                .setNegativeButton("No", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).create();
    }

    public void show() {
        alertDialog.show();
    }
}
