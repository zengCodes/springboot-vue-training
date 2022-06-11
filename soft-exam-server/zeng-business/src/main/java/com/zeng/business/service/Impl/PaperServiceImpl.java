package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.deploy.net.URLEncoder;
import com.zeng.business.converter.ChoiceQuestionConverter;
import com.zeng.business.converter.PaperConverter;
import com.zeng.business.dto.PaperDTO;
import com.zeng.business.entity.*;
import com.zeng.business.mapper.*;
import com.zeng.business.service.ChoiceQuestionService;
import com.zeng.business.service.PaperService;
import com.zeng.business.vo.ChoiceQuestionVO;
import com.zeng.business.vo.PageVO;
import com.zeng.business.vo.PaperQuestionVO;
import com.zeng.business.vo.PaperVO;
import com.zeng.common.config.ZengConfig;
import com.zeng.common.converter.ImageBase64Converter;
import com.zeng.common.core.domain.model.LoginUser;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.NumberUtils;
import com.zeng.common.utils.PaperUtil;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.StringUtils;
import com.zeng.common.utils.html.HTMLFilter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.IImageExtractor;
import org.apache.poi.xwpf.converter.core.IURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.omg.CORBA.SystemException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/22 16:33
 **/
@Service("PaperService")
@Slf4j
public class PaperServiceImpl implements PaperService {
    @Value("${zeng.staticPath}")
    private String staticPath;

    @Resource
    private PaperMapper paperDao;

    @Resource
    private CourseMapper courseDao;

    @Resource
    private CategoryMapper courseCategoryDao;

    @Resource
    private AnswerQuestionMapper answerQuestionDao;

    @Resource
    private ChoiceQuestionMapper choiceQuestionDao;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private ChoiceQuestionService choiceQuestionService;

    // 项目根路径下的目录  -- SpringBoot static 目录相当于是根路径下（SpringBoot 默认）
    public final static String UPLOAD_PATH_PREFIX = "upload/word/";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");

    /**
     * @param response 客户端响应
     * @throws IOException io异常
     */
    public void downLoad(HttpServletResponse response, String downloadUrl) throws Throwable {
        if (Objects.isNull(downloadUrl)) {
            // 如果接收参数为空则抛出异常，由全局异常处理类去处理。
            throw new NullPointerException("下载地址为空");
        }
        // 读文件
        File file = new File(downloadUrl);
        if (!file.exists()) {
            log.error("下载文件的地址不存在:{}", file.getPath());
            // 如果不存在则抛出异常，由全局异常处理类去处理。
            throw new HttpMediaTypeNotAcceptableException("文件不存在");
        }
        // 获取用户名
        String fileName = file.getName();
        // 重置response
        response.reset();
        response.setCharacterEncoding("utf-8");
        // ContentType，即告诉客户端所发送的数据属于什么类型
        response.setContentType("application/msword; charset=UTF-8");
        // 获得文件的长度
        response.setHeader("Content-Length", String.valueOf(file.length()));
        // 设置编码格式
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
        // 发送给客户端的数据
        OutputStream outputStream = response.getOutputStream();
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        // 读取文件
        bis = new BufferedInputStream(new FileInputStream(new File(downloadUrl)));
        int i = bis.read(buff);
        // 只要能读到，则一直读取
        while (i != -1) {
            // 将文件写出
            outputStream.write(buff, 0, buff.length);
            // 刷出
            outputStream.flush();
            i = bis.read(buff);
        }
    }


    @Override
    public void downloadTemplate(String type, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        try {
            //生成临时文件地址
            String filePath = "D:\\Download\\";
            String fileOnlyName = "";
            String template = "";
            //文件唯一名称
            if (type.equals("morning")) {
                fileOnlyName = "choice-import-template.doc";
                template = "choice-import-template.ftl";
            } else {
                fileOnlyName = "answer-import-template.doc";
                template = "answer-import-template.ftl";
            }
            createWord(params, template, filePath, fileOnlyName, response);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    @Override
    public Paper importAfternoonPracticeWord(MultipartFile wordFile, HttpServletRequest request) throws IOException, ParserConfigurationException, TransformerException, ParseException {
        Paper paper = null;
        String realPath = staticPath;
        String format = simpleDateFormat.format(new Date());
        File folder = new File(realPath + format);
        if (!folder.isDirectory()) {
            //递归生成文件夹
            folder.mkdirs();
        }
        //获取原始的名字  original:最初的，起始的  方法是得到原来的文件名在客户机的文件系统名称
        String oldName = wordFile.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."), oldName.length());
        if ((oldName.endsWith(".doc") || oldName.endsWith(".docx"))) {
            //构建真实的文件路径
            File newFile = new File(folder.getAbsolutePath() + File.separator + newName);
            //转存文件到指定路径，如果文件名重复的话，将会覆盖掉之前的文件,这里是把文件上传到 “绝对路径”
            wordFile.transferTo(newFile);
            String fileType = null;
            fileType = newFile.getPath();
            if (fileType.endsWith(".doc")) {
                InputStream input = new FileInputStream(newFile);
                HWPFDocument wordDocument = new HWPFDocument(input);
                WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
                //设置图片存放的位置
                wordToHtmlConverter.setPicturesManager(new PicturesManager() {
                    public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches, float heightInches) {
                        String url = null;
                        File imgPath = new File(realPath);
                        if (!imgPath.exists()) {//图片目录不存在则创建
                            imgPath.mkdirs();
                        }
                        File file = new File(realPath + suggestedName);
                        try {
                            OutputStream os = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(os);
                            bos.write(content, 0, content.length);
                            bos.flush();
                            bos.close();
                            String data = null;
                            if (file.getAbsolutePath().endsWith("jpg") || file.getAbsolutePath().endsWith("jpeg")) {
                                data = "data:image/jpeg;base64,";
                            } else if (file.getAbsolutePath().endsWith("png")) {
                                data = "data:image/png;base64,";
                            } else if (file.getAbsolutePath().endsWith("gif")) {
                                data = "data:image/gif;base64,";
                            }
                            String base64 = ImageBase64Converter.convertFileToBase64(file.getAbsolutePath());
                            url = data + base64;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return url;
                    }
                });
                //解析word文档
                wordToHtmlConverter.processDocument(wordDocument);
                Document htmlDocument = wordToHtmlConverter.getDocument();
                //也可以使用字符数组流获取解析的内容
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputStream outStream = new BufferedOutputStream(baos);
                DOMSource domSource = new DOMSource(htmlDocument);
                StreamResult streamResult = new StreamResult(outStream);
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer serializer = factory.newTransformer();
                serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
                serializer.setOutputProperty(OutputKeys.INDENT, "yes");
                serializer.setOutputProperty(OutputKeys.METHOD, "html");
                serializer.transform(domSource, streamResult);
                //也可以使用字符数组流获取解析的内容
                String content = baos.toString();
                // 样式
                // System.out.println(getHtmlStyle(content));
                paper = saveAfternoon2003PaperData(getHtmlBody(content));
                baos.close();
                outStream.close();
                return paper;
            } else if (fileType.endsWith(".docx")) {
                XWPFDocument document = new XWPFDocument(new FileInputStream(newFile));
                XHTMLOptions options = XHTMLOptions.create().indent(4);
                // 导出图片
                File imageFolder = new File(ZengConfig.getUploadPath());
                // 2) 解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录)
                Map<String, String> imgMap = new HashMap<>();
                options.setExtractor(new IImageExtractor() {
                    @Override
                    public void extract(String imagePath, byte[] imageData) throws IOException {
                        //获取图片数据并且上传
                        String fileName = ZengConfig.getFile() + imagePath.substring(imagePath.lastIndexOf("."));
//                        String imgUrl = uploadFileUtil.uploadFile(imageData, bucket, directory, fileName, visitPoint);
//                        imgMap.put(imagePath, imgUrl);
                    }
                });
                // html中图片的路径 相对路径
                options.URIResolver(new IURIResolver() {
                    @Override
                    public String resolve(String uri) {
                        //设置图片路径
                        return imgMap.get(uri);
                    }
                });
                // 3) 将 XWPFDocument转换成XHTML
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                XHTMLConverter.getInstance().convert(document, baos, options);
                String content = baos.toString();
                log.info("---"+content);
                baos.close();
            }
            return paper;
        }
        return null;
    }

    public Paper saveAfternoon2003PaperData(String content) throws ParseException {
        Paper paper = null;
        PaperVO paperVO = new PaperVO();
        // 内容
        String bodyContent = getHtmlBody(content);
        // 过滤html标签后去除空白行
        String textContent = HTMLFilter.Html2Text(bodyContent).replaceAll("[<br>]{0,}", "").replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "   ");//去掉空行;
        String[] arr = textContent.split("\\n");
        // 保存试卷
        String pOne = arr[0].replaceAll("\\\\s*|\\t|\\r|\\n", "");
        String[] paperTitle = pOne.split("-");
        String time = paperTitle[1];
        int num = RandomUtils.nextInt();
        Example o = new Example(Paper.class);
        Example.Criteria criteria = o.createCriteria();
        criteria.andEqualTo("id", num);
        List<Paper> paperList = paperDao.selectByExample(o);
        while (paperList.size() > 0) {
            num = RandomUtils.nextInt();
        }
        paperVO.setId((long) num);
        paperVO.setTime(PaperUtil.getYear(time))
                .setName(paperTitle[0].trim())
                .setSuggestTime(150);
        // 查询科目
        Example example = new Example(Course.class);
        Example.Criteria oCriteria = example.createCriteria();
        oCriteria.andEqualTo("name", paperTitle[0].trim());
        oCriteria.andEqualTo("time", PaperUtil.getYear(time));
        List<Course> courses = courseDao.selectByExample(example);
        // 保存试题 计算有试题题目数目
        int total = appearNumber("试题", bodyContent);
        // 保存试卷
        if (courses.size() > 0) {
            paperVO.setCourseId(courses.get(0).getId());
        } else {
            // 如果不存在科目 新增科目
            Category category2 = courseCategoryDao.selectOne(new Category().setName(time.substring(time.indexOf("年") + 1)));
            Category category1 = courseCategoryDao.selectOne(new Category().setName(paperTitle[0].trim()));
            Course course = new Course();
            course.setName(paperTitle[0].trim());
            course.setOneCategoryId(category1.getParentId());
            course.setTwoCategoryId(category1.getId());
            course.setThreeCategoryId(category2.getId());
            course.setStatus(CommonStatusEnum.AVAILABLE.getStatusCode());
            course.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(PaperUtil.getYear(time)));
            courseDao.insert(course);
            paperVO.setCourseId(course.getId());
        }
        paperVO.setCourse(paperTitle[0].trim());
        paperVO.setUser(SecurityUtils.getLoginUser().getUsername());
        paperVO.setQuestionCount(total);
        paperVO.setScore(total);
        paper = add(paperVO);
        // 除了标题第一行，保存第一行每道题目
        String nextNumber = null;
        int currentIndex = 0;
        int nextIndex = 0;
        String currentQuestion = "";
        for (int i = 1; i < total + 1; i++) {
            //  从第一题开始遍历  转中文数字
            String number = NumberUtils.int2chineseNum(i);
            // 保存保存当前题目分数
            String grade = bodyContent.substring(bodyContent.indexOf("试题" + number + "（共"), bodyContent.indexOf(number + "（共") + 5);
//                    log.info("分数--"+grade);
            // 保存当前index
            currentIndex = bodyContent.indexOf("试题" + number);
            // 保存下一题目变量
            if (i < total) {
                nextNumber = NumberUtils.int2chineseNum(i + 1);
                nextIndex = bodyContent.indexOf("试题" + nextNumber);
            }
            if (i < total) {
                // 截取当前 到下一题
                currentQuestion = getCurrentQuestion(currentIndex, nextIndex, bodyContent);
            } else {
                currentQuestion = bodyContent.substring(currentIndex + 23, bodyContent.indexOf("</body>") - 2);
            }
            // 保存试题题目
            AnswerQuestion answerQuestion = new AnswerQuestion();
            answerQuestion.setCreateTime(new Date());
            answerQuestion.setName(currentQuestion);
            answerQuestion.setPaperId(paper.getId());
            answerQuestion.setSort(Integer.valueOf(i));
            answerQuestion.setTypeId((long) 33);
            answerQuestion.setScore(Integer.valueOf(grade.substring(5)));
            answerQuestionDao.insert(answerQuestion);
        }
        return paper;
    }


    public Paper saveAfternoon2007PaperData(String content) throws ParseException {
        Paper paper = null;
        PaperVO paperVO = new PaperVO();
        // 内容
        String bodyContent = getHtmlBody(content);
        // 过滤html标签后去除空白行
        String textContent = HTMLFilter.Html2Text(bodyContent).replaceAll("[<br>]{0,}", "").replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "   ");//去掉空行;
        String[] arr = textContent.split("\\n");
        // 保存试卷
        String pOne = arr[0].replaceAll("\\\\s*|\\t|\\r|\\n", "");
        String[] paperTitle = pOne.split("-");
        String time = paperTitle[1];
        int num = RandomUtils.nextInt();
        Example o = new Example(Paper.class);
        Example.Criteria criteria = o.createCriteria();
        criteria.andEqualTo("id", num);
        List<Paper> paperList = paperDao.selectByExample(o);
        while (paperList.size() > 0) {
            num = RandomUtils.nextInt();
        }
        paperVO.setId((long) num);
        paperVO.setTime(PaperUtil.getYear(time))
                .setName(paperTitle[0].trim())
                .setSuggestTime(150);
        // 查询科目
        Example example = new Example(Course.class);
        Example.Criteria oCriteria = example.createCriteria();
        oCriteria.andEqualTo("name", paperTitle[0].trim());
        oCriteria.andEqualTo("time", PaperUtil.getYear(time));
        List<Course> courses = courseDao.selectByExample(example);
        // 保存试题 计算有试题题目数目
        int total = appearNumber("试题", bodyContent);
        // 保存试卷
        if (courses.size() > 0) {
            paperVO.setCourseId(courses.get(0).getId());
        } else {
            // 如果不存在科目 新增科目
            Category category2 = courseCategoryDao.selectOne(new Category().setName(time.substring(time.indexOf("年") + 1)));
            Category category1 = courseCategoryDao.selectOne(new Category().setName(paperTitle[0].trim()));
            Course course = new Course();
            course.setName(paperTitle[0].trim());
            course.setOneCategoryId(category1.getParentId());
            course.setTwoCategoryId(category1.getId());
            course.setThreeCategoryId(category2.getId());
            course.setStatus(CommonStatusEnum.AVAILABLE.getStatusCode());
            course.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(PaperUtil.getYear(time)));
            courseDao.insert(course);
            paperVO.setCourseId(course.getId());
        }
        paperVO.setCourse(paperTitle[0].trim());
        paperVO.setUser(SecurityUtils.getLoginUser().getUsername());
        paperVO.setQuestionCount(total);
        paperVO.setScore(total);
        paper = add(paperVO);
        // 除了标题第一行，保存第一行每道题目
        String nextNumber = null;
        int currentIndex = 0;
        int nextIndex = 0;
        String currentQuestion = "";
        for (int i = 1; i < total + 1; i++) {
            //  从第一题开始遍历  转中文数字
            String number = NumberUtils.int2chineseNum(i);
            // 保存保存当前题目分数
            String grade = bodyContent.substring(bodyContent.indexOf("试题" + number + "（共"), bodyContent.indexOf(number + "（共") + 5);
//                    log.info("分数--"+grade);
            // 保存当前index
            currentIndex = bodyContent.indexOf("试题" + number);
            // 保存下一题目变量
            if (i < total) {
                nextNumber = NumberUtils.int2chineseNum(i + 1);
                nextIndex = bodyContent.indexOf("试题" + nextNumber);
            }
            if (i < total) {
                // 截取当前 到下一题
                currentQuestion = getCurrentQuestion(currentIndex, nextIndex, bodyContent);
            } else {
                currentQuestion = bodyContent.substring(currentIndex + 23, bodyContent.indexOf("</body>") - 2);
            }
            // 保存试题题目
            AnswerQuestion answerQuestion = new AnswerQuestion();
            answerQuestion.setCreateTime(new Date());
            answerQuestion.setName(currentQuestion);
            answerQuestion.setPaperId(paper.getId());
            answerQuestion.setSort(Integer.valueOf(i));
            answerQuestion.setTypeId((long) 33);
            answerQuestion.setScore(Integer.valueOf(grade.substring(5)));
            answerQuestionDao.insert(answerQuestion);
        }
        return paper;
    }


    /*
     * unicode编码转中文
     */
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    /**
     * 获取html中style元素内容
     *
     * @param str
     * @return
     */
    public String getHtmlStyle(String str) {
        int start = str.indexOf("<style");
        int end = str.indexOf("</style>");
        return str.substring(start, end + 8);
    }

    /**
     * 获取html中body元素内容
     *
     * @param str
     * @return
     */
    public String getHtmlBody(String str) {
        int start = str.indexOf("<body");
        int end = str.indexOf("</body>");
        return str.substring(start, end + 7);
    }

    /**
     * 获取当前问题
     *
     * @param start
     * @param end
     * @param str
     * @return
     */
    public String getCurrentQuestion(int start, int end, String str) {
        return str.substring(start + 23, end - 33);
    }

    /**
     * 计算当前题目数量
     *
     * @param findText
     * @param srcText
     * @return
     */
    public int appearNumber(String findText, String srcText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }


    /**
     * 试卷状态
     *
     * @param id
     * @param status
     */
    @Override
    public void updateStatus(Long id, Boolean status) throws SystemException {
        Paper paper = paperDao.selectByPrimaryKey(id);
        if (StringUtils.isNull(paper)) {
            throw new CustomException("试卷不存在");
        }
        Paper p = new Paper();
        p.setId(id);
        p.setStatus(status ?
                CommonStatusEnum.AVAILABLE.getStatusCode() : CommonStatusEnum.DISABLE.getStatusCode());
        // 更新某一字段
        paperDao.updateByPrimaryKeySelective(p);
    }

    public Paper saveDocxChoicePaper(InputStream is, String wordType) throws IOException, ParseException {
        String content = "";
        PaperVO paperVO = new PaperVO();
        if (wordType.equals("doc")) {
            WordExtractor doc = new WordExtractor(is);
            content = doc.getText().trim();
        } else {
            XWPFDocument xdoc = new XWPFDocument(is);
            XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
            content = extractor.getText().trim();
        }
        String[] arr = content.split("\\n");
        // 保存试卷
        String pOne = arr[0].replaceAll("\\\\s*|\\t|\\r|\\n", "");
        String[] paperTitle = pOne.split("-");
        String time = paperTitle[1];
        int num = RandomUtils.nextInt();
        Example o = new Example(Paper.class);
        Example.Criteria criteria = o.createCriteria();
        criteria.andEqualTo("id", num);
        List<Paper> paperList = paperDao.selectByExample(o);
        while (paperList.size() > 0) {
            num = RandomUtils.nextInt();
        }
        paperVO.setId((long) num);
        paperVO.setTime(PaperUtil.getYear(time))
                .setName(paperTitle[0])
                .setSuggestTime(150);
        Course course = new Course();
        course.setName(paperTitle[0]);
        course.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(PaperUtil.getYear(time)));
        Course courses = courseDao.selectOne(course);
        paperVO.setCourseId(courses.getId());
        paperVO.setCourse(paperTitle[0]);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        paperVO.setUser(loginUser.getUsername());
        // 保存试题 计算有几道题目
        int number = (arr.length - 1) / 5;
        paperVO.setQuestionCount(number);
        paperVO.setScore(number);
        Paper paper = add(paperVO);
        // 除了标题第一行，每道题目遍历五行
        if (pOne.contains("上午题")) {
            // 当前段落
            String paragraph = "";
            for (int n = 0; n < number; n++) {
                int currentIndex = 0;
                ChoiceQuestionVO choiceQuestionVO = new ChoiceQuestionVO();
                for (int i = 5 * n + 1; i <= 5 * (n + 1); i++) {
                    // 读取段落内容
                    paragraph = arr[i];
                    // 读取正确答案 （B）
                    String correctRegex = "(?<=\\（)[^\\）]+";
                    Pattern correctPattern = Pattern.compile(correctRegex);
                    Matcher correctMatcher = correctPattern.matcher(paragraph);
                    StringBuffer sb = new StringBuffer(paragraph);
                    while (correctMatcher.find()) {
                        String[] name = String.valueOf(sb.replace(correctMatcher.start(), correctMatcher.start() + 1, "")).split("：");
                        choiceQuestionVO.setSort(Integer.valueOf(name[0].substring(1, 2)));
                        choiceQuestionVO.setName(name[1]);
                        choiceQuestionVO.setCorrectAnswer(correctMatcher.group().trim());
                    }
                    // 读取选项 不能读取自动生成的序号
                    // 当前选项
                    String currentOption = paragraph.replaceAll("\\\\s*|\\t|\\r|\\n", "");
                    if (wordType.equals("doc")) {
                        if (currentOption.substring(0, 1).equals("A")) {
                            choiceQuestionVO.setSelectA(PaperUtil.getGoalOption(currentOption));
                        }
                        if (currentOption.substring(0, 1).equals("B")) {
                            choiceQuestionVO.setSelectB(PaperUtil.getGoalOption(currentOption));
                        }
                        if (currentOption.substring(0, 1).equals("C")) {
                            choiceQuestionVO.setSelectC(PaperUtil.getGoalOption(currentOption));
                        }
                        if (currentOption.substring(0, 1).equals("D")) {
                            choiceQuestionVO.setSelectD(PaperUtil.getGoalOption(currentOption));
                        }
                    } else {
                        if (i % 2 == 0) {
                            choiceQuestionVO.setSelectA(PaperUtil.getGoalOption(currentOption));
                        }
                        if (i % 3 == 0) {
                            choiceQuestionVO.setSelectB(PaperUtil.getGoalOption(currentOption));
                        }
                        if (i % 4 == 0) {
                            choiceQuestionVO.setSelectC(PaperUtil.getGoalOption(currentOption));
                        }
                        if (i % 5 == 0) {
                            choiceQuestionVO.setSelectD(PaperUtil.getGoalOption(currentOption));
                        }
                    }
                    currentIndex++;
                    // 如果已经遍历了五行，则赋值保存
                    if (currentIndex == 5) {
                        choiceQuestionVO.setPaperId(paper.getId());
                        choiceQuestionVO.setTypeId((long) 34);
                        choiceQuestionVO.setRate(3);
                        choiceQuestionService.add(choiceQuestionVO);
                        currentIndex = 0;
                    }
                }
            }
        }
        return paper;
    }

    @Override
    public Paper importMorningPracticeWord(MultipartFile wordFile, HttpServletRequest req) throws IOException {
        Paper paper = null;
        //构建文件上传所要保存的"文件夹路径"--这里是相对路径，保存到项目根路径的文件夹下
        String realPath = staticPath;
        String format = simpleDateFormat.format(new Date());
        File folder = new File(realPath + format);
        if (!folder.isDirectory()) {
            //递归生成文件夹
            folder.mkdirs();
        }
        //获取原始的名字  original:最初的，起始的  方法是得到原来的文件名在客户机的文件系统名称
        String oldName = wordFile.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."), oldName.length());
        if ((oldName.endsWith(".doc") || oldName.endsWith(".docx"))) {
            //构建真实的文件路径
            File newFile = new File(folder.getAbsolutePath() + File.separator + newName);
            //转存文件到指定路径，如果文件名重复的话，将会覆盖掉之前的文件,这里是把文件上传到 “绝对路径”
            wordFile.transferTo(newFile);
            InputStream is;
            String fileType = null;
            List<ChoiceQuestionVO> choiceQuestionVOList;
            try {
                is = new FileInputStream(newFile);
                fileType = newFile.getPath();
                if (fileType.endsWith(".docx")) {
                    // 保存到数据库
                    paper = saveDocxChoicePaper(is, "docx");
                } else if (fileType.endsWith(".doc")) {
                    paper = saveDocxChoicePaper(is, "doc");
                }
                return paper;
            } catch (FileNotFoundException | ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 生成word文件
     *
     * @param dataMap      word中需要展示的动态数据，用map集合来保存
     * @param templateName word模板名称，例如：test.ftl
     * @param filePath     文件生成的目标路径，例如：D:/wordFile/
     * @param fileName     生成的文件名称，例如：test.doc
     * @return
     */
    @SuppressWarnings("unchecked")
    public void createWord(Map dataMap, String templateName, String filePath, String fileName, HttpServletResponse response) throws IOException {
        Writer out = null;
        try {
            // 创建配置实例
            // freemarker包
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
            // 设置编码
            configuration.setDefaultEncoding("UTF-8");
            // 设置模板加载文件夹
            configuration.setDirectoryForTemplateLoading(new File(ResourceUtils.getURL("classpath:").getPath() + "template"));
            // 设置模板
            Template template = configuration.getTemplate(templateName);
            //输出文件
            File outFile = new File(filePath + File.separator + fileName);
            //如果输出目标文件夹不存在，则创建
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            // 文件名
            String modelName = "文件";
            String title =
                    modelName + "（" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + "）";
            String wordFileName = String.format("%s.doc", title);
            // 下载中文乱码问题
            response.setHeader(
                    "content-disposition",
                    "attachment;filename*=UTF-8''" + URLEncoder.encode(wordFileName, "UTF-8"));
            response.setContentType("application/msword;charset=UTF-8");
            response.setHeader("content-type", "application/msword");
            out = response.getWriter();
            //  文件绑定数据
            template.process(dataMap, out);
            log.info("文件---" + outFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (NullPointerException e) {
                throw new CustomException("responseFileStream stream close() error:NullPointerException" + e.toString());
            } catch (Exception e) {
                throw new CustomException("responseFileStream stream close() error:" + e.toString());
            }
        }
    }


    /**
     * 导出试卷到word
     *
     * @param pid
     * @param res
     * @throws IOException
     * @throws
     */
    @Override
    public void exportPracticeWord(Long pid, HttpServletResponse res) throws IOException {
        Paper paper = null;
        if (pid != null) {
            Paper p = new Paper();
            p.setId(pid);
            paper = paperDao.selectOne(p);
        } else {
            throw new CustomException("试卷不能为空！");
        }
        /** 用于组装word页面需要的数据 */
        Map<String, Object> params = new HashMap<>();
        //装填参数
        log.info("月--" + paper.getTime());
        Integer time = Integer.valueOf(paper.getTime().split("-")[1]);
        //   判断是否属于上半年还是下半年
        if (time > 0 && time <= 6) {
            params.put("paper_year", "上半年");
        } else {
            params.put("paper_year", "下半年");
        }
        params.put("paper_name", paper.getName());
        params.put("paper_time", paper.getTime().split("-")[0]);
        params.put("paper_type", "上午题");
        // 根据试卷编号查询所有选择题
        Example example = new Example(ChoiceQuestion.class);
        example.createCriteria().andEqualTo("paperId", pid);
        List<ChoiceQuestion> choiceQuestionVOS = choiceQuestionDao.selectByExample(example);
        List<ChoiceQuestionVO> choiceQuestionVOList = ChoiceQuestionConverter.converterToVOList(choiceQuestionVOS);
        List<Map<String, Object>> dataList = new ArrayList<>();
        if (choiceQuestionVOS.size() > 0) {
            //动态填充数据
            int num = 0;
            for (ChoiceQuestionVO c : choiceQuestionVOList) {
                Map<String, Object> data = new HashMap<>();
                data.put("serial_number", num + 1);
                data.put("quest_name", c.getName());
                data.put("select_a", c.getSelectA());
                data.put("select_b", c.getSelectB());
                data.put("select_c", c.getSelectC());
                data.put("select_d", c.getSelectD());
                dataList.add(data);
                num++;
            }
        }
        params.put("practiceList", dataList);
        //生成临时文件地址
        String filePath = "D:\\Download\\";
        //文件唯一名称
        String fileOnlyName = "results.doc";
        /** 生成word  数据包装，模板名，文件生成路径，生成的文件名*/
        createWord(params, "export-paper-template.ftl", filePath, fileOnlyName, res);
    }


    @Override
    public PageVO<PaperVO> getPaperList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Paper> papers = paperDao.selectAll();
        List<PaperVO> pagerVOS = PaperConverter.converterToVOList(papers);
        PageInfo<Paper> pageInfo = new PageInfo<>(papers);
        return new PageVO<>(pageInfo.getTotal(), pagerVOS);
    }

    /**
     * 试卷列表
     *
     * @param pageNum
     * @param pageSize
     * @param paperVO
     * @return
     */
    @Override
    public PageVO<PaperVO> findPaperList(Integer pageNum, Integer pageSize, PaperVO paperVO) {
        PageHelper.startPage(pageNum, pageSize);
        List<Paper> papers;
        Example o = new Example(Paper.class);
        Example.Criteria criteria = o.createCriteria();
        String time = "";
        String type = "";
        if (StringUtils.isNotEmpty(paperVO.getName())) {
            criteria.andEqualTo("name", paperVO.getName());
        }
        if (StringUtils.isNotNull(paperVO.getStatus())) {
            criteria.andEqualTo("status", paperVO.getStatus() ? CommonStatusEnum.AVAILABLE.getStatusCode() : CommonStatusEnum.DISABLE.getStatusCode());
        }
        if(StringUtils.isNotNull(paperVO.getCourseId())){
            criteria.andEqualTo("courseId", paperVO.getCourseId());
        }
        if(StringUtils.isNotEmpty(paperVO.getTime())){
            type= paperVO.getTime();
        }
        if(StringUtils.isNotEmpty(time)){
            if (type.equals("0")) {
                time = "-5-";
            }else{
                time = "-11-";
            }
            criteria.andLike("time", "%" + time + "%");
        }
        papers = paperDao.selectByExample(o);
        List<PaperVO> paperVOList = PaperConverter.converterToVOList(papers);
        for (PaperVO p : paperVOList) {
            Course cou = new Course();
            cou.setId(p.getCourseId());
            Course c = courseDao.selectOne(cou);
            p.setCourse(c.getName());
            // 当前用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            paperVO.setUser(loginUser.getUsername());
            TestRecord testRecord = new TestRecord();
            testRecord.setUserId(loginUser.getUserId());
            testRecord.setType(CommonStatusEnum.DISABLE.getStatusCode());
            testRecord.setPaperId(p.getId());
            TestRecord t = testRecordMapper.selectOne(testRecord);
            if (StringUtils.isNotNull(t)) {
                p.setExerciseNum(t.getNum());
            } else {
                p.setExerciseNum(CommonStatusEnum.DISABLE.getStatusCode());
            }
        }
        PageInfo<Paper> info = new PageInfo<>(papers);
        return new PageVO<>(info.getTotal(), paperVOList);
    }


    /**
     * 添加试卷
     *
     * @param paperVO
     */
    @Override
    public Paper add(PaperVO paperVO) {
        Paper paper = new Paper();
        BeanUtils.copyProperties(paperVO, paper);
        paper.setCreateTime(new Date());
        paper.setModifiedTime(new Date());
        @NotNull(message = "科目不能为空") Long course = paperVO.getCourseId();
        if (StringUtils.isNull(course)) {
            throw new CustomException("科目不能为空");
        }
        paper.setStatus(CommonStatusEnum.DISABLE.getStatusCode());
        paperDao.insert(paper);
        return paper;
    }

    /**
     * 编辑试卷
     *
     * @param id
     * @return
     */
    @Override
    public PaperVO edit(Long id) throws SystemException {
        Paper paper = paperDao.selectByPrimaryKey(id);
        if (StringUtils.isNull(paper)) {
            throw new CustomException("编辑的试卷不存在");
        }
        return PaperConverter.converterToPaperVO(paper);
    }

    /**
     * 更新试卷
     *
     * @param id
     * @param paperVO
     */
    @Override
    public void update(Long id, PaperVO paperVO) {
        Paper paper = new Paper();
        BeanUtils.copyProperties(paperVO, paper);
        paper.setStatus(paperVO.getStatus() ? CommonStatusEnum.AVAILABLE.getStatusCode() : CommonStatusEnum.DISABLE.getStatusCode());
        paper.setModifiedTime(new Date());
        @NotNull(message = "科目不能为空") Long categoryKeys = paperVO.getCourseId();
        if (StringUtils.isNull(categoryKeys)) {
            throw new CustomException("科目不能为空");
        }
        paperDao.updateByPrimaryKey(paper);
    }

    /**
     * 删除试卷
     *
     * @param id
     */
    @Override
    public int delete(Long id) {
        // 判断试卷下是否有试题
        Example o1 = new Example(ChoiceQuestion.class);
        Example.Criteria criteria1 = o1.createCriteria();
        criteria1.andEqualTo("paperId", id);
        Example o2 = new Example(AnswerQuestion.class);
        Example.Criteria criteria2 = o2.createCriteria();
        criteria2.andEqualTo("paperId", id);
        if (choiceQuestionDao.selectByExample(o1).size() > 0 || answerQuestionDao.selectByExample(o2).size() > 0) {
            return 0;
        } else {
            Paper paper = paperDao.selectByPrimaryKey(id);
            if (StringUtils.isNull(paper)) {
                throw new CustomException("试卷不存在");
            } else {
                return paperDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<PaperVO> getAllPaperList(PaperDTO paperDTO) {
        System.out.println("---" + paperDTO);
        List<PaperVO> paperVOList = null;
        Example o = new Example(Paper.class);
        Example.Criteria criteria = o.createCriteria();
        // 先查询科目 如果有等级 时间 查询具体科目
        Course course = new Course();
        // 如果只有名称模糊查询试卷名称
        if (StringUtils.isNotEmpty(paperDTO.getName())) {
            criteria.andLike("name", "%" + paperDTO.getName() + "%");
        }
        criteria.andEqualTo("status", paperDTO.getStatus());
        // 查询一级分类 -- 中级 初级
        Long id = null;
        if (StringUtils.isNotEmpty(paperDTO.getLevel())) {
            Category category = new Category();
            category.setName(paperDTO.getLevel());
            if (StringUtils.isNotNull(courseCategoryDao.selectOne(category))) {
                id = courseCategoryDao.selectOne(category).getId();
                course.setOneCategoryId(id);
            }
        }
        // 查询三级分类 -- 年度
        if (StringUtils.isNotNull(paperDTO.getAnnual())) {
            Category category = new Category();
            category.setName(paperDTO.getAnnual());
            if (StringUtils.isNotNull(courseCategoryDao.selectOne(category))) {
                id = courseCategoryDao.selectOne(category).getId();
                course.setThreeCategoryId(id);
            }
        }
        if (StringUtils.isNotNull(paperDTO.getYear())) {
            criteria.andLike("time", "%" + paperDTO.getYear() + "%");
        }
        if (StringUtils.isNotNull(courseDao.select(course))) {
            for (Course c : courseDao.select(course)) {
                Example.Criteria e = o.createCriteria();
                e.orEqualTo("courseId", c.getId());
            }
            List<Paper> paperList = paperDao.selectByExample(o);
            paperVOList = PaperConverter.converterToVOList(paperList);
        }
        return paperVOList;
    }

    /**
     * 获取试卷试题列表
     *
     * @return
     */
    @Override
    public List<PaperQuestionVO> getPaperQuestionList() {
        List<PaperQuestionVO> paperQuestionVOS = new ArrayList<>();
        List<AnswerQuestion> answerQuestions = answerQuestionDao.selectAll();
        List<ChoiceQuestion> choiceQuestions = choiceQuestionDao.selectAll();
        Map<Long, List<AnswerQuestion>> answerListMap = answerQuestions.stream().collect(Collectors.groupingBy(AnswerQuestion::getPaperId));
        Map<Long, List<ChoiceQuestion>> choiceListMap = choiceQuestions.stream().collect(Collectors.groupingBy(ChoiceQuestion::getPaperId));
        for (Long choiceKey : choiceListMap.keySet()) {
            Paper paper = new Paper();
            paper.setId(choiceKey);
            Paper paperItem = paperDao.selectOne(paper);
            PaperQuestionVO paperQuestionVO = new PaperQuestionVO();
            paperQuestionVO.setName(paperItem.getName());
            paperQuestionVO.setTime(paperItem.getTime());
            paperQuestionVO.setChildren(Collections.singletonList(choiceListMap.get(choiceKey)));
            paperQuestionVOS.add(paperQuestionVO);
        }
        for (Long answerKey : answerListMap.keySet()) {
            Paper paper = new Paper();
            paper.setId(answerKey);
            Paper paperItem = paperDao.selectOne(paper);
            PaperQuestionVO paperQuestionVO = new PaperQuestionVO();
            paperQuestionVO.setName(paperItem.getName());
            paperQuestionVO.setTime("上半年");
            paperQuestionVO.setChildren(Collections.singletonList(answerListMap.get(answerKey)));
            paperQuestionVOS.add(paperQuestionVO);
        }
        if (!StringUtils.isEmpty(paperQuestionVOS)) {
            return paperQuestionVOS;
        }

        return null;
    }

}
