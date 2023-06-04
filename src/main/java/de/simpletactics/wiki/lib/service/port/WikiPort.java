package de.simpletactics.wiki.lib.service.port;

public interface WikiPort {

  String getTopicsAsJson(int id, String username);

  <T> T getTopicsAsObject(int id, String username);

  void updateEntry(String topic, String content, int id);

  void updateTopic(String topic, int id);

  int addEntry(String topic, String content, String threadId);

  int addTopic(String id, String topic);

  void deleteTopic(int id);

  void deleteEntry(int id);

}
