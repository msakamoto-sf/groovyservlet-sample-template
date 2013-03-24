import gsst.*

new ToolKit(request, response, context).serve {

log.trace('trace message')
log.debug('debug message')
log.info('info message')
log.warn('warn message')
log.error('error message')

headers.each {
    log.trace('headers[' + it.key + ']=[' + it.value + ']')
}

def m = [:]
m['title'] = 'Welcome'
m['req'] = [
    [key: 'context_path', value: request.getContextPath()],
    [key: 'method', value: request.getMethod()],
    [key: 'path_info', value: request.getPathInfo()],
    [key: 'query_string', value: request.getQueryString()],
    [key: 'request_uri', value: request.getRequestURI()],
    [key: 'servlet_path', value: request.getServletPath()],
]

if (request.getParameter('changelayoutcs') == 'on') {
    render(data:m, template:'index.html', layout:'layout2.html')
} else {
    render(data:m, template:'index.html')
}
}
