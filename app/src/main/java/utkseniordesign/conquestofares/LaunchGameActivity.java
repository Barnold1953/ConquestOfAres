/**
 * Created by Aaron Pool on 1/31/2015
 * This activity governs the screen for creating a new game.
 */

package utkseniordesign.conquestofares;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.util.ArrayList;
import Game.GameSettings;
import UI.UserInterfaceHelper;

public class LaunchGameActivity extends googleClientApiActivity {

    final static int RC_SELECT_PLAYERS = 10000;
    GameSettings gameSettings = new GameSettings();

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
        int lightBlue = resources.getColor( R.color.darkBlue);
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
        String numPlayers = UserInterfaceHelper.getSelectedToggleButton(findViewById(R.id.PlayerCountToggle));
        String horizontalWrap = ((Spinner)findViewById(R.id.horizontalWrapSpinner)).getSelectedItem().toString();
        String territoriesForVictory = ((Spinner)findViewById(R.id.victoryConditionSpinner)).getSelectedItem().toString();
        String turnLength = ((Spinner)findViewById(R.id.turnLengthSpinner)).getSelectedItem().toString();
        String mapSize = ((Spinner)findViewById(R.id.mapSizeSpinner)).getSelectedItem().toString();
        String mapSymmetry = ((Spinner)findViewById(R.id.mapSymmetrySpinner)).getSelectedItem().toString();

        GameSettings.GameSettingErrors returnCode = gameSettings.parseSettings(
                numPlayers,
                horizontalWrap,
                territoriesForVictory,
                turnLength,
                mapSize,
                mapSymmetry
        );

        switch(returnCode) {
            case NUM_PLAYERS_UNSET:
                UserInterfaceHelper.createDialog( this, "Please Specify Number of Players!", "Missing Information");
                return null;
            case MAP_WRAP_UNSET:
                UserInterfaceHelper.createDialog( this, "Please Specify Map Wrap Settings!", "Missing Information");
                return null;
            case VICTORY_CONDITION_UNSET:
                UserInterfaceHelper.createDialog( this, "Please Specify a Victory Condition!", "Missing Information");
                return null;
            case MAP_SIZE_UNSET:
                UserInterfaceHelper.createDialog( this, "Please Specify a Map Size!", "Missing Information");
                return null;
            case MAP_SYMMETRY_UNSET:
                UserInterfaceHelper.createDialog( this, "Please Specify a Map Symmetry Settings!", "Missing Information");
                return null;
            case TURN_LENGTH_UNSET:
                UserInterfaceHelper.createDialog( this, "Please Specify a Turn Length!", "Missing Information");
                return null;
            default:
                return gameSettings;
        }
    }

    public void startNewGame( View v )
    {
        gameSettings = getSettings();
        Intent intent =
                Games.TurnBasedMultiplayer.getSelectOpponentsIntent(getClient(), 1, gameSettings.getNumPlayers()-1);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request,response,data);
        if(request == RC_SELECT_PLAYERS) {
            if(response != Activity.RESULT_OK) {
                //user canceled
                Log.d("Play Services","Match status not ok!");
                return;
            }

            // get invitees list
            final ArrayList<String> invitees =
                    data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch stuff
            Bundle automatchCriteria = null;
            int minAutoMatchedPLayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS,0);
            int maxAutoMatchedPLayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS,0);
            if(minAutoMatchedPLayers > 0) {
                automatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchedPLayers, maxAutoMatchedPLayers, 0);
            }

            TurnBasedMatchConfig turnBasedMatchConfig = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(automatchCriteria)
                    .build();

            Games.TurnBasedMultiplayer
                    .createMatch(getClient(),turnBasedMatchConfig)
                    .setResultCallback(new MatchInitiatedCallback(this));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MatchInitiatedCallback implements
            ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> {

        LaunchGameActivity gameActivity = null;

        public MatchInitiatedCallback(LaunchGameActivity activity) {
            super();
            gameActivity = activity;
        }
        @Override
        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
            Status status = result.getStatus();
            if(!status.isSuccess()) {
                Log.i("Match status: ", Integer.toString(status.getStatusCode()));
                return;
            }

            TurnBasedMatch match = result.getMatch();

            if(gameSettings != null) {
                Intent gameIntent = new Intent(gameActivity, GameActivity.class);
                gameIntent.putExtra("Settings",gameSettings);
                gameIntent.putExtra("Match",match);
                startActivity(gameIntent);
            }
        }
    }

}
