package com.wxm.keepaccount;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Created by 123 on 2016/5/2.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityInstrumentationTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void sayHello(){
        //onView(withText("Say hello!")).perform(click());
        //onView(withId(R.id.textView)).check(matches(withText("Hello, World!")));
        assertEquals(1, 1);
        //assertEquals(1, 2);
    }
}
