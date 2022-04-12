package com.company;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.tika.Tika;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    static int COUNTER = 0;
    static Folder SOURCE_FOLDER;
    static Folder DESTINATION_FOLDER;
    static String SOURCE_PATH = "/";
    static String DESTINATION_PATH = "/Sites/safe-legacy/documentLibrary";
    static Session SOURCE_SESSION;
    static Session DESTINATION_SESSION;
    static DateTimeFormatter DFT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    static Tika TIKA = new Tika();

    static Map<String, String> mime_types = new HashMap<String, String>();

    static SafeRelations safeRelations = new SafeRelations();

    private static void initMimeTypes() {
        mime_types.put("application/pdf", ".pdf");
        mime_types.put("application/msword", ".doc");
        mime_types.put("application/vnd.ms-excel", ".xls");
        mime_types.put("application/vnd.ms-outlook", ".msg");
        mime_types.put("application/vnd.ms-powerpoint", ".ppt");
        mime_types.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx");
        mime_types.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx");
        mime_types.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");
        mime_types.put("application/x-zip-compressed", ".zip");
        mime_types.put("image/bmp", ".bmp");
        mime_types.put("image/jpeg", ".jpg");
        mime_types.put("image/png", ".png");
        mime_types.put("image/tiff", ".tif");
        mime_types.put("application/acad", ".dwg");
        mime_types.put("text/xml", ".xml");
        mime_types.put("application/fdf", ".fdf");
        mime_types.put("image/vnd.dwg", ".dwg");
        mime_types.put("application/rtf", ".rtf");

    }

    private static Map<String, String> getSourceParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SessionParameter.USER, "dmatessov");
        params.put(SessionParameter.ATOMPUB_URL, "http://bkzalmadb22:9080/cmis/resources/");
        params.put(SessionParameter.PASSWORD, "*******");
        params.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        params.put(SessionParameter.REPOSITORY_ID, "KATCO");
        return params;
    }

    private static Map<String, String> getDestinationParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SessionParameter.USER, "admin");
        params.put(SessionParameter.PASSWORD, "*******");
        params.put(SessionParameter.ATOMPUB_URL, "https://katco-ecm-prod.bdom.ad.corp/alfresco/api/-default-/public/cmis/versions/1.1/atom");
        params.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        params.put(SessionParameter.REPOSITORY_ID, "-default-");
        return params;
    }


    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }

    private static String listToString(List<String> list) {
        String temp = "";
        for (String s : list) {
            if (!temp.isEmpty()) {
                s = ", " + s;
            }
            temp = temp + s;
        }
        return temp;
    }

    private static boolean propertyAccessible(String property, CmisObject document) {
        boolean returnValue = false;
        try {
            Property p = document.getProperty(property);
            String s = p.getValueAsString();
            returnValue = true;
        } catch (Exception e) {
        }
        ;
        return returnValue;
    }

    private static String getValidName(String name, String mimetype) {
        String return_value = name.trim();
        return_value = return_value.replaceAll("[:\\\\/*?|<>/%]", "");
        return_value = return_value.replaceAll("[.]$", "");
        return_value = return_value.replaceAll("[\"]", "'");

        String extension = mime_types.get(mimetype);
        String curExt = "";
        int lof = return_value.lastIndexOf(".");
        if (lof != -1){
            curExt = return_value.substring(lof);
            curExt = curExt.toLowerCase();
        }
        if (extension != null) {
            if (!extension.equals(curExt)) {
                if (mime_types.containsValue(curExt)){
                    return_value = return_value.substring(0,return_value.lastIndexOf(curExt)).concat(extension);
                } else
                {
                    return_value = return_value.concat(extension);
                }
            }
        }
        return return_value;
    }

    private static String getCorrectMimeType(ContentStream contentStream){
        String mimeType = null; //если данных нет
        if (contentStream != null) {
            mimeType = contentStream.getMimeType();
            if (contentStream.getLength() != 0) {
                //Некоторые автокадовские документы не имеют mimeType, ниже исправление этого
                if (mimeType == null) {
                    mimeType = "application/acad";
                }
                //Некоторые документы имеют неверный MimeType в SAFE, поэтому проверяем по возможности с
                //помощью Apache Tika
                String mt_detected = null;
                try {
                    mt_detected = TIKA.detect(contentStream.getStream());
                } catch (Exception e) {
                }
                if (mt_detected != null & !mimeType.equals(mt_detected)) {
                    if (mime_types.get(mt_detected) != null) {
                        System.out.println("Another mime type detected, changing " + mimeType + " ==> " + mt_detected);
                        mimeType = mt_detected;
                    }
                    else{
                        System.out.println("!!! Unknown mime type detected " + mt_detected);
                    }
                }
            }
        }
        return mimeType;
    }

    private static Folder createCMISFolder(Folder parent, String name){
        name = getValidName(name, "");
        Folder newFolder = parent;
        if (!name.equals("")) {
            if (DESTINATION_SESSION.existsPath(parent.getPath(), name)) {
                newFolder = (Folder) DESTINATION_SESSION.getObjectByPath(parent.getPath() + "/" + name);
            } else {
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
                properties.put(PropertyIds.NAME, name);
                newFolder = parent.createFolder(properties);
            }
        }
        return newFolder;
    }

    private static Folder createCMISPath(Folder parent, String path){
        String[] sequence = path.split("/");
        Folder new_folder = parent;
        for(String item : sequence){
            new_folder = createCMISFolder(new_folder, item);
        }
        return new_folder;
    }

    private static Document createCMISDocument(CmisObject obj, Folder des_folder){
        String objTypeId = obj.getProperty(PropertyIds.OBJECT_TYPE_ID).getValueAsString();
        Document newDocument = null;

        if (DocType.getList().contains(objTypeId)) {

            DocType docType = DocType.getType(objTypeId);
            COUNTER = COUNTER + 1;
            LocalDateTime now = LocalDateTime.now();

            Document d = (Document) obj;
            String mimeType = getCorrectMimeType(d.getContentStream());
            String obj_name = getValidName(d.getName(), mimeType);
            String nameToFind = obj_name;


            Rendition pdfRendition = null;
            ContentStream pdfContentStream = null;
            String pdfMimeType = "application/pdf";
            String pdf_obj_name = null;

            try {
                if (!d.getRenditions().isEmpty()) {
                    pdfRendition = d.getRenditions().get(0);
                    pdfContentStream = pdfRendition.getContentStream();
                    pdf_obj_name = getValidName(d.getName(), pdfMimeType);
                    nameToFind = pdf_obj_name;
                }
            } catch (Exception e) { }

            // 1. Создание документа
            if (DESTINATION_SESSION.existsPath(des_folder.getPath(), nameToFind)) {
                System.out.println("INFO "+DFT.format(now)+" "+COUNTER+ " Updating document [" + nameToFind + "]");
                newDocument = (Document) DESTINATION_SESSION.getObjectByPath(des_folder.getPath() + "/" + nameToFind);
            } else {
                System.out.println("INFO "+DFT.format(now)+" "+COUNTER+" Creating new document [" + obj_name
                        + "] with content of "+String.format("%.2f", d.getContentStreamLength()/1048576.0)+" Mb");
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put(PropertyIds.OBJECT_TYPE_ID, docType.getAlf_type_name());
                properties.put(PropertyIds.NAME, obj_name);

                if (d.getContentStreamLength() != 0){
                    ContentStream contentStream = DESTINATION_SESSION.getObjectFactory().createContentStream(
                            d.getContentStream().getFileName(), d.getContentStream().getLength(),
                            mimeType, d.getContentStream().getStream());
                    newDocument = des_folder.createDocument(properties, contentStream, VersioningState.MAJOR);
                } else
                {
                    newDocument = des_folder.createDocument(properties, d.getContentStream(), VersioningState.MAJOR);
                }

                if (pdfRendition != null){
                    System.out.println("INFO "+DFT.format(now)+" "+COUNTER+" Creating pdf rendition [" + pdf_obj_name
                            + "] with content of "+String.format("%.2f", pdfContentStream.getLength()/1048576.0)+" Mb");
                    ObjectId pwcId = newDocument.checkOut();
                    Document pwc = (Document) DESTINATION_SESSION.getObject(pwcId);
                    pwc.rename(pdf_obj_name);
                    pwc.checkIn(true,null,pdfContentStream,"PDF rendition");
                    newDocument = (Document) DESTINATION_SESSION.getObjectByPath(des_folder.getPath() + "/" + nameToFind);
                }
            }
            // 2. обновление специфичных свойств safe
            //обновление SECONDARY_OBJECT_TYPE_IDS
            Property<String> p = newDocument.getProperty(PropertyIds.SECONDARY_OBJECT_TYPE_IDS);
            ArrayList al = p.getValue();
            Map<String, Object> updateAspects = new HashMap<String, Object>();
            if (al.indexOf("P:cm:titled") == -1) {
                al.add("P:cm:titled");
            }
            if (al.indexOf("P:cm:author") == -1) {
                al.add("P:cm:author");
            }
            updateAspects.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, al);
            newDocument.updateProperties(updateAspects);


            Map<String, Object> updateProperties = new HashMap<String, Object>();
            ItemIterable<QueryResult> results = SOURCE_SESSION.query(
                    "SELECT * FROM cmis:document WHERE cmis:objectid = '" + obj.getId() + "'", false);
            for (QueryResult hit : results) {
                String author = "";
                String description = "";
                String title = "";
                for (PropertyData<?> property : hit.getProperties()) {
                    String queryName = property.getQueryName();
                    if (queryName.equals("ka_author") | queryName.equals("ka_hrd_author") | queryName.equals("authors")) {
                        author = author + " " + listToString((List<String>) property.getValues());
                    } else if (queryName.equals("ka_comments_en") | queryName.equals("ka_comments_kzt")) {
                        description = description + " " + listToString((List<String>) property.getValues());
                    } else if (queryName.equals("title") | queryName.equals("ka_title_en")) {
                        title = title + " " + listToString((List<String>) property.getValues());
                    } else {
                        //String prop_name = properties_map.get(queryName);
                        Map<String, String> map = new HashMap<String, String>();
                        map = docType.properties_map();
                        String prop_name = map.get(queryName);
                        if (prop_name != null) {
                            List value = property.getValues();
                            if (Dictionaries.getList().contains(queryName)) {
                                value = Dictionaries.getValues(queryName, (List<Object>) property.getValues());
                            }
                            updateProperties.put(prop_name, value);
                        }
                    }
                }

                if (propertyAccessible("cm:author", newDocument)) {updateProperties.put("cm:author", author.trim());}
                if (propertyAccessible("cm:description", newDocument)) {updateProperties.put("cm:description", description.trim());}
                if (propertyAccessible("cm:title", newDocument)) {
                    updateProperties.put("cm:title", title.trim());}
            }
            if (!updateProperties.isEmpty()) {
                newDocument.updateProperties(updateProperties);
            }

        } else {
            System.out.println("WARN Document type ["+objTypeId+"] is out of migration scope");
        }
        return newDocument;
    }



    private static void CopyCmisObject(CmisObject obj, Folder des_folder)
            throws IOException {
        Property p_base_type = obj.getProperty(PropertyIds.BASE_TYPE_ID);

        if (p_base_type.getValueAsString().equals("cmis:folder")) {
            Folder newFolder = createCMISFolder(des_folder, obj.getName());
            Folder f = (Folder) obj;
            ItemIterable<CmisObject> children = f.getChildren();
            while (children.iterator().hasNext()) {
                CmisObject item = children.iterator().next();
                CopyCmisObject(item, newFolder);
            }

        } else if (p_base_type.getValueAsString().equals("cmis:document"))
        {
            Document newDocument = createCMISDocument(obj, des_folder);

            //create document relations
            ArrayList<String[]> relation_list = safeRelations.data.get(obj.getId());
            if (relation_list != null) {

                for (String[] params : relation_list) {

                    Document originalDoc = (Document) SOURCE_SESSION.getObject(params[4]);
                    String fp = originalDoc.getPaths().get(0);
                    String path = fp.substring(0, fp.lastIndexOf("/"));
                    Folder targetFolder = createCMISPath(DESTINATION_FOLDER, path);
                    Document targetDoc = createCMISDocument(originalDoc, targetFolder);

                    OperationContext operationContext = DESTINATION_SESSION.createOperationContext();
                    operationContext.setIncludeRelationships(IncludeRelationships.SOURCE);
                    ObjectType objectType = DESTINATION_SESSION.getTypeDefinition("R:safe:"+params[0]);
                    ItemIterable<Relationship> relationships =
                            DESTINATION_SESSION.getRelationships(newDocument, true,
                                    RelationshipDirection.SOURCE, objectType,operationContext);

                    boolean relationshipExits = false;
                    boolean sameType = false;
                    boolean sameSource = false;
                    boolean sameTarget = false;
                    for (Relationship relationship : relationships){
                        RelationshipType relationshipType = relationship.getRelationshipType();
                        CmisObject sourceObject = relationship.getSource();
                        CmisObject targetObject = relationship.getTarget();

                        sameType = relationshipType.equals(objectType);
                        sameSource = sourceObject.getId().equals(newDocument.getId());
                        sameTarget = targetObject.getId().equals(targetDoc.getId());
                        relationshipExits = (sameType & sameSource & sameTarget);
                        if (relationshipExits) {break;}
                    }
                    if (!relationshipExits) {
                        Map<String, Object> properties = new HashMap<>();
                        properties.put(PropertyIds.NAME, "a new relationship");
                        properties.put(PropertyIds.OBJECT_TYPE_ID, "R:safe:" + params[0]);
                        properties.put(PropertyIds.SOURCE_ID, newDocument.getId());
                        properties.put(PropertyIds.TARGET_ID, targetDoc.getId());
                        ObjectId newRelId = DESTINATION_SESSION.createRelationship(properties);
                    }

                }
            }
        }
    }


    public static <key> void main(String[] args) throws IOException {

        initMimeTypes();

        //SAFE Connection
        SessionFactory sourceFactory = SessionFactoryImpl.newInstance();
        List<Repository> sourceRepositories = sourceFactory.getRepositories(getSourceParams());
        Repository sourceRepo = sourceRepositories.get(0);
        SOURCE_SESSION = sourceRepo.createSession();
        OperationContext oc = SOURCE_SESSION.createOperationContext();
        oc.setRenditionFilterString("application/pdf");
        SOURCE_SESSION.setDefaultContext(oc);
        SOURCE_FOLDER = (Folder) SOURCE_SESSION.getObjectByPath(SOURCE_PATH);

        //ALFRESCO Connection
        SessionFactory destinationFactory = SessionFactoryImpl.newInstance();
        List<Repository> destinationRepositories = destinationFactory.getRepositories(getDestinationParams());
        Repository destinationRepo = destinationRepositories.get(0);
        DESTINATION_SESSION = destinationRepo.createSession();
        DESTINATION_FOLDER = (Folder) DESTINATION_SESSION.getObjectByPath(DESTINATION_PATH);


        String[] folders_to_migrate = {
                //"Matessov Danil"
                //"1 - TECHNICAL DOCUMENTS"
                //"2 - MANAGEMENT DOCUMENTS"
                //"3 - TRANSMITTALS"
                "4 - SHARED DRAFT AREA"
                //"5 - PROJECT DOCUMENTS"
                //,"Shipments"
                //"GENERAL"
                //,"Templates"
                //,"Resources"
                };

        ItemIterable<CmisObject> children = SOURCE_FOLDER.getChildren();
        while (children.iterator().hasNext()) {
            CmisObject item = children.iterator().next();
            if (Arrays.asList(folders_to_migrate).contains(item.getName())) {
                CopyCmisObject(item, DESTINATION_FOLDER);
            }
        }



//        //CmisObject folder = sourceSession.getObjectByPath("/1 - TECHNICAL DOCUMENTS");
//        OperationContext context = sourceSession.createOperationContext();
//        context.setIncludeAcls(false);
//        context.setIncludePolicies(false);
//        context.setMaxItemsPerPage(100000);
//
//        for (String path : folders_to_migrate) {
//
//            CmisObject folder = sourceSession.getObjectByPath("/"+path);
//            System.out.println("* Number of documents in folder [" + path + "] *");
//            System.out.println("---------------------------------------------------");
//            for (String doc_type : DocType.getList()) {
//                String cql = "SELECT cmis:objectId FROM " + doc_type + " WHERE IN_TREE('" + folder.getId() + "')";
//                ItemIterable<QueryResult> results = sourceSession.query(cql, false);
//                long tot = 0;
//                for (QueryResult hit : results) {
//                    tot++;
//                }
//                ;
//                System.out.println("Total of " + doc_type + ": " + tot);
//            }
//            System.out.println("---------------------------------------------------");
//        }

    }
}
