package org.hamcrest;

import junit.framework.TestCase;

import static org.hamcrest.AbstractMatcherTest.assertDescription;

public class CustomMatcherTest extends TestCase {

    public void testUsesStaticDescription() throws Exception {
        Matcher<String> matcher = new CustomMatcher<String>("I match strings") {
            @Override
            public boolean matches(Object item) {
                return (item instanceof String);
            }
        };

        assertDescription("I match strings", matcher);
    }
}
