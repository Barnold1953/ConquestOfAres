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

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        if( convertView == null ) {
            convertView = LayoutInflater.from( getContext() ).inflate( R.layout.spinner_default, parent, false );
        }

        View v = super.getView( position, convertView, parent );

        return v;
    }

    public static HintedArrayAdapter createFromResource( Context context, int arrayResourceId, int layoutId )
    {
        //create new adapter
        HintedArrayAdapter adapterFromResource = new HintedArrayAdapter( context, arrayResourceId );

        //set the layout
        adapterFromResource.setDropDownViewResource( layoutId );

        //populate it
        String [] strings = context.getResources().getStringArray( arrayResourceId );
        for( String string : strings ) {
            adapterFromResource.add( string );
        }

        //return the instance
        return adapterFromResource;
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }
}