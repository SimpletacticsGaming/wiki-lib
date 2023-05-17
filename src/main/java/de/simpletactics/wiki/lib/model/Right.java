package de.simpletactics.wiki.lib.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Right {

  private boolean read;
  private boolean edit;
  private boolean create;
  private boolean write;

  private boolean admin;
  private boolean mod;
}
