import gsst.*

new ToolKit(request, response, context).serve {

String textdata = loadResourceAsString('/WEB-INF/data/text.txt')
String ct = 'text/plain; charset='
ct += request.getParameter('cs') ?: 'UTF-8'

response.setContentType(ct)
out << textdata
}

