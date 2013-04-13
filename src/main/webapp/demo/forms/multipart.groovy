import gsst.*
import org.apache.commons.fileupload.FileItem

new ToolKit(request, response, context).serve {

def m = [:]
m['title'] = 'File Upload Form Demo(multipart/form-data)'

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

def files = []
sreqw.FILE.all().each { kvm ->
    pname = kvm.key
    def values = []
    kvm.value.each { fileItem ->
        def display = [:]
        display.content_type = fileItem.getContentType()
        display.size = fileItem.getSize()
        display.content_body = '(text or unknown octet binary)'
        if (display.content_type.contains('image/')) {
            def bytes = fileItem.get() as byte[]
            display.content_body = sprintf('<img src="data:%s;base64,%s">', display.content_type, bytes.encodeBase64())
        }
        values << display
    }
    files << ['key': kvm.key, 'values': values]
}
m['files'] = files

render(data:m, template:'demo/forms/multipart.html')
}
