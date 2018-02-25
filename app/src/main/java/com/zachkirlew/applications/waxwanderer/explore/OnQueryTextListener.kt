package com.zachkirlew.applications.waxwanderer.explore


interface OnQueryTextListener {

    fun onQueryTextSubmit(searchText : String?)
    fun onQueryTextChange(searchText : String?)
}