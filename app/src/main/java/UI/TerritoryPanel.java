package UI;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import Game.GameState;
import Game.Player;
import Game.Territory;
import Game.Unit;
import utkseniordesign.conquestofares.GameActivity;
import utkseniordesign.conquestofares.R;

/**
 * Created by lasth_000 on 3/25/2015.
 */

public class TerritoryPanel extends LinearLayout {
    GameActivity parentActivity = null;
    Territory currentTerritory = null;
    TextView emptyIndicator = null;
    LinearLayout militaryPanel = null;
    ImageView addUnitButton = null;
    ImageView subtractUnitButton = null;
    Boolean ownerButtonsVisible = false;
    Boolean panelVisible = false;
    Animator.AnimatorListener animationListener = null;

    public TerritoryPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setUpPanel(Activity gameScreen) {
        parentActivity = (GameActivity) gameScreen;
        addUnitButton = (ImageView) findViewById(R.id.add_units);
        subtractUnitButton = (ImageView) findViewById(R.id.subtract_units);
        emptyIndicator = (TextView) findViewById(R.id.emptyIndicator);
        setListeners();
    }

    public void setListeners() {
        final GameState.State state = parentActivity.getGameController().getGameState().currentState;
        animationListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (getVisibility() == GONE && !panelVisible) {
                    setVisibility(VISIBLE);
                }
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                if (getVisibility() == VISIBLE && !panelVisible) {
                    panelVisible = true;
                } else {
                    setVisibility(GONE);
                    panelVisible = false;
                }
            }
            @Override public void onAnimationCancel(Animator animator) {
                // if the animation is canceled, go back to invisible
                if( getVisibility() == VISIBLE) {
                    setVisibility(GONE);
                }
            }
            @Override public void onAnimationRepeat(Animator animator) {}
        };
        addUnitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentTerritory.addUnit(Unit.Type.soldier)) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(parentActivity.getGamePlayBanner().counterLabel);
                } else if(currentTerritory.owner.placeableUnits == 0 && (state == GameState.State.PLACING_UNITS || state == GameState.State.INITIAL_UNIT_PLACEMENT)) { parentActivity.setCheckMark(true); }
                parentActivity.getGamePlayBanner().changeContent();
                update(currentTerritory);
            }
        });
        subtractUnitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentTerritory.removeUnits(Unit.Type.soldier)) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(emptyIndicator);
                } else if(currentTerritory.owner.placeableUnits == 1 && (state == GameState.State.PLACING_UNITS || state == GameState.State.INITIAL_UNIT_PLACEMENT)) { parentActivity.setCheckMark(false); }
                parentActivity.getGamePlayBanner().changeContent();
                update(currentTerritory);
            }
        });
    }

    public Boolean isVisible() {
        return panelVisible;
    }

    private void populateMilitaryPanel() {
        if(currentTerritory.units.isEmpty()) {
            militaryPanel.addView(emptyIndicator);
        }
        else {
            for (int i = 0; i < currentTerritory.units.size(); i++)
                militaryPanel.addView(new UnitIcon(getContext(), parentActivity.getGameController().getGameState(), i));
        }
    }

    public void setMilitaryPanel(View v) {
        militaryPanel = (LinearLayout)v;
    }

    public void toggle() {
        if(getVisibility()==GONE) {
            YoYo.with(Techniques.SlideInUp).withListener(animationListener).duration(500).playOn(this);
        } else {
            YoYo.with(Techniques.SlideOutDown).withListener(animationListener).duration(500).playOn(this);
        }
    }

    public void update(Territory territory) {
        if(militaryPanel==null){
            militaryPanel = (LinearLayout)findViewById(R.id.militaryPanel);
        }
        currentTerritory = territory;
        if(territory.owner != parentActivity.getGameController().getCurrentPlayer()
                || parentActivity.getGameController().getGameState().currentState != GameState.State.INITIAL_UNIT_PLACEMENT
                && parentActivity.getGameController().getGameState().currentState != GameState.State.PLACING_UNITS)
        {
            if(ownerButtonsVisible) {
                // if they're visible, but they shouldn't be, hide them smoothly
                YoYo.with(Techniques.SlideOutDown).duration(500).playOn(addUnitButton);
                YoYo.with(Techniques.SlideOutDown).duration(500).playOn(subtractUnitButton);
            } else {
                // otherwise, this is the initialization of the menu, and they need to go away immediately
                YoYo.with(Techniques.SlideOutDown).duration(0).playOn(addUnitButton);
                YoYo.with(Techniques.SlideOutDown).duration(0).playOn(subtractUnitButton);
            }
            ownerButtonsVisible = false;
        } else {
            if(!ownerButtonsVisible) {
                YoYo.with(Techniques.SlideInUp).duration(500).playOn(addUnitButton);
                YoYo.with(Techniques.SlideInUp).duration(500).playOn(subtractUnitButton);
            }
            ownerButtonsVisible = true;
        }
        // TODO: Optimize so I just add new views, rather than removing them all and reading
        militaryPanel.removeAllViews();
        populateMilitaryPanel();
    }

}
