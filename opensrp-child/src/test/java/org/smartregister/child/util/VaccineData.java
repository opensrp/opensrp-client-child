package org.smartregister.child.util;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-02-12
 */

public class VaccineData {

    public static String bcg2JsonData = "{\n" +
            "    \"name\": \"BCG 2\",\n" +
            "    \"type\": \"BCG\",\n" +
            "    \"openmrs_date\": {\n" +
            "      \"parent_entity\": \"886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "      \"entity\": \"concept\",\n" +
            "      \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"openmrs_calculate\": {\n" +
            "      \"parent_entity\": \"886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "      \"entity\": \"concept\",\n" +
            "      \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "      \"calculation\": 2\n" +
            "    },\n" +
            "    \"schedule\": {\n" +
            "      \"due\": [\n" +
            "        {\n" +
            "          \"reference\": \"prerequisite\",\n" +
            "          \"prerequisite\": \"BCG\",\n" +
            "          \"offset\": \"+105d\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"expiry\": [\n" +
            "        {\n" +
            "          \"reference\": \"dob\",\n" +
            "          \"offset\": \"+2y\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }";

}
