package de.simpletactics.wiki.lib.service.port;

import java.util.List;
import java.util.Map;

public interface WikiEntryPort {

  //gibt alle mit der übergebenen ID verlinkten Topic-Einträge zurück
  List<Map<String, Object>> getTopics(int id);

  //gibt alle mit der übergebenen ID verlinkten Content-Einträge zurück
  List<Map<String, Object>> getContents(int id);

  //gibt die ID, den Titel (Topic), den Inhalt (Content) und den Type eines DB-Eintrages zurück
  List<Map<String, Object>> getContent(int id);

  //gibt die ID und den Titel (Topic) eines DB-Eintrages zurück
  List<Map<String, Object>> getTopic(int id);

  //Aktuallisiert einen Content-Eintrag in der DB
  void updateContent(String topic, String content, int id);

  //Aktuallisiert einen Topic-Eintrag in der DB
  void updateTopic(String topic, int id);

  //löscht einen Content-Eintrag aus der DB und enfernt dessen Verknüpfungen
  String deleteContent(int id);

  //löscht einen Topic-Eintrag aus der DB sowie alle mit dem Topic verknüpften Topic- und Content-Einträge recursiv und entfernt deren Verknüpfungen
  String deleteTopic(int id);

  //sichert einen DB-Eintrag in der wiki_deleted Tabelle. Wird von Delete-Methoden aufgerufen.
  void createDeleted(String topid, int id, String topic, String content, String type);

  //Methode legt neue ID in DB-Tabelle "wiki" zusammen mit dem übergebenen Eintrags-Typ an und gibt diese zurück.
  int newId(String type);

  //Methode legt eine neue Verknüpfung zweier DB-Einträge an
  void linkIds(int topid, int subid);

}
