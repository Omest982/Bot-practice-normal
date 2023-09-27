package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.CryptoTool;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.example.repository.AppDocumentRepository;
import org.example.repository.AppPhotoRepository;
import org.example.repository.BinaryContentRepository;
import org.example.service.FileService;
import org.example.service.enums.LinkType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAddress;
    private final AppDocumentRepository appDocumentRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final AppPhotoRepository appPhotoRepository;
    private final CryptoTool cryptoTool;
    @Override
    public AppDocument processDoc(Message telegramMessage) throws RuntimeException {
        Document telegramDocument = telegramMessage.getDocument();
        String fileId = telegramDocument.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if(response.getStatusCode() == HttpStatus.OK){
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppDocument appDocument = buildAppDocument(persistentBinaryContent, telegramDocument);
            return appDocumentRepository.save(appDocument);
        }else {
            throw new RuntimeException("Bad response from telegram service" + response);
        }
    }

    public BinaryContent getPersistentBinaryContent(ResponseEntity<String> response){
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent
                .builder()
                .fileAsArrayOfBites(fileInByte)
                .build();
        return binaryContentRepository.save(transientBinaryContent);
    }

    public String getFilePath(ResponseEntity<String> response){
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    @Override
    public String generateLink(Long id, LinkType linkType) {
        String hash = cryptoTool.hashOf(id);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) throws RuntimeException{
        int photoSizeCount = telegramMessage.getPhoto().size();
        int photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if(response.getStatusCode() == HttpStatus.OK){
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto appPhoto = buildAppPhoto(persistentBinaryContent, telegramPhoto);
            return appPhotoRepository.save(appPhoto);
        }else {
            throw new RuntimeException("Bad response from telegram service" + response);
        }
    }

    private AppPhoto buildAppPhoto(BinaryContent persistentBinaryContent, PhotoSize telegramPhoto) {
        return AppPhoto
                .builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
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
