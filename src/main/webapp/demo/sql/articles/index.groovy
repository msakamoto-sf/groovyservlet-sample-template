import gsst.*
import groovy.sql.*

new ToolKit(request, response, context).serve {
def m = [:]
m['title'] = 'Articles'

def articles = []
Sql sql = dbcon.borrow()
sql.eachRow('select id, title, updated_at from articles') { row ->
    articles << [
        id: row.id,
        title: row.title,
        updated_at: row.updated_at,
    ]
}
m['articles'] = articles
render(data:m, template:'demo/sql/articles/list.html')
}
