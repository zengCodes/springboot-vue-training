package com.zeng.controller.common;

import com.zeng.business.service.FileResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通用请求处理
 *
 * @author ruoyi
 */
@RestController
public class CommonController {
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Resource
    private FileResourceService fileResourceService;


    /**
     * 下载文件
     *
     * @param req
     * @param resp
     */
    @RequestMapping(value = "common/download", method = RequestMethod.GET)
    public void download(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String id = req.getParameter("id");
        fileResourceService.download(id,resp);
    }

}
