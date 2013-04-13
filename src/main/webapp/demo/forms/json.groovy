import gsst.*
import groovy.json.*

new ToolKit(request, response, context).serve {

def m = [:]

def json = new JsonSlurper().parseText(sreqw.getRequestBodyAsText())
HoganRenderer hr = context.getAttribute(HoganRenderer.CONTEXT_ATTR_HR_KEY)

m.key1 = json.key1
m.key2 = json.key2
m.key3 = []
json.key3.each {
    m.key3 << [name: it.key, value: it.value]
}

def template_src = """
<dl>
<dt>key1</dt><dd>{{key1}}</dd>
<dt>key2</dt>{{#key2}}<dd>{{.}}</dd>{{/key2}}
<dt>key3</dt>{{#key3}}<dd>{{name}} - {{value}}</dd>{{/key3}}
</dl>
"""

response.setContentType('text/html; charset=' + config.defaultEncoding)
out << hr.render(m, 'demo/forms/json.groovy__pseudo_key__', template_src)
}
