UPDATE tasks
SET title = title || '_' || id
WHERE title IN (
  SELECT title
  FROM tasks
  GROUP BY title
  HAVING COUNT(*) > 1
);

ALTER TABLE tasks
ADD CONSTRAINT unique_title UNIQUE (title);