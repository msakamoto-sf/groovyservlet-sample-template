import gsst.*

new ToolKit(request, response, context).serve {

def m = [:]
m['title'] = 'POST Form Demo(x-www-form-urlencoded)'

def kmv_g = sreqw.GET.all()
kmv_g.each {
    log.debug('------------------------')
    log.debug(it.key)
    def lists = it.value
    lists.each { it2 ->
        log.debug(it2)
    }
}
def kmv_p = sreqw.POST.all()
kmv_p.each {
    log.debug('------------------------')
    log.debug(it.key)
    def lists = it.value
    lists.each { it2 ->
        log.debug(it2)
    }
}

render(data:m, template:'demo/forms/post.html')
}
