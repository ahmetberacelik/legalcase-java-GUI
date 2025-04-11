package com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums;

/**
 * @brief Enumeration of document types
 * @details This enum defines the categories of legal documents that can be
 * associated with cases in the legal case management system. Each type
 * represents a different class of legal document with distinct purposes
 * and handling requirements.
 */
public enum DocumentType {
    /**
     * @brief Contract document
     * @details Legal agreements between parties that outline terms, conditions,
     * obligations, and rights. Examples include service agreements, employment
     * contracts, and settlement agreements.
     */
    CONTRACT,

    /**
     * @brief Evidence document
     * @details Documents that support claims or defenses in a legal case.
     * This includes photographs, witness statements, expert reports, and
     * other documentary evidence submitted to support legal arguments.
     */
    EVIDENCE,

    /**
     * @brief Petition document
     * @details Formal written applications to a court requesting specific
     * legal action. This includes initial complaints, motions, and other
     * requests submitted to the court for consideration.
     */
    PETITION,

    /**
     * @brief Court order document
     * @details Official directives issued by a court. These include judgments,
     * rulings, decisions, and other formal pronouncements that have legal
     * force and effect in the context of a case.
     */
    COURT_ORDER,

    /**
     * @brief Other document types
     * @details Documents that do not fall into the above categories. This
     * catch-all category includes correspondence, notes, memoranda, and
     * miscellaneous documents related to a case.
     */
    OTHER
}