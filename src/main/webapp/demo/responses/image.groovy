import gsst.*

new ToolKit(request, response, context).serve {
def image = loadResourceAsByte('/WEB-INF/data/hello.png')
response.setContentType('image/png')
sout << image
sout.flush()
}

