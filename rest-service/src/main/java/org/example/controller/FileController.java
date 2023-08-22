package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.example.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @RequestMapping(method = RequestMethod.GET, value = "get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id){
        //TODO для формирования badRequest добавить ControllerAdvice
        AppDocument appDocument = fileService.getAppDocument(id);
        if (appDocument == null){
            return ResponseEntity.badRequest().build();
        }
        BinaryContent binaryContent = appDocument.getBinaryContent();
        FileSystemResource fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResource == null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(appDocument.getMimeType()))
                .header("Content-disposition", "attachment; filename=" + appDocument.getDocName())
                .body(fileSystemResource);
    }

    @RequestMapping(method = RequestMethod.GET, value = "get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id){
        //TODO для формирования badRequest добавить ControllerAdvice
        AppPhoto appPhoto = fileService.getAppPhoto(id);
        if (appPhoto == null){
            return ResponseEntity.badRequest().build();
        }
        BinaryContent binaryContent = appPhoto.getBinaryContent();
        FileSystemResource fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResource == null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResource);
    }
}
