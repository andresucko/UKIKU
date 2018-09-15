package knf.kuma.commons

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import knf.kuma.R
import kotlinx.android.synthetic.main.lay_map.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class EAMActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lay_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val point = LatLng(35.702067, 139.774528)
        marker = mMap!!.addMarker(MarkerOptions().position(point).title("Easter Egg completado!").visible(false).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVD(this, R.drawable.ic_treasure))))
        googleMap.setOnCameraMoveListener { marker!!.isVisible = mMap!!.cameraPosition.zoom >= 13 }
        googleMap.setOnMarkerClickListener { m ->
            if (m == marker) {
                EAHelper.enter3()
                konfetti.build()
                        .addColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        .setDirection(0.0, 359.0)
                        .setSpeed(4f, 7f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(Size(12, 6f), Size(16, 6f))
                        .setPosition(-50f, konfetti.width + 50f, -50f, -50f)
                        .streamFor(300, 10000L)
            }
            false
        }
    }

    private fun getBitmapFromVD(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    companion object {

        fun start(context: Activity) {
            context.startActivityForResult(Intent(context, EAMActivity::class.java), 5698)
        }
    }
}
