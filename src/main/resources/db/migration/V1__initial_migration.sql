CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255),
    description TEXT,
    status VARCHAR(50) CHECK (status IN ('PENDING', 'IN_PROGRESS', 'DONE')),
    due_date TIMESTAMP
);