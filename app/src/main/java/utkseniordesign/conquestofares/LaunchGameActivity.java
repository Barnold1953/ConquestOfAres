/**
 * Created by Aaron Pool on 1/31/2015
 * This activity governs the screen for creating a new game.
 */

package utkseniordesign.conquestofares;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ToggleButton;

import Game.GameSettings;
import UI.DiscreteSeekBar;
import UI.DiscreteSeekBarListener;
import UI.UserInterfaceHelper;

public class LaunchGameActivity extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_creategame);

        createSpinners();

        findViewById(R.id.CustomMapOptions).setVisibility( View.GONE );
        findViewById(R.id.RandomMapOptions).setVisibility( View.GONE );
    }

    protected void createSpinners()
    {
        //set up spinners
        Spinner mapSizeSpinner = UserInterfaceHelper.createHintedSpinner(
                findViewById( R.id.mapSizeSpinner),
                R.array.map_sizes,
                R.layout.spinner_default
        );

        Spinner mapNameSpinner = UserInterfaceHelper.createHintedSpinner(
                findViewById( R.id.mapNameSpinner),
                R.array.map_names,
                R.layout.spinner_default
        );

        Spinner mapSymmetrySpinner = UserInterfaceHelper.createHintedSpinner(
                findViewById( R.id.mapSymmetrySpinner),
                R.array.map_symmetry,
                R.layout.spinner_default
        );

        Spinner horizontalWrapSpinner = UserInterfaceHelper.createHintedSpinner(
                findViewById( R.id.horizontalWrapSpinner),
                R.array.horizontal_wrap,
                R.layout.spinner_default
        );

        Spinner turnLengthSpinner = UserInterfaceHelper.createHintedSpinner(
                findViewById( R.id.turnLengthSpinner),
                R.array.turn_length,
                R.layout.spinner_default
        );

        Spinner victoryConditionSpinner = UserInterfaceHelper.createHintedSpinner(
                findViewById( R.id.victoryConditionSpinner),
                R.array.victory_conditions,
                R.layout.spinner_default
        );
    }

    public void handleToggleClick( View v )
    {
        //Get Resources
        Resources resources = getResources();
        int lightBlue = resources.getColor( R.color.lightBlue );
        int offWhite = resources.getColor( R.color.offWhite );
        int darkGrey = resources.getColor( R.color.darkGrey );
        int white = resources.getColor( R.color.white );

        UserInterfaceHelper.handleToggleButton( v, lightBlue, offWhite, white, darkGrey );

        if( v.getId() == R.id.RandomMap ) {
            findViewById(R.id.RandomMapOptions).setVisibility( View.VISIBLE );
            findViewById(R.id.CustomMapOptions).setVisibility(View.GONE);
        }

        if( v.getId() == R.id.CustomMap ) {
            findViewById(R.id.CustomMapOptions).setVisibility( View.VISIBLE );
            findViewById(R.id.RandomMapOptions).setVisibility( View.GONE );
        }
    }

    private GameSettings getSettings()
    {
        GameSettings gameSettings = new GameSettings();
        String numPlayers = UserInterfaceHelper.getSelectedToggleButton(findViewById(R.id.PlayerCountToggle));
        String horizontalWrap = ((Spinner)findViewById(R.id.horizontalWrapSpinner)).getSelectedItem().toString();
        String territoriesForVictory = ((Spinner)findViewById(R.id.victoryConditionSpinner)).getSelectedItem().toString();
        String turnLength = ((Spinner)findViewById(R.id.turnLengthSpinner)).getSelectedItem().toString();
        String mapSize = ((Spinner)findViewById(R.id.mapSizeSpinner)).getSelectedItem().toString();
        String mapSymmetry = ((Spinner)findViewById(R.id.mapSymmetrySpinner)).getSelectedItem().toString();

        int returnCode = gameSettings.parseSettings(
                numPlayers,
                horizontalWrap,
                territoriesForVictory,
                turnLength,
                mapSize,
                mapSymmetry
        );

        switch(returnCode) {
            case 1:
                UserInterfaceHelper.createDialog( this, "Please Specify Number of Players!", "Missing Information");
                return null;
            case 2:
                UserInterfaceHelper.createDialog( this, "Please Specify Map Wrap Settings!", "Missing Information");
                return null;
            case 3:
                UserInterfaceHelper.createDialog( this, "Please Specify a Victory Condition!", "Missing Information");
                return null;
            case 4:
                UserInterfaceHelper.createDialog( this, "Please Specify a Map Size!", "Missing Information");
                return null;
            case 5:
                UserInterfaceHelper.createDialog( this, "Please Specify a Map Symmetry Settings!", "Missing Information");
                return null;
            case 6:
                UserInterfaceHelper.createDialog( this, "Please Specify a Turn Length!", "Missing Information");
                return null;
            default:
                return gameSettings;
        }
    }

    public void startNewGame( View v )
    {
        GameSettings settings = getSettings();
        if(settings != null) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("Settings", settings);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
