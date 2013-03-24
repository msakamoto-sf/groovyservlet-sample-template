import gsst.*

new ToolKit(request, response, context).serve {
def m = [:]
m['title'] = 'Session Usage'

render(data:m, template:'demo/sessions/index.html')
}
