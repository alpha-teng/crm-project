package com.crm.service.impl;

import com.crm.dto.request.KnowledgeRequest;
import com.crm.dto.response.KnowledgeResponse;
import com.crm.service.IFileUploadService;
import com.crm.service.IKnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements IFileUploadService {

    private final IKnowledgeService knowledgeService;

    @Override
    public KnowledgeResponse uploadAndIndex(String filename, byte[] fileContent, Long customerId) {
        String text = extractText(filename, fileContent);
        
        if (text == null || text.trim().isEmpty()) {
            throw new RuntimeException("无法从文件提取文本内容: " + filename);
        }

        log.info("Extracted {} chars from {}, creating knowledge doc...", text.length(), filename);

        KnowledgeRequest request = new KnowledgeRequest();
        request.setContent(text);
        request.setTitle(filename);
        request.setCustomerId(customerId);

        return knowledgeService.createDoc(request);
    }

    public String extractText(String filename, byte[] content) {
        String lower = filename.toLowerCase();
        try {
            if (lower.endsWith(".pdf")) {
                return extractPdfText(content);
            } else if (lower.endsWith(".docx")) {
                return extractDocxText(content);
            } else if (lower.endsWith(".doc")) {
                // .doc not supported by POI OOXML, try basic approach
                return extractBasicText(content);
            } else if (lower.endsWith(".txt")) {
                return new String(content, "UTF-8");
            } else {
                throw new RuntimeException("不支持的文件格式: " + filename + "，支持 PDF/DOCX/DOC/TXT");
            }
        } catch (Exception e) {
            log.error("Failed to extract text from {}: {}", filename, e.getMessage(), e);
            throw new RuntimeException("文件解析失败: " + e.getMessage(), e);
        }
    }

    private String extractPdfText(byte[] content) throws IOException {
        try (PDDocument doc = Loader.loadPDF(content)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        }
    }

    private String extractDocxText(byte[] content) {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(content))) {
            StringBuilder sb = new StringBuilder();
            List<XWPFParagraph> paras = doc.getParagraphs();
            for (int i = 0; i < paras.size(); i++) {
                String line = paras.get(i).getText();
                if (line != null && !line.trim().isEmpty()) {
                    if (sb.length() > 0) sb.append("\n");
                    sb.append(line);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("DOCX解析失败: " + e.getMessage(), e);
        }
    }

    private String extractBasicText(byte[] content) {
        // Try UTF-8 then GBK fallback
        try {
            return new String(content, "UTF-8");
        } catch (Exception e1) {
            try {
                return new String(content, "GBK");
            } catch (Exception e2) {
                return new String(content);
            }
        }
    }
}
