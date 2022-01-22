package andriell.dictionary.helpers;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class StringHelperTest {
    @Test
    public void test1() {
        String[] r = StringHelper.split(null, null);
        assertNull(r);
        r = StringHelper.split("", null);
        assertNull(r);
        r = StringHelper.split(null, "");
        assertNull(r);
        r = StringHelper.split("     ", " ");
        assertNull(r);

        r = StringHelper.split(" I  love  Java  ", " ");
        assertEquals("[I, love, Java]", Arrays.toString(r));

    }
}
