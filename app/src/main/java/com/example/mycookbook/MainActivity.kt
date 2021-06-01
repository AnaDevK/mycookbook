package com.example.mycookbook

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

   // private lateinit var drawerLayout: DrawerLayout
    //private var folder: File? = null


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
//            this,
//            R.layout.activity_main
//        )

        //drawerLayout = binding.drawerLayout

        //val navController = this.findNavController(R.id.myNavHostFragment)

        //NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        //NavigationUI.setupWithNavController(binding.navView, navController)

        //create a folder with app name in Gallery to save receipt images
        //folder = getAppSpecificAlbumStorageDir(this, "Receipts")
 //   }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = this.findNavController(R.id.myNavHostFragment)
//        return NavigationUI.navigateUp(navController, drawerLayout)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        for (fragment in supportFragmentManager.fragments) {
//            fragment.onActivityResult(requestCode, resultCode, data)
//        }
//    }

    fun getAppSpecificAlbumStorageDir(context: Context, albumName: String): File? {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        val file = File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName)
        if (!file?.mkdirs()) {
            Log.e("TAG", "Directory not created")
        }
        return file
    }
}