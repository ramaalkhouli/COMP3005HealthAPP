CREATE TABLE students (
    student_id SERIAL Primary Key,
    first_name Text Not Null,
    last_name Text Not Null,
    email Text Not Null Unique,
    enrollment_date Date
);
