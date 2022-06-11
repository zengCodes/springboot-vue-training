package com.zeng.business.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Id;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/23 0:06
 **/

@Data
public class FileChunkVO {

    @Id
    private Long id;

    //附件编号
    private String serialNum;
    /**
     * 当前文件块，从1开始
     */
    private Integer chunkNumber;
    /**
     * 每块大小
     */
    private Integer chunkSize;
    /**
     * 当前分块大小
     */
    private Integer currentChunkSize;
    /**
     * 总大小
     */
    private Integer totalSize;
    /**
     * 文件标识
     */
    private String identifier;
    /**
     * 文件名
     */
    private String filename;
    /**
     * 相对路径
     */
    private String relativePath;
    /**
     * 总块数
     */
    private Integer totalChunks;
    /**
     * 文件类型
     */
    private String type;

    /**
     * 块内容
     */
    private transient MultipartFile uploadFile;

}
