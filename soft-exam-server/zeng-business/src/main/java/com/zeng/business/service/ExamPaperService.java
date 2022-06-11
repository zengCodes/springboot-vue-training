package com.zeng.business.service;


import com.zeng.business.dto.TestDTO;

import java.util.ArrayList;

public interface ExamPaperService {

    int sendPaper(Long id, ArrayList<TestDTO> testDTO);
}
