package com.ericwathome.stovetime.navigation

import androidx.navigation3.runtime.NavKey

class Navigator(
    private val backStack: MutableList<NavKey>,
    private val tabs: List<Destination>,
) {
    private val homeTab: Destination = tabs.first()

    val backStackEntries: List<NavKey>
        get() = backStack

    val currentTab: Destination
        get() = backStack.lastOrNull { it in tabs } as? Destination ?: homeTab

    val isOnTab: Boolean
        get() = backStack.lastOrNull() in tabs

    fun selectTab(tab: Destination) {
        if (tab == backStack.lastOrNull()) return
        while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
        if (tab != homeTab) backStack.add(tab)
    }

    fun open(destination: Destination) {
        if (destination == backStack.lastOrNull()) return
        if (destination in tabs) selectTab(destination) else navigate(destination)
    }

    fun navigate(destination: Destination) {
        backStack.add(destination)
    }

    fun replace(destination: Destination) {
        backStack.removeAt(backStack.lastIndex)
        backStack.add(destination)
    }

    fun replaceAllWith(destination: Destination) {
        backStack.clear()
        backStack.add(destination)
    }

    fun backTo(destination: Destination) {
        while (backStack.size > 1 && backStack.last() != destination) backStack.removeAt(backStack.lastIndex)
    }

    inline fun <reified T : Destination> lastOrNull(): T? = backStackEntries.lastOrNull { it is T } as? T

    fun backward() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }
}
