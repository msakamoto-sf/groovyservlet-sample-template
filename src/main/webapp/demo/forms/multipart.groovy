import gsst.*

new ToolKit(request, response, context).serve {

def m = [:]
m['title'] = 'File Upload Form Demo(multipart/form-data)'

render(data:m, template:'demo/forms/multipart.html')
}
