package com.waxwanderer.base

interface BaseView<T> {

    fun setPresenter(presenter: T)
    fun showMessage(message : String?)
}