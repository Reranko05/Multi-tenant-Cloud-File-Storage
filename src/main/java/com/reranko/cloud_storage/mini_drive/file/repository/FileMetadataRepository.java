package com.reranko.cloud_storage.mini_drive.file.repository;

import com.reranko.cloud_storage.mini_drive.file.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/* Repository for managing FileMetadata entities in the database.
 */

public interface FileMetadataRepository
        extends JpaRepository<FileMetadata, Long> {

    List<FileMetadata> findAllByOwnerUserId(Long ownerUserId);

    Optional<FileMetadata> findByIdAndOwnerUserId(
        Long id,
        Long ownerUserId
    );
}