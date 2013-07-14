import gsst.*

new ToolKit(request, response, context).serve {

def m = [:]
m['title'] = 'GET Form Demo'

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
render(data:m, template:'demo/forms/get.html')
}
