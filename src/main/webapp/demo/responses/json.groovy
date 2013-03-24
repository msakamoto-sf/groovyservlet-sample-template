import gsst.*

new ToolKit(request, response, context).serve {

boolean use_pretty = (request.getParameter('pretty') == 'on')

def m = [
    'key1': [100, 200, 300],
    'key2': ['name':'Bob', 'age':15, 'hobby':'football'],
    'key3': '日本語',
]

renderAsJson(data:m, pretty:use_pretty)
}
