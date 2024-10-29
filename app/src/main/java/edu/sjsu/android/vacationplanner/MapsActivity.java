package edu.sjsu.android.vacationplanner;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ScrollView;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;

import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.sjsu.android.vacationplanner.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

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
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
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
        mapAdapter = new MapAdapter(this, placeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mapAdapter);

        NestedScrollView nestedScrollView = findViewById(R.id.scroll_view);
        BottomSheetBehavior<NestedScrollView> bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(100);
        bottomSheetBehavior.setHideable(false);

        updateBottomSheet(new ArrayList<>());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));

    }
    private void updateBottomSheet(List<MyPlace> places) {
        placeList.clear();
        placeList.addAll(places);
        mapAdapter.notifyDataSetChanged();
    }
    // Get multiple search results from the input query. For each result, call searchResult to get and save place info
    private void searchPlaces(String query) {
        if (mMap != null) {
            mMap.clear();
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
}