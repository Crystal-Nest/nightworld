package crystalspider.nightworld.api;

/**
 * Handles checking whether a Nether Portal frame makes the portal a Nightworld Portal.
 */
public interface NightworldPortalChecker {
  /**
   * Whether the portal is a Nightworld Portal.
   * 
   * @return
   */
  public boolean isNightworldPortal();
}
