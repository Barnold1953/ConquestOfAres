package UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import Game.Army;
import Game.Territory;
import Game.Unit;
import utkseniordesign.conquestofares.R;

/**
 * Created by lasth_000 on 3/25/2015.
 */
public class TerritoryPanel extends LinearLayout {
    Territory currentTerritory;
    LinearLayout militaryPanel = null;
    LinearLayout attributesPanel = null;

    public TerritoryPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TerritoryPanel(Context context, Territory territory) {
        super(context);

        // set up the layout
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,200);
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.setMargins(10,0,10,10);
        setLayoutParams(layoutParams);

        // deal with members
        currentTerritory = territory;
        militaryPanel = createMilitaryPanel();
        attributesPanel = createAttributesPanel();
        setBackgroundColor(getResources().getColor(R.color.lightGrey));
        addView(militaryPanel);
    }

    private LinearLayout createMilitaryPanel() {
        LinearLayout panel = new LinearLayout(getContext());
        panel.setBackgroundColor(getResources().getColor(R.color.offWhite));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        panel.setLayoutParams(layoutParams);
        panel.setPadding(20,20,20,20);
        if(currentTerritory.armies.isEmpty()) {
            TextView territoryEmptyIndicator = new TextView(getContext());
            territoryEmptyIndicator.setText("No Units Available");
            territoryEmptyIndicator.setTextSize(24);
            territoryEmptyIndicator.setTextColor(getResources().getColor(R.color.darkGrey));
            territoryEmptyIndicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            territoryEmptyIndicator.setGravity(Gravity.CENTER);
            panel.addView(territoryEmptyIndicator);
        }
        else {
            for (Army army : currentTerritory.armies) {
                for (Unit unit : army.units) {
                    ImageView soldierIcon = new ImageView(getContext());
                    soldierIcon.setBackgroundColor(getResources().getColor(R.color.offWhite));
                    soldierIcon.setImageResource(R.drawable.soldier);
                    panel.addView(soldierIcon);
                }
            }
        }
        return panel;
    }

    private LinearLayout createAttributesPanel() {
        LinearLayout panel = new LinearLayout(getContext());
        return panel;
    }

    public void update(Territory territory) {
        this.removeAllViews();
        currentTerritory = territory;
        militaryPanel = createMilitaryPanel();
        addView(militaryPanel);
    }

}
