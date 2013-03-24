import gsst.*

new ToolKit(request, response, context).serve {

String sid = sess.start()
sess.invalidate()

def m = [:]
m['title'] = 'Clear Session.'
m['session_id'] = sid

render(data:m, template:'demo/sessions/clear.html')
}
