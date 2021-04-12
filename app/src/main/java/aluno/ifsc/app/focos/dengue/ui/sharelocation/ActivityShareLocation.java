package aluno.ifsc.app.focos.dengue.ui.sharelocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import aluno.ifsc.app.focos.dengue.R;
import aluno.ifsc.app.focos.dengue.model.state.State;
import aluno.ifsc.app.focos.dengue.resources.utilidades.ConnectionUtil;
import aluno.ifsc.app.focos.dengue.resources.utilidades.baseview.BaseActivty;
import aluno.ifsc.app.focos.dengue.utilidades.GpsUtil;

/**
 * Created by wilson.junior on 03/04/2021.
 * 11:43
 */
public class ActivityShareLocation extends BaseActivty implements
        OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private static final int PERMISSION_ACCESS_LOCATION = 1010;

    private GoogleMap mMap;
    private LatLng mUserLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    private LinearLayout mLinearLayoutSelect;
    private TextView mTextViewEndereco;
    private ExpandableLayout mViewMoveMarker;
    private CardView mCardViewMarker;
    private ProgressBar mProgreesBarMarker;
    private ProgressBar mProgreesBarFooter;
    private TextView mTextViewEnderecoMarker;

    private LatLng mLocationSelected;
    private List<Address> addresses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        setDisplayHomeAs(true);
        setTitleToolbar(getString(R.string.title_toolbar_my_location));
        MapFragment lMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.ll_map, lMapFragment);
        fragmentTransaction.commit();

        lMapFragment.getMapAsync(this);
    }


    @Override
    public void mapComponents() {
        super.mapComponents();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Intent data = getIntent();
        Double lLat = data.getDoubleExtra("latitude", 0L);
        Double lLng = data.getDoubleExtra("longitude", 0L);

        if (lLat != 0 && lLng != 0){
            mLocationSelected = new LatLng(lLat,lLng);
        }

        mLinearLayoutSelect = findViewById(R.id.ll_location_selected);
        mTextViewEndereco = findViewById(R.id.textView_endereco);
        mViewMoveMarker = findViewById(R.id.view_move_marker);

        mCardViewMarker = findViewById(R.id.cardView_marker);
        mProgreesBarMarker = findViewById(R.id.progress_bar_marker);
        mProgreesBarFooter = findViewById(R.id.progress_bar_endereco);
        mTextViewEnderecoMarker = findViewById(R.id.textView_endereco_marker);

    }

    public State getJsonArray(String aAdminArea) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<State>>(){}.getType();
        List<State> contactList = gson.fromJson(loadJSONFromAsset(getApplicationContext(), "estados.json"), type);

        for(State  state : contactList) {
            if(state.getName().equalsIgnoreCase(aAdminArea)) {
                return state;
            }
        }
        return null;
    }



    @Override
    public void mapActionComponents() {
        super.mapActionComponents();

        mLinearLayoutSelect.setOnClickListener(v -> {
            if (mLocationSelected != null) {
                createIntentFinish();
            }
        });

        mCardViewMarker.setOnClickListener(v -> {
            if (mLocationSelected != null) {
                createIntentFinish();
            }
        });
    }

    private void createIntentFinish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("endereco", mTextViewEndereco.getText());
        returnIntent.putExtra("latitude", mLocationSelected.latitude);
        returnIntent.putExtra("longitude", mLocationSelected.longitude);
        if(addresses != null) {
            Address address = addresses.get(0);
            State state = getJsonArray(address.getAdminArea());
            if (state != null) {
                returnIntent.putExtra("cidade", address.getSubAdminArea() + ", " + state.getInitials());
            } else {
                returnIntent.putExtra("cidade", address.getSubAdminArea() + ", " + address.getAdminArea());
            }
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap pGoogleMap) {
        mMap = pGoogleMap;
        if (pGoogleMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_ACCESS_LOCATION);
                return;
            } else {
                mMap.setMyLocationEnabled(true);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            }
                        });
            }

            if (mUserLocation == null && !hasPosicaoAtual()) {
                showErrorMessage(getResources().getString(R.string.toast_gps_nao_ativado));
            } else {
                if (mLocationSelected == null){
                    mLocationSelected = mUserLocation;
                }

                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(mLocationSelected.latitude, mLocationSelected.longitude), 10));
            }

            mMap.setOnCameraMoveListener(() -> animateMarker(true));
            mMap.setOnCameraMoveCanceledListener(() -> {});
            mMap.setOnCameraIdleListener(() -> {
                animateMarker(false);
                mLocationSelected = mMap.getCameraPosition().target;
                new ReverseGeocodingTask(this).execute(mLocationSelected.latitude, mLocationSelected.longitude);
            });

            mMap.setOnCameraMoveStartedListener(reason -> {
                switch (reason) {
                    case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:
                        animateMarker(true);
                        break;
                    case GoogleMap.OnCameraMoveStartedListener
                            .REASON_API_ANIMATION:
                        break;
                    case GoogleMap.OnCameraMoveStartedListener
                            .REASON_DEVELOPER_ANIMATION:
                        animateMarker(true);
                        break;
                }
            });

            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    private void animateMarker(boolean pShow) {
       if (pShow){
           mViewMoveMarker.expand();
           mCardViewMarker.setVisibility(View.GONE);
       } else {
           mViewMoveMarker.collapse();
           mCardViewMarker.setVisibility(View.VISIBLE);
       }
    }

    public void animateMarkerMap(final Marker marker, final LatLng toPosition,
                                 final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(mMap);
                } else {
                    finish();
                }
                break;
        }
    }

    public boolean hasPosicaoAtual() {
        if (mMap != null && !mMap.isMyLocationEnabled()) {
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        mUserLocation = GpsUtil.getLocationNovo(this);
        return (mUserLocation != null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapLoaded() {
        mLocationSelected = mMap.getCameraPosition().target;
    }

    @SuppressLint("StaticFieldLeak")
    private class ReverseGeocodingTask extends AsyncTask<Double, Void, String> {
        Context mContext;

        ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgreesBarMarker.setVisibility(View.VISIBLE);
            mProgreesBarFooter.setVisibility(View.VISIBLE);
            mLinearLayoutSelect.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Double... params) {
            Geocoder geocoder = new Geocoder(mContext);
            double latitude = params[0];
            double longitude = params[1];
            String addressText = "";

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                addressText = address.getAddressLine(0);
            }

            mLocationSelected = new LatLng(latitude,longitude);
            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            mProgreesBarMarker.setVisibility(View.GONE);
            mProgreesBarFooter.setVisibility(View.GONE);
            mLinearLayoutSelect.setVisibility(View.VISIBLE);

            if (!addressText.isEmpty()){
                mTextViewEndereco.setText(addressText);
                mTextViewEnderecoMarker.setText(addressText);
            } else if (!ConnectionUtil.INSTANCE.isNetworkAvailable(ActivityShareLocation.this)) {
                mTextViewEndereco.setText(R.string.sem_conexão_sem_endereco);
                mTextViewEnderecoMarker.setText(R.string.sem_conexão_sem_endereco);
            } else {
                mTextViewEndereco.setText(R.string.endereco_desconhecido);
                mTextViewEnderecoMarker.setText(R.string.endereco_desconhecido);
            }
        }
    }
}
