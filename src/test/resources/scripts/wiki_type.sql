INSERT INTO wiki_topic(id, topic, child_id)
VALUES (11, 'Thema 1', ARRAY [14]);
INSERT INTO wiki_entry(id, headline, body)
VALUES (14, 'Eintrag 1', '<p>Test</p>');
INSERT INTO wiki_poll (id, question, description, end_date, ended, data)
VALUES (5, 'Frage 1', 'Beschreibung 1', null, 'false',
        '[{"votes": [{"userId": "1", "date": "2023-10-21T00:00", "option": "FALSE"}], "pollOption": {"text": "test1", "uuid": "egal1"}}, {"votes": [{"userId": "2", "date": "2023-10-22T00:00", "option": "TRUE"}], "pollOption": {"text": "test2", "uuid": "egal2"}}]');