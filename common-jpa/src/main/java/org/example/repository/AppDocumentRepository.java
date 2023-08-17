package org.example.repository;

import org.example.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentRepository extends JpaRepository<AppDocument, Long> {
}
