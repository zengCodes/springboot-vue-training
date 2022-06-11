package com.zeng.business.service.Impl;


import com.zeng.business.entity.FileChunk;
import com.zeng.business.entity.SnowflakeIdWorker;
import com.zeng.business.mapper.FileChunkMapper;
import com.zeng.business.service.FileChunkService;
import com.zeng.business.vo.FileChunkVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/22 23:59
 **/

@Service("FileChunkService")
public class FileChunkServiceImpl implements FileChunkService {


    @Resource
    private FileChunkMapper fileChunkDao;

    @Override
    public int saveChunk(FileChunkVO fileChunkVO) {
        FileChunk fileChunk = new FileChunk();
        BeanUtils.copyProperties(fileChunkVO, fileChunk);
        String name = fileChunkVO.getFilename();
        fileChunk.setSerialNum(SnowflakeIdWorker.getUUID() + SnowflakeIdWorker.getUUID())
                .setType(name.substring(name.indexOf(".") + 1));
        fileChunkDao.insert(fileChunk);
        return fileChunkDao.insert(fileChunk);
    }

    @Override
    public ArrayList<Integer> checkChunk(FileChunkVO fileChunkVO) {
        @NotNull(message = "文件名称不能为空") String fileName = fileChunkVO.getFilename();
        @NotNull(message = "文件md5标识不能为空") String identifier = fileChunkVO.getIdentifier();
        ArrayList<Integer> arrayList = null;
        Example o = new Example(FileChunk.class);
        Example.Criteria criteria = o.createCriteria();
        if (!StringUtils.isEmpty(fileName)) {
            criteria.andEqualTo("filename", fileName);
        }
        if (!StringUtils.isEmpty(identifier)) {
            criteria.andEqualTo("identifier", identifier);
        }
        List<FileChunk> fileChunks = fileChunkDao.selectByExample(o);
        for (FileChunk fileChunk : fileChunks) {
            arrayList.add(fileChunk.getChunkNumber());
        }
        return arrayList;
    }

}
