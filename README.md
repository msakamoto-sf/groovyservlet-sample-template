# groovyservlet-sample-template

groovyservlet-sample-template is a simple source code template for Servlet programming with Groovy.
If you want some light-weight web application framework, this template will be a good answer.

## quick start

1. Run ```gradle tomcatRun```.
2. Put ```xxxx.groovy``` or copy sample files under ```src/main/webapp/```.
3. Open browser, access to ```http://localhost:8090/xxxx.groovy```.
4. NO NEED to restarting groovy neither tomcat.

## how to customize

1. Read ```src/main/groovy/gsst/ToolKit.groovy```, and hack it. :)

## notes

1. If you want to add some little bit complicated class libraries, You can put and use Groovy/Java classes under ```src/main/groovy|java/``` directory. These sources are compiled and put to ```src/main/webapp/WEB-INF/classes/```, so, you can import that in xxxx.groovy. Of course, it is also possible to use your favorite external libraries by embedding dependencies in ```build.gradle```.
2. This sample uses mustache/Hogan.groovy as view component. If you prefer other template engine, you can put it.
3. If you want more functional web application frameworks, we'll recommend other heavy-weight web application frameworks such as Grails, Spring, Play framework, e.t.c.


