package aluno.ifsc.app.focos.dengue.utilidades.google;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

public class GooglePlaces {

	// Google API Key
    private static String API_KEY = "AIzaSyAJI-kxntkms-doh0xlE4CwR2uJ4WXZmlA";

    public static void setApiKey(String aApiKey) {
        API_KEY = aApiKey;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    
    // Google Places serach url's
    private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    @SuppressWarnings("unused")
	private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
 
    private double _latitude;
    private double _longitude;
    private double _radius;

    public GooglePlaces(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            API_KEY =  getApiKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public PlacesList search(double latitude, double longitude, double radius, String name)
            throws Exception {

        this._latitude = latitude;
        this._longitude = longitude;
        this._radius = radius;

        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("location", _latitude + "," + _longitude);
            request.getUrl().put("radius", _radius); // in meters
            request.getUrl().put("sensor", "false");

            if(name != null)
                request.getUrl().put("name", name);

            PlacesList list = request.execute().parseAs(PlacesList.class);
            // Check log cat for places response status
            Log.d("Places Status", "" + list.status);
            return list;

        } catch (HttpResponseException e) {
            Log.e("Error:", e.getMessage());
            return null;
        }

    }

    public PlacesList search(double latitude, double longitude, double radius, String types, String keyword, String name)
            throws Exception {
 
        this._latitude = latitude;
        this._longitude = longitude;
        this._radius = radius;
 
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("location", _latitude + "," + _longitude);
            request.getUrl().put("radius", _radius); // in meters
            request.getUrl().put("sensor", "false");
            if(types != null)
                request.getUrl().put("type", types);
            if(keyword != null)
                request.getUrl().put("keyword", keyword);
            if(name !=  null)
                request.getUrl().put("name", name);

            PlacesList list = request.execute().parseAs(PlacesList.class);
            // Check log cat for places response status
            Log.d("Places Status", "" + list.status);
            return list;
 
        } catch (HttpResponseException e) {
            Log.e("Error:", e.getMessage());
            return null;
        }
 
    }
 
    public PlaceDetails getPlaceDetails(String reference) throws Exception {
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("reference", reference);
            request.getUrl().put("sensor", "false");
 
            PlaceDetails place = request.execute().parseAs(PlaceDetails.class);
             
            return place;
 
        } catch (HttpResponseException e) {
            throw e;
        }
    }

    public static HttpRequestFactory createRequestFactory(final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
            	HttpHeaders  headers = new HttpHeaders ();
                headers.setUserAgent("SMCore");
                request.setHeaders(headers);
                JsonObjectParser  parser = new JsonObjectParser (new JacksonFactory());
                request.setParser(parser);
            }
        });
    }

}
