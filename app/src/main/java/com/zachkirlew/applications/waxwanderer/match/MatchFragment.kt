package com.zachkirlew.applications.waxwanderer.match

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.view.*
import com.zachkirlew.applications.waxwanderer.R


class MatchFragment : Fragment(), MatchContract.View {

    private lateinit var favouritePresenter : MatchContract.Presenter

    override fun onResume() {
        super.onResume()
        favouritePresenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater?.inflate(R.layout.fragment_match, container, false)

        favouritePresenter = MatchPresenter(this)



        return root
    }


    override fun setPresenter(presenter: MatchContract.Presenter) {
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