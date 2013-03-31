defaultEncoding = 'UTF-8'
defaultContentType = 'text/html; charset=UTF-8'
applicationTitle = 'groovyservlet-sample-template'
renderer {
    templateDir = '/WEB-INF/templates/'
    defaultLayout = 'layout.html'
}
datasources {
    dev {
        url = 'jdbc:h2:mem:test'
        username = 'sa'
        password = 'sa'
    }
    setup = [
'''
CREATE TABLE USERS(
id integer primary key auto_increment,
login varchar not null,
password varchar not null,
name varchar not null,
email varchar not null,
created_at timestamp default CURRENT_TIMESTAMP(),
updated_at timestamp default CURRENT_TIMESTAMP()
)
''',
'''
INSERT INTO USERS(login, password, name, email) VALUES
('user1', 'password', 'user1', 'user1@example.com'),
('user2', 'password', 'user2', 'user2@example.com'),
('user3', 'password', 'user3', 'user3@example.com')
''',
'''
CREATE TABLE ARTICLES(
id integer primary key auto_increment,
title varchar not null,
body varchar,
user_id integer not null,
created_at timestamp default CURRENT_TIMESTAMP(),
updated_at timestamp default CURRENT_TIMESTAMP()
)
''',
'''
INSERT INTO ARTICLES(title, body, user_id) VALUES
('title1', 'content1', 1),
('title2', 'content2', 2),
('title3', 'content3', 3)
''',
    ]
}
