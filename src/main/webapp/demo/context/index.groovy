import gsst.*

new ToolKit(request, response, context).serve {

def m = [:]
m['title'] = 'Context Datas'
def initparams = []
context.getInitParameterNames().each {
    attrs << ['key' : it, 'value' : context.getInitParameter(it).dump()]
}
m['initparams'] = initparams

def attrs = []
context.getAttributeNames().each {
    attrs << ['key' : it, 'value' : context.getAttribute(it).dump()]
}
m['attrs'] = attrs

render(data:m, template:'demo/context/index.html')
}
