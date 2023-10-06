package de.simpletactics.wiki.lib.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.simpletactics.wiki.lib.model.WikiType;
import de.simpletactics.wiki.lib.service.port.WikiEntryPort;
import de.simpletactics.wiki.lib.service.port.WikiPort;
import de.simpletactics.wiki.lib.service.port.WikiRightsPort;
import de.simpletactics.wiki.lib.util.WikiMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@SuppressWarnings("unchecked")
public class WikiAdapter implements WikiPort {

  private WikiEntryPort wikiEntryPort;
  private WikiRightsPort wikiRightsPort;
  private Gson gson;

  @PostConstruct
  public void initGsonObject() {
    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
    this.gson = builder.create();
  }

  public String getTopicsAsJson(int id, String username) {

    if (!wikiRightsPort.getWikiBerechtigung(username, id).equals("none")) {
      if (id == 0 || getWikiType(id) == WikiType.THEMENBEREICH) {

        List<Map<String, Object>> filteredTopics = getFilteredTopics(id, username);
        List<Map<String, Object>> filteredContents = getFilteredContents(id, username);

        if (id == 0) {
          return gson
              .toJson(WikiMapper.subjectEntryMapper(id,
                  wikiRightsPort.getWikiBerechtigung(username, id), filteredTopics,
                  filteredContents, List.of(), wikiEntryPort.getTopic(id),
                  getWikiType(id), List.of()));
        } else {
          return gson
              .toJson(WikiMapper.subjectEntryMapper(id,
                  wikiRightsPort.getWikiBerechtigung(username, id), filteredTopics,
                  filteredContents, getFilteredNavTopics(id, username), wikiEntryPort.getTopic(id),
                  getWikiType(id), getFilteredNavContents(id, username)));
        }


      } else if (getWikiType(id) == WikiType.STANDARDEINTRAG) {
        List<Map<String, Object>> content = wikiEntryPort.getContent(id);
        return gson.toJson(
            WikiMapper.wikiEntryMapper(wikiRightsPort.getWikiBerechtigung(username, id), content,
                getFilteredNavTopics(id, username), wikiEntryPort.getTopic(id), getWikiType(id),
                getFilteredNavContents(id, username)));
      }
    }
    return "access denied";
  }


  public <T> T getTopicsAsObject(int id, String username) {

    if (!wikiRightsPort.getWikiBerechtigung(username, id).equals("none")) {
      if (id == 0 || getWikiType(id) == WikiType.THEMENBEREICH) {

        List<Map<String, Object>> filteredTopics = getFilteredTopics(id, username);
        List<Map<String, Object>> filteredContents = getFilteredContents(id, username);

        return (T) WikiMapper.subjectEntryMapper(id,
            wikiRightsPort.getWikiBerechtigung(username, id), filteredTopics,
            filteredContents, getFilteredNavTopics(id, username), wikiEntryPort.getTopic(id),
            getWikiType(id), getFilteredNavContents(id, username));

      } else if (getWikiType(id) == WikiType.STANDARDEINTRAG) {
        List<Map<String, Object>> content = wikiEntryPort.getContent(id);
        return (T) WikiMapper.wikiEntryMapper(wikiRightsPort.getWikiBerechtigung(username, id),
            content,
            getFilteredNavTopics(id, username), wikiEntryPort.getTopic(id), getWikiType(id),
            getFilteredNavContents(id, username));
      }
    }
    return null;
  }

  public void updateEntry(String topic, String content, int id) {
    wikiEntryPort.updateContent(topic, content, id);
  }

  public void updateTopic(String topic, int id) {
    wikiEntryPort.updateTopic(topic, id);
  }

  public int addEntry(String topic, String content, String threadId, WikiType wikiType) {
    int newId = wikiEntryPort.newId(wikiType == WikiType.STANDARDEINTRAG ? "2" : "3");
    wikiEntryPort.linkIds(Integer.parseInt(threadId), newId);
    wikiEntryPort.updateContent(topic, content, newId);
    wikiRightsPort.setWikiRightsCopyOvertopic(newId, Integer.parseInt(threadId));
    return newId;
  }

  public int addTopic(String id, String topic) {
    int newId = wikiEntryPort.newId("1");
    wikiEntryPort.linkIds(Integer.parseInt(id), newId);
    wikiEntryPort.updateTopic(topic, newId);
    wikiRightsPort.setWikiRightsCopyOvertopic(newId, Integer.parseInt(id));
    return newId;
  }

  public void deleteTopic(int id) {
    wikiEntryPort.deleteTopic(id);
  }

  public void deleteEntry(int id) {
    wikiEntryPort.deleteContent(id);
  }

  public WikiType getWikiType(int id) {
    List<Map<String, Object>> type = wikiEntryPort.getContent(id);
    switch (Integer.parseInt(type.get(0).get("type").toString())) {
      case 1:
        return WikiType.THEMENBEREICH;
      case 2:
        return WikiType.STANDARDEINTRAG;
      case 3:
        return WikiType.POLL;
      default:
        return null;
    }

  }

  private List<Map<String, Object>> getFilteredTopics(int id, String username) {
    List<Map<String, Object>> topics = new ArrayList<>();

    List<Map<String, Object>> returnedTopics;
    returnedTopics = wikiEntryPort.getTopics(
        id); //Alle Topics mit id-Verknüpfung in der Datenbank werden in Liste geladen

    //filtert die Einträge aus rTopics, die gelesen werden dürfen und trägt diese in topics ein
    for (Map<String, Object> rTopic : returnedTopics) {
      String rights = wikiRightsPort.getWikiBerechtigung(username,
          Integer.parseInt(rTopic.get("id").toString()));
      if (!rights.equals("none")) {
        topics.add(rTopic);
      }
    }

    return topics;
  }

  private List<Map<String, Object>> getFilteredContents(int id, String username) {
    List<Map<String, Object>> contents = new ArrayList<>();

    List<Map<String, Object>> returnedContents;
    returnedContents = wikiEntryPort.getContents(
        id); //Alle Contents mit id-Verknüpfung in der Datenbank werden in Liste geladen

    //filtert die Einträge aus rContents, die gelesen werden dürfen und trägt diese in contents ein
    for (Map<String, Object> rContent : returnedContents) {
      String rights = wikiRightsPort.getWikiBerechtigung(username,
          Integer.parseInt(rContent.get("id").toString()));
      if (!rights.equals("none")) {
        contents.add(rContent);
      }
    }

    return contents;
  }

  private List<Map<String, Object>> getFilteredNavTopics(int id, String username) {

    List<Map<String, Object>> parentSubject = wikiEntryPort.getTopic(id);
    List<Map<String, Object>> topics = new ArrayList<>();

    List<Map<String, Object>> returnedTopics;
    returnedTopics = wikiEntryPort.getTopics(Integer.parseInt(parentSubject.get(0).get("id")
        .toString())); //Alle Topics mit id-Verknüpfung in der Datenbank werden in Liste geladen

    //filtert die Einträge aus rTopics, die gelesen werden dürfen und trägt diese in topics ein
    for (Map<String, Object> rTopic : returnedTopics) {
      String rights = wikiRightsPort.getWikiBerechtigung(username,
          Integer.parseInt(rTopic.get("id").toString()));
      if (!rights.equals("none")) {
        topics.add(rTopic);
      }
    }

    return topics;
  }

  private List<Map<String, Object>> getFilteredNavContents(int id, String username) {

    List<Map<String, Object>> parentSubject = wikiEntryPort.getTopic(id);
    List<Map<String, Object>> contents = new ArrayList<>();
    int subjectId = Integer.parseInt(parentSubject.get(0).get("id").toString());
    List<Map<String, Object>> returnedContents;

    returnedContents = wikiEntryPort.getContents(
        subjectId); //Alle Contents mit id-Verknüpfung in der Datenbank werden in Liste geladen

    //filtert die Einträge aus rContents, die gelesen werden dürfen und trägt diese in contents ein
    for (Map<String, Object> rContent : returnedContents) {
      String rights = wikiRightsPort
          .getWikiBerechtigung(username, Integer.parseInt(rContent.get("id").toString()));
      if (!rights.equals("none")) {
        contents.add(rContent);
      }
    }

    return contents;
  }

}
