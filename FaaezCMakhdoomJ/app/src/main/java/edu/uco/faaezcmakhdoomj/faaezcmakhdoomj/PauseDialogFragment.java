package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;


public class PauseDialogFragment extends DialogFragment {

    public interface PauseDialogListener {
        public void onPauseDialogPositiveClick();
        public void onPauseDialogNegativeClick();
    }

    PauseDialogFragment.PauseDialogListener listener;


    public PauseDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (PauseDialogFragment.PauseDialogListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement PasueDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_pausedialog, null);

        builder.setView(view)
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener = (PauseDialogFragment.PauseDialogListener) getActivity();
                        listener.onPauseDialogPositiveClick();
                        PauseDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton("Resume", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener = (PauseDialogFragment.PauseDialogListener) getActivity();
                        listener.onPauseDialogNegativeClick();
                        PauseDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
