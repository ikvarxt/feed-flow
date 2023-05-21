package com.prof18.feedflow

sealed class Screen(val name: String) {
    object Home: Screen("home")
    object ImportFeed: Screen("import_feed")
    object Settings: Screen("setting")
    object AddFeed: Screen("add_feed")
    object FeedList: Screen("feed_list")
}
