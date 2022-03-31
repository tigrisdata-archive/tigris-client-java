package com.tigrisdata.maven.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class GitUtils {
  private GitUtils() {}

  public static String getHeadContent(String repoRootPath, String filePath) throws IOException {
    Repository repo = Git.open(new File(repoRootPath)).getRepository();
    ObjectId head = repo.resolve(Constants.HEAD);
    RevCommit commit = repo.parseCommit(head);

    try (TreeWalk walk = TreeWalk.forPath(repo, filePath, commit.getTree())) {
      if (walk != null) {
        byte[] bytes = repo.open(walk.getObjectId(0)).getBytes();
        return new String(bytes, StandardCharsets.UTF_8);
      } else {
        throw new IllegalArgumentException("No path found.");
      }
    }
  }
}
