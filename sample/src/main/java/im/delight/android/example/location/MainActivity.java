package im.delight.android.example.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends Activity {

    private SimpleLocation mLocation;
	private TextView miPosicionActual;
	private TextView distancia1;
	private TextView distancia2;

	// Access Location Permissions
	private static final int REQUEST_ACCESS_LOCATION = 1;

	private static String[] PERMISSIONS_ACCESS_LOCATION = {
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION
	};

	//La vienesa
	final double startLatitude = -25.301083;
	final double startLongitude = -57.589614;

	//La negrita
	final double startLatitudeNegrita = -25.284793;
	final double startLongitudeNegrita = -57.642014;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);



		miPosicionActual = (TextView) findViewById(R.id.mi_posicion);
		distancia1 = (TextView) findViewById(R.id.distancia1);
		distancia2 = (TextView) findViewById(R.id.distancia2);

		// construct a new instance
		mLocation = new SimpleLocation(this);

		// reduce the precision to 5,000m for privacy reasons
		//mLocation.setBlurRadius(5000);

		// if we can't access the location yet
		if (!mLocation.hasLocationEnabled()) {
			// ask the user to enable location access
			SimpleLocation.openSettings(this);
		}

		verifyAccessLocationPermissions(this);

	}

	/**
	 * Checks if the app has permission to access location
	 *
	 * If the app does not has permission then the user will be prompted to grant permissions
	 *
	 * @param activity
	 */
	private void verifyAccessLocationPermissions(Activity activity) {
		// Check if we have location permission
		if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED &&
				ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
						!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.ACCESS_FINE_LOCATION) ||
					ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.ACCESS_COARSE_LOCATION)) {

				Toast.makeText(MainActivity.this, "Necesitamos acceso al GPS para tener una posicion mas precisa", Toast.LENGTH_SHORT).show();

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(activity,
						PERMISSIONS_ACCESS_LOCATION,
						REQUEST_ACCESS_LOCATION);

			}


		} else {
			obtenerPosicionActual();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case REQUEST_ACCESS_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
					obtenerPosicionActual();

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.

					Toast.makeText(this, "No podemos obtener su localización sin tener permiso", Toast.LENGTH_SHORT).show();
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	private void obtenerPosicionActual() {

		findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final double latitude = mLocation.getLatitude();
				final double longitude = mLocation.getLongitude();

				final double vienesa = mLocation.calculateDistance(startLatitude, startLongitude, latitude, longitude);
				final double negrita = mLocation.calculateDistance(startLatitudeNegrita, startLongitudeNegrita, latitude, longitude);

				Toast.makeText(MainActivity.this, "Latitude: "+latitude, Toast.LENGTH_SHORT).show();
				Toast.makeText(MainActivity.this, "Longitude: "+longitude, Toast.LENGTH_SHORT).show();

				miPosicionActual.setText("Lat: " + latitude + " Long: " + longitude);
				distancia1.setText("La distancia de donde estoy a la Vienesa es: " + vienesa + " metros");
				distancia2.setText("La distancia de donde estoy a la negrita es: " + negrita + " metros");
			}

		});
	}

	@Override
    protected void onResume() {
        super.onResume();

        // make the device update its location
        mLocation.beginUpdates();
    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        mLocation.endUpdates();

        super.onPause();
    }

}
