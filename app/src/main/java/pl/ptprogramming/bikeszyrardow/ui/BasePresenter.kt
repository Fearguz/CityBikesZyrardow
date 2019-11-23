package pl.ptprogramming.bikeszyrardow.ui

interface BasePresenter<in T> {
    fun attach(view: T)
}