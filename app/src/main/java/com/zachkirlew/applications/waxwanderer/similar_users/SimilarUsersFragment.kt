package com.zachkirlew.applications.waxwanderer.similar_users

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.user_detail.UserDetailActivity
import kotlinx.android.synthetic.main.similar_user_item.view.*
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType


class SimilarUsersFragment : Fragment(), SimilarUsersContract.View {

    private lateinit var similarUsersPresenter : SimilarUsersContract.Presenter

    private lateinit var similarUserAdapter: SimilarUsersFragment.SimilarUserAdapter

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        similarUserAdapter = SimilarUserAdapter(listOf<User>())
    }

    override fun onResume() {
        super.onResume()
        similarUsersPresenter.start()
    }

    lateinit var similarUserList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater?.inflate(R.layout.fragment_similar_users, container, false)

        similarUsersPresenter = SimilarUsersPresenter(this)

        similarUserList = root?.findViewById<RecyclerView>(R.id.similar_user_list) as RecyclerView

        val mLayoutManager = LinearLayoutManager(activity)

        similarUserList.layoutManager = mLayoutManager
        similarUserList.adapter = similarUserAdapter

        return root
    }


    override fun setPresenter(presenter: SimilarUsersContract.Presenter) {
        similarUsersPresenter = presenter
    }

    override fun showSimilarUsers(users: List<User>) {
        similarUserAdapter.addUsers(users)
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

    class SimilarUserAdapter(private var users: List<User>) : RecyclerView.Adapter<SimilarUserAdapter.ViewHolder>() {


        fun addUsers(users : List<User>){
            this.users = users
            notifyDataSetChanged()

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarUserAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.similar_user_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: SimilarUserAdapter.ViewHolder, position: Int) {
            holder.bindItems(users[position])

            holder.itemView.setOnClickListener {

                val context = holder.itemView.context

                val intent = Intent(context, UserDetailActivity::class.java)
                intent.putExtra("selected user", users[position])
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return users.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(user: User) {
                itemView.text_name.text = user.name
                itemView.text_location.text=user.location

                val birthDate = LocalDate(user.dob)

                val todaysDate = LocalDate()

                val period = Period(birthDate, todaysDate, PeriodType.yearMonthDay())

                itemView.text_age.text = period.years.toString()

//                if(!vinyl.thumb.isNullOrEmpty()) {
//                    Picasso.with(itemView.context).load(vinyl.thumb).into(itemView.cover_art)
//                }
            }
        }
    }


}