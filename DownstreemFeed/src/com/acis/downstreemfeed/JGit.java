package com.acis.downstreemfeed;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class JGit {

	public static void main(String[] args) {
		try {
			Git remoteGit = Git.cloneRepository().setURI("https://github.com/Srikanth346/TestProject.git")
					.setCredentialsProvider(
							new UsernamePasswordCredentialsProvider("srikanth.m5593@gmail.com", "Srik@nth.m5593"))
					.setDirectory(new java.io.File("/DownstreemFeed/src/git/bash1")).call();

			remoteGit.add().addFilepattern("Config.java").call();
			remoteGit.commit().setMessage("Initial Commit");
			PushCommand pushCommand = remoteGit.push()
					.setCredentialsProvider(
							new UsernamePasswordCredentialsProvider("srikanth.m5593@gmail.com", "Srik@nth.m5593"));
			pushCommand.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
