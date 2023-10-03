package de.simpletactics.wiki.lib.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WikiEntry {

  private final WikiType wikiType = WikiType.STANDARDEINTRAG;
  private int id;
  private String topic;
  private String htmlEntry;
  private Right right;
  private WikiNavigation wikiNavigation;


  public WikiEntry(int id, String topic) {
    this.id = id;
    this.topic = topic;
  }
}
