CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  email varchar(255) UNIQUE NOT NULL,
  password varchar(255) NOT NULL,
  handle varchar(100) UNIQUE,
  role varchar(20) NOT NULL,
  created_at timestamptz DEFAULT now()
);

CREATE TABLE recipes (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  chef_id uuid REFERENCES users(id),
  title varchar(255) NOT NULL,
  summary text,
  ingredients text,
  steps text,
  labels text,
  status varchar(20) NOT NULL,
  published_at timestamptz,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz
);

CREATE TABLE follows (
  follower_id uuid NOT NULL,
  followee_id uuid NOT NULL,
  created_at timestamptz DEFAULT now(),
  PRIMARY KEY (follower_id, followee_id),
  FOREIGN KEY (follower_id) REFERENCES users(id),
  FOREIGN KEY (followee_id) REFERENCES users(id)
);

CREATE TABLE images (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  recipe_id uuid REFERENCES recipes(id),
  path varchar(1024),
  width int,
  height int,
  processed boolean DEFAULT false
);
