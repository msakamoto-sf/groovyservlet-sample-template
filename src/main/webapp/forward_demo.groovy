import gsst.*

new ToolKit(request, response, context).serve {
    forward('/WEB-INF/groovy/demo/urlmap/index.groovy')
}
