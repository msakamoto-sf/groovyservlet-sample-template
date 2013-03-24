import gsst.*

new ToolKit(request, response, context).serve {

String sid = sess.start()

def m = [:]
m['title'] = 'Get and Clear Flush Messages.'
m['session_id'] = sid
m['flush'] = sess.getFlush()
sess.clearFlush()

render(data:m, template:'demo/sessions/show-flush.html')
}
