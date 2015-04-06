package UI;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

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
    public YoYo.YoYoString animating = null;

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
        addUnitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Player currentPlayer = parentActivity.getGameController().getCurrentPlayer();
                if (!currentTerritory.addUnit(currentTerritory.x, currentTerritory.y, Unit.Type.soldier)) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(parentActivity.getGamePlayBanner().label);
                } else if(currentTerritory.owner.placeableUnits == 0) { parentActivity.setCheckMark(true); }
                parentActivity.getGamePlayBanner().changeContent(parentActivity.getGameController().getGameState());
                update(currentTerritory);
            }
        });
        subtractUnitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Player currentPlayer = parentActivity.getGameController().getCurrentPlayer();
                if(!currentTerritory.removeUnits(0, 0, Unit.Type.soldier)) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(emptyIndicator);
                } else if(currentTerritory.owner.placeableUnits == 1) { parentActivity.setCheckMark(false); }
                parentActivity.getGamePlayBanner().changeContent(parentActivity.getGameController().getGameState());
                update(currentTerritory);
            }
        });
    }

    private void populateMilitaryPanel() {
        if(currentTerritory.units.isEmpty()) {
            militaryPanel.addView(emptyIndicator);
        }
        else {
            for (Unit unit : currentTerritory.units) {
                ImageView soldierIcon = new ImageView(getContext());
                soldierIcon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                soldierIcon.setPadding(2,1,2,1);
                soldierIcon.setBackgroundColor(getResources().getColor(R.color.offWhite));
                soldierIcon.setImageResource(R.drawable.soldier);
                soldierIcon.setAdjustViewBounds(true);
                militaryPanel.addView(soldierIcon);
            }
        }
    }

    public void setMilitaryPanel(View v) {
        militaryPanel = (LinearLayout)v;
    }

    public void update(Territory territory) {
        if(militaryPanel==null){
            militaryPanel = (LinearLayout)findViewById(R.id.militaryPanel);
        }
        currentTerritory = territory;
        if(territory.owner != parentActivity.getGameController().getCurrentPlayer())
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
        // TODO: Optomize so I just add new views, rather than removing them all and readding
        militaryPanel.removeAllViews();
        populateMilitaryPanel();
    }

}
