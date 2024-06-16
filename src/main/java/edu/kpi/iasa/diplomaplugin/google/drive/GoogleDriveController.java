package edu.kpi.iasa.diplomaplugin.google.drive;

import edu.kpi.iasa.diplomaplugin.Res;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class GoogleDriveController {

	private final GoogleDriveService googleDriveService;

	@PostMapping("/upload")
	public Object handleFileUpload(@RequestParam("image")MultipartFile file) {
		if(file.isEmpty()){
			return "File is empty";
		}

		File tempFile = null;
		try {
			tempFile = File.createTempFile("temp", null);
			file.transferTo(tempFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Res res = googleDriveService.uploadFileToDrive(tempFile);
		return res;
	}

}
