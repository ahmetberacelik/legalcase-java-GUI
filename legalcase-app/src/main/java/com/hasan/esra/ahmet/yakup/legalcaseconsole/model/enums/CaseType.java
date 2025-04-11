package com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums;

/**
 * @brief Enumeration of case types
 * @details This enum defines the different categories of legal cases managed
 * in the system. Each type represents a distinct area of law with different
 * procedures, requirements, and legal considerations.
 */
public enum CaseType {
    /**
     * @brief Civil case type
     * @details Cases involving disputes between individuals or organizations
     * seeking remedies such as monetary damages or specific performance.
     * Examples include personal injury, property disputes, contract disputes,
     * and tort claims.
     */
    CIVIL,

    /**
     * @brief Criminal case type
     * @details Cases involving prosecution by the state against individuals or
     * entities for alleged violations of criminal law. These cases can result
     * in penalties such as fines, imprisonment, or probation.
     */
    CRIMINAL,

    /**
     * @brief Family law case type
     * @details Cases involving family relationships and domestic matters.
     * Examples include divorce, child custody, adoption, child support,
     * and domestic violence cases.
     */
    FAMILY,

    /**
     * @brief Corporate/Business case type
     * @details Cases involving business entities and commercial matters.
     * Examples include corporate governance, mergers and acquisitions,
     * securities regulation, intellectual property, and commercial disputes.
     */
    CORPORATE,

    /**
     * @brief Other case types
     * @details Cases that do not fall into the above categories. This can include
     * administrative law, immigration, tax law, environmental law, and other
     * specialized areas of legal practice.
     */
    OTHER
}