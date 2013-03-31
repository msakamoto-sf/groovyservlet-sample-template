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

import javax.servlet.*
import javax.servlet.http.*
import groovy.util.logging.*
import groovy.json.*
import com.github.plecong.hogan.*
import org.apache.commons.lang3.StringEscapeUtils

// {{{ SessionWrapper

class SessionWrapper
{
    static final String SESSION_SCOPE_APP = '__APP__'
    static final String SESSION_SCOPE_FLUSH = '__FLUSH__'

    protected HttpServletRequest request
    protected HttpSession session = null

    def SessionWrapper(HttpServletRequest _request)
    {
        this.request = _request
    }

    String start()
    {
        this.session = this.request.getSession() // auto start
        return this.session.getId()
    }

    String id()
    {
        if (!this.session) { return '' }
        this.session.getId()
    }

    def invalidate()
    {
        if (!this.session) { return }
        this.session.invalidate()
    }

    def set(String key, Object newValue)
    {
        if (!this.session) { return null }
        def appScope = this.session.getAttribute(SESSION_SCOPE_APP) ?: [:]
        def oldValue = appScope[key]
        appScope[key] = newValue
        this.session.setAttribute(SESSION_SCOPE_APP, appScope)
    }

    def get(String key, Object defaultValue = null)
    {
        if (!this.session) { return defaultValue }
        def appScope = this.session.getAttribute(SESSION_SCOPE_APP) ?: [:]
        return appScope[key] ?: defaultValue
    }

    def addFlush(String m)
    {
        if (!this.session) { return }
        def flushScope = this.session.getAttribute(SESSION_SCOPE_FLUSH) ?: [] as ArrayList<String>
        flushScope << m
        this.session.setAttribute(SESSION_SCOPE_FLUSH, flushScope)
    }

    def getFlush()
    {
        if (!this.session) { return [] as ArrayList<String> }
        def flushScope = this.session.getAttribute(SESSION_SCOPE_FLUSH) ?: [] as ArrayList<String>
        return flushScope
    }

    def clearFlush()
    {
        if (!this.session) { return }
        this.session.setAttribute(SESSION_SCOPE_FLUSH, [] as ArrayList<String>)
    }
}

// }}}

@Slf4j
class SampleContextListener implements ServletContextListener
{
    def SampleContextListener()
    {
        log.info('Customize SampleContextListener.')
        log.info(this.dump())
    }
    void contextInitialized(ServletContextEvent sce)
    {
        ServletContext ctx = sce.getServletContext()
        ctx.setAttribute('sample.attr', 'Hello, ServletContext')
        ctx.log('Customize Context Initialize Event Here.')
        log.info('Customize Context Initialize Event Here.')
        log.info(this.dump())
    }
    void contextDestroyed(ServletContextEvent sce)
    {
        ServletContext ctx = sce.getServletContext()
        ctx.log('Customize Context Destory Event Here.')
        log.info('Customize Context Destroy Event Here.')
        log.info(this.dump())
    }
}

@Slf4j
class ToolKit
{
    HttpServletRequest request
    HttpServletResponse response
    ServletContext context
    SessionWrapper sess
    ConfigObject config

    def ToolKit(HttpServletRequest req, HttpServletResponse res, ServletContext ctx)
    {
        this.request = req
        this.response = res
        this.context = ctx
        this.config = new ConfigSlurper().parse(ctx.getResourceAsStream('/WEB-INF/config.groovy').getText('UTF-8'))
        this.sess = new SessionWrapper(req)
    }

    void serve(cl)
    {
        cl.delegate = this
        cl.call()
    }

    def redirect(url)
    {
        response.sendRedirect(url)
    }

    def renderAsJson(args)
    {
        def contentType = args['contentType'] ?: 'application/json; charset=UTF-8'
        def data = args['data'] ?: [:]
        def pretty = args['pretty'] ?: false
        def json = new JsonBuilder()
        json(data)
        response.setContentType(contentType)
        if (pretty) {
            response.writer << json.toPrettyString()
        } else {
            response.writer << json.toString()
        }
    }

    def render(args)
    {
        def contentType = args['contentType'] ?: config.defaultContentType
        def data = args['data'] ?: [:]
        def template = args['template'] ?: 'template is not specified.'
        def layout = args['layout'] ?: config.renderer.defaultLayout

        def bindData = [
            'config': config,
            'context_path': url(''),
            'util': [
                'h': { return { text -> h(text) } },
                'ecma': { return { text -> ecma(text) } },
                'urlencode': { return { text -> urlencode(text) } },
                'html':[
                    'script': { return { text -> '<script src="' + url(text) + '"></script>' } },
                    ]
                ]
            ]
        data.each { it -> bindData[it.key] = it.value }

        def templatePath = config.renderer.templateDir + template
        def layoutPath = config.renderer.templateDir + layout

        def contents = Hogan.compile(loadResourceAsString(templatePath)).render(bindData)

        bindData['__placeholder__'] = ['contents': contents]

        def pagetext = Hogan.compile(loadResourceAsString(layoutPath)).render(bindData)
        response.setContentType(contentType)
        response.writer << pagetext
    }

    String loadResourceAsString(String r)
    {
        context.getResourceAsStream(r).getText(config.defaultEncoding)
    }

    byte[] loadResourceAsByte(String r)
    {
        context.getResourceAsStream(r).getBytes()
    }

    String url(String u)
    {
        String cp = request.getContextPath()
        if (cp.size() == 0) { return u }

        // Tomcat's getContextPath() adjusting
        if (cp == '/') { return u }
        if (cp.endsWith('/')) {
            return cp.substring(0, cp.size() - 1) + u
        } else {
            return cp + u
        }
    }

    String h(String s)
    {
        StringEscapeUtils.escapeHtml4(s).replace("'", '&#39;')
    }

    String ecma(String s)
    {
        // needs more work : unicode escapement for alphabet & number & control chars.
        StringEscapeUtils.escapeEcmaScript(s)
    }

    String urlencode(String s)
    {
        // rfc3986 compatible
        URLEncoder.encode(s, config.defaultEncoding).replace('+', '%20').replace('*', '%2A')
    }
}

