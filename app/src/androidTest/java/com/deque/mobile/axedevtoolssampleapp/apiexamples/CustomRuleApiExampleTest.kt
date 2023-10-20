package com.deque.mobile.axedevtoolssampleapp.apiexamples

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.deque.axe.android.constants.AxeStatus
import com.deque.mobile.axedevtoolssampleapp.BuildConfig
import com.deque.mobile.axedevtoolssampleapp.MainActivity
import com.deque.mobile.devtools.AxeDevTools
import com.deque.mobile.devtools.testingconfigs.AxeDevToolsEspressoConfig
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CustomRuleApiExampleTest {

    private val axe = AxeDevTools()

    init {
        axe.connect(BuildConfig.AXE_DEVTOOLS_APIKEY)
        BuildConfig.IS_TESTING.set(true)
    }

    @Rule
    @JvmField
    val rule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        axe.setTestingConfig(AxeDevToolsEspressoConfig(IdlingRegistry.getInstance()))
        axe.resetIgnoredRules()
    }

    @Test
    fun xml_customRule() {
        //Custom Rule API
        axe.addCustomRule(CustomVisibleTextRule::class.java)

        Espresso.onView(ViewMatchers.withText("Start XML")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Next")).perform(ViewActions.click())

        rule.scenario.onActivity { mainActivity ->
            // Scan and receive the ScanResultHandler locally
            val scanResultHandler = axe.scan(mainActivity)

            // Upload it to the axeDevTools Mobile Dashboard
            scanResultHandler?.uploadToDashboard()

            //Iterate through the result and make assertions
            scanResultHandler?.serializedResult?.axeRuleResults?.forEach {
                if (it.ruleId == CustomVisibleTextRule::class.java.simpleName) {
                    TestCase.assertEquals(AxeStatus.PASS, it.status)
                }
            }

            axe.tearDown()
        }
    }
}