package com.anxinban.mapper;

import com.anxinban.entity.FileMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 文件元数据数据访问接口。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    /**
     * 根据文件业务 ID 查询。
     */
    FileMetadata findByFileId(String fileId);

    /**
     * 按老人 ID 分页查询，按创建时间降序。
     */
    Page<FileMetadata> findByElderIdOrderByCreatedAtDesc(String elderId, Pageable pageable);

    /**
     * 按老人 ID 和告警类型分页查询，按创建时间降序。
     */
    Page<FileMetadata> findByElderIdAndAlarmTypeOrderByCreatedAtDesc(String elderId, String alarmType, Pageable pageable);
}
