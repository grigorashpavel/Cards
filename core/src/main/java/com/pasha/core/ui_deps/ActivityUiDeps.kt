package com.pasha.core.ui_deps


interface ActivityUiDeps {
    fun showMessage(header: String = "Error", message: String)
    fun offerToAddAccount(callback: () -> Unit)
    fun isOpenedByAuthenticatorToCreateAccount(): Boolean
    fun hideBottomNavigationView()
    fun showBottomNavigationView()
    fun checkForAuthNavigation(errorCode: Int)
    fun checkForAuthNavigation(errorCode: String)
    fun switchBackPressedNavigationMode()
}