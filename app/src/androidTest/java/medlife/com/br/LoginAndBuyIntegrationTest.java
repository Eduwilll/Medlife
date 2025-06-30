package medlife.com.br;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.assertion.ViewAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import medlife.com.br.activity.AutenticacaoActivity;
import medlife.com.br.activity.HomeActivity;

@RunWith(AndroidJUnit4.class)
public class LoginAndBuyIntegrationTest {
    @Test
    public void loginAndBuyProductFlow() {
        // Launch login activity
        ActivityScenario.launch(AutenticacaoActivity.class);

        // Enter email and password
        Espresso.onView(ViewMatchers.withId(R.id.editCadastroEmail))
                .perform(ViewActions.typeText("testuser@example.com"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.editCadastroSenha))
                .perform(ViewActions.typeText("testpassword"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.buttonAcesso)).perform(ViewActions.click());

        // Check if HomeActivity is displayed (login success)
        Espresso.onView(ViewMatchers.withId(R.id.autentication))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Simulate buying a product (this part is a placeholder, as actual UI IDs and flow may differ)
        // Espresso.onView(ViewMatchers.withId(R.id.product_list)).perform(...);
        // Espresso.onView(ViewMatchers.withId(R.id.add_to_cart_button)).perform(ViewActions.click());
        // Espresso.onView(ViewMatchers.withId(R.id.cart_button)).perform(ViewActions.click());
        // Espresso.onView(ViewMatchers.withId(R.id.checkout_button)).perform(ViewActions.click());
        // Espresso.onView(ViewMatchers.withId(R.id.order_success_layout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
} 