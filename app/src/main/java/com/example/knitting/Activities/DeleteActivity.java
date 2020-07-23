package com.example.knitting.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.knitting.Pattern;
import com.parse.ParseException;

public class DeleteActivity extends DialogFragment {

    Pattern pattern;

    public DeleteActivity(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Are you sure you want to delete this pattern?");
        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                try {
                    pattern.delete();
                    dialog.dismiss();
                    Intent i = new Intent(getContext(), MainActivity.class);
                    startActivity(i);
                    getActivity().finish();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });

        return alertDialogBuilder.create();
    }
}
