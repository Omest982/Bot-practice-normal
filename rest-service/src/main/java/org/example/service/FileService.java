package org.example.service;

import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;

public interface FileService {
    AppDocument getAppDocument(String id);
    AppPhoto getAppPhoto(String id);
}
