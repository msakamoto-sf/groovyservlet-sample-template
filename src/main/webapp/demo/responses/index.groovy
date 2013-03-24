import gsst.*

new ToolKit(request, response, context).serve {
def m = [:]
m['title'] = 'Various Output'

render(data:m, template:'demo/responses/index.html')
}
