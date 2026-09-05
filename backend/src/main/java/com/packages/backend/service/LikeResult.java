package com.packages.backend.service;

/**
 * Outcome of a like. The three cases used to share a single nullable string, where the nickname of
 * a match and the rejection of an already answered profile could not be told apart by the type.
 */
public sealed interface LikeResult {

  /** The like has been registered and the other profile has not answered yet. */
  record Liked() implements LikeResult {
  }

  /**
   * The like has been registered and completes a match, the other profile having liked first.
   *
   * @param nickname nickname to display to the connected user
   */
  record Matched(String nickname) implements LikeResult {
  }

  /** The connected user had already liked or disliked that profile. */
  record Forbidden() implements LikeResult {
  }
}
