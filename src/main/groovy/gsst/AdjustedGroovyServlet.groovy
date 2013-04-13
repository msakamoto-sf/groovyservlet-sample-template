/* {{{ LICENSE
Copyright 2013 sakamoto.gsyc.3s@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
}}} */
package gsst

import org.apache.commons.lang3.*
import javax.servlet.*
import groovy.servlet.*

public class AdjustedGroovyServlet extends GroovyServlet {
    public URLConnection getResourceConnection(String name) throws ResourceException {
        ServletConfig sconfig = getServletConfig()
        ServletContext sctx = sconfig.getServletContext()
        String sinfo = sctx.getServerInfo().toLowerCase()
        if (sinfo && sinfo.contains("tomcat") && SystemUtils.IS_OS_WINDOWS) {
            if (name.startsWith("file:")) name = name.replaceFirst("file:", "")
        }
        return super.getResourceConnection(name)
    }
}
