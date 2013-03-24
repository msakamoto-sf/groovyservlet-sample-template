import gsst.*

new ToolKit(request, response, context).serve {

String sid = sess.start()

def m = [:]
m['title'] = 'Session Started.'
m['session_id'] = sid

render(data:m, template:'demo/sessions/start.html')
}
