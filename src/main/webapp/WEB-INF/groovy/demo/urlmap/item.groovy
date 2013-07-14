import gsst.*

new ToolKit(request, response, context).serve {
def m = [:]
m['title'] = 'Url Map (/item)'

m['path_params'] = []
def pathparams = request.getAttribute(UrlMapForwarderServlet.PATH_PARAM_ATTR_NAME)
pathparams.each { pp ->
    m['path_params'] << [key: pp.key, value: pp.value]
}

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

render(data:m, template:'/demo/urlmap/common.html')
}
