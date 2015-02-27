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
import android.widget.Spinner;
import android.widget.ToggleButton;

import UI.UserInterfaceHelper;

public class LaunchGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategame);

        createSpinners();

        findViewById(R.id.CustomMapOptions).setVisibility(View.GONE);
        findViewById(R.id.RandomMapOptions).setVisibility(View.GONE);
    }

    protected void createSpinners() {
        //set up spinners
        Spinner mapSizeSpinner = UserInterfaceHelper.createHintedSpinner(
                findViewById(R.id.mapSizeSpinner),
                R.array.map_sizes,
                R.layout.spinner_default
       );

        Spinner mapNameSpinner = UserInterfaceHelper.createHintedSpinner(
                findViewById(R.id.mapNameSpinner),
                R.array.map_names,
                R.layout.spinner_default
       );

        Spinner mapSymmetrySpinner = UserInterfaceHelper.createHintedSpinner(
                findViewById(R.id.mapSymmetrySpinner),
                R.array.map_symmetry,
                R.layout.spinner_default
       );

        Spinner horizontalWrapSpinner = UserInterfaceHelper.createHintedSpinner(
                findViewById(R.id.horizontalWrapSpinner),
                R.array.horizontal_wrap,
                R.layout.spinner_default
       );
    }

    public void handleToggleClick(View v) {
        //Get Resources
        Resources resources = getResources();
        int lightBlue = resources.getColor(R.color.lightBlue);
        int offWhite = resources.getColor(R.color.offWhite);
        int darkGrey = resources.getColor(R.color.darkGrey);
        int white = resources.getColor(R.color.white);

        ToggleButton toggleButton = (ToggleButton) v;
        LinearLayout linearLayout = (LinearLayout) toggleButton.getParent();
        int buttonNumber = linearLayout.getChildCount();
        for(int i = 0; i < buttonNumber; i++) {
            ToggleButton tempButton = (ToggleButton)linearLayout.getChildAt(i);
            tempButton.setBackgroundColor(offWhite);
            tempButton.setTextColor(darkGrey);
            tempButton.setChecked(false);
        }

        toggleButton.setBackgroundColor(lightBlue);
        toggleButton.setTextColor(white);

        if(v.getId() == R.id.RandomMap) {
            findViewById(R.id.RandomMapOptions).setVisibility(View.VISIBLE);
            findViewById(R.id.CustomMapOptions).setVisibility(View.GONE);
        }

        if(v.getId() == R.id.CustomMap) {
            findViewById(R.id.CustomMapOptions).setVisibility(View.VISIBLE);
            findViewById(R.id.RandomMapOptions).setVisibility(View.GONE);
        }
    }

    public void startNewGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
