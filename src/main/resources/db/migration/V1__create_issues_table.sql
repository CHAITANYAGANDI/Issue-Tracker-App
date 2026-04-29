CREATE TABLE issue (

   id BIGSERIAL PRIMARY KEY,

   title VARCHAR(100) NOT NULL,
   description VARCHAR(500) NOT NULL,

   status VARCHAR(50) NOT NULL,
   priority VARCHAR(50) NOT NULL,

   due_date DATE,

   assignee VARCHAR(100),
   reporter VARCHAR(100),

   created_at TIMESTAMP,
   updated_at TIMESTAMP
);