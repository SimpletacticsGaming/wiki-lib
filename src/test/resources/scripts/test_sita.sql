SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE TYPE wiki_type AS ENUM (
    'TOPIC',
    'ENTRY',
    'POLL'
);

CREATE TABLE wiki (
    id integer NOT NULL,
    type wiki_type
);


CREATE TABLE IF NOT EXISTS wiki_entry
(
    headline text COLLATE pg_catalog."default",
    body text COLLATE pg_catalog."default"
)
    INHERITS (wiki)
TABLESPACE pg_default;

CREATE SEQUENCE wiki_id_seq
    AS integer
    START WITH 21
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

    ALTER SEQUENCE wiki_id_seq OWNED BY wiki.id;

CREATE TABLE IF NOT EXISTS wiki_topic
(
    topic text COLLATE pg_catalog."default",
    child_id integer[] DEFAULT ARRAY[]::integer[]
)
    INHERITS (wiki)
TABLESPACE pg_default;

CREATE TABLE IF NOT EXISTS wiki_poll
(
    question text COLLATE pg_catalog."default",
    description text COLLATE pg_catalog."default",
    end_date date,
    ended boolean,
    data jsonb
)
    INHERITS (wiki)
TABLESPACE pg_default;


ALTER TABLE ONLY wiki ALTER COLUMN id SET DEFAULT nextval('wiki_id_seq'::regclass);
ALTER TABLE ONLY wiki_entry ALTER COLUMN id SET DEFAULT nextval('wiki_id_seq'::regclass);
ALTER TABLE ONLY wiki_entry ALTER COLUMN type SET DEFAULT 'ENTRY'::wiki_type;
ALTER TABLE ONLY wiki_poll ALTER COLUMN id SET DEFAULT nextval('wiki_id_seq'::regclass);
ALTER TABLE ONLY wiki_poll ALTER COLUMN type SET DEFAULT 'POLL'::wiki_type;
ALTER TABLE ONLY wiki_topic ALTER COLUMN id SET DEFAULT nextval('wiki_id_seq'::regclass);
ALTER TABLE ONLY wiki_topic ALTER COLUMN type SET DEFAULT 'TOPIC'::wiki_type;

INSERT INTO wiki_topic(id, topic, child_id) VALUES (0, 'Startseite', ARRAY[11, 12, 13]);
INSERT INTO wiki_topic(id, topic, child_id) VALUES (11, 'Thema 1', ARRAY[14]);
INSERT INTO wiki_topic(id, topic) VALUES (12, 'Thema 2');
INSERT INTO wiki_topic(id, topic) VALUES (9, 'Thema 9');
INSERT INTO wiki_topic(id, topic, child_id) VALUES (10, 'Thema 10', ARRAY[9]);
INSERT INTO wiki_topic(id, topic, child_id) VALUES (13, 'Thema 3', ARRAY[5]);
INSERT INTO wiki_topic(id, topic, child_id) VALUES (4, 'Thema 4', ARRAY[15]);
INSERT INTO wiki_entry(id, headline, body) VALUES (14, 'Eintrag 1', '<p>Test</p>');
INSERT INTO wiki_entry(id, headline, body) VALUES (15, 'Eintrag 2', '<p>Test</p>');
insert into wiki_poll (id, question, description, end_date, ended, data)
    values(5, 'Frage 1', 'Beschreibung 1', null, 'false', '[{"votes": [{"userId": "1", "date": "2023-10-21T00:00", "option": "FALSE"}], "pollOption": {"text": "test1", "uuid": "egal1"}}, {"votes": [{"userId": "2", "date": "2023-10-22T00:00", "option": "TRUE"}], "pollOption": {"text": "test2", "uuid": "egal2"}}]');

ALTER TABLE ONLY wiki
    ADD CONSTRAINT idx_16440_primary PRIMARY KEY (id);

REVOKE USAGE ON SCHEMA public FROM PUBLIC;