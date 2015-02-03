/**
 * Created by Aaron Pool on 1/31/2015
 * This activity governs the screen for creating a new game.
 */

package utkseniordesign.conquestofares;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class LaunchGameActivity extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_creategame);
        createSpinner( R.id.mapSizeSpinner, R.array.map_sizes, R.layout.spinner_default );
        createSpinner( R.id.mapSymmetrySpinner, R.array.map_symmetry, R.layout.spinner_default );

        //set options invisible by default
        findViewById(R.id.CustomMapOptions).setVisibility(View.INVISIBLE);
        findViewById(R.id.RandomMapOptions).setVisibility(View.INVISIBLE);
    }

    /**
     * @param spinnerId: The id of the spinner to be created (from xml layout)
     * @param textResId: The id of the text array to populate the spinner
     * @param layoutId: The id of the layout you want the spinner to utilize
     *
     * This function creates the spinner views utilized on this page. Once we have a better
     * idea of where we'll be querying user choices from, I'll probably create a more
     * streamlined interface for this. But, until then, a simple placeholder will do.
     */
    protected void createSpinner( int spinnerId, int textResId, int layoutId )
    {
        //Set up spinner
        Spinner spinner = ( Spinner ) findViewById( spinnerId );
        ArrayAdapter< CharSequence > arrayAdapter = ArrayAdapter.createFromResource( this, textResId, layoutId );
        spinner.setAdapter( arrayAdapter );
    }

    /**
     *
     * @param v: The button view
     *
     * This method deals with clicks of the toggle buttons. We only want one toggle button
     * to be active at a time, so any time a button is activated, this one deactivates any other
     * button in the view.
     */
    public void handleToggleClick( View v )
    {
        //Get Resources
        Resources resources = getResources();
        int lightBlue = resources.getColor( R.color.lightBlue );
        int offWhite = resources.getColor( R.color.offWhite );
        int darkGrey = resources.getColor( R.color.darkGrey );
        int white = resources.getColor( R.color.white );

        ToggleButton toggleButton = (ToggleButton) v;
        LinearLayout linearLayout = (LinearLayout) toggleButton.getParent();
        int buttonNumber = linearLayout.getChildCount();
        for( int i = 0; i < buttonNumber; i++ )
        {
            ToggleButton tempButton = ( ToggleButton ) linearLayout.getChildAt( i );
            tempButton.setBackgroundColor( offWhite );
            tempButton.setTextColor( darkGrey );
            tempButton.setChecked( false );
        }

        toggleButton.setBackgroundColor( lightBlue );
        toggleButton.setTextColor( white );

        if( v.getId() == R.id.RandomMap ) {
            findViewById(R.id.RandomMapOptions).setVisibility(View.VISIBLE);
            findViewById(R.id.CustomMapOptions).setVisibility(View.INVISIBLE);
        }

        if( v.getId() == R.id.CustomMap ) {
            findViewById(R.id.CustomMapOptions).setVisibility(View.VISIBLE);
            findViewById(R.id.RandomMapOptions).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
