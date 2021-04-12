package aluno.ifsc.app.focos.dengue.utilidades;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class GpsUtil implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int PERMISSION_ACCESS_LOCATION = 10;
    private static Location location;
    public static final int RESULT_GPS = 2;


    public static class MeuLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location aLocFromGps) {
            location = aLocFromGps;
        }

        @Override
        public void onProviderDisabled(String aProvider) {
            // called when the GPS provider is turned off (user turning off the
            // GPS on the phone)
        }

        @Override
        public void onProviderEnabled(String aProvider) {
            // called when the GPS provider is turned on (user turning on the
            // GPS on the phone)
        }

        @Override
        public void onStatusChanged(String aProvider, int aStatus, Bundle aExtras) {
            // called when the status of the GPS provider changes
        }
    }

    public synchronized static LatLng getLocationNovo(Activity aContext) {
        return getLocationNovo(aContext,null);
    }

    public synchronized static LatLng getLocationNovo(Activity aContext, LocationListener aMeuLocationListener) {
        if (ActivityCompat.checkSelfPermission(aContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(aContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(aContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_LOCATION);
            return null;
        }
        LocationManager locationManager = (LocationManager) aContext.getSystemService(Context.LOCATION_SERVICE);

        Location l = null;
        String gps = LocationManager.GPS_PROVIDER;
        String rede = LocationManager.NETWORK_PROVIDER;
        String passivo = LocationManager.PASSIVE_PROVIDER;
        LocationListener mm;
        if(aMeuLocationListener == null) {
            mm = new MeuLocationListener();
        }else{
            mm = aMeuLocationListener;
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        if (locationManager != null) {
            String provider = locationManager.getBestProvider(criteria, true);

            // locationManager.requestLocationUpdates(gps, 100, 5, mm);
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 100, 5, mm);
                location = locationManager.getLastKnownLocation(provider);
            }
            if (location == null && locationManager.getAllProviders().contains(rede)) {
                locationManager.requestLocationUpdates(rede, 100, 5, mm);
                location = locationManager.getLastKnownLocation(rede);
            }
            if (location == null && locationManager.getAllProviders().contains(passivo)) {
                locationManager.requestLocationUpdates(passivo, 100, 5, mm);
                location = locationManager.getLastKnownLocation(passivo);
            }
        }

        if (location != null) {
            return new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            return null;
        }
    }

    public LatLng getLocation(Context aContext) {
        if (ActivityCompat.checkSelfPermission(aContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(aContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        GoogleApiClient mGoogleApiClient = null;
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(aContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                return new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        }
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle aBundle) {
    }

    @Override
    public void onConnectionSuspended(int aValue) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult aConnectionResult) {

    }

    private static GoogleApiClient googleApiClient;

    final static int REQUEST_LOCATION = 199;

    // check whether gps is enabled
    public static boolean ativarGps(Activity aActivity) {
        final LocationManager manager = (LocationManager) aActivity.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //  buildAlertMessageNoGps();
            enableLoc(aActivity);
            return true;
        }
        return false;
//        String provider = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//
//        if(!provider.contains("gps")){ //if gps is disabled
//            final Intent poke = new Intent();
//            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//            poke.setData(Uri.parse("3"));
//            activity.sendBroadcast(poke);
//        }
    }

    public static Boolean checkGpsStatus(Activity aActivity, int aRequestCode, boolean open){
        LocationManager locationManager = (LocationManager) aActivity.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gpsStatus) {
            Toast.makeText(aActivity, "GPS não está ativado, favor verificar.", Toast.LENGTH_LONG).show();
            if(open) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                aActivity.startActivityForResult(intent, aRequestCode);
            }
        }
        return gpsStatus;
    }

    private static void enableLoc(final Activity aActivity) {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(aActivity)
                    .addApi(LocationServices.API)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult aConnectionResult) {
                            Log.v("GPS", "Location error " + aConnectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1 * 1000);
            locationRequest.setFastestInterval(1 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult aResult) {
                    final Status status = aResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(aActivity, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException aException) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    public static void showDialogActiveGPS(final Activity activity) {

        View.OnClickListener yesListener = new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(intent, RESULT_GPS);
            }
        };

//        SmDialog.instantiate(activity)
//                .withTitulo(activity.getResources().getString(R.string.gps_inativo))
//                .withMensagem(activity.getString(R.string.dialog_msg_ative_gps_para_acessar))
//                .withYesListener(yesListener)
//                .withDialogMode(SmDialog.DIALOG_MODE_YES_NO)
//                .show();
    }

    public static long getDistanceLatLong(LatLng origem, LatLng destino) {
        double latOrigem = origem.latitude;
        double longOrigem = origem.longitude;
        double latDestino = destino.latitude;
        double longDestino = destino.longitude;
        double distancia = 6371 * Math.acos(Math.cos(Math.PI * (90 - latOrigem) / 180) * Math.cos((90 - latDestino) *
                Math.PI / 180) + Math.sin((90 - latOrigem) * Math.PI / 180) * Math.sin((90 - latDestino) * Math.PI / 180)
                * Math.cos((longDestino - longOrigem) * Math.PI / 180));

        BigDecimal bigDecimal = new BigDecimal(distancia);
        if (bigDecimal.precision() > 3) {
            bigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_UP);
        }

        return bigDecimal.longValue();
    }

    public static long getDistanceLatLonginMeters(LatLng origem, LatLng destino) {
        double latOrigem = origem.latitude;
        double longOrigem = origem.longitude;
        double latDestino = destino.latitude;
        double longDestino = destino.longitude;
        double distancia = 6371 * Math.acos(Math.cos(Math.PI * (90 - latOrigem) / 180) * Math.cos((90 - latDestino) *
                Math.PI / 180) + Math.sin((90 - latOrigem) * Math.PI / 180) * Math.sin((90 - latDestino) * Math.PI / 180)
                * Math.cos((longDestino - longOrigem) * Math.PI / 180));

        BigDecimal bigDecimal = new BigDecimal(distancia);
        if (bigDecimal.precision() > 3) {
            bigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_UP);
        }
        bigDecimal = bigDecimal.multiply(new BigDecimal(1000));
        return bigDecimal.longValue();
    }

    public static String getStateFromLocation(Context context, Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        if (Geocoder.isPresent()){
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String address = addresses.get(0).getAdminArea();

                if (address.isEmpty())
                    return address;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }


    public static String getEnderecoFormatado(Context context, Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        if (Geocoder.isPresent()){
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String address = addresses.get(0).getAddressLine(0);

                if (address.isEmpty())
                    return address;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public static String getCidadeFormatada(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        if (Geocoder.isPresent()){
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String cidade = addresses.get(0).getLocality();
                if (cidade.isEmpty()) {
                    return cidade;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public static Address getEnderecoCompleto(Context aContext, Location aLocation) {
        try {
            Geocoder lGeocoder = new Geocoder(aContext, Locale.getDefault());
            if (Geocoder.isPresent()) {
                List<Address> lAddressList = lGeocoder.getFromLocation(aLocation.getLatitude(), aLocation.getLongitude(), 1);
                if (lAddressList != null && lAddressList.size() > 0) {
                    return lAddressList.get(0);
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double calcDistanciaEntreLatLng(double latitudeOrigem, double longitudeOrigem, double latitudeDestino, double longitudeDestino) {
        double raioTerra = 6371000; //metros
        double latitudeDelta = Math.toRadians(latitudeDestino - latitudeOrigem);
        double longitudeDelta = Math.toRadians(longitudeDestino - longitudeOrigem);
        double anguloDelta = Math.sin(latitudeDelta / 2) * Math.sin(latitudeDelta / 2)
                + Math.cos(Math.toRadians(latitudeOrigem)) * Math.cos(Math.toRadians(latitudeDestino))
                * Math.sin(longitudeDelta / 2) * Math.sin(longitudeDelta / 2);
        return (float) (raioTerra * (2 * Math.atan2(Math.sqrt(anguloDelta), Math.sqrt(1 - anguloDelta))));
    }
}