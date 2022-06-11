package com.zeng.business.converter;

import com.zeng.common.converter.ImageBase64Converter;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.core.XWPFConverterException;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/26 19:50
 **/
public class ConvertHtml {
    /**
     * 导入WORD2007
     *
     * @param wordFile            被转换的word文件
     * @param outputFolder        转换后HTML文件存放位置
     * @param outputPictureFolder 转换后原word中图片存放位置
     * @throws TransformerException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static String convert2Html(File wordFile, File outputFolder,
                                      final File outputPictureFolder, String tempPath) throws TransformerException,
            IOException, ParserConfigurationException {
        //创建被转换的word HWPFDocument对象
        String filePath = tempPath;
        HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(wordFile));

        //创建word转换器，并设置对于图片如何处理
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            public String savePicture(byte[] content, PictureType pictureType,
                                      String suggestedName, float widthInches, float heightInches) {
                return filePath
                        + suggestedName;
            }
        });
        //开始转换word为HTML
        wordToHtmlConverter.processDocument(wordDocument);
        //开始转换word中图片到图片存放目录
        List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                try {
                    pic.writeImageContent(new FileOutputStream(
                            filePath
                                    + pic.suggestFullFileName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        //将word转换为HTML，输出到指定目录
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(out);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        out.close();
        return new String(out.toByteArray());
    }


    public static String docx2Html(File docxFile, String fileName, String tempPath) {
        String html = null;
        String htmlPath = docxFile.getAbsolutePath().replaceAll(docxFile.getName(), "") + docxFile.getName().replaceAll(".docx", ".html");
        String imagePath = docxFile.getAbsolutePath().replaceAll(docxFile.getName(), "") + "docxImage/";
        File imageFile = new File(imagePath);
        if (!docxFile.exists()) {
            System.out.println("文件不存在!");
        }
        try {
            InputStream in = new FileInputStream(docxFile);
            XWPFDocument document = new XWPFDocument(in);
            //存储图片
            XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver(imageFile));
            options.setExtractor(new FileImageExtractor(imageFile));
            options.URIResolver((uri) -> {
                File imgFile = new File(imagePath + uri);//获取图片
                String img = null;
                String imgName = System.currentTimeMillis() + "_" + imgFile.getName();//设置图片文件名
                InputStream is = null;
                try {
                    is = new FileInputStream(imgFile);
                    byte[] isbyte = new byte[is.available()];
                    OutputStream os = new FileOutputStream(imagePath + imgName);
                    BufferedOutputStream bos = new BufferedOutputStream(os);
                    byte[] bytes = toByteArray(imgFile);
                    bos.write(bytes, 0, bytes.length);
                    //上传图片
//                    int temp;
//                    //一个一个字节的读取并写入
//                    while((temp=is.read())!=(-1))
//                    {
//                        os.write(temp);
//                    }
                    bos.flush();
                    bos.close();
                    is.close();
                    String data = null;
                    if (imgName.endsWith("jpg") || imgName.endsWith("jpeg")) {
                        data = "data:image/jpeg;base64,";
                    } else if (imgName.endsWith("png")) {
                        data = "data:image/png;base64,";
                    } else if (imgName.endsWith("gif")) {
                        data = "data:image/gif;base64,";
                    }
                    img = ImageBase64Converter.convertFileToBase64(imagePath + imgName);
                    img = data + img;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return img;
            });
            options.setIgnoreStylesIfUnused(false);
            options.setFragment(true);
            // 3) 将 XWPFDocument转换成XHTML
//                OutputStream out = new FileOutputStream(new File(tempPath+"\\a.html"));
//                XHTMLConverter.getInstance().convert(document, out, options);


            //也可以使用字符数组流获取解析的内容
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XHTMLConverter.getInstance().convert(document, baos, options);
            html = new String(baos.toByteArray());
//                                System.out.println(html);
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XWPFConverterException e) {
            e.printStackTrace();
        }
        return html;
    }


    /**
     * /**
     * 2003版本word转换成html
     *
     * @throws IOException
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    public static String Word2003ToHtml(String filepath, String tempPath) throws IOException, TransformerException, ParserConfigurationException {
        final String imagepath = tempPath;//解析时候如果doc文件中有图片  图片会保存在此路径
        String htmlName = "123.html";
        final String file = filepath;
        InputStream input = new FileInputStream(new File(file));
        HWPFDocument wordDocument = new HWPFDocument(input);
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        //设置图片存放的位置
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches, float heightInches) {
                String url = null;
                File imgPath = new File(imagepath);
                if (!imgPath.exists()) {//图片目录不存在则创建
                    imgPath.mkdirs();
                }
                File file = new File(imagepath + suggestedName);
                try {
                    OutputStream os = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(os);
                    bos.write(content, 0, content.length);
//                    os.write(content);
//                    os.close();
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

//        File htmlFile = new File(filepath + htmlName);
//        OutputStream outStream = new FileOutputStream(htmlFile);

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
        baos.close();
        outStream.close();
        return content;
    }

    public static byte[] toByteArray(File file) throws IOException {
        File f = file;
        if (!f.exists()) {
            throw new FileNotFoundException("file not exists");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

}
