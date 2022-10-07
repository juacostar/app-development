package com.example.challenge8

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val map = findViewById<MapView>(R.id.map)
        val context =  this.applicationContext

        // Request Location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED) {
            println("Location Permission GRANTED")
        } else {
            println("Location Permission DENIED")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        //Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        map.setUseDataConnection(true)

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(true)
        val mapController: IMapController = map.controller

        mapController.setZoom(14)
        val mGpsMyLocationProvider = GpsMyLocationProvider(this)
        val mLocationOverlay = MyLocationNewOverlay(mGpsMyLocationProvider, map)
        mLocationOverlay.enableMyLocation()
        map.overlays.add(mLocationOverlay)
        mapController.animateTo(mLocationOverlay.myLocation.latitude.toInt(), mLocationOverlay.myLocation.longitude.toInt())
//        val mGpsMyLocationProvider = GpsMyLocationProvider(this)
//        val mLocationOverlay = MyLocationNewOverlay(mGpsMyLocationProvider, map)
//        mLocationOverlay.enableMyLocation()
//        mLocationOverlay.enableFollowLocation()
//        map.overlays.add(mLocationOverlay)
//
//        val icon = BitmapFactory.decodeResource(resources, com.example.challenge8.R.drawable.ic_menu_compass)
//        mLocationOverlay.setPersonIcon(icon)
//        map.getOverlays().add(mLocationOverlay)

//        mLocationOverlay.runOnFirstFix {
//            map.overlays.clear()
//            map.overlays.add(mLocationOverlay)
//            mapController.animateTo(mLocationOverlay.myLocation)
//        }



    }
}