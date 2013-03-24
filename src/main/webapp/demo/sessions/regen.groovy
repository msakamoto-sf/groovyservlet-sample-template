import gsst.*

new ToolKit(request, response, context).serve {

String old_sid = sess.start()
sess.invalidate()
String new_sid = sess.start()

Integer c = sess.get('countup', 0)
def m = [:]
m['title'] = 'Count-Up Integer in Session.'
m['old_sid'] = old_sid
m['new_sid'] = new_sid
m['countup'] = c

render(data:m, template:'demo/sessions/regen.html')
}
