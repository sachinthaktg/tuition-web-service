package biz.codex55.web_service.controller;

import biz.codex55.web_service.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/profile-pics/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {

        // 1. Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // 2. Try to determine file's content type dynamically
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // 3. Fallback to default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // 4. Return the file
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // "inline" means the browser will display the image. "attachment" would force a download.
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
