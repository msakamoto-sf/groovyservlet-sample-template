import gsst.*

new ToolKit(request, response, context).serve {

def m = [:]
m['title'] = 'Context Attributes Count-Up Demo'

def c = context.getAttribute('demo.context.countup') as Integer
if (c) {
    c++
} else {
    c = 1;
}
context.setAttribute('demo.context.countup', c)
m['count'] = c

render(data:m, template:'demo/context/countup.html')
}
