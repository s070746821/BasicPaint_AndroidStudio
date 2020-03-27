package com.gu11q.gu11qart420;

/**
 * Created by Jack Lee on 2018-02-12.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

// class for the Erase Image dialog
public class EraseImageDialogFragment extends DialogFragment {
      // create an AlertDialog and return it
    @Override
      public Dialog onCreateDialog(Bundle bundle) {

                 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // set the AlertDialog's message
                builder.setMessage(R.string.message_erase);

                 // add Erase Button
                builder.setPositiveButton(R.string.button_erase,
                            new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   getGu11qFragment().getGu11qArtView().clear(); // clear image
                               }
                }
                 );

        // add cancel Button
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create(); // return dialog
    }

    // gets a reference to the MainActivityFragment
    private MainActivityFragment getGu11qFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(
                R.id.gu11qfragment);
    }

    // tell MainActivityFragment that dialog is now displayed
    @Override
      public void onAttach(Context context) {
        super.onAttach(context);
        MainActivityFragment fragment =getGu11qFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

              // tell MainActivityFragment that dialog is no longer displayed
    @Override
      public void onDetach() {
                 super.onDetach();
                 MainActivityFragment fragment = getGu11qFragment();

                 if (fragment != null)
                        fragment.setDialogOnScreen(false);
              }
   }
