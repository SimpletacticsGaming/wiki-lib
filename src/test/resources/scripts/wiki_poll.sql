INSERT INTO wiki_topic(id, topic)
VALUES (24, 'Thema for poll');
INSERT INTO wiki_poll (id, question, description, end_date, ended, data)
values (5, 'Frage 1', 'Beschreibung 1', null, 'false',
        '[{"votes": [{"userId": "1", "date": "2023-10-21T00:00", "option": "FALSE"}], "pollOption": {"text": "test1", "uuid": "egal1"}}, {"votes": [{"userId": "2", "date": "2023-10-22T00:00", "option": "TRUE"}], "pollOption": {"text": "test2", "uuid": "egal2"}}]');
INSERT INTO wiki_topic(id, topic, child_id)
VALUES (1, 'Thema for poll 2', ARRAY[2]);
INSERT INTO wiki_poll (id, question, description, end_date, ended, data)
values (2, 'This is a test', 'Test description', null, 'false',
        '[{"votes": [], "pollOption": {"text": "testCase", "uuid": "ddb11436-bdc8-4488-87f6-fsdfsd"}}, {"votes": [], "pollOption": {"text": "testCaseTwo", "uuid": "aaa1436-bdc8-4488-87f6-fsdfsd"}}]');
INSERT INTO wiki_poll (id, question, description, end_date, ended, data)
values (3, 'This is a test', '', null, 'false',
        '[{"votes": [{"userId": "1", "date": "2023-10-21T00:00", "option": "FALSE"}], "pollOption": {"text": "testCase", "uuid": "ddb11436-bdc8-4488-87f6-fsdfsd"}}]');