package com.zeng.business.service;


import com.zeng.business.vo.FileChunkVO;

import java.util.ArrayList;

public interface FileChunkService {

    int saveChunk(FileChunkVO fileChunkVO);

    ArrayList<Integer> checkChunk(FileChunkVO fileChunkVO);

}
