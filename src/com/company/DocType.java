package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DocType {
    AR_GENERAL("ar_general"),
    AR_GEOSPR("ar_geospr"),
    AR_HRDMEMO("ar_hrdmemo"),
    AR_MANAGEMENT("ar_management"),
    AR_PROJECTDOC("ar_projectdoc"),
    //DM_DOCUMENT("dm_document"),
    DMC_JAR("dmc_jar"),
    DMC_TCF_ACTIVITY_TEMPLATE("dmc_tcf_activity_template"),
    EMC_MAIL_OBJECT("emc_mail_object"),
    EMC_REPORT_DESIGN("emc_report_design"),
    EXPORT_REQUEST("export_request"),
    IOSO_DOCUMENT("ioso_document"),
    PJC_NAVIGATION_C("pjc_navigation_c"),
    PJC_TRANSLATION("pjc_translation"),
    REVIEW_COMMENT_DOC("review_comment_doc"),
    SAFE_APPENDIX("safe_appendix"),
    SAFE_IN("safe_in"),
    SAFE_OUT("safe_out"),
    SAFE_TECH_DOC("safe_tech_doc"),
    SPDF_DOCUMENT("spdf_document"),
    SUMMARY_DOC("summary_doc"),//,
    CMIS_GENERIC("cmis:document");

    private final String type_name;
    private final String alf_type_name;
    private final Map properties_map;
    //private final List<String> aspects;
    static List<String> getList(){
        List<String> doc_types = new ArrayList<String>(1);
        for (DocType  dt : DocType.values()) {
            doc_types.add(dt.type_name());
        }
        return doc_types;
    }
    static DocType getType(String type_name){
        DocType docType = null;
        for (DocType  dt : DocType.values()) {
            if (dt.type_name().equals(type_name)) {
                docType = dt;
                break;
            }
        }
        return docType;
    }

    DocType(String type_name){
        String alf_type_name1;
        this.type_name = type_name;
        alf_type_name1 = "D:safe:" + type_name;
        Map<String, String> properties_map = new HashMap<String, String>();
        properties_map.put("cmis:objectId", "safe:original_id");

        switch (type_name) {
            case "ar_general":
                properties_map.put("ka_agreed_by", "safe:ka_agreed_by");
                properties_map.put("ar_department", "safe:ar_department");
                properties_map.put("ar_service", "safe:ar_service");
                properties_map.put("ka_copy_to", "safe:ka_copy_to");
                properties_map.put("ar_status", "safe:ar_status");
                properties_map.put("doc_type", "safe:doc_type");
                properties_map.put("is_confidential", "safe:is_confidential");
                properties_map.put("ka_approved_by", "safe:ka_approved_by");
                properties_map.put("ka_exp_resp_date", "safe:ka_exp_resp_date");
                properties_map.put("ka_creation_date", "safe:ka_creation_date");
                properties_map.put("ka_folder", "safe:ka_folder");
                properties_map.put("ka_katco_no", "safe:ka_katco_no");
                properties_map.put("ka_keywords", "safe:ka_keywords");
                properties_map.put("ka_received_date", "safe:ka_received_date");
                properties_map.put("ka_more_rec", "safe:ka_more_rec");
                properties_map.put("ka_recipients", "safe:ka_recipients");
                properties_map.put("ka_reply_req_str", "safe:ka_reply_req_str");
                properties_map.put("ka_sent_date", "safe:ka_sent_date");
                properties_map.put("revision", "safe:revision");
                properties_map.put("seq_no", "safe:seq_no");
                properties_map.put("unit", "safe:unit");
                properties_map.put("ka_theme", "safe:ka_theme");
                break;
            case "ar_geospr":
                properties_map.put("ar_department", "safe:ar_department");
                properties_map.put("ar_service", "safe:ar_service");
                properties_map.put("ar_status", "safe:ar_status");
                properties_map.put("doc_type", "safe:doc_type");
                properties_map.put("ka_creation_date", "safe:ka_creation_date");
                properties_map.put("ka_material", "safe:ka_material");
                properties_map.put("ka_sent_date", "safe:ka_sent_date");
                properties_map.put("ka_sap_no", "safe:ka_sap_no");
                properties_map.put("ka_sap_code", "safe:ka_sap_code");
                properties_map.put("revision", "safe:revision");
                properties_map.put("seq_no", "safe:seq_no");
                properties_map.put("unit", "safe:unit");
                properties_map.put("ka_three_letters_code", "safe:ka_three_letters_code");
                break;
            case "ar_hrdmemo":
                properties_map.put("ka_hrd_agreed_by", "safe:ka_hrd_agreed_by");
                properties_map.put("ka_agreed_position", "safe:ka_agreed_position");
                properties_map.put("ka_approver_position", "safe:ka_approver_position");
                properties_map.put("ar_department", "safe:ar_department");
                properties_map.put("ka_author_position", "safe:ka_author_position");
                properties_map.put("ar_service", "safe:ar_service");
                properties_map.put("ar_status", "safe:ar_status");
                properties_map.put("doc_type", "safe:doc_type");
                properties_map.put("is_confidential", "safe:is_confidential");
                properties_map.put("ka_approved_by", "safe:ka_approved_by");
                properties_map.put("ka_creation_date", "safe:ka_creation_date");
                properties_map.put("ka_folder", "safe:ka_folder");
                properties_map.put("ka_recipients", "safe:ka_recipients");
                properties_map.put("ka_sent_date", "safe:ka_sent_date");
                properties_map.put("revision", "safe:revision");
                properties_map.put("seq_no", "safe:seq_no");
                properties_map.put("unit", "safe:unit");
                break;
            case "ar_management":
                properties_map.put("a_stamp_date", "safe:a_stamp_date");
                properties_map.put("ar_status", "safe:ar_status");
                properties_map.put("contract", "safe:contract");
                properties_map.put("doc_type", "safe:doc_type");
                properties_map.put("is_confidential", "safe:is_confidential");
                properties_map.put("ka_contractor_number", "safe:ka_contractor_number");
                properties_map.put("ka_katco_no", "safe:ka_katco_no");
                properties_map.put("ka_received_date", "safe:ka_received_date");
                properties_map.put("ka_reply_req_str", "safe:ka_reply_req_str");
                properties_map.put("ka_sent_date", "safe:ka_sent_date");
                properties_map.put("ka_recipient", "safe:ka_recipient");
                properties_map.put("organization", "safe:organization");
                properties_map.put("originator", "safe:originator");
                properties_map.put("revision", "safe:revision");
                properties_map.put("seq_no", "safe:seq_no");
                properties_map.put("unit", "safe:unit");
                properties_map.put("ka_owner", "safe:ka_owner");
                break;
            case "ar_projectdoc":
                properties_map.put("a_stamp_date", "safe:a_stamp_date");
                properties_map.put("ar_language", "safe:ar_language");
                properties_map.put("ar_status", "safe:ar_status");
                properties_map.put("discipline", "safe:discipline");
                properties_map.put("doc_type", "safe:doc_type");
                properties_map.put("ka_area", "safe:ka_area");
                properties_map.put("ka_subarea", "safe:ka_subarea");
                properties_map.put("organization", "safe:organization");
                properties_map.put("originator", "safe:originator");
                properties_map.put("ka_rfm_nb", "safe:ka_rfm_nb");
                properties_map.put("ka_section", "safe:ka_section");
                properties_map.put("revision", "safe:revision");
                properties_map.put("revision_date", "safe:revision_date");
                properties_map.put("seq_no", "safe:seq_no");
                properties_map.put("unit", "safe:unit");
                properties_map.put("ka_unit", "safe:ka_unit");
                break;
            case "dm_document":
                properties_map.put("current_state", "safe:current_state");
                properties_map.put("keywords", "safe:keywords");
                properties_map.put("owner_name", "safe:owner_name");
                properties_map.put("subject", "safe:subject");
                break;
            case "dmc_jar":
            case "dmc_tcf_activity_template":
            case "emc_report_design":
            case "export_request":
            case "pjc_translation":
            case "safe_out":
            case "spdf_document":
                properties_map.put("keywords", "safe:keywords");
                properties_map.put("owner_name", "safe:owner_name");
                properties_map.put("subject", "safe:subject");
                break;
            case "emc_mail_object":
                properties_map.put("current_state", "safe:current_state");
                properties_map.put("is_confidential", "safe:is_confidential");
                properties_map.put("sender", "safe:sender");
                properties_map.put("priority", "safe:priority");
                properties_map.put("message_subject", "safe:message_subject");
                properties_map.put("recipients_reasons", "safe:recipients_reasons");
                properties_map.put("recipients_addresses", "safe:recipients_addresses");
                properties_map.put("send_sequentially", "safe:send_sequentially");
                properties_map.put("recipients_names", "safe:recipients_names");
                break;
            case "ioso_document":
                properties_map.put("owner_name", "safe:owner_name");
                properties_map.put("subject", "safe:subject");
                break;
            case "pjc_navigation_c":
                break;
            case "review_comment_doc":
            case "safe_appendix":
                properties_map.put("owner_name", "safe:owner_name");
                break;
            case "safe_in":
                properties_map.put("a_stamp_date", "safe:a_stamp_date");
                properties_map.put("ar_status", "safe:ar_status");
                properties_map.put("contract", "safe:contract");
                properties_map.put("issue_date", "safe:issue_date");
                properties_map.put("doc_type", "safe:doc_type");
                properties_map.put("originator_name", "safe:originator_name");
                properties_map.put("recipient", "safe:recipient");
                properties_map.put("organization", "safe:organization");
                properties_map.put("originator", "safe:originator");
                properties_map.put("recipients_names", "safe:recipients_names");
                properties_map.put("seq_no", "safe:seq_no");
                properties_map.put("unit", "safe:unit");
                break;
            case "safe_tech_doc":
                properties_map.put("a_stamp_date", "safe:a_stamp_date");
                properties_map.put("ka_building", "safe:ka_building");
                properties_map.put("ar_confidentiality", "safe:ar_confidentiality");
                properties_map.put("ar_language", "safe:ar_language");
                properties_map.put("ar_status", "safe:ar_status");
                properties_map.put("contract", "safe:contract");
                properties_map.put("discipline", "safe:discipline");
                properties_map.put("doc_type", "safe:doc_type");
                properties_map.put("ka_design_revision", "safe:ka_design_revision");
                properties_map.put("is_confidential", "safe:is_confidential");
                properties_map.put("ka_discipline", "safe:ka_discipline");
                properties_map.put("ka_area", "safe:ka_area");
                properties_map.put("ka_contractor_number", "safe:ka_contractor_number");
                properties_map.put("ka_folio", "safe:ka_folio");
                properties_map.put("a_last_return_code", "safe:a_last_return_code");
                properties_map.put("a_last_return_date", "safe:a_last_return_date");
                properties_map.put("ar_exp_number", "safe:ar_exp_number");
                properties_map.put("ar_exp_flag", "safe:ar_exp_flag");
                properties_map.put("ka_subarea", "safe:ka_subarea");
                properties_map.put("ka_project_extension", "safe:ka_project_extension");
                properties_map.put("organization", "safe:organization");
                properties_map.put("originator", "safe:originator");
                properties_map.put("revision_date", "safe:revision_date");
                properties_map.put("seq_no", "safe:seq_no");
                properties_map.put("ka_phase", "safe:ka_phase");
                properties_map.put("subject", "safe:subject");
                properties_map.put("unit", "safe:unit");
                break;
            case "summary_doc":
                break;
            case "cmis:document":
                alf_type_name1 = type_name;
                break;
            default:
                break;
        }
        this.alf_type_name = alf_type_name1;
        this.properties_map = properties_map;

    }
    public String type_name() { return type_name; }
    public Map properties_map() { return properties_map; }
    public String getAlf_type_name() {
        return alf_type_name;
    }
}
