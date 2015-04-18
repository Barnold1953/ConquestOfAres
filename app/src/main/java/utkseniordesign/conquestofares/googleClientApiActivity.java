package utkseniordesign.conquestofares;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

/**
 * Created by lasth_000 on 4/12/2015.
 */
public class googleClientApiActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    GoogleApiClient googleApiClient = null;
    Boolean resolvingError = false;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final int REQUEST_MISCONFIGURED = 10004;
    private static final String DIALOG_ERROR = "dialog_error";

    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    public GoogleApiClient getClient() {
        return googleApiClient;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, resolvingError);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();
        resolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
    }

    public void setupApiClient(Activity activity) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!resolvingError
                && !googleApiClient.isConnected()
                && !googleApiClient.isConnecting()) {
            Log.d("Play Services","connecting");
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if(googleApiClient.isConnected()) {
            Log.d("Play Services", "disconnecting");
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("Play Services","connected!");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("Play Services","connection suspended!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("Play Services","connected failed!");
        if(resolvingError) {
            return;
        } else if(result.hasResolution()) {
            Log.d("Play Services","Resolving...");
            try {
                resolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                Log.d("Play Services","Trying again...");
                googleApiClient.connect();
            }
        } else{
            Log.d("Play Services","No resolution possible!");
            showErrorDialog(result.getErrorCode());
            resolvingError = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_RESOLVE_ERROR) {
            resolvingError = false;
            if(resultCode == RESULT_OK) {
                if(!googleApiClient.isConnecting()
                        && !googleApiClient.isConnected() ) {
                    Log.d("Play Services","Trying again...");
                    googleApiClient.connect();
                }
            } else {
                Log.d("Play Services","App Misconfigured!");
            }
        }
    }

    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR,errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(),"errordialog");
    }

    public void onDialogDismissed() {
        resolvingError = false;
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((googleClientApiActivity)getActivity()).onDialogDismissed();
        }
    }
}
