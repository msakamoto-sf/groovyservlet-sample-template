import gsst.*

new ToolKit(request, response, context).serve {

String sid = sess.start()

Integer c = sess.get('countup', 0)
c++
sess.set('countup', c)

def m = [:]
m['title'] = 'Count-Up Integer in Session.'
m['session_id'] = sid
m['countup'] = c

render(data:m, template:'demo/sessions/countup.html')
}
