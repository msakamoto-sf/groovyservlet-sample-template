forward_base = '/WEB-INF/groovy/demo/urlmap'
urlmap = [
["/item/@id@", [id: /(\d+)/], '/item.groovy'],
['/list/@year@/@mon@/@day@/', [year: /(\d{4})/, mon: /(\d{2})/, day: /(\d{2})/], '/list.groovy'],
['/search/@query@', [query: /(\w+)/], '/search.groovy'],
]
nomap = '/WEB-INF/groovy/demo/urlmap/index.groovy'
verbose_log = true
