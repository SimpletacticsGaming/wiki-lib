INSERT INTO wiki_topic(id, topic, child_id)
VALUES (0, 'Startseite', ARRAY [11, 12, 13]);
INSERT INTO wiki_topic(id, topic, child_id)
VALUES (11, 'Thema 1', ARRAY [14]);
INSERT INTO wiki_topic(id, topic)
VALUES (24, 'Thema for poll');
INSERT INTO wiki_topic(id, topic)
VALUES (12, 'Thema 2');
INSERT INTO wiki_topic(id, topic)
VALUES (9, 'Thema 9');
INSERT INTO wiki_topic(id, topic, child_id)
VALUES (10, 'Thema 10', ARRAY [9]);
INSERT INTO wiki_topic(id, topic, child_id)
VALUES (13, 'Thema 3', ARRAY [5]);
INSERT INTO wiki_topic(id, topic, child_id)
VALUES (2, 'Topic to delete', ARRAY [17, 19]);
INSERT INTO wiki_topic(id, topic, child_id)
VALUES (20, 'Topic to delete 2', ARRAY [2, 21]);
INSERT INTO wiki_topic(id, topic, child_id)
VALUES (8, 'Topic with child delete', ARRAY [7]);
INSERT INTO wiki_topic(id, topic)
VALUES (3, 'Topic to delete 2');
INSERT INTO wiki_topic(id, topic, child_id)
VALUES (4, 'Thema 4', ARRAY [15]);
INSERT INTO wiki_entry(id, headline, body)
VALUES (14, 'Eintrag 1', '<p>Test</p>');
insert into wiki_poll (id, question, description, end_date, ended, data)
values (5, 'Frage 1', 'Beschreibung 1', null, 'false',
        '[{"votes": [{"userId": "1", "date": "2023-10-21T00:00", "option": "FALSE"}], "pollOption": {"text": "test1", "uuid": "egal1"}}, {"votes": [{"userId": "2", "date": "2023-10-22T00:00", "option": "TRUE"}], "pollOption": {"text": "test2", "uuid": "egal2"}}]');
INSERT INTO wiki_entry(id, headline, body)
VALUES (17, 'Eintrag to delete', '<p>Test</p>');
INSERT INTO wiki_entry(id, headline, body)
VALUES (19, 'Eintrag for topic delete', '<p>Test</p>');
INSERT INTO wiki_entry(id, headline, body)
VALUES (21, 'Eintrag to delete 21', '<p>Test</p>');
INSERT INTO wiki_entry(id, headline, body)
VALUES (7, 'Eintrag to delete 3', '<p>Test</p>');
