package de.simpletactics.wiki.lib.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WikiNavigation {

  private final int id;
  private final String title;
  private final List<SubjectEntry> subjectEntries;
  private List<WikiEntry> wikiEntries;

  public WikiNavigation(int id, String title,
      List<SubjectEntry> subjectEntries) {
    this.id = id;
    this.title = title;
    this.subjectEntries = subjectEntries;
  }
}
