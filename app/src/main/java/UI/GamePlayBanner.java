package UI;

import android.content.Context;
import android.content.res.Resources;
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
import utkseniordesign.conquestofares.R;

/**
 * Created by lasth_000 on 4/1/2015.
 */

public class GamePlayBanner extends RelativeLayout {
    ImageView background = null;
    TextView label = null;
    int width = 0;
    int height = 0;
    public GamePlayBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public GamePlayBanner(Context context) {
        super(context);
        //set up layout
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);

        // add background
        background = new ImageView(getContext());
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageParams.addRule(ALIGN_PARENT_TOP);
        background.setLayoutParams(imageParams);
        background.setAdjustViewBounds(true);
        background.setId(1);
        addView(background);
    }

    public void changeContent(GameState state) {
        switch(state.currentState) {
            case PLACING_UNITS:
                // get positioning information
                background.setImageResource(R.drawable.game_start_banner);
                background.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                width = background.getMeasuredWidth();
                height = background.getMeasuredHeight();

                // add label if needed
                if( label == null ) {
                    label = new TextView(getContext());
                    RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    textParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    textParams.addRule(RelativeLayout.ALIGN_TOP, 1);
                    textParams.setMargins(0, height / 4, width / 8, 0);
                    label.setLayoutParams(textParams);
                    label.setTextColor(getResources().getColor(R.color.darkGrey));
                    label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                    addView(label);
                }
                label.setText(Integer.toString(state.players.get(state.currentPlayerIndex).placeableUnits));
                break;
            default:
        }
    }
}
