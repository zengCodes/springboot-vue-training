package com.zeng.business.service;


import com.zeng.business.dto.PaperDTO;
import com.zeng.business.entity.Paper;
import com.zeng.business.vo.PageVO;
import com.zeng.business.vo.PaperQuestionVO;
import com.zeng.business.vo.PaperVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/12 1:17
 **/
public interface PaperService {


    /**
     * 导入word
     *
     * @param wordFile
     * @throws IOException
     */
    Paper importMorningPracticeWord(MultipartFile wordFile, HttpServletRequest req) throws IOException;


    /**
     * 根据角色状态
     *
     * @param id
     * @param status
     */
    void updateStatus(Long id, Boolean status);


    void exportPracticeWord(Long id, HttpServletResponse res) throws IOException;

    PageVO<PaperVO> getPaperList(Integer pageNum, Integer pageSize);

    /**
     * 试卷列表
     *
     * @param pageNum
     * @param pageSize
     * @param paperVO
     * @return
     */
    PageVO<PaperVO> findPaperList(Integer pageNum, Integer pageSize, PaperVO paperVO);


    /**
     * 添加试卷
     *
     * @param paperVO
     */
    Paper add(PaperVO paperVO);


    /**
     * 编辑试卷
     *
     * @param id
     * @return
     */
    PaperVO edit(Long id);

    /**
     * 更新试卷
     *
     * @param id
     * @param paperVO
     */
    void update(Long id, PaperVO paperVO);

    /**
     * 删除试卷
     *
     * @param id
     */
    int delete(Long id);

    List<PaperVO> getAllPaperList(PaperDTO paperDTO);

    List<PaperQuestionVO> getPaperQuestionList();

    void downloadTemplate(String type, HttpServletResponse response);

    Paper importAfternoonPracticeWord(MultipartFile wordFile, HttpServletRequest request) throws IOException, ParserConfigurationException, TransformerException, ParseException;
}
