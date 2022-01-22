package andriell.dictionary.file;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class AffLinesTest {
    @Test
    public void test1() {
        Pattern pattern = Pattern.compile(".*я$");
        assertTrue(pattern.matcher("Чувашия").matches());
        assertTrue(pattern.matcher("Чувашия").matches());
        pattern = Pattern.compile(".*.$");
        assertTrue(pattern.matcher("Чувашия").matches());
    }

    @Test
    public void test2() {

    }
}
