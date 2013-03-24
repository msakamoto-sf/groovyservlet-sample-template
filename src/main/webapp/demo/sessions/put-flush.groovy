import gsst.*

new ToolKit(request, response, context).serve {

String sid = sess.start()
sess.addFlush('Message - <b>1</b>')
sess.addFlush('Message - 2')
sess.addFlush('日本語メッセージ')

redirect(url('/demo/sessions/show-flush.groovy'))
}
