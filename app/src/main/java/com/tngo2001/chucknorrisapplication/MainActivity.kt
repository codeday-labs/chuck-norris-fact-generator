package com.tngo2001.chucknorrisapplication

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.tngo2001.chucknorrisapplication.databinding.ActivityMainBinding
import com.tngo2001.chucknorrisapplication.model.CategoriesResponseModel
import com.tngo2001.chucknorrisapplication.model.JokeResponseModel
import com.tngo2001.chucknorrisapplication.model.JokesResponseModel
import com.tngo2001.chucknorrisapplication.model.NumberResponseModel
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val chuckNorrisAPI = ChuckNorrisAPI()
        val randomnessBtn: ToggleButton = findViewById(R.id.randomness_button)
        val idTxt: EditText = findViewById(R.id.enter_id)
        val quantityBtn: ToggleButton = findViewById(R.id.quantity_button)
        val amountTxt: EditText = findViewById(R.id.enter_amount)
        val nameBtn: ToggleButton = findViewById(R.id.name_button)
        val namesTxt: LinearLayout = findViewById(R.id.name_text)
        val firstTxt: EditText = findViewById(R.id.first_name)
        val lastTxt: EditText = findViewById(R.id.last_name)
        val categories: LinearLayout = findViewById(R.id.check_boxes)
        val categoryLst: MutableList<String> = mutableListOf()
        val generateBtn: Button = findViewById(R.id.button_first)

        var numId = 0
        var numJokes = 0
        var randomness = "/random"
        var quantity = "/1"
        var firstName = "?firstName=Chuck"
        var lastName = "&lastName=Norris"
        var limitTo = ""

        idTxt.visibility = View.GONE
        amountTxt.visibility = View.GONE
        namesTxt.visibility = View.GONE
        categories.visibility = View.GONE

        chuckNorrisAPI.getNumber { response: NumberResponseModel -> numJokes = response.value }

        randomnessBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                idTxt.visibility = View.GONE
                quantityBtn.visibility = View.VISIBLE
                quantityBtn.isChecked = true
                randomness = "/random"
                numId = 0
            } else {
                idTxt.visibility = View.VISIBLE
                quantityBtn.visibility = View.GONE
                amountTxt.visibility = View.GONE
                categories.visibility = View.GONE
                randomness = "/" + idTxt.text.toString()
                if (idTxt.text.toString() != "") numId = idTxt.text.toString().toInt()
                idTxt.addTextChangedListener(createTextListener { s: String ->
                    randomness = "/$s"
                    if (s != "") numId = s.toInt()
                })
            }
        }

        quantityBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amountTxt.visibility = View.GONE
                categories.visibility = View.GONE
                quantity = "/1"
            } else {
                amountTxt.visibility = View.VISIBLE
                categories.visibility = View.VISIBLE
                quantity = "/" + amountTxt.text.toString()
                amountTxt.addTextChangedListener(createTextListener { s: String -> quantity = "/$s" })
            }
        }

        nameBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                namesTxt.visibility = View.GONE
                firstName = "?firstName=Chuck"
                lastName = "&lastName=Norris"
            } else {
                namesTxt.visibility = View.VISIBLE
                firstName = "?firstName=" + firstTxt.text.toString()
                lastName = "&lastName=" + lastTxt.text.toString()
                firstTxt.addTextChangedListener(createTextListener { s: String -> firstName = "?firstName=$s" })
                lastTxt.addTextChangedListener(createTextListener { s: String -> lastName = "&lastName=$s" })
            }
        }

        chuckNorrisAPI.getCategories { response: CategoriesResponseModel ->
            for (category in response.value) {
                runOnUiThread {
                    categories.addView(CheckBox(this@MainActivity).apply {
                        setText(category.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        })
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                categoryLst.add(category)
                            } else {
                                if (category in categoryLst) categoryLst.remove(category)
                            }
                            limitTo = if (categoryLst.isEmpty()) "" else "&limitTo=$categoryLst"
                        }
                    })
                }
            }
        }

        generateBtn.setOnClickListener {
            if (quantityBtn.isChecked || !randomnessBtn.isChecked) {
                if (!randomnessBtn.isChecked && (numId < 1 || numId > numJokes)) {
                    showDialog("Error: The ID entered must be between 1 and $numJokes.")
                } else {
                    chuckNorrisAPI.getJoke("http://api.icndb.com/jokes$randomness$firstName$lastName") { response: JokeResponseModel ->
                        showDialog(response.value.joke)
                    }
                }
            } else {
                chuckNorrisAPI.getJokes("http://api.icndb.com/jokes/random$quantity$firstName$lastName$limitTo") { response: JokesResponseModel ->
                    for (jokesModel in response.value) showDialog(jokesModel.joke)
                }
            }
        }
    }

    private fun createTextListener(onTextUpdate: (newText: String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                onTextUpdate("$s")
            }
        }
    }

    private fun showDialog(message: String) {
        runOnUiThread {
            val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
            alertDialogBuilder.setMessage(message)
                .setCancelable(false)
                .setNegativeButton(
                    "Close",
                    DialogInterface.OnClickListener { dialog, id -> dialog.cancel() }
                )
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}