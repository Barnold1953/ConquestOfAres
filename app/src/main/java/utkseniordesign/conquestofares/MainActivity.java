package utkseniordesign.conquestofares;

import android.content.Intent;
import android.support.v4.view.PagerTabStrip;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        //Sets tabbed layout
        setContentView(R.layout.activity_main);

        //Action bar is unnecessary for our game, and it looks weird. Hide it.
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Create a view pager in the container specified by @id.pager
        ViewPager viewPager = ( ViewPager ) findViewById( R.id.pager );

        //Create an adapter to populate the view pager with fragments (tabs)
        viewPager.setAdapter( new SampleFragmentPagerAdapter() );

        //Frustratingly, you can't set the indicator color in xml without changing the font color
        //So I was forced to do it programatically
        PagerTabStrip pagerTabStrip = ( PagerTabStrip ) findViewById( R.id.pager_header );
        pagerTabStrip.setTabIndicatorColor( getResources().getColor( R.color.darkBlue) );
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        //Set the tab count
        final int PAGE_COUNT = 3;
        public SampleFragmentPagerAdapter() {
            super( getSupportFragmentManager() );
        }

        //Override super class functions appropriately
        @Override
        public int getCount()
        {
            return PAGE_COUNT;
        }

        //Get item creates a 1-indexed page fragment off a position
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
            //function for creating the view of each individual page fragment (tab)
            View view;
            switch( mPage ) {
                case 1:
                    view = inflater.inflate(R.layout.activity_main_subtab_main, container, false);
                    break;
                case 2:
                    view = inflater.inflate(R.layout.activity_main_subtab_activegames, container, false);
                    break;
                case 3:
                    view = inflater.inflate(R.layout.activity_main_subtab_leaderboards, container, false);
                    break;
                default:
                    view = null;
            }

            //make sure it isn't null before returning
            try
            {
                if(view == null) {
                    throw new NullPointerException("Creating an illegal view!\n");
                }
            }
            catch( NullPointerException e)
            {
                Log.e("Error",e.toString());
            }
            return view;
        }
    }

    public void launchNewGame( View v ) {
        Intent intent = new Intent( this, LaunchGameActivity.class );
        startActivity( intent );
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
