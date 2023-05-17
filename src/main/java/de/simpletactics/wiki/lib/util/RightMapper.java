package de.simpletactics.wiki.lib.util;

import de.simpletactics.wiki.lib.model.Right;

public class RightMapper {

  public static Right rightMapper(String highestRight) {
    boolean admin = highestRight.equals("admin");
    boolean mod = admin || highestRight.equals("mod");
    boolean write = mod || highestRight.equals("write");
    boolean create = write || highestRight.equals("create");
    boolean edit = create || highestRight.equals("edit");
    boolean read = edit || highestRight.equals("read");

    return new Right(read, edit, create, write, admin, mod);
  }


}
