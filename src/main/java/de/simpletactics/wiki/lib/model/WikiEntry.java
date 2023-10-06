package de.simpletactics.wiki.lib.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WikiEntry {

  private WikiType wikiType;
  private int id;
  private String topic;
  private String htmlEntry;
  private Right right;
  private WikiNavigation wikiNavigation;


  public WikiEntry(int id, String topic, WikiType wikiType) {
    this.id = id;
    this.topic = topic;
    this.wikiType = wikiType;
  }
}
