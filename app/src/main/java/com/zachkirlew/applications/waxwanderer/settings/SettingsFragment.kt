package com.zachkirlew.applications.waxwanderer.settings

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.*
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.local.UserPreferences
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.vinyl_preferences.VinylPreferencesActivity
import com.zachkirlew.applications.waxwanderer.util.BorderedCircleTransform
import java.text.DateFormat
import java.util.*


class SettingsFragment : Fragment(), SettingsContract.View, DatePickerDialog.OnDateSetListener {

    private lateinit var favouritePresenter : SettingsContract.Presenter

    private val REQUEST_CAMERA = 3
    private val SELECT_FILE = 2

    lateinit var nameText: EditText
    lateinit var dobText: EditText
    lateinit var editTextDOB : EditText

    lateinit var userGenderSpinner : Spinner
    lateinit var matchGenderSpinner : Spinner

    lateinit var ageRangeText : TextView

    lateinit var pickerView : LinearLayout

    lateinit var uploadImage : ImageView

    lateinit var fromAgePicker : NumberPicker
    lateinit var toAgePicker : NumberPicker


    override fun onResume() {
        super.onResume()
        favouritePresenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        activity?.title = "Settings"

        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        userGenderSpinner = root?.findViewById<Spinner>(R.id.user_gender_spinner) as Spinner

        val userGendersAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, activity?.resources?.getStringArray(R.array.spinner_user_genders))

        userGendersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        userGenderSpinner.adapter = userGendersAdapter

        matchGenderSpinner = root.findViewById<Spinner>(R.id.match_gender_spinner) as Spinner
        val matchGenderAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, activity?.resources?.getStringArray(R.array.spinner_matching_genders))

        matchGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        matchGenderSpinner.adapter = matchGenderAdapter

        fromAgePicker = root.findViewById<NumberPicker>(R.id.age_from) as NumberPicker
        fromAgePicker.minValue = 18
        fromAgePicker.maxValue = 100


        toAgePicker = root.findViewById<NumberPicker>(R.id.age_to) as NumberPicker
        toAgePicker.minValue = 18
        toAgePicker.maxValue = 100

        fromAgePicker.setOnValueChangedListener { numberPicker, oldVal, newVal ->

            toAgePicker.minValue = newVal

            val oldAgeRange = ageRangeText.text.toString()

            val split = oldAgeRange.split(" - ")

            if(newVal > Integer.parseInt(split[1])){
                ageRangeText.text = newVal.toString() + " - " + newVal
            }

            else{
                ageRangeText.text = newVal.toString() + " - " + split[1]
            }
        }

        toAgePicker.setOnValueChangedListener { numberPicker, oldVal, newVal ->

            val oldAgeRange = ageRangeText.text.toString()

            val split = oldAgeRange.split(" - ")

            val newAgeRange = split[0] + " - " + newVal.toString()

            ageRangeText.text = newAgeRange
        }

        toAgePicker.minValue = 18
        toAgePicker.maxValue = 100

        favouritePresenter = SettingsPresenter(this, UserPreferences())

        nameText = root.findViewById<EditText>(R.id.input_name)
        dobText = root.findViewById<EditText>(R.id.input_dob)

        editTextDOB = root.findViewById<EditText>(R.id.input_dob) as EditText

        editTextDOB.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) showDateDialog() }

        ageRangeText = root.findViewById<TextView>(R.id.text_age_range) as TextView

        pickerView = root.findViewById<LinearLayout>(R.id.picker_view) as LinearLayout

        ageRangeText.setOnClickListener { showNumberPicker() }

        val submitButton = root.findViewById<Button>(R.id.button_submit_details) as Button

        submitButton.setOnClickListener { getDetails() }

        uploadImage = root.findViewById<ImageView>(R.id.image_upload) as ImageView

        uploadImage.setOnClickListener{ profilePicSelection() }

        return root
    }

    override fun showMessage(message: String?) {
        Toast.makeText(activity, message,
                Toast.LENGTH_LONG).show()
    }
    private fun profilePicSelection() {

        //DISPLAY DIALOG TO CHOOSE CAMERA OR GALLERY

        val items = arrayOf<CharSequence>("Choose from Library", "Cancel")
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Add Photo!")

        builder.setItems(items, { dialog, item ->
//            if (items[item] == "Take Photo") {
//                cameraIntent()
//            }

            if (items[item] == "Choose from Library") {
                galleryIntent()
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()

    }

    override fun startStylesActivity() {
        val intent = Intent(activity, VinylPreferencesActivity::class.java)
        intent.putExtra("fromSettings",true)
        startActivity(intent)
    }

//    private fun cameraIntent() {
//
//        //CHOOSE CAMERA
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(intent, REQUEST_CAMERA)
//    }

    private fun galleryIntent() {

        //CHOOSE IMAGE FROM GALLERY
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_FILE)
    }

    private fun showNumberPicker() {

        if(pickerView.visibility == View.GONE)
            pickerView.visibility = View.VISIBLE
        else
            pickerView.visibility = View.GONE
    }

    private fun showDateDialog() {

        val  c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog (activity, this, mYear, mMonth, mDay).show()
    }


    override fun setPresenter(presenter: SettingsContract.Presenter) {
        favouritePresenter = presenter
    }

    override fun showDateFormatted(date: String) {
        editTextDOB.setText(date)
    }

    override fun showUserDetails(user: User, minMatchAge: Int, maxMatchAge: Int, matchGender: String?) {

        nameText.setText(user.name)

        val dfMediumUK = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
        val dateFormatted = dfMediumUK.format(user.dob)

        dobText.setText(dateFormatted)

        val userGenders = activity?.resources?.getStringArray(R.array.spinner_user_genders)

        userGenders?.forEachIndexed { index, gender ->  if(gender == user.gender) userGenderSpinner.setSelection(index)}

        val matchGenders = activity?.resources?.getStringArray(R.array.spinner_matching_genders)
        matchGenders?.forEachIndexed { index, gender ->

            println(gender)
            if(gender == matchGender) matchGenderSpinner.setSelection(index)

        }

        fromAgePicker.value = minMatchAge
        toAgePicker.value = maxMatchAge

        val ageRange = "$minMatchAge - $maxMatchAge"

        ageRangeText.text = ageRange

        user.imageurl?.let{setProfileImage(user.imageurl)}

        uploadImage
    }

    private fun setProfileImage(imageurl: String?) {
        Picasso.with(activity)
                .load(imageurl)
                .resize(160, 160)
                .centerCrop()
                .transform(BorderedCircleTransform())
                .into(uploadImage)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        favouritePresenter.getFormattedDate(year, month, day)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.menu_clear -> mPresenter.clearCompletedTasks()
//            R.id.menu_filter -> showFilteringPopUpMenu()
//            R.id.menu_refresh -> mPresenter.loadTasks(true)
        }
        return true
    }

    fun getDetails(){

        val name = nameText.text.toString()

        val userGender = userGenderSpinner.selectedItem.toString()

        val matchGender = matchGenderSpinner.selectedItem.toString()


        val minMatchAge = fromAgePicker.value
        val maxMatchAge = toAgePicker.value

        favouritePresenter.submitDetails(name,userGender,matchGender,minMatchAge,maxMatchAge)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //SAVE URI FROM GALLERY
        if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            uploadImage.setImageURI(imageUri)

            favouritePresenter.saveProfileImage(imageUri)

//        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
//            //SAVE URI FROM CAMERA
//
//            val imageUri = data?.data
//
//            uploadImage.setImageURI(null)
//            uploadImage.setImageURI(imageUri)
//
//
//        }
        }

    }

    override fun onStop() {
        super.onStop()
        favouritePresenter.dispose()
    }



}