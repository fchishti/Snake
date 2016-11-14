package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class NameDialogFragment extends DialogFragment {

    public interface NameDialogListener {
        public void onNameDialogPositiveClick(String name);
    }

    NameDialogListener listener;

    public NameDialogFragment() {
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_name_dialog, null);

        final EditText nameField = (EditText) view.findViewById(R.id.name);

        builder.setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                            listener = (NameDialogListener) getActivity();
                            listener.onNameDialogPositiveClick(nameField.getText().toString());
                            NameDialogFragment.this.getDialog().cancel();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    listener = (NameDialogListener) getActivity();
                    listener.onNameDialogPositiveClick("");
                    NameDialogFragment.this.getDialog().cancel();
                }
                return false;
            }
        });

        return builder.create();
    }
}
