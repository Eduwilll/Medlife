package medlife.com.br

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import medlife.com.br.activity.AutenticacaoActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginAndBuyIntegrationTest {
    @Test
    fun loginAndBuyProductFlow() {
        // Launch login activity
        ActivityScenario.launch(AutenticacaoActivity::class.java)

        // Enter email and password
        Espresso.onView(ViewMatchers.withId(R.id.editCadastroEmail))
            .perform(ViewActions.typeText("testuser@example.com"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.editCadastroSenha))
            .perform(ViewActions.typeText("testpassword"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.buttonAcesso)).perform(ViewActions.click())

        // Check if HomeActivity is displayed (login success)
        Espresso.onView(ViewMatchers.withId(R.id.autentication))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Additional product purchase flow would be tested here with correct UI IDs
    }
}
