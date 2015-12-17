# --- !Ups

CREATE TABLE users (
  id CHAR(36) PRIMARY KEY,
  first_name VARCHAR(30) NOT NULL,
  last_name VARCHAR(30) NOT NULL,
  email VARCHAR(30) NOT NULL,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE projects (
  id CHAR(36) PRIMARY KEY,
  owner_id CHAR(36) NOT NULL,
  CONSTRAINT fk_project_owner_id FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE project_drafts (
  id CHAR(36) PRIMARY KEY,
  revision_number INT NOT NULL,
  project_id CHAR(36) NOT NULL,
  content LONGTEXT NOT NULL,
  CONSTRAINT fk_draft_project_id FOREIGN KEY (project_id) REFERENCES projects(id)
);

CREATE TABLE peer_reviews (
  id CHAR(36) PRIMARY KEY,
  owner_id CHAR(36) NOT NULL,
  project_draft_id CHAR(36) NOT NULL,
  content LONGTEXT NOT NULL,
  CONSTRAINT fk_peer_review_owner_id FOREIGN KEY (owner_id) REFERENCES users(id),
  CONSTRAINT fk_peer_review_project_draft_id FOREIGN KEY (project_draft_id) REFERENCES project_drafts(id)
);

CREATE TABLE critiques (
  id CHAR(36) PRIMARY KEY,
  owner_id CHAR(36) NOT NULL,
  project_draft_id CHAR(36) NOT NULL,
  content LONGTEXT NOT NULL,
  CONSTRAINT fk_critique_owner_id FOREIGN KEY (owner_id) REFERENCES users(id),
  CONSTRAINT fk_critique_project_draft_id FOREIGN KEY (project_draft_id) REFERENCES project_drafts(id)
);

CREATE TABLE microdegrees (
  id CHAR(36) PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  owner_id CHAR(36),
  CONSTRAINT fk_microdegree_owner_id FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE microdegree_revision_proposals (
  id CHAR(36) PRIMARY KEY,
  owner_id CHAR(36) NOT NULL,
  microdegree_id CHAR(36) NOT NULL,
  new_revision_number INT NOT NULL,
  content LONGTEXT NOT NULL,
  CONSTRAINT fk_microdegree_revision_proposal_owner_id FOREIGN KEY (owner_id) REFERENCES users(id),
  CONSTRAINT fk_microdegree_revision_proposal_microdegree_id FOREIGN KEY (microdegree_id) REFERENCES microdegrees(id)
);

CREATE TABLE microdegree_revisions (
  id CHAR(36) PRIMARY KEY,
  revision_number INT NOT NULL,
  microdegree_id CHAR(36) NOT NULL,
  proposal_id CHAR(36) NOT NULL,
  CONSTRAINT fk_microdegree_revision_microdegree_id FOREIGN KEY (microdegree_id) REFERENCES microdegrees(id),
  CONSTRAINT fk_microdegree_revision_proposal_id FOREIGN KEY (proposal_id) REFERENCES microdegree_revision_proposals(id)
);

CREATE TABLE topics (
  id CHAR(36) PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  owner_id CHAR(36),
  CONSTRAINT fk_topic_owner_id FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE topic_revision_proposals (
  id CHAR(36) PRIMARY KEY,
  owner_id CHAR(36) NOT NULL,
  topic_id CHAR(36) NOT NULL,
  new_revision_number INT NOT NULL,
  content LONGTEXT NOT NULL,
  CONSTRAINT fk_topic_revision_proposal_owner_id FOREIGN KEY (owner_id) REFERENCES users(id),
  CONSTRAINT fk_topic_revision_proposal_topic_id FOREIGN KEY (topic_id) REFERENCES topics(id)
);

CREATE TABLE topic_revisions (
  id CHAR(36) PRIMARY KEY,
  revision_number INT NOT NULL,
  topic_id CHAR(36) NOT NULL,
  proposal_id CHAR(36) NOT NULL,
  CONSTRAINT fk_topic_revision_topic_id FOREIGN KEY (topic_id) REFERENCES topics(id),
  CONSTRAINT fk_topic_revision_proposal_id FOREIGN KEY (proposal_id) REFERENCES topic_revision_proposals(id)
);

CREATE TABLE topic_requirements (
  id CHAR(36) PRIMARY KEY,
  microdegree_revision_proposal_id CHAR(36) NOT NULL,
  topic_id CHAR(36) NOT NULL,
  revision_number INT NOT NULL,
  CONSTRAINT fk_topic_requirement_microdegree_revision_proposal FOREIGN KEY (microdegree_revision_proposal_id) REFERENCES microdegree_revision_proposals(id),
  CONSTRAINT fk_topic_requirement_topic_id FOREIGN KEY (topic_id) REFERENCES topics(id)
);

# --- !Downs

DROP TABLE users;

DROP TABLE projects;

DROP TABLE project_drafts;

DROP TABLE peer_reviews;

DROP TABLE critiques;

DROP TABLE microdegrees;

DROP TABLE microdegree_revision_proposals;

DROP TABLE microdegree_revisions;

DROP TABLE topics;

DROP TABLE topic_revision_proposals;

DROP TABLE topic_revisions;

DROP TABLE topic_requirements;