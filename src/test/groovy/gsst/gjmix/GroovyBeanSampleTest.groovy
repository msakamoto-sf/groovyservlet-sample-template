package gsst.gjmix

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

import org.junit.Test

class GroovyBeanSampleTest {
    @Test void greeting() {
        def b = new GroovyBeanSample("Jon")
        assertThat b.greeting("Bob"), is("Good morning Bob. I'm Jon.")
    }
    @Test void greeting2() {
        def b = new GroovyBeanSample("Jon")
        assertThat b.greeting2("Bob"), is("Hello, Bob. I'm Jon.")
    }
}
