package org.example.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.example.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response){
        //TODO для формирования badRequest добавить ControllerAdvice
        AppDocument appDocument = fileService.getAppDocument(id);
        if (appDocument == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType(MediaType.parseMediaType(appDocument.getMimeType()).toString());
        response.setHeader("Content-disposition", "attachment; filename=" + appDocument.getDocName());
        response.setStatus(HttpServletResponse.SC_OK);

        BinaryContent binaryContent = appDocument.getBinaryContent();
        try{
            ServletOutputStream out = response.getOutputStream();
            out.write(binaryContent.getFileAsArrayOfBites());
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response){
        //TODO для формирования badRequest добавить ControllerAdvice
        AppPhoto appPhoto = fileService.getAppPhoto(id);
        if (appPhoto == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-disposition", "attachment;");
        response.setStatus(HttpServletResponse.SC_OK);

        BinaryContent binaryContent = appPhoto.getBinaryContent();
        try{
            ServletOutputStream out = response.getOutputStream();
            out.write(binaryContent.getFileAsArrayOfBites());
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
