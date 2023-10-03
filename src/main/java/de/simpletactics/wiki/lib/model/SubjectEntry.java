package de.simpletactics.wiki.lib.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SubjectEntry {

  private final WikiType wikiType = WikiType.THEMENBEREICH;
  private int id;
  private String title;
  private List<SubjectEntry> subjectEntries;
  private List<WikiEntry> wikiEntries;
  private Right right;
  private WikiNavigation wikiNavigation;


  public SubjectEntry(int id, String title) {
    this.id = id;
    this.title = title;
  }

  public SubjectEntry(int id, List<SubjectEntry> subjectEntries,
      List<WikiEntry> wikiEntries, Right right, WikiNavigation wikiNavigation) {
    this.id = id;
    this.subjectEntries = subjectEntries;
    this.wikiEntries = wikiEntries;
    this.right = right;
    this.wikiNavigation = wikiNavigation;
  }

}
