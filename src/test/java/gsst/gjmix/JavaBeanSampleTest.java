package gsst.gjmix;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class JavaBeanSampleTest {
    @Test
    public void greeting() {
        JavaBeanSample b = new JavaBeanSample("Bob");
        assertThat(b.greeting("Jon"), is("Hello, Jon. I'm Bob."));
    }
    @Test
    public void greeting2() {
        JavaBeanSample b = new JavaBeanSample("Bob");
        assertThat(b.greeting2("Jon"), is("Good morning Jon. I'm Bob."));
    }
}
