package de.simpletactics.wiki.lib.util;

import de.simpletactics.wiki.lib.model.Right;
import de.simpletactics.wiki.lib.model.WikiEntry;
import de.simpletactics.wiki.lib.model.WikiNavigation;
import de.simpletactics.wiki.lib.model.SubjectEntry;
import de.simpletactics.wiki.lib.model.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WikiMapper {

  public static WikiEntry wikiEntryMapper(String berechtigung,
      List<Map<String, Object>> content, List<Map<String, Object>> filteredNavTopics,
      List<Map<String, Object>> parentTopic, Type type,
      List<Map<String, Object>> filteredNavContents) {
    int id = Integer.parseInt(content.get(0).get("id").toString());
    String topic = content.get(0).get("topic").toString();
    String htmlEntry = content.get(0).get("content").toString();
    Right rights = RightMapper.rightMapper(berechtigung);

    return new WikiEntry(id, topic, htmlEntry, rights, wikiNavigationMapper(
        filteredNavTopics, parentTopic, type, filteredNavContents));
  }

  public static SubjectEntry subjectEntryMapper(int id, String berechtigung,
      List<Map<String, Object>> topics,
      List<Map<String, Object>> contents, List<Map<String, Object>> filteredNavTopics,
      List<Map<String, Object>> parentTopic, Type type,
      List<Map<String, Object>> filteredNavContents) {

    List<SubjectEntry> listedSubjectEntries = new ArrayList<>();
    List<WikiEntry> listedContentEntries = new ArrayList<>();

    for (Map<String, Object> subject : topics) {
      int subjectId = Integer.parseInt(subject.get("id").toString());
      String topic = subject.get("topic").toString();

      listedSubjectEntries.add(new SubjectEntry(subjectId, topic));
    }

    for (Map<String, Object> entry : contents) {
      int entryId = Integer.parseInt(entry.get("id").toString());
      String topic = entry.get("topic").toString();

      listedContentEntries.add(new WikiEntry(entryId, topic));
    }
    Right rights = RightMapper.rightMapper(berechtigung);

    if (id == 0) {
      return new SubjectEntry(id, listedSubjectEntries, listedContentEntries, rights,
          null);
    } else {
      return new SubjectEntry(id, listedSubjectEntries, listedContentEntries, rights,
          wikiNavigationMapper(filteredNavTopics,
              parentTopic, type, filteredNavContents));
    }
  }

  //Baut das Wiki-Navigations-Objekt zusammen.
  public static WikiNavigation wikiNavigationMapper(List<Map<String, Object>> filteredNavTopics,
      List<Map<String, Object>> parentTopic, Type type,
      List<Map<String, Object>> filteredNavContents) {

    List<SubjectEntry> listedSubjectEntries = new ArrayList<>();

    //Baut die Liste der Subject-Einträge zusammen. Dises wird für alle Wiki-Typen benötigt.
    for (Map<String, Object> subject : filteredNavTopics) {
      int subjectId = Integer.parseInt(subject.get("id").toString());
      String topic = subject.get("topic").toString();

      listedSubjectEntries.add(new SubjectEntry(subjectId, topic));
    }
    //Gibt das WikiNavigations-Objekt für Subjects zurück.
    if (type == Type.THEMENBEREICH) {

      return new WikiNavigation(Integer.parseInt(parentTopic.get(0).get("id").toString()),
          parentTopic.get(0).get("topic").toString(),
          listedSubjectEntries);
    }

    //Baut die Liste der Content-Einträge (Entries) zusammen und gibt das WikiNavigations-Objekt für Entrys zurück.
    else if (type == Type.STANDARDEINTRAG) {
      List<WikiEntry> listedContentEntries = new ArrayList<>();
      for (Map<String, Object> content : filteredNavContents) {
        int subjectId = Integer.parseInt(content.get("id").toString());
        String topic = content.get("topic").toString();

        listedContentEntries.add(new WikiEntry(subjectId, topic));
      }
      return new WikiNavigation(Integer.parseInt(parentTopic.get(0).get("id").toString()),
          parentTopic.get(0).get("topic").toString(),
          listedSubjectEntries, listedContentEntries);
    }
    return null;
  }

}
