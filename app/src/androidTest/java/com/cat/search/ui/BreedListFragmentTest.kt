package com.cat.search.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cat.search.R
import com.cat.search.adapter.BreedListAdapter
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BreedListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test_breed_list_fragment_when_activity_launched_displayed_breeds_list_fragment() {
        ActivityScenario.launch(BreedActivity::class.java)

        Thread.sleep(1000)
        onView(withId(R.id.breedListFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun test_breed_list_fragment_when_launched_displayed_cat_breeds_recycler_view() {
        ActivityScenario.launch(BreedActivity::class.java)

        Thread.sleep(1000)
        onView(withId(R.id.rvCatBreeds)).check(matches(isDisplayed()))
    }

    @Test
    fun test_breed_list_fragment_when_launched_displayed_progressBar() {
        ActivityScenario.launch(BreedActivity::class.java)

        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }

    @Test
    fun test_breed_list_fragment_when_item_clicked_displayed_breed_fragment() {
        ActivityScenario.launch(BreedActivity::class.java)

        Thread.sleep(1000)
        onView(withId(R.id.rvCatBreeds)).perform(
            actionOnItemAtPosition<BreedListAdapter.BreedsViewHolder>(1, click())
        )

        onView(withId(R.id.breedFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun test_breed_list_fragment_when_item_clicked_displayed_breed_fragment_webview() {
        ActivityScenario.launch(BreedActivity::class.java)

        Thread.sleep(1000)
        onView(withId(R.id.rvCatBreeds)).perform(
            actionOnItemAtPosition<BreedListAdapter.BreedsViewHolder>(2, click())
        )

        onView(withId(R.id.webView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_breed_list_fragment_when_item_clicked_displayed_breed_fragment_favbutton() {
        ActivityScenario.launch(BreedActivity::class.java)

        Thread.sleep(1000)
        onView(withId(R.id.rvCatBreeds)).perform(
            actionOnItemAtPosition<BreedListAdapter.BreedsViewHolder>(3, click())
        )

        onView(withId(R.id.favButton)).check(matches(isDisplayed()))
    }
}