package edu.kpi.iasa.diplomaplugin.google.drive;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import edu.kpi.iasa.diplomaplugin.Res;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveService {

	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	@Value("${drive.credentials.file.path}")
	private String SERVICE_ACCOUNT_KEY_PATH;

	@Value("${drive.folder.id}")
	private String GOOGLE_DRIVE_FOLDER_ID;

	public Res uploadFileToDrive(File file) {

		Res res = new Res();

		try {

			Drive drive = createDriveService();

			com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
			fileMetaData.setName(file.getName());
			fileMetaData.setParents(Collections.singletonList(GOOGLE_DRIVE_FOLDER_ID));
			FileContent mediaContent = new FileContent("*/*", file);
			com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
					.setFields("id").execute();
			String imageUrl = "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();

			System.out.println("IMAGE_URL: " + imageUrl);

			res.setUrl(imageUrl);
			res.setStatus(200);
			res.setMessage("File Successfully Uploaded To Drive");
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			res.setStatus(500);
			res.setMessage(e.getMessage());
		}
		return res;
	}

	public String crateFolderInFolderWithId(String folderName, String parentFolderId) throws GeneralSecurityException, IOException {
		Res res = new Res();
		try {
			Drive drive = createDriveService();
			com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
			fileMetadata.setName(folderName);
			fileMetadata.setMimeType("application/vnd.google-apps.folder");
			fileMetadata.setParents(Collections.singletonList(parentFolderId));
			com.google.api.services.drive.model.File folder = drive.files().create(fileMetadata)
					.setFields("id, parents")
					.execute();
			res.setStatus(200);
			res.setMessage("Folder successfully created with ID: " + folder.getId());
			return folder.getId();
		} catch (Exception e) {
			res.setStatus(500);
			res.setMessage(e.getMessage());
		}
		return "Error";
	}

	public String createFolderForGroup(String groupName) throws Exception {
		Drive drive = createDriveService();
		com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
		fileMetadata.setName(groupName);
		fileMetadata.setMimeType("application/vnd.google-apps.folder");
		fileMetadata.setParents(Collections.singletonList(GOOGLE_DRIVE_FOLDER_ID));

		com.google.api.services.drive.model.File file = drive.files().create(fileMetadata)
				.setFields("id, parents")
				.execute();
		System.out.println("Folder ID: " + file.getId());
		return file.getId();
	}

	public void deleteFolderWithId(String folderId) throws Exception {
		Drive drive = createDriveService();
		drive.files().delete(folderId).execute();
		System.out.println("Folder with ID: " + folderId + " has been deleted.");
	}

	public String uploadFileToFolderWithId(String folderId, File file) {
		try {
			Drive drive = createDriveService();
			com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
			fileMetaData.setName(file.getName());
			fileMetaData.setParents(Collections.singletonList(folderId));
			FileContent mediaContent = new FileContent("*/*", file);
			com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
					.setFields("id").execute();
			System.out.println("File ID: " + uploadedFile.getId());
			return uploadedFile.getId();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	private Drive createDriveService() throws GeneralSecurityException, IOException {
		GoogleCredential credentials = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
				.createScoped(Collections.singleton(DriveScopes.DRIVE));
		return new Drive.Builder(
				GoogleNetHttpTransport.newTrustedTransport(),
				JSON_FACTORY,
				credentials)
				.build();
	}

	public List<com.google.api.services.drive.model.File> getFilesInFolder(String folderId) throws IOException, GeneralSecurityException {
		Drive drive = createDriveService();
		FileList allElements = drive.files().list()
				.setQ("'" + folderId + "' in parents")
				.setFields("nextPageToken, files(id, name, mimeType)")
				.execute();
		List<com.google.api.services.drive.model.File> fileList = allElements.getFiles();
		List<com.google.api.services.drive.model.File> result = new ArrayList<>();
		for (var file : fileList) {
			if (!"application/vnd.google-apps.folder".equals(file.getMimeType())) {
				result.add(file);
			}
		}
		return result;
	}
}
