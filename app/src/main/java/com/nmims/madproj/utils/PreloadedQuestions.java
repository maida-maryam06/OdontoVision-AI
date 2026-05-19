/*
 * File: PreloadedQuestions.java
 * Purpose: Provide preloaded interview questions across categories.
 */

package com.nmims.madproj.utils;

import com.nmims.madproj.models.Question;

import java.util.ArrayList;
import java.util.List;

public final class PreloadedQuestions {
    private PreloadedQuestions() {}

    public static List<Question> getJavaQuestions() {
        List<Question> list = new ArrayList<>();
        list.add(new Question(
                "Which of the following is the most common odontogenic cyst?",
                "Dentigerous cyst",
                "Radicular cyst",
                "Odontogenic keratocyst",
                "Lateral periodontal cyst",
                2,
                Constants.CATEGORY_JAVA,
                Constants.DIFFICULTY_EASY,
                "Radicular cyst is the most common odontogenic cyst. It is inflammatory in origin and usually develops at the apex of a non-vital tooth."

        ));
        list.add(new Question(
                "Radicular cyst is most commonly associated with which type of tooth?",
                "Vital tooth",
                "Non-vital tooth",
                "Impacted tooth",
                "Supernumerary tooth",
                2,
                Constants.CATEGORY_JAVA,
                Constants.DIFFICULTY_EASY,
                "Radicular cyst usually develops around the apex of a non-vital tooth due to pulpal necrosis and chronic periapical inflammation."
        ));
        list.add(new Question(
                "The epithelial lining of a radicular cyst mainly arises from which structure?",
                "Reduced enamel epithelium",
                "Dental lamina rests",
                "Epithelial rests of Malassez",
                "Oral surface epithelium",
                3,
                Constants.CATEGORY_JAVA,
                Constants.DIFFICULTY_EASY,
                "Chronic inflammation stimulates the epithelial rests of Malassez present in the periodontal ligament, leading to cyst formation."
        ));
        list.add(new Question(
                "Radicular cyst is classified as which type of cyst?",
                "Developmental odontogenic cyst",
                "Inflammatory odontogenic cyst",
                "Non-odontogenic cyst",
                "Soft tissue cyst",
                2,
                Constants.CATEGORY_JAVA,
                Constants.DIFFICULTY_EASY,
                "Radicular cyst is an inflammatory odontogenic cyst because it forms secondary to pulpal infection and periapical inflammation."
        ));
        list.add(new Question(
                "Which radiographic feature is most typical of a radicular cyst?",
                "Multilocular radiopacity",
                "Well-defined periapical radiolucency",
                "Sunburst appearance",
                "Mixed radiolucent-radiopaque lesion around crown",
                2,
                Constants.CATEGORY_JAVA,
                Constants.DIFFICULTY_MEDIUM,
                "Radicular cyst usually appears as a round or oval well-defined radiolucency at the apex of a non-vital tooth."
        ));
        list.add(new Question(
                "Which condition commonly precedes the formation of a radicular cyst?",
                "Pulpal necrosis",
                "Tooth eruption",
                "Enamel hypoplasia",
                "Tooth impaction",
                1,
                Constants.CATEGORY_JAVA,
                Constants.DIFFICULTY_EASY,
                "Pulpal necrosis leads to chronic periapical inflammation, which can stimulate epithelial proliferation and cyst formation."
        ));
        list.add(new Question(
                "Histologically, the lining of a radicular cyst is usually:",
                "Keratinized stratified squamous epithelium",
                "Non-keratinized stratified squamous epithelium",
                "Respiratory epithelium",
                "Simple cuboidal epithelium",
                2,
                Constants.CATEGORY_JAVA,
                Constants.DIFFICULTY_MEDIUM,
                "Radicular cyst is usually lined by non-keratinized stratified squamous epithelium with inflammatory cells in the cyst wall."
        ));
        list.add(new Question(
                "Which microscopic finding is commonly seen in the wall of a radicular cyst?",
                "Ghost cells",
                "Auer rods",
                "Chronic inflammatory cell infiltrate",
                "Psammoma bodies",
                3,
                Constants.CATEGORY_JAVA,
                Constants.DIFFICULTY_EASY,
                "Because radicular cyst is inflammatory, its cyst wall commonly contains chronic inflammatory cells such as lymphocytes and plasma cells."
        ));
        return list;
    }

    public static List<Question> getAndroidQuestions() {
        List<Question> list = new ArrayList<>();
        list.add(new Question(
                "Dentigerous cyst is most commonly associated with which condition?",
                "Non-vital tooth apex",
                "Crown of an unerupted or impacted tooth",
                "Gingival soft tissue",
                "Root furcation area",
                2,
                Constants.CATEGORY_ANDROID,
                Constants.DIFFICULTY_MEDIUM,
                "Dentigerous cyst is a developmental odontogenic cyst that surrounds the crown of an unerupted or impacted tooth."
        ));
        list.add(new Question(
                "Dentigerous cyst develops from which odontogenic structure?",
                "Epithelial rests of Malassez",
                "Reduced enamel epithelium",
                "Dental papilla",
                "Oral mucosal epithelium",
                2,
                Constants.CATEGORY_ANDROID,
                Constants.DIFFICULTY_EASY,
                "Dentigerous cyst develops due to fluid accumulation between the reduced enamel epithelium and the crown of an unerupted tooth."
        ));
        list.add(new Question(
                "Which tooth is most commonly associated with a dentigerous cyst?",
                "Mandibular third molar",
                "Maxillary central incisor",
                "Mandibular first premolar",
                "Maxillary first molar",
                1,
                Constants.CATEGORY_ANDROID,
                Constants.DIFFICULTY_EASY,
                "Dentigerous cyst is most commonly associated with impacted mandibular third molars, followed by maxillary canines."
        ));
        list.add(new Question(
                "Dentigerous cyst is classified as which type of odontogenic cyst?",
                "Inflammatory odontogenic cyst",
                "Developmental odontogenic cyst",
                "Non-odontogenic cyst",
                "Soft tissue cyst",
                2,
                Constants.CATEGORY_ANDROID,
                Constants.DIFFICULTY_MEDIUM,
                "Dentigerous cyst is a developmental odontogenic cyst because it forms in relation to the development and eruption of a tooth."
        ));
        list.add(new Question(
                "The typical radiographic appearance of a dentigerous cyst is:",
                "Periapical radiolucency around a non-vital tooth",
                "Unilocular radiolucency attached at the cementoenamel junction of an unerupted tooth",
                "Sunburst radiopacity",
                "Generalized bone sclerosis",
                2,
                Constants.CATEGORY_ANDROID,
                Constants.DIFFICULTY_EASY,
                "Dentigerous cyst appears as a well-defined unilocular radiolucency surrounding the crown of an unerupted tooth and attached near the cementoenamel junction."
        ));
        list.add(new Question(
                "Which clinical feature is commonly seen in a large dentigerous cyst?",
                "Painless jaw swelling",
                "Severe burning pain of tongue",
                "Bleeding gingival mass",
                "Ulcerated lip lesion",
                1,
                Constants.CATEGORY_ANDROID,
                Constants.DIFFICULTY_MEDIUM,
                "Dentigerous cyst is often asymptomatic when small, but larger cysts may cause painless expansion or swelling of the jaw."
        ));
        list.add(new Question(
                "Dentigerous cyst most commonly surrounds which part of the tooth?",
                "Root apex",
                "Tooth crown",
                "Root furcation",
                "Periodontal ligament only",
                2,
                Constants.CATEGORY_ANDROID,
                Constants.DIFFICULTY_MEDIUM,
                "Dentigerous cyst surrounds the crown of an unerupted tooth. This is its key diagnostic feature."
        ));
        list.add(new Question(
                "The usual treatment of a dentigerous cyst is:",
                "Chemotherapy",
                "Radiotherapy",
                "Enucleation with removal of the associated unerupted tooth, or marsupialization in large lesions",
                "No treatment in all cases",
                3,
                Constants.CATEGORY_ANDROID,
                Constants.DIFFICULTY_EASY,
                "Treatment usually involves enucleation and removal of the associated tooth. Large cysts may first be marsupialized to reduce size and preserve nearby structures."
        ));
        return list;
    }

    public static List<Question> getDSAQuestions() {
        List<Question> list = new ArrayList<>();
        list.add(new Question(
                "Odontogenic keratocyst is now commonly referred to as which lesion in WHO terminology?",
                "Radicular cyst",
                "Dentigerous cyst",
                "Odontogenic keratocyst",
                "Nasopalatine duct cyst",
                3,
                Constants.CATEGORY_DSA,
                Constants.DIFFICULTY_EASY,
                "Odontogenic keratocyst is a developmental odontogenic cyst known for aggressive behavior, posterior mandibular predilection, and high recurrence tendency."
        ));
        list.add(new Question(
                "Odontogenic keratocyst most commonly arises from which odontogenic tissue?",
                "Reduced enamel epithelium",
                "Epithelial rests of Malassez",
                "Dental lamina remnants",
                "Oral surface epithelium only",
                3,
                Constants.CATEGORY_DSA,
                Constants.DIFFICULTY_EASY,
                "Odontogenic keratocyst is believed to arise from remnants of dental lamina, which explains its odontogenic developmental origin."
        ));
        list.add(new Question(
                "The most common site of odontogenic keratocyst is:",
                "Anterior maxilla",
                "Posterior mandible and ramus area",
                "Hard palate only",
                "Gingival soft tissue",
                2,
                Constants.CATEGORY_DSA,
                Constants.DIFFICULTY_EASY,
                "OKC most commonly occurs in the posterior mandible, especially the angle and ramus region."
        ));
        list.add(new Question(
                "Which feature is most characteristic of odontogenic keratocyst?",
                "Very low recurrence rate",
                "High recurrence tendency",
                "Always associated with a non-vital tooth",
                "Always presents as a painful ulcer",
                2,
                Constants.CATEGORY_DSA,
                Constants.DIFFICULTY_EASY,
                "OKC has a high recurrence tendency because of its thin fragile lining, satellite cysts, daughter cysts, and possible epithelial remnants left after surgery."
        ));
        list.add(new Question(
                "The epithelial lining of odontogenic keratocyst is typically:",
                "Non-keratinized stratified squamous epithelium with heavy inflammation",
                "Parakeratinized stratified squamous epithelium with corrugated surface",
                "Respiratory epithelium",
                "Simple cuboidal epithelium",
                2,
                Constants.CATEGORY_DSA,
                Constants.DIFFICULTY_MEDIUM,
                "The classic histological lining of OKC is thin parakeratinized stratified squamous epithelium with a corrugated surface."
        ));

        list.add(new Question(
                "Radiographically, odontogenic keratocyst commonly appears as:",
                "Well-defined unilocular or multilocular radiolucency",
                "Sunburst radiopacity",
                "Generalized radiopaque jaw lesion",
                "Ground-glass opacity only",
                1,
                Constants.CATEGORY_DSA,
                Constants.DIFFICULTY_MEDIUM,
                "OKC may appear as a well-defined unilocular or multilocular radiolucency. It may grow extensively along the medullary cavity."
        ));
        return list;
    }
}


