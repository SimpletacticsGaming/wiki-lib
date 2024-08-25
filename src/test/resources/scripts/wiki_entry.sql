INSERT INTO wiki_topic(id, topic, child_id)
VALUES (11, 'Thema 1', ARRAY [14]);
INSERT INTO wiki_entry(id, headline, body)
VALUES (14, 'Eintrag 1', '<p>Test</p>');
INSERT INTO wiki_topic(id, topic, child_id)
VALUES (8, 'Topic with child delete', ARRAY [7]);
INSERT INTO wiki_entry(id, headline, body)
VALUES (7, 'Eintrag to delete 3', '<p>Test</p>');
INSERT INTO wiki_entry(id, headline, body)
VALUES (15, 'Eintrag 2', '<p>Test</p>');