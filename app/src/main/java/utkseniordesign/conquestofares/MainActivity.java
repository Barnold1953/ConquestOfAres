package utkseniordesign.conquestofares;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.internal.GamesClientImpl;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchBuffer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

public class MainActivity extends googleClientApiActivity {

    // main activity member variables
    Context mContext = this; // for reference in Listeners and the like

    // active matches to be displayed in "active matches" tab
    static TurnBasedMultiplayer.LoadMatchesResult mActiveMatches = null;

    // tabs [0] = main menu, [1] = active games, [2] = leaderboards
    static View mTabs[] = new View[3];

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        //Sets tabbed layout
        setContentView(R.layout.activity_main);

        //Create a view pager in the container specified by @id.pager
        ViewPager viewPager = ( ViewPager ) findViewById( R.id.pager );

        //Create an adapter to populate the view pager with fragments (tabs)
        viewPager.setAdapter( new SampleFragmentPagerAdapter() );

        //Frustratingly, you can't set the indicator color in xml without changing the font color
        //So I was forced to do it programatically
        PagerTabStrip pagerTabStrip = ( PagerTabStrip ) findViewById( R.id.pager_header );
        pagerTabStrip.setTabIndicatorColor( getResources().getColor( R.color.darkBlue) );

        //Populate active games screen.
        getActiveGames();

    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        //Set the tab count
        final int PAGE_COUNT = 3;
        public SampleFragmentPagerAdapter() {
            super( getSupportFragmentManager() );
        }

        @Override
        public int getCount()
        {
            return PAGE_COUNT;
        }

        //Get item creates a 1-indexed page fragment of a position
        @Override
        public Fragment getItem( int position )
        {
            return PageFragment.create( position + 1 );
        }

        //Returns title
        @Override
        public CharSequence getPageTitle( int position )
        {
            switch( position ) {
                case 0:
                    return "Main Menu";
                case 1:
                    return "Active Games";
                case 2:
                    return "Leaderboards";
                default:
                    return "Unexpected page!";
            }
        }
    }

    public static class PageFragment extends Fragment {
        public static final String ARG_PAGE = "ARG_PAGE";

        private int mPage;

        public static PageFragment create( int page )
        {
            //creates and returns page with appropriate page number
            Bundle args = new Bundle();
            args.putInt( ARG_PAGE, page );
            PageFragment fragment = new PageFragment();
            fragment.setArguments( args );
            return fragment;
        }

        @Override
        public void onCreate( Bundle savedInstanceState )
       {
            super.onCreate( savedInstanceState );

            //Everytime a page is created, use the arguments passed from create() to get a unique page #
            mPage = getArguments().getInt( ARG_PAGE );
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
        {
            // function for creating the view of each individual page fragment (tab)
            switch( mPage ) {
                case 1:
                    mTabs[0] = inflater.inflate(R.layout.activity_main_subtab_main, container, false);
                    return mTabs[0];
                case 2:
                    mTabs[1] = inflater.inflate(R.layout.activity_main_subtab_activegames, container, false);
                    return mTabs[1];
                case 3:
                    mTabs[2] = inflater.inflate(R.layout.activity_main_subtab_leaderboards, container, false);
                    return mTabs[2];
                default:
                    Log.d("Page fragment","Page doesn't exist");
                    return null;
            }
        }
    }

    public void addActiveMatches(ScrollView v) {
        TurnBasedMatchBuffer myTurnMatches = mActiveMatches.getMatches().getMyTurnMatches();
        LinearLayout list = (LinearLayout)v.findViewById(R.id.activeGames);
        int gamesCounter = 1;
        for(TurnBasedMatch match : myTurnMatches) {
            LayoutInflater.from(v.getContext()).inflate(
                    R.layout.activity_main_subtab_activegames_activegame,
                    list);
            if( match.getData() != null && match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN ) {
                final TurnBasedMatch matchToLoad = match;
                list.getChildAt(gamesCounter-1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent gameIntent = new Intent(mContext, GameActivity.class);
                        Games.TurnBasedMultiplayer.loadMatch(getClient(),matchToLoad.getMatchId())
                                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchResult>() {
                            @Override
                            public void onResult(TurnBasedMultiplayer.LoadMatchResult loadMatchResult) {
                                TurnBasedMatch loadedMatch = loadMatchResult.getMatch().freeze();
                                gameIntent.putExtra("Match", loadedMatch);
                                startActivity(gameIntent);
                            }
                        });
                    }
                });
                TextView matchNumber = (TextView) list.getChildAt(gamesCounter - 1).findViewById(R.id.matchNumber);
                matchNumber.setText(match.getParticipant(match.getCreatorId()).getDisplayName());
                TextView numberLabel = (TextView) list.getChildAt(gamesCounter - 1).findViewById(R.id.numberLabel);
                numberLabel.setText(Integer.toString(gamesCounter));
                gamesCounter++;
            }
        }
    }


    public void getActiveGames() {
        final LinearLayout activeGamesScreen = (LinearLayout) findViewById(R.id.activeGames);
        int statuses[] = {TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN, TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN};
        Games.TurnBasedMultiplayer.loadMatchesByStatus(getClient(),statuses).setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchesResult>() {
            @Override
            public void onResult(TurnBasedMultiplayer.LoadMatchesResult loadMatchesResult) {
                mActiveMatches = loadMatchesResult;
                addActiveMatches((ScrollView)mTabs[1]);
            }
        });
    }

    public void launchNewGame( View v ) {
        Intent intent = new Intent(this, LaunchGameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Clean up everything here
    }
}
