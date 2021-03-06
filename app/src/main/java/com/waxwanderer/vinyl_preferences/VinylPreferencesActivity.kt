package com.waxwanderer.vinyl_preferences

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.DefaultItemAnimator
import android.widget.*
import com.waxwanderer.R
import com.waxwanderer.data.model.Style
import com.waxwanderer.main.MainActivity


class VinylPreferencesActivity : AppCompatActivity(), VinylPreferencesContract.View, AdapterView.OnItemSelectedListener {

    private lateinit var presenter: VinylPreferencesPresenter

    private val recyclerView by lazy{findViewById<RecyclerView>(R.id.recycler_view)}
    private val spinner by lazy{findViewById<Spinner>(R.id.spinner_genres)}
    private val submitButton by lazy{findViewById<Button>(R.id.btn_submit_preferences)}

    private val selectedStylesText by lazy{findViewById<TextView>(R.id.text_selected_styles)}

    private val selectedStyles : ArrayList<Style> = ArrayList(0)

    private lateinit var stylesAdapter: StylesAdapter

    private var hasComeFromMain: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_styles)

        presenter = VinylPreferencesPresenter(this)

        hasComeFromMain = intent.getBooleanExtra("fromMain",false)

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        recyclerView.itemAnimator = DefaultItemAnimator()

        stylesAdapter = StylesAdapter(emptyList())
        recyclerView.adapter = stylesAdapter

        submitButton.setOnClickListener(submitListener)
    }

    var submitListener = View.OnClickListener {

        presenter.savePreferences(selectedStyles)
    }

    public override fun onStart() {
        super.onStart()
        presenter.loadGenres()

        if(hasComeFromMain)
            presenter.loadVinylPrefs()
    }

    override fun showGenres(genres : List<String>) {

        spinner.onItemSelectedListener = this

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genres)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = dataAdapter
    }

    override fun showMessage(message: String?) {
        Toast.makeText(this@VinylPreferencesActivity, message,
                Toast.LENGTH_SHORT).show()
    }


    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

        val selectedGenre = parent?.getItemAtPosition(pos).toString()

        presenter.loadStyles(selectedGenre)
    }

    override fun showStyles(styles: List<Style>) {
        for (style in styles) {
            println(style)

        }
        stylesAdapter.replaceStyles(styles)
        stylesAdapter.notifyDataSetChanged()
    }

    override fun startNextActivity() {
        if(hasComeFromMain)
            onBackPressed()
        else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun showUsersPreferredStyles(styles: List<Style>) {
        selectedStyles.addAll(styles)
        updateSelectedText()
        checkStylesList()
    }

    fun addStyleToSelectedList(style : Style){
        selectedStyles.add(style)
        updateSelectedText()
        checkStylesList()
    }

    fun removeStyleFromSelectedList(style : Style){
        val position = selectedStyles.indexOfFirst { it.style == style.style }
        selectedStyles.removeAt(position)
        updateSelectedText()
        checkStylesList()
    }

    private fun updateSelectedText(){
        val map = selectedStyles.map { it.style }
        val commaSeparatedStyles = android.text.TextUtils.join(", ", map)
        selectedStylesText.text = commaSeparatedStyles
    }

    private fun checkStylesList(){

        if(selectedStyles.isNotEmpty())
            submitButton.visibility = View.VISIBLE
        else
            submitButton.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        presenter.dispose()
    }

    internal inner class StylesAdapter(private var styles: List<Style>) : RecyclerView.Adapter<StylesAdapter.MyViewHolder>() {

        fun replaceStyles(newStyles : List<Style>){
            styles =newStyles
        }

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var name: TextView = view.findViewById(R.id.style_name)
            var checkBox : CheckBox = view.findViewById(R.id.check_box)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.style_item, parent, false)

            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val style = styles[position]
            holder.name.text = style.style

            println(style.style)

            //in some cases, it will prevent unwanted situations
            holder.checkBox.setOnCheckedChangeListener(null)

            //if true, your checkbox will be selected, else unselected
            holder.checkBox.isChecked = styles[position].isSelected


            selectedStyles.filter{it.style == styles[position].style}
                    .map { holder.checkBox.isChecked = true }


            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->

                styles[holder.adapterPosition].isSelected= isChecked

                if(isChecked)
                    addStyleToSelectedList(styles[holder.adapterPosition])
                else
                    removeStyleFromSelectedList(styles[holder.adapterPosition])
            }
        }

        override fun getItemCount(): Int {
            return styles.size
        }
    }
}