import gsst.*

new ToolKit(request, response, context).serve {

def m = [:]
m['title'] = 'POST Form Demo(x-www-form-urlencoded)'

m['requestBody'] = sreqw.getRequestBodyAsText()

def gets = []
sreqw.GET.all().each { kvm ->
    pname = kvm.key
    def values = []
    kvm.value.each {
        values << it
    }
    gets << ['key': kvm.key, 'values': values]
}
m['gets'] = gets

def posts = []
sreqw.POST.all().each { kvm ->
    pname = kvm.key
    def values = []
    kvm.value.each {
        values << it
    }
    posts << ['key': kvm.key, 'values': values]
}
m['posts'] = posts

render(data:m, template:'demo/forms/post.html')
}
