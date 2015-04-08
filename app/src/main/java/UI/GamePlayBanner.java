package UI;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import Game.GameState;
import utkseniordesign.conquestofares.GameActivity;
import utkseniordesign.conquestofares.R;

/**
 * Created by lasth_000 on 4/1/2015.
 */

public class GamePlayBanner extends RelativeLayout {
    GameActivity parentActivity = null;
    ImageView background = null;
    ImageView counter = null;
    TextView counterLabel = null;
    public GamePlayBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setUpPanel(Activity gameScreen) {
        parentActivity = (GameActivity) gameScreen;
        counter = (ImageView) findViewById(R.id.counter);
        counterLabel = (TextView) findViewById(R.id.counterLabel);
        background = (ImageView) findViewById(R.id.backgroundImage);
        changeContent(parentActivity.getGameController().getGameState());
    }

    public void changeContent(GameState state) {
        switch(state.currentState) {
            case PLACING_UNITS:
                // get positioning information
                background.setImageResource(R.drawable.game_start_banner);
                counterLabel.setText(Integer.toString(state.players.get(state.currentPlayerIndex).placeableUnits));
                break;
            default:
        }
    }
}
