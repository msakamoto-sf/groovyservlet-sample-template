import gsst.*
import groovy.sql.*

new ToolKit(request, response, context).serve {
def m = [:]
m['title'] = 'Users'

def users = []
Sql sql = dbcon.borrow()
sql.eachRow('select id, login, name, email, updated_at from users') { row ->
    users << [
        id: row.id,
        login: row.login,
        name: row.name,
        email: row.email,
        updated_at: row.updated_at,
    ]
}
m['users'] = users
render(data:m, template:'demo/sql/users/list.html')
}
