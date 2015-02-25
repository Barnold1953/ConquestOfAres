package UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import utkseniordesign.conquestofares.R;


public class HintedArrayAdapter extends ArrayAdapter<CharSequence> {
    int arrayResourceId;

    public HintedArrayAdapter( Context context, int resourceId ) {
        super( context, resourceId );
    }

    public HintedArrayAdapter( Context context, int resourceId, CharSequence [] strings ) {
        super( context, resourceId, strings );
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        if( convertView == null ) {
            convertView = LayoutInflater.from( getContext() ).inflate( R.layout.spinner_default, parent, false );
        }

        View v = super.getView( position, convertView, parent );
        return v;
    }

    @Override
    public View getDropDownView( int position, View convertView, ViewGroup parent )
    {
        View v = null;
        if( getItem( position ) == getItem( 0 ) ) {
            TextView tv = new TextView( getContext() );
            tv.setHeight( 0 );
            v = tv;
        }
        else {
            v = super.getDropDownView( position, null, parent );
        }

        return v;
    }

    public static HintedArrayAdapter createFromResource( Context context, int arrayResourceId, int layoutId )
    {
        CharSequence[] strings = context.getResources().getTextArray( arrayResourceId );
        return new HintedArrayAdapter(context, layoutId, strings);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}