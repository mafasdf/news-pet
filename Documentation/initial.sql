BEGIN;
CREATE TABLE "feed_feed" (
    "id" integer NOT NULL PRIMARY KEY,
    "url" varchar(4000) NOT NULL UNIQUE,
    "title" varchar(255) NOT NULL,
    "description" text NULL,
    "subscriber_id" integer NOT NULL REFERENCES "auth_user" ("id"),
    "last_crawled" datetime NOT NULL
)
;
CREATE TABLE "feed_feeditem" (
    "id" integer NOT NULL PRIMARY KEY,
    "date_added" datetime NOT NULL,
    "title" varchar(255) NOT NULL,
    "author" varchar(255) NOT NULL,
    "body" text NOT NULL,
    "link" varchar(200) NOT NULL,
    "opinion" integer NOT NULL, -- default to 0
    "category_id" integer NOT NULL,
    "feed_id" integer NOT NULL REFERENCES "feed_feed" ("id"),
    "was_viewed" bool NOT NULL -- default to "false"
)
;
CREATE TABLE "feed_category" (
    "id" integer NOT NULL PRIMARY KEY,
    "name" varchar(63) NOT NULL,
    "is_trash" bool NOT NULL,
    "owner_id" integer NOT NULL REFERENCES "auth_user" ("id")
)
;
CREATE TABLE "feed_trainingset" (
    "id" integer NOT NULL PRIMARY KEY,
    "name" varchar(255) NOT NULL,
    "path" text NOT NULL
)
;
CREATE TABLE "auth_user" (
    "id" integer NOT NULL PRIMARY KEY,
    "username" varchar(30) NOT NULL UNIQUE,
    "first_name" varchar(30) NOT NULL,
    "last_name" varchar(30) NOT NULL,
    "email" varchar(75) NOT NULL,
    "password" varchar(128) NOT NULL,
    "is_staff" bool NOT NULL,
    "is_active" bool NOT NULL,
    "is_superuser" bool NOT NULL,
    "last_login" datetime NOT NULL,
    "date_joined" datetime NOT NULL
)
;
COMMIT;

CREATE TABLE Classifier
(
ID int NOT NULL,
serializedClassifier longblob,
CONSTRAINT PRIMARY KEY USING HASH (ID),
CONSTRAINT FOREIGN KEY (ID) REFERENCES auth_user(ID)
);
