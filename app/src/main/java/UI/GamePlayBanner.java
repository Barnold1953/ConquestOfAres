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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

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
    Animator.AnimatorListener animationListener = null;
    Boolean isVisible = false;
    public GamePlayBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setUpPanel(Activity gameScreen) {
        parentActivity = (GameActivity) gameScreen;
        counter = (ImageView) findViewById(R.id.counter);
        counterLabel = (TextView) findViewById(R.id.counterLabel);
        background = (ImageView) findViewById(R.id.backgroundImage);
        changeContent();
        setListeners();
        refresh();
    }

    private void setListeners() {
        animationListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // if the banner is out of the window, refresh content
                if(isVisible) {
                    isVisible = false;
                } else {
                    refresh(); // slide back in as soon as content is refreshed
                    isVisible = true;
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // animation is not user instigated, will never cancel
            }

            @Override
            public void onAnimationRepeat(Animator animator) {}
        };
    }

    public void refresh() {
        if(isVisible) YoYo.with(Techniques.SlideOutUp).duration(500).withListener(animationListener).playOn(this);
        else {
            changeContent();
            YoYo.with(Techniques.SlideInUp).duration(500).playOn(this);
        }
    }

    public void changeContent() {
        switch(parentActivity.getGameController().getGameState().currentState) {
            case PLACING_UNITS:
                // get positioning information
                background.setImageResource(R.drawable.game_start_banner);
                counterLabel.setText(Integer.toString(parentActivity.getGameController().getCurrentPlayer().placeableUnits));
                break;
            case PLAYING:
                background.setImageResource(R.drawable.attack_banner);
                counter.setVisibility(GONE);
                counterLabel.setVisibility(GONE);
            default:
        }
    }
}
