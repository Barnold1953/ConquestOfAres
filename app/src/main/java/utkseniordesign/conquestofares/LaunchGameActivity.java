/**
 * Created by Aaron Pool on 1/31/2015
 * This activity governs the screen for creating a new game.
 */

package utkseniordesign.conquestofares;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import UI.HintedArrayAdapter;

public class LaunchGameActivity extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_creategame);

        //TODO: Reformat this in a cleaner form.
        //set up spinners
        Spinner mapSizeSpinner = ( Spinner ) findViewById( R.id.mapSizeSpinner );
        ArrayAdapter<CharSequence> mapSizeAdapter =
                ArrayAdapter.createFromResource( this, R.array.map_sizes, R.layout.spinner_default );
        mapSizeSpinner.setAdapter( mapSizeAdapter );

        Spinner mapSymmetrySpinner = ( Spinner ) findViewById( R.id.mapSymmetrySpinner );
        ArrayAdapter<CharSequence> mapSymmetryAdapter =
                ArrayAdapter.createFromResource( this, R.array.map_symmetry, R.layout.spinner_default );
        mapSymmetrySpinner.setAdapter( mapSymmetryAdapter );

        Spinner horizontalWrapSpinner = ( Spinner ) findViewById( R.id.horizontalWrapSpinner );
        ArrayAdapter<CharSequence> horizontalWrapAdapter =
                ArrayAdapter.createFromResource( this, R.array.horizontal_wrap, R.layout.spinner_default );
        horizontalWrapSpinner.setAdapter( horizontalWrapAdapter );

        Spinner mapNameSpinner = ( Spinner ) findViewById( R.id.mapNameSpinner );
        ArrayAdapter<CharSequence> mapNameAdapter =
                ArrayAdapter.createFromResource( this, R.array.map_names, R.layout.spinner_default );
        mapNameSpinner.setAdapter( mapNameAdapter );

        //set default selections and visibilities
        mapNameSpinner.setSelection( mapNameAdapter.getPosition( "Map Name" ) );
        mapSizeSpinner.setSelection( mapSizeAdapter.getPosition( "Map Size" ) );
        mapSymmetrySpinner.setSelection( mapSymmetryAdapter.getPosition( "Map Symmetry Setting" ) );
        horizontalWrapSpinner.setSelection( horizontalWrapAdapter.getPosition( "Map Wrap Setting" ) );

        findViewById(R.id.CustomMapOptions).setVisibility( View.GONE );
        findViewById(R.id.RandomMapOptions).setVisibility( View.GONE );
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
            findViewById(R.id.RandomMapOptions).setVisibility( View.VISIBLE );
            findViewById(R.id.CustomMapOptions).setVisibility(View.GONE);
        }

        if( v.getId() == R.id.CustomMap ) {
            findViewById(R.id.CustomMapOptions).setVisibility( View.VISIBLE );
            findViewById(R.id.RandomMapOptions).setVisibility( View.GONE );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startNewGame( View v )
    {
        Intent intent = new Intent( this, GameActivity.class );
        startActivity( intent );
    }
}
