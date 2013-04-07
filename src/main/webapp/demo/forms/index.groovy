import gsst.*

new ToolKit(request, response, context).serve {

def m = [:]
m['title'] = 'Form Demos'

render(data:m, template:'demo/forms/index.html')
}
