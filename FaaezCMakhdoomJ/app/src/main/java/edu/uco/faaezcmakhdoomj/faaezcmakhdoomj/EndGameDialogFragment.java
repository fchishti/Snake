package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class EndGameDialogFragment extends DialogFragment {


    public interface EndGameDialogListener {
        public void onEndGameDialogPositiveClick();
        public void onEndGameDialogNegativeClick();
    }

    EndGameDialogListener listener;
    int score;

    public EndGameDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (EndGameDialogListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement EndGameDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_end_game_dialog, null);

        TextView header = (TextView) view.findViewById(R.id.score);
        header.setText("Your score is " + getArguments().getInt("index"));

        builder.setView(view)
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener = (EndGameDialogListener) getActivity();
                        listener.onEndGameDialogPositiveClick();
                        EndGameDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener = (EndGameDialogListener) getActivity();
                        listener.onEndGameDialogNegativeClick();
                        EndGameDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

}
