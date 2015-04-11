package UI;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import Game.GameState;
import Utils.Utils;
import utkseniordesign.conquestofares.R;

public class UnitIcon extends ImageView {
    int soldierId = -1;
    Boolean selected = false;
    public UnitIcon(Context context, AttributeSet attrs) {super(context, attrs);}
    public UnitIcon(Context context, final GameState state, int id) {
        super(context);

        // do layout stuff
        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
        iconParams.setMargins(0,Utils.convertToDP(15),0,Utils.convertToDP(15));
        setLayoutParams(iconParams);
        setPadding(Utils.convertToDP(5),Utils.convertToDP(5),Utils.convertToDP(5),Utils.convertToDP(5));
        setAdjustViewBounds(true);
        setBackgroundColor(getResources().getColor(R.color.lightGrey));
        setImageResource(R.drawable.soldier);

        // do game logic
        soldierId = id;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean attaking = state.currentState == GameState.State.ATTACKING ? true : false;
                if(state.currentState == GameState.State.ATTACKING || state.currentState == GameState.State.FORTIFYING ) {
                    if(!selected) {
                        if(state.selectedTerritory.selectedUnits.size() == 0) {
                            state.selectedTerritory.unselect();
                            state.selectedTerritory.selectNeighbors(attaking);
                        }
                        setBackgroundColor(getResources().getColor(R.color.darkBlue));
                        state.selectedTerritory.selectedUnits.add(state.selectedTerritory.units.get(soldierId));
                        selected = true;
                    } else {
                        setBackgroundColor(getResources().getColor(R.color.lightGrey));
                        state.selectedTerritory.selectedUnits.remove(state.selectedTerritory.units.get(soldierId));
                        if(state.selectedTerritory.selectedUnits.size() == 0) {
                            state.selectedTerritory.unselectNeighbors();
                            state.selectedTerritory.select();
                        }
                        selected = false;
                    }
                }
            }
        });
    }
}