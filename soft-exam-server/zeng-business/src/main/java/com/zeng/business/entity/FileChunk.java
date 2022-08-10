package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * (ExamFileChunk)实体类
 *
 * @author zenghuiqing
 * @since 2021-11-22 23:45:50
 */
@Data
@Table(name = "exam_file_chunk")
@Accessors(chain = true)
public class FileChunk implements Serializable {
    private static final long serialVersionUID = -83595561292219823L;
    /**
     * 块编号
     */
    @Id
    private Long id;

    //附件编号
    private String serialNum;
    /**
     * 当前块数
     */
    private Integer chunkNumber;
    /**
     * 块总大小
     */
    private Integer chunkSize;
    /**
     * 当前分块大小
     */
    private Integer currentChunkSize;
    /**
     * 文件标识
     */
    private String identifier;
    /**
     * 文件名称
     */
    private String filename;
    /**
     * 相对路径
     */
    private String relativePath;
    /**
     * 总快数
     */
    private Integer totalChunks;
    /**
     * 文件类型
     */
    private String type;
}
