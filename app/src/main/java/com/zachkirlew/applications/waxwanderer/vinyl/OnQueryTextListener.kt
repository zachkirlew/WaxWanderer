package com.zachkirlew.applications.waxwanderer.vinyl


interface OnQueryTextListener {

    fun onQueryTextSubmit(searchText : String?)
    fun onQueryTextChange(searchText : String?)
}