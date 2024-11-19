package edu.sjsu.android.vacationplanner;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;

import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.sjsu.android.vacationplanner.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor>, GPSTracker.LocationListener {
    @Nullable
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private PlacesClient placesClient;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placeRating;
    private TextView placeBusinessHour;
    private ImageView placeImage;
    private boolean isSaved = false;
    private ArrayList<MyPlace> placeList;
    private MapAdapter mapAdapter;
    private RecyclerView recyclerView;
    private FusedLocationProviderClient fusedLocationClient;
    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;

    private final Uri CONTENT_URI = Uri.parse("content://edu.sjsu.android.vacationplanner");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.FloatingHomeButton.setOnClickListener(this::getLocation);

        String apiKey = BuildConfig.MAPS_API_KEY;
        if (TextUtils.isEmpty(apiKey) || apiKey.equals("DEFAULT_API_KEY")) {
            Log.e("Places test", "No API key");
            finish();
            return;
        }
        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.BUSINESS_STATUS));

        // Set up a PlaceSelectionListener to handle the response
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            // Search and move to place
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    Log.i(TAG, "LatLng: " + latLng.toString());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                }

                searchPlaces(place.getName());
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        placeName = findViewById(R.id.place_name);
        placeAddress = findViewById(R.id.place_address);
        placeRating = findViewById(R.id.place_rating);
        placeBusinessHour = findViewById(R.id.place_business_hour);
        placeImage = findViewById(R.id.place_image);


        // add sample data to bottom sheet
        // placeList.add(new MyPlace("SJSU", "California", "5.0", "9-5", false, R.drawable.default_place_image));
        recyclerView = findViewById(R.id.recycler_view);
        placeList = new ArrayList<>();
        mapAdapter = new MapAdapter(this, placeList, false, new UpdateSavesListener() {
            @Override
            public void updateTotalCost() {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mapAdapter);

        NestedScrollView nestedScrollView = findViewById(R.id.scroll_view);
        BottomSheetBehavior<NestedScrollView> bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(100);
        bottomSheetBehavior.setHideable(false);

        updateBottomSheet(new ArrayList<>());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // placesClient = Places.createClient(this);

        findViewById(R.id.Restaurants_button).setOnClickListener(v -> searchNearbyPlaces("restaurant", LOCATION_UNIV));
        findViewById(R.id.Hotels_button).setOnClickListener(v -> searchNearbyPlaces("hotel", LOCATION_UNIV));
        findViewById(R.id.ThingsToDo_button).setOnClickListener(v -> searchNearbyPlaces("tourist_attraction", LOCATION_UNIV));
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                } else {
                    Log.e(TAG, "Current location is null");
                    Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this::addLocation);
        mMap.setOnMapLongClickListener(p -> deleteAllLocations());
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);
        getLastLocation();

        // LatLng sydney = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        // mMap.addMarker(new MarkerOptions().position(sydney).title("my location"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private void addLocation(LatLng point) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(point));
            ContentValues value = new ContentValues();
            value.put("latitude", point.latitude);
            value.put("longitude", point.longitude);
            value.put("zoom_level", mMap.getCameraPosition().zoom);
            new MyTask().execute(value);
        } else {
            Log.e(TAG, "GoogleMap object is null");
        }
    }

    private void deleteAllLocations() {
        if (mMap != null) {
            mMap.clear();
            new MyTask().execute();
        } else {
            Log.e(TAG, "GoogleMap object is null");
        }
    }

    public void getLocation(View view) {
        GPSTracker tracker = new GPSTracker(this, this);
        tracker.getLocation();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 10f));


    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                    } else {
                        Log.e(TAG, "Current location is null");
                    }
                });
    }

    private void updateBottomSheet(List<MyPlace> places) {
        placeList.clear();
        placeList.addAll(places);
        mapAdapter.notifyDataSetChanged();
    }

    // Get multiple search results from the input query. For each result, call searchResult to get and save place info
    private void searchPlaces(String query) {
        if (placesClient == null) {
            Log.e(TAG, "PlacesClient is null");
            return;
        }

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            List<MyPlace> searchResults = new ArrayList<>();
            updateBottomSheet(searchResults);

            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                String placeId = prediction.getPlaceId();
                searchPlace(placeId, searchResults);
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Autocomplete predictions not found: " + exception.getMessage());
            }
        });
    }


    private void searchPlace(String placeId, List<MyPlace> searchResults) {
        if (placesClient == null) {
            Log.e(TAG, "PlacesClient is null");
            return;
        }
        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.RATING, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS);

        // Construct a request object, passing the place ID and fields array
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            String name = place.getName();
            String address = place.getAddress();
            String rating = place.getRating() != null ? place.getRating().toString() : "N/A";
            String businessHour = place.getOpeningHours() != null ? place.getOpeningHours().getWeekdayText().toString() : "N/A";

            Log.i(TAG, "Place found: " + name);

            Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_place_image);
            MyPlace myPlace = new MyPlace(name, address, rating, businessHour, false, defaultImage);

            // Get the photo metadata
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata != null && !metadata.isEmpty()) {
                Log.w(TAG, "photo metadata found");

                final PhotoMetadata photoMetadata = metadata.get(0);
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500)
                        .setMaxHeight(300)
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    myPlace.setImage(bitmap);

                    mapAdapter.notifyDataSetChanged();
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getMessage());
                        final int statusCode = apiException.getStatusCode();
                        Log.e(TAG, "Status code: " + statusCode);
                    } else {
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    }
                });
            }
            searchResults.add(myPlace);
            updateBottomSheet(searchResults);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            LatLng point;
            float zoom;

            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
            zoom = cursor.getFloat(cursor.getColumnIndexOrThrow("zoom_level"));

            point = new LatLng(lat, lng);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(point, zoom);
            mMap.animateCamera(update);

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onLocationReceived(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        searchNearbyPlaces("tourist_attraction", currentLocation);

    }

    public void searchNearbyPlaces(String query, LatLng location) {
        deleteAllLocations();
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING);
        CircularBounds circle = CircularBounds.newInstance(location, 1000);
        final List<String> includedTypes = Arrays.asList(query);

        final SearchNearbyRequest searchNearbyRequest = SearchNearbyRequest.builder(circle, placeFields)
                .setIncludedTypes(includedTypes)
                .setMaxResultCount(10)
                .build();

        // Call placesClient.searchNearby() to perform the search.
        placesClient.searchNearby(searchNearbyRequest)
        .addOnSuccessListener(response -> {
            List<Place> places = response.getPlaces();
            List<MyPlace> searchResults = new ArrayList<>();
            for (Place place : places) {
                String name = place.getName();
                String address = place.getAddress();
                String rating = place.getRating() != null ? place.getRating().toString() : "N/A";
                LatLng latLng = place.getLatLng();
                MyPlace myPlace = new MyPlace(name, address, rating, null, false, null);
                searchResults.add(myPlace);
                if (latLng != null) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                }
            }
            updateBottomSheet(searchResults);
        })
        .addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "searchNearby failed: " + exception.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class MyTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... values) {
            try {
                // if there is a ContentValues object passed in, we insert.
                if (values != null && values.length > 0 && values[0] != null) {
                    getContentResolver().insert(CONTENT_URI, values[0]);
                }
                // else, delete
                else {
                    getContentResolver().delete(CONTENT_URI, null, null);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in doInBackground: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

}