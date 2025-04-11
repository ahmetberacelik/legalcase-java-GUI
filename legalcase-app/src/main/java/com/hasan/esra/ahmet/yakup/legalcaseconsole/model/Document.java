/**
 * @file Document.java
 * @brief Document entity class for the Legal Case Tracker system
 *
 * This file defines the Document entity class which represents legal documents
 * in the system. Documents can be attached to cases and contain content of various types.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */

package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.*;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief Document entity representing legal documents
 * @details This class represents a document in the legal system. Documents
 * can be attached to cases and contain content of various types.
 */
@DatabaseTable(tableName = "documents")
@Getter
@Setter
@NoArgsConstructor
public class Document extends BaseEntity {

    /**
     * @brief Title of the document
     * @details Descriptive name of the document
     */
    @DatabaseField(canBeNull = false)
    private String title;

    /**
     * @brief Type of document
     * @details Classification of the document (e.g., contract, evidence, ruling)
     */
    @DatabaseField(canBeNull = false)
    private DocumentType type;

    /**
     * @brief Reference to the associated case
     * @details Foreign key relationship to the Case entity
     */
    @DatabaseField(foreign = true, columnName = "case_id")
    private Case cse;

    /**
     * @brief MIME type of the document content
     * @details Describes the format of the content (e.g., text/plain, application/pdf)
     */
    @DatabaseField(columnName = "content_type")
    private String contentType;

    /**
     * @brief Textual content of the document
     * @details The actual content or text of the document
     */
    @DatabaseField(columnDefinition = "TEXT")
    private String content;

    /**
     * @brief Constructor with id
     * @param id The unique identifier for this document
     * @param title The title of the document
     * @param type The type of document
     */
    public Document(Long id, String title, DocumentType type) {
        super(id);
        this.title = title;
        this.type = type;
    }

    /**
     * @brief Constructor without id
     * @param title The title of the document
     * @param type The type of document
     * @param cse The case this document is associated with
     * @param content The textual content of the document
     */
    public Document(String title, DocumentType type, Case cse, String content) {
        this.title = title;
        this.type = type;
        this.cse = cse;
        this.content = content;
        this.contentType = "text/plain";
    }

    /**
     * @brief String representation of the Document
     * @details Returns a string containing the document's key information
     * @return Formatted string with document details
     */
    @Override
    public String toString() {
        return "Document{id=" + getId() + ", title='" + title +
                "', type=" + type + ", caseId=" + (cse != null ? cse.getId() : "null") + "}";
    }
}