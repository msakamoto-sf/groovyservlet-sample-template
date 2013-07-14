import gsst.*

new ToolKit(request, response, context).serve {
UrlMap um = new UrlMap(context)
def m = [:]
m['title'] = 'Url Map Demos'
m['original_path_info'] = []
UrlMap.extractOriginalInfo(request).each { pi ->
    m['original_path_info'] << [key: pi.key, value: pi.value]
}
m['forwarded_request'] = [
    [key: 'getContextPath()', value: request.getContextPath()],
    [key: 'getMethod()', value: request.getMethod()],
    [key: 'getPathInfo()', value: request.getPathInfo()],
    [key: 'getPathTranslated()', value: request.getPathTranslated()],
    [key: 'getRequestURI()', value: request.getRequestURI()],
    [key: 'getRequestURL()', value: request.getRequestURL().toString()],
    [key: 'getServletPath()', value: request.getServletPath()],
]

def opi = UrlMap.extractOriginalInfo(request)
String urlbase = opi.context_path + opi.servlet_path
m['sample_urls'] = [
[url:urlbase + um.buildUrl('/item/@id@', [id:10]), label:'/item/@id@'],
[url:urlbase + um.buildUrl('/list/@year@/@mon@/@day@/', [year:2013, mon:'04', day:14]), label:'/list/@year@/@mon@/@day@/'],
[url:urlbase + um.buildUrl('/search/@query@', [query:'keywordsqueries']), label:'/search/@query@'],
]

render(data:m, template:'/demo/urlmap/index.html')
}
