package com.crm.service;

import com.crm.dto.response.KnowledgeResponse;

public interface IFileUploadService {
    KnowledgeResponse uploadAndIndex(String filename, byte[] fileContent, Long customerId);
}
