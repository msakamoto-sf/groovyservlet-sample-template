package gsst.gjmix;

public class JavaBeanSample {
    String name;
    public JavaBeanSample(String myname) {
        this.name = myname;
    }
    public String greeting(String yourname) {
        return "Hello, " + yourname + ". I'm " + this.name + ".";
    }
    public String greeting2(String yourname) {
        GroovyBeanSample b = new GroovyBeanSample(this.name);
        return b.greeting(yourname);
    }
}
