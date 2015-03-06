package UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.ToggleButton;

/**
 * Created by Aaron Pool on 2/24/2015.
 *
 * This class is basically a miscellaneous module to hold commonly used operations
 * so as to make code cleaner and more readable.
 */
public class UserInterfaceHelper {

    /**
     * This function returns a hinted (prompted) spinner that has already been defined in the xml. It could technically be used
     * with a programmatically created spinner as well, but you'd have to create it, give it an id, and then pass
     * that id to this function.
     *
     * @param spinnerView
     *      The view of the predefined spinner.
     * @param arrayResourceId
     *      The id of the string array for the spinner to display. The first entry in this array should be what you want
     *      the spinner prompt (hint) to be.
     * @param spinnerLayout
     *      The id of the layout for each individual spinner option view. This should probably be a textview.
     *      Unless you want it to display images, for some reason.
     * @return
     *      Returns the spinner instance, for later use.
     */
    public static Spinner createHintedSpinner ( View spinnerView, int arrayResourceId, int spinnerLayout ) {
        // set relevant variables
        Context context = spinnerView.getContext();
        ViewGroup parent = ( ViewGroup ) spinnerView.getParent();

        if( spinnerView == null ) {
            spinnerView = LayoutInflater.from( spinnerView.getContext() ).inflate( spinnerLayout, parent, false );
        }

        Spinner spinner = ( Spinner ) spinnerView;
        HintedArrayAdapter spinnerAdapter =
                HintedArrayAdapter.createFromResource( context, arrayResourceId, spinnerLayout );
        spinner.setAdapter( spinnerAdapter );
        spinner.setSelection( 0 );

        return spinner;
    }

    public static void handleToggleButton (
            View button,
            int colorBackgroundOn,
            int colorBackgroundOff,
            int colorfontOn,
            int colorFontOff ) {
        ToggleButton toggleButton = (ToggleButton) button;
        LinearLayout linearLayout = (LinearLayout) toggleButton.getParent();
        int buttonNumber = linearLayout.getChildCount();
        for( int i = 0; i < buttonNumber; i++ )
        {
            ToggleButton tempButton = ( ToggleButton ) linearLayout.getChildAt( i );
            tempButton.setBackgroundColor( colorBackgroundOff );
            tempButton.setTextColor( colorFontOff );
            tempButton.setChecked( false );
        }

        toggleButton.setChecked( true );
        toggleButton.setBackgroundColor( colorBackgroundOn );
        toggleButton.setTextColor( colorfontOn );
    }

    public static String getSelectedToggleButton( View v ) {
        LinearLayout linearLayout = (LinearLayout) v;
        int buttonNumber = linearLayout.getChildCount();
        for( int i = 0; i < buttonNumber; i++ )
        {
            ToggleButton tempButton = ( ToggleButton ) linearLayout.getChildAt( i );
            if( tempButton.isChecked() ) return tempButton.getText().toString();
        }

        return null;
    }

    public static void createDialog(Context context, String displayText, String titleText) {
        new AlertDialog.Builder(context)
                .setTitle(titleText)
                .setMessage(displayText)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
