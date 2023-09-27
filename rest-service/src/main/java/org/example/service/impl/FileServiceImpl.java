package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.CryptoTool;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.repository.AppDocumentRepository;
import org.example.repository.AppPhotoRepository;
import org.example.service.FileService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
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
}
