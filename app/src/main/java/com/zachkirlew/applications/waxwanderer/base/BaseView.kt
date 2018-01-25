package com.zachkirlew.applications.waxwanderer.base

interface BaseView<T> {

    fun setPresenter(presenter: T)

    fun showMessage(message : String?)
}