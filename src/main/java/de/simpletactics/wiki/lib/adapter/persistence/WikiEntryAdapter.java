package de.simpletactics.wiki.lib.adapter.persistence;

import de.simpletactics.wiki.lib.service.port.WikiEntryPort;
import de.simpletactics.wiki.lib.service.port.WikiRightsPort;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class WikiEntryAdapter implements WikiEntryPort {

  private final JdbcTemplate jdbc;
  @Autowired
  private WikiRightsPort wikiRightsPort;

  public WikiEntryAdapter(JdbcTemplate jdbcTemplate) {
    this.jdbc = jdbcTemplate;
  }

  //gibt alle mit der übergebenen ID verlinkten Topic-Einträge zurück
  @Override
  public List<Map<String, Object>> getTopics(int id) {
    List<Map<String, Object>> topics;
    String sql =
        "SELECT w.id, w.topic FROM wiki AS w INNER JOIN wiki_link AS wl ON w.id = wl.subid WHERE wl.topid = '"
            + id + "' AND w.type = '1' ORDER BY wl.order;";
    topics = jdbc.queryForList(sql);

    return topics;
  }

  //gibt alle mit der übergebenen ID verlinkten Content-Einträge zurück
  @Override
  public List<Map<String, Object>> getContents(int id) {
    List<Map<String, Object>> contents;
    String sql =
        "SELECT w.id, w.topic, w.content, w.type FROM wiki AS w INNER JOIN wiki_link AS wl ON w.id = wl.subid WHERE wl.topid = '"
            + id + "' AND w.type = '2' ORDER BY wl.order;";
    contents = jdbc.queryForList(sql);

    return contents;
  }

  //gibt die ID, den Titel (Topic), den Inhalt (Content) und den Type eines DB-Eintrages zurück
  @Override
  public List<Map<String, Object>> getContent(int id) {
    List<Map<String, Object>> content;
    String sql = "SELECT id, topic, content, type FROM wiki WHERE id = '" + id + "';";
    content = jdbc.queryForList(sql);
    return content;
  }

  //gibt die ID und den Titel (Topic) eines DB-Eintrages zurück
  @Override
  public List<Map<String, Object>> getTopic(int id) {
    List<Map<String, Object>> topic;
    String sql =
        "SELECT w.id, w.topic FROM wiki AS w INNER JOIN wiki_link AS wl ON w.id = wl.topid WHERE wl.subid = '"
            + id + "';";
    topic = jdbc.queryForList(sql);
    return topic;
  }

  //Aktuallisiert einen Content-Eintrag in der DB
  @Override
  public void updateContent(String topic, String content, int id) {
    jdbc.execute(
        "UPDATE wiki SET topic = '" + topic + "', content = '" + content + "' WHERE id = " + id
            + ";");
  }

  //Aktuallisiert einen Topic-Eintrag in der DB
  @Override
  public void updateTopic(String topic, int id) {
    jdbc.execute("UPDATE wiki SET topic = '" + topic + "' WHERE id = " + id + ";");
  }

  //löscht einen Content-Eintrag aus der DB und enfernt dessen Verknüpfungen
  @Override
  public String deleteContent(int id) {
    List<Map<String, Object>> topidList = jdbc.queryForList(
        "SELECT topid FROM wiki_link WHERE subid = '" + id + "';");
    String topid = topidList.get(0).get("topid").toString();
    List<Map<String, Object>> deleted = getContent(id);
    createDeleted(topid, id, deleted.get(0).get("topic").toString(),
        deleted.get(0).get("content").toString(), deleted.get(0).get("type").toString());
    wikiRightsPort.deleteWikiAllRights(id);
    wikiRightsPort.deleteWikiDefaultRead(id);
    jdbc.execute("DELETE FROM wiki_link WHERE subid=" + id + ";");
    jdbc.execute("DELETE FROM wiki WHERE id=" + id + ";");

    return topid;
  }

  //löscht einen Topic-Eintrag aus der DB sowie alle mit dem Topic verknüpften Topic- und Content-Einträge recursiv und entfernt deren Verknüpfungen
  @Override
  public String deleteTopic(int id) {
    List<Map<String, Object>> topidList = jdbc.queryForList(
        "SELECT topid FROM wiki_link WHERE subid = '" + id + "';");
    String topid = topidList.get(0).get("topid").toString();

    List<Map<String, Object>> topics = getTopics(id);
    List<Map<String, Object>> contents = getContents(id);
    List<Map<String, Object>> topic = getContent(id);
    createDeleted(topid, id, topic.get(0).get("topic").toString(), null,
        topic.get(0).get("type").toString());
    for (Map<String, Object> stringObjectMap : topics) {
      deleteTopic(Integer.parseInt(stringObjectMap.get("id").toString()));
    }
    for (Map<String, Object> content : contents) {
      createDeleted(String.valueOf(id), Integer.parseInt(content.get("id").toString()),
          content.get("topic").toString(), content.get("content").toString(),
          content.get("type").toString());
      wikiRightsPort.deleteWikiAllRights(Integer.parseInt(content.get("id").toString()));
      wikiRightsPort.deleteWikiDefaultRead(Integer.parseInt(content.get("id").toString()));
    }

    wikiRightsPort.deleteWikiAllRights(id);
    wikiRightsPort.deleteWikiDefaultRead(id);
    jdbc.execute(
        "DELETE w FROM wiki AS w INNER JOIN wiki_link AS wl ON w.id = wl.subid WHERE wl.topid = '"
            + id + "' AND w.type = '2';");
    jdbc.execute("DELETE FROM wiki_link WHERE topid=" + id + ";");
    jdbc.execute("DELETE FROM wiki WHERE id = '" + id + "';");
    jdbc.execute("DELETE FROM wiki_link WHERE subid = '" + id + "';");

    return topid;
  }

  //sichert einen DB-Eintrag in der wiki_deleted Tabelle. Wird von Delete-Methoden aufgerufen.
  @Override
  public void createDeleted(String topid, int id, String topic, String content, String type) {
    List<Map<String, Object>> newDeletedId = jdbc.queryForList(
        "SELECT id + 1 as id FROM wiki_deleted ORDER BY id DESC LIMIT 1;");
    String deletedId;
    if (newDeletedId.isEmpty()) {
      deletedId = "1";
    } else {
      deletedId = newDeletedId.get(0).get("id").toString();
    }
    jdbc.execute("INSERT INTO wiki_deleted (id, linked_to_id, deleted_id, topic, content, type, delete_date)" +
      "values('" + deletedId + "', '" + topid + "', '" + id + "', '" + topic + "', '" + content + "', '" + 
      type + "', (SELECT now()));");
  }

  //Methode legt neue ID in DB-Tabelle "wiki" zusammen mit dem übergebenen Eintrags-Typ an und gibt diese zurück.
  @Override
  public int newId(String type) {
    jdbc.execute("START TRANSACTION;");
    List<Map<String, Object>> newId = jdbc.queryForList(
        "SELECT id + 1 as id FROM wiki ORDER BY id DESC LIMIT 1;");
    int iNewId = Integer.parseInt(newId.get(0).get("id").toString());
    jdbc.execute("INSERT INTO wiki (id, type) VALUES ('" + iNewId + "', '" + type + "');");
    jdbc.execute("commit;");
    return iNewId;
  }

  //Methode legt eine neue Verknüpfung zweier DB-Einträge an
  @Override
  public void linkIds(int topid, int subid) {
    List<Map<String, Object>> newOrder = jdbc.queryForList(
        "SELECT wl.order + 1 as order FROM wiki_link as wl WHERE topid = '" + topid
            + "' ORDER BY wl.order DESC LIMIT 1;");
    String sNewOrder;
    if (newOrder.isEmpty()) {
      sNewOrder = "1";
    } else {
      sNewOrder = newOrder.get(0).get("order").toString();
    }
    jdbc.execute(
        "INSERT INTO wiki_link (topid, subid, order) values('" + topid + "', '" + subid + "','" + sNewOrder + "');");
  }
}
