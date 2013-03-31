import gsst.*

new ToolKit(request, response, context).serve {
def m = [:]
m['title'] = 'SQL Demos'

render(data:m, template:'demo/sql/index.html')
}
