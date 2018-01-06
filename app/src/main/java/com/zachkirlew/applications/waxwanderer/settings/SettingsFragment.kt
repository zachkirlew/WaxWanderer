package com.zachkirlew.applications.waxwanderer.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Spinner
import com.zachkirlew.applications.waxwanderer.R


class SettingsFragment : Fragment(), SettingsContract.View {

    private lateinit var favouritePresenter : SettingsContract.Presenter

    override fun onResume() {
        super.onResume()
        favouritePresenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater?.inflate(R.layout.fragment_settings, container, false)

        val userGenderSpinner = root?.findViewById<Spinner>(R.id.user_gender_spinner) as Spinner

        val userGendersAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, activity.resources.getStringArray(R.array.spinner_user_genders))

        userGendersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        userGenderSpinner.adapter = userGendersAdapter

        val matchGenderSpinner = root.findViewById<Spinner>(R.id.match_gender_spinner) as Spinner
        val matchGenderAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, activity.resources.getStringArray(R.array.spinner_matching_genders))

        matchGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        matchGenderSpinner.adapter = matchGenderAdapter

        val fromAgePicker = root.findViewById<NumberPicker>(R.id.age_from) as NumberPicker
        fromAgePicker.minValue = 18
        fromAgePicker.maxValue = 100


        val toAgePicker = root.findViewById<NumberPicker>(R.id.age_to) as NumberPicker
        toAgePicker.minValue = 18
        toAgePicker.maxValue = 100



        favouritePresenter = SettingsPresenter(this)


        return root
    }


    override fun setPresenter(presenter: SettingsContract.Presenter) {
        favouritePresenter = presenter
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


}