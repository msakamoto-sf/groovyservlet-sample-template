import gsst.*

new ToolKit(request, response, context).serve {

def m = [:]
m['title'] = 'POST Form Demo(x-www-form-urlencoded)'

m['dummy_url_query'] = buildQueryString([
    ['var1', 'val1'],
    ['var2', '日本語'],
    ['日本語キー', '日本語値'],
    ['arr1', 'el1', 'el2'],
    ['日本語配列', '日本語要素1', '日本語要素2'],
    ['no-value'],
    [], /* ignored */
])

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
