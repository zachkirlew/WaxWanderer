package com.zachkirlew.applications.waxwanderer.styles

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.explore.ExploreActivity
import android.support.v7.widget.DefaultItemAnimator
import android.widget.*
import com.zachkirlew.applications.waxwanderer.data.model.Style


class StylesActivity : AppCompatActivity(), StylesContract.View, AdapterView.OnItemSelectedListener {

    private lateinit var presenter: StylesPresenter

    private val recyclerView by lazy{findViewById<RecyclerView>(R.id.recycler_view)}
    private val spinner by lazy{findViewById<Spinner>(R.id.spinner_genres)}
    private val submitButton by lazy{findViewById<Button>(R.id.btn_submit_preferences)}

    private val selectedStylesText by lazy{findViewById<TextView>(R.id.text_selected_styles)}

    private val selectedStyles : ArrayList<String> = ArrayList(0)

    private lateinit var stylesAdapter: StylesAdapter

    private var hasComeFromSettings : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_styles)

        presenter = StylesPresenter(this)

        hasComeFromSettings = intent.getBooleanExtra("fromSettings",false)

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        recyclerView.itemAnimator = DefaultItemAnimator()

        stylesAdapter = StylesAdapter(emptyList())
        recyclerView.adapter = stylesAdapter

        submitButton.setOnClickListener{

            val selectedGenre = spinner.selectedItem.toString()

            val selectedStyles = stylesAdapter.getAllSelectedStyles()

            presenter.savePreferences(selectedGenre,selectedStyles)
        }
    }

    public override fun onStart() {
        super.onStart()
        presenter.loadGenres()
    }

    override fun showGenres(genres : List<String>) {

        spinner.onItemSelectedListener = this

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genres)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = dataAdapter
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

        val selectedGenre = parent?.getItemAtPosition(pos).toString()

        presenter.loadStyles(selectedGenre)
    }

    override fun showStyles(styles: List<Style>) {
        stylesAdapter.replaceStyles(styles)
        stylesAdapter.notifyDataSetChanged()
    }

    override fun startNextActivity() {
        if(hasComeFromSettings)
            onBackPressed()
        else{
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }
    }

    fun addStyleToSelectedList(style : String){
        selectedStyles.add(style)

        val commaSeparatedStyles = android.text.TextUtils.join(", ", selectedStyles)
        selectedStylesText.text = commaSeparatedStyles
        checkStylesList()
    }

    fun removeStyleFromSelectedList(style : String){
        selectedStyles.remove(style)
        val commaSeparatedStyles = android.text.TextUtils.join(", ", selectedStyles)
        selectedStylesText.text = commaSeparatedStyles
        checkStylesList()
    }

    private fun checkStylesList(){

        if(selectedStyles.isNotEmpty())
            submitButton.visibility = View.VISIBLE
        else
            submitButton.visibility = View.GONE
    }

    companion object {

        private val TAG = StylesActivity::class.java.simpleName
    }

    internal inner class StylesAdapter(private var styles: List<Style>) : RecyclerView.Adapter<StylesAdapter.MyViewHolder>() {

        fun replaceStyles(newStyles : List<Style>){
            styles =newStyles
        }

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var name: TextView = view.findViewById(R.id.style_name)
            var checkBox : CheckBox = view.findViewById(R.id.check_box)
        }

        fun getAllSelectedStyles(): List<String> {
            return styles.filter {it.isSelected}.map { it.styleName }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.style_item, parent, false)

            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val style = styles[position]
            holder.name.text = style.styleName

            //in some cases, it will prevent unwanted situations
            holder.checkBox.setOnCheckedChangeListener(null)

            //if true, your checkbox will be selected, else unselected
            holder.checkBox.isChecked = styles[position].isSelected

            if(selectedStyles.contains(styles[position].styleName))
                holder.checkBox.isChecked = true

            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->

                styles[holder.adapterPosition].isSelected= isChecked

                if(isChecked)
                    addStyleToSelectedList(styles[holder.adapterPosition].styleName)
                else
                    removeStyleFromSelectedList(styles[holder.adapterPosition].styleName)
            }
        }


        override fun getItemCount(): Int {
            return styles.size
        }
    }
}