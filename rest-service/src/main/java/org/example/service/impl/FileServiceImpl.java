package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.example.CryptoTool;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.example.repository.AppDocumentRepository;
import org.example.repository.AppPhotoRepository;
import org.example.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    private final AppDocumentRepository appDocumentRepository;
    private final AppPhotoRepository appPhotoRepository;
    private final CryptoTool cryptoTool;
    @Override
    public AppDocument getAppDocument(String hash) {
        Long id = cryptoTool.idOf(hash);
        if(id == null){
            return null;
        }
        return appDocumentRepository.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getAppPhoto(String hash) {
        Long id = cryptoTool.idOf(hash);
        if(id == null){
            return null;
        }
        return appPhotoRepository.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try{
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBites());
            return new FileSystemResource(temp);
        } catch (IOException e){
            log.error(e.getMessage());
            return null;
        }
    }
}
