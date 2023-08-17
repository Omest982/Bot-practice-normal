package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppDocument;
import org.example.entity.BinaryContent;
import org.example.repository.AppDocumentRepository;
import org.example.repository.BinaryContentRepository;
import org.example.service.FileService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    private final AppDocumentRepository appDocumentRepository;
    private final BinaryContentRepository binaryContentRepository;
    @Override
    public AppDocument processDoc(Message telegramMessage) throws RuntimeException {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if(response.getStatusCode() == HttpStatus.OK){
            JSONObject jsonObject = new JSONObject(response);
            String filePath = String.valueOf(jsonObject
                    .getJSONObject("result")
                    .getString("file_path"));
            byte[] fileInByte = downloadFile(filePath);
            BinaryContent transientBinaryContent = BinaryContent
                    .builder()
                    .fileAsArrayOfBites(fileInByte)
                    .build();
            BinaryContent persistentBinaryContent = binaryContentRepository.save(transientBinaryContent);
            Document telegramDocument = telegramMessage.getDocument();
            AppDocument appDocument = buildAppDocument(persistentBinaryContent, telegramDocument);
            return appDocumentRepository.save(appDocument);
        }else {
            throw new RuntimeException("Bad response from telegram service" + response);
        }
    }

    private AppDocument buildAppDocument(BinaryContent persistentBinaryContent, Document telegramDocument) {
        return AppDocument
                .builder()
                .telegramFileId(telegramDocument.getFileId())
                .docName(telegramDocument.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDocument.getMimeType())
                .fileSize(telegramDocument.getFileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        }catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        //TODO подумать над оптимизацией!!

        try(InputStream inputStream = urlObj.openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(urlObj.toExternalForm(), e);
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }
}
