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
import groovy.sql.*
import com.github.plecong.hogan.*
import org.apache.commons.lang3.StringEscapeUtils
import org.h2.jdbcx.*

// {{{ SessionWrapper

/*
 * THIS IS ''NOT'' THREAD SAFE OPERATION
 */
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
// {{{ SampleContextListener

@Slf4j
class SampleContextListener implements ServletContextListener
{
    JdbcConnectionPool h2cp
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

        ConfigObject initconfig = new ConfigSlurper().parse(ctx.getResourceAsStream('/WEB-INF/config.groovy').getText('UTF-8'))
        ctx.setAttribute(ToolKit.CONTEXT_ATTR_CONFIG_CACHE, initconfig)

        HoganRenderer hr = new HoganRenderer(initconfig)
        ctx.setAttribute(HoganRenderer.CONTEXT_ATTR_HR_KEY, hr)

        def ds = initconfig.datasources.dev
        this.h2cp = JdbcConnectionPool.create(ds.url, ds.username, ds.password)
        log.info('h2db connection pool initialized for ' + ds.url)
        ctx.setAttribute(SampleH2DbConnector.H2DBCP_KEY, this.h2cp)

        Sql sql = new Sql(this.h2cp.getConnection())
        def creates = initconfig.datasources.setup
        creates.each {
            log.trace(it)
            sql.execute(it)
        }
        sql.close()
    }
    void contextDestroyed(ServletContextEvent sce)
    {
        ServletContext ctx = sce.getServletContext()
        ctx.log('Customize Context Destory Event Here.')
        log.info('Customize Context Destroy Event Here.')
        log.info(this.dump())
        this.h2cp.dispose()
        log.info('h2db connection pool disposed.')
    }
}

// }}}
// {{{ SampleH2DbConnector

class SampleH2DbConnector
{
    static final String H2DBCP_KEY = 'sample.h2db.ConnectionPool'
    ServletContext context
    List<Sql>conns = new ArrayList<Sql>();
    def SampleH2DbConnector(ServletContext ctx)
    {
        this.context = ctx
    }
    Sql borrow()
    {
        JdbcConnectionPool h2cp = context.getAttribute(H2DBCP_KEY)
        Sql c = new Sql(h2cp.getConnection())
        conns << c
        return c
    }
    void close()
    {
        conns.each { c -> c.close() }
    }
}

// }}}
// {{{ HoganRenderer

@Slf4j
class HoganRenderer
{
    static final String CONTEXT_ATTR_HR_KEY = 'gsst.HoganRenderer'
    class CompiledPair {
        String templateName
        String templateSrc
        Class<HoganTemplate> templateClass
    }

    ConfigObject config

    Map<String, CompiledPair> compiledClasses = new HashMap<String, CompiledPair>()

    def HoganRenderer(ConfigObject _config)
    {
        this.config = _config
    }

    protected Class<HoganTemplate> getTemplate(_templateName, _templateSrc)
    {
        /*
         * NEED MORE BETTER THREAD SAFETY
         */
        if (this.config.renderer.use_compile_cache) {
            synchronized(this.compiledClasses) {
                if (this.compiledClasses[_templateName]) {
                    CompiledPair cp = this.compiledClasses[_templateName]
                    String cachedBody = cp.templateSrc
                    if (cachedBody.equals(_templateSrc)) {
                        log.debug("cache for ${_templateName} hit.")
                        return cp.templateClass
                    }
                }
            }
        }
        Class<HoganTemplate> htclazz = Hogan.compileClass(_templateSrc)
        if (this.config.renderer.use_compile_cache) {
            CompiledPair cp = new CompiledPair(
                templateName: _templateName,
                templateSrc: _templateSrc,
                templateClass: htclazz)
            synchronized(this.compiledClasses) {
                log.debug("cache for ${_templateName} was updated.")
                this.compiledClasses[_templateName] = cp
            }
        }
        return htclazz
    }

    String render(bindData, templateName, templateSrc)
    {
        Class<HoganTemplate> htclazz = getTemplate(templateName, templateSrc)
        HoganTemplate t = Hogan.create(htclazz, templateSrc)
        return t.render(bindData)
    }
}

// }}}
// {{{ SeparateRequestFilter

class SeparateRequestFilter implements Filter {

    void init(FilterConfig fconfig) throws ServletException {}

    void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest hreq = (HttpServletRequest)req;
        req = new BufferedServletRequestWrapper(hreq);
        chain.doFilter(req, res);
    }

    void destroy() {}
}

// }}}
// {{{ KeyedMultiValues<T>

class KeyedMultiValues<T>
{
    final Map<String, LinkedList<T>> kmv

    def KeyedMultiValues()
    {
        this.kmv = new HashMap<String, LinkedList<T>>();
    }

    void add(String key, T val)
    {
        LinkedList<T> mv = new LinkedList<T>();
        if (this.kmv.containsKey(key)) {
            mv = this.kmv.get(key)
        }
        mv.add(val)
        this.kmv.put(key, mv)
    }

    Map<String, LinkedList<T>> all()
    {
        return Collections.unmodifiableMap(kmv)
    }

    boolean has(String key)
    {
        return this.kmv.containsKey(key)
    }

    T head(String key)
    {
        if (!this.kmv.containsKey(key)) {
            return null
        }
        LinkedList<T> mv = this.kmv.get(key)
        if (0 == mv.size()) {
            return null
        }
        return mv.getFirst()
    }

    LinkedList<T> list(String key)
    {
        if (!this.kmv.containsKey(key)) {
            return Collections.EMPTY_LIST
        }
        LinkedList<T> mv = this.kmv.get(key)
        return Collections.unmodifiableList(mv)
    }
}

// }}}
// {{{ SeparateRequestWrapper

class SeparateRequestWrapper
{
    HttpServletRequest req
    String defaultEncoding

    final KeyedMultiValues<String> GET
    final KeyedMultiValues<String> POST

    def SeparateRequestWrapper(HttpServletRequest _req, String _defaultEncoding)
    {
        this.req = _req
        this.defaultEncoding = _defaultEncoding
        this.GET = new KeyedMultiValues<String>()
        this.POST = new KeyedMultiValues<String>()

        parseUrlEncoded(this.GET, req.getQueryString())

        if (!'GET'.equals(req.getMethod())) {
            String contentType = req.getHeader('Content-Type')
            if ('application/x-www-form-urlencoded'.equals(contentType)) {
                ServletInputStream sis = req.getInputStream()
                String postBody = sis.getText()
                sis.reset()
                parseUrlEncoded(this.POST, postBody)
            } else if ('multipart/form-data'.equals(contentType)) {
            }
        }
    }

    protected String urldecode(String encoded)
    {
        String v = ''
        try {
            v = URLDecoder.decode(encoded, defaultEncoding)
        } catch (UnsupportedEncodingException ignore) {}
        return v
    }

    protected void parseUrlEncoded(KeyedMultiValues<String> kmv, String source)
    {
        if (!source) { source = '' }
        source.split('&').each { kvsrc ->
            def kv = kvsrc.split('=', 2) as String[]
            if (kv.size() == 2) {
                def decodedK = urldecode(kv[0])
                def decodedV = urldecode(kv[1])
                kmv.add(decodedK, decodedV)
            } else {
                def decodedK = urldecode(kvsrc)
                kmv.add(decodedK, '')
            }
        }
    }
}

// }}}

@Slf4j
class ToolKit
{
    static final String CONTEXT_ATTR_CONFIG_CACHE = 'gsst.ToolKit.CONFIG_CACHE'
    HttpServletRequest request
    HttpServletResponse response
    ServletContext context
    SessionWrapper sess
    ConfigObject config
    SeparateRequestWrapper sreqw

    // Customize This
    SampleH2DbConnector dbcon // per request instance

    def ToolKit(HttpServletRequest req, HttpServletResponse res, ServletContext ctx)
    {
        this.request = req
        this.response = res
        this.context = ctx
        this.config = ctx.getAttribute(CONTEXT_ATTR_CONFIG_CACHE) ?: new ConfigSlurper().
            parse(ctx.getResourceAsStream('/WEB-INF/config.groovy').getText('UTF-8'))
        this.sess = new SessionWrapper(req)
        this.sreqw = new SeparateRequestWrapper(req, this.config.defaultEncoding)
        this.dbcon = new SampleH2DbConnector(ctx)
    }

    void serve(cl)
    {
        cl.delegate = this
        cl.call()
        this.dbcon.close()
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
        HoganRenderer hr = this.context.getAttribute(HoganRenderer.CONTEXT_ATTR_HR_KEY)

        def contents = hr.render(bindData, template, loadResourceAsString(templatePath))
        bindData['__placeholder__'] = ['contents': contents]
        def pagetext = hr.render(bindData, layout, loadResourceAsString(layoutPath))
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

