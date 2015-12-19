# --- !Ups

CREATE TABLE enrollments (
  id CHAR(36) PRIMARY KEY,
  topic_revision_id CHAR(36) NOT NULL,
  student_id CHAR(36) NOT NULL,
  CONSTRAINT fk_enrollment_topic_revision_id FOREIGN KEY (topic_revision_id) REFERENCES topic_revisions(id)
);

ALTER TABLE critiques ADD score INT NOT NULL;

ALTER TABLE projects ADD enrollment_id CHAR(36);

ALTER TABLE projects ADD CONSTRAINT fk_project_enrollment_id FOREIGN KEY (enrollment_id) REFERENCES enrollments(id);

# --- !Downs

DROP TABLE enrollments;

ALTER TABLE critiques DROP score;

ALTER TABLE projects DROP FOREIGN KEY fk_project_enrollment_id;

ALTER TABLE projects DROP enrollment_id;