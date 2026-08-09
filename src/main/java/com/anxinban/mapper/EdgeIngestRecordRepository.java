package com.anxinban.mapper;

import com.anxinban.entity.EdgeIngestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 边缘网关数据接入记录数据访问接口 — 通过 upload_id UNIQUE 约束实现幂等去重。
 *
 * <p>核心方法：{@link #existsByUploadId(String)} 判断是否已处理过同一请求。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface EdgeIngestRecordRepository extends JpaRepository<EdgeIngestRecord, Long> {

    /**
     * 检查指定 upload_id 是否已存在。
     *
     * @param uploadId PC 生成的幂等上传 ID
     * @return true 如果已存在
     */
    boolean existsByUploadId(String uploadId);
}
