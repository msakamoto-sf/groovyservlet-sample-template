import gsst.*

new ToolKit(request, response, context).serve {
def image = loadResourceAsByte('/WEB-INF/data/hello.png')
response.setContentType('image/png')

String dt = new Date().format('yyyyMMdd-HHmmss')
String fname = 'hello-' + dt + '.png'
response.addHeader('Content-Disposition', 'attachment; filename="' + fname + '"')

sout << image
sout.flush()
}

