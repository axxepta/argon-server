package de.axxepta.services.interfaces;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jvnet.hk2.annotations.Contract;

import de.axxepta.models.UserAuthModel;

@Contract
public interface IDocumentGitService {

	public List<String> getRemoteNames(String gitURL, UserAuthModel userAuth);

	public Pair<List<String>, List<String>> getFileNamesFromCommit(String GitURL, UserAuthModel userAuth);

	public byte[] getDocumentFromRepository(String gitURL, String fileName, UserAuthModel userAuth);

	public boolean commitDocumentLocalStored(String gitURL, File localFile, String directoryOnGit, String commitMessage,
			UserAuthModel authUser);

}
