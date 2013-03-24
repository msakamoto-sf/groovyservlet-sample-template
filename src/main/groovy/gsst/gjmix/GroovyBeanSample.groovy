package gsst.gjmix

class GroovyBeanSample {
    String name
    def GroovyBeanSample(String myname) {
        this.name = myname
    }
    String greeting(String yourname) {
        return sprintf("Good morning %s. I'm %s.", yourname, name)
    }
    String greeting2(String yourname) {
        JavaBeanSample b = new JavaBeanSample(name)
        return b.greeting(yourname)
    }
}
