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
            "          \"reference\": \"dob\",\n" +
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

    public static final String SPECIAL_VACCINES_JSON = "[\n" +
            "  {\n" +
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
            "  }\n" +
            "]";

    public static final String VACCINES_JSON = "[\n" +
            "  {\n" +
            "    \"name\": \"Birth\",\n" +
            "    \"id\": \"Birth\",\n" +
            "    \"days_after_birth_due\": 0,\n" +
            "    \"vaccines\": [\n" +
            "      {\n" +
            "        \"name\": \"OPV 0\",\n" +
            "        \"type\": \"OPV\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 0\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+0d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+13d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"conditions\": [\n" +
            "            {\n" +
            "              \"type\": \"not_given\",\n" +
            "              \"vaccine\": \"OPV 4\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"BCG\",\n" +
            "        \"type\": \"BCG\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 1\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+0d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+1y\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"6 Weeks\",\n" +
            "    \"id\": \"Six_Wks\",\n" +
            "    \"days_after_birth_due\": 42,\n" +
            "    \"vaccines\": [\n" +
            "      {\n" +
            "        \"name\": \"OPV 1\",\n" +
            "        \"type\": \"OPV\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 1\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+42d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+4y,10m\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Penta 1\",\n" +
            "        \"type\": \"Penta\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 1\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+42d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+4y,10m\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"PCV 1\",\n" +
            "        \"type\": \"PCV\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 1\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+42d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+4y,10m\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Rota 1\",\n" +
            "        \"type\": \"Rota\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 1\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+42d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+7m\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"10 Weeks\",\n" +
            "    \"id\": \"Ten_Wks\",\n" +
            "    \"days_after_birth_due\": 70,\n" +
            "    \"vaccines\": [\n" +
            "      {\n" +
            "        \"name\": \"OPV 2\",\n" +
            "        \"type\": \"OPV\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 2\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"prerequisite\",\n" +
            "              \"prerequisite\": \"OPV 1\",\n" +
            "              \"offset\": \"+28d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+4y,11m\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Penta 2\",\n" +
            "        \"type\": \"Penta\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 2\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"prerequisite\",\n" +
            "              \"prerequisite\": \"Penta 1\",\n" +
            "              \"offset\": \"+28d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+4y,11m\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"PCV 2\",\n" +
            "        \"type\": \"PCV\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 2\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"prerequisite\",\n" +
            "              \"prerequisite\": \"PCV 1\",\n" +
            "              \"offset\": \"+28d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+4y,11m\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Rota 2\",\n" +
            "        \"type\": \"Rota\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 2\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"prerequisite\",\n" +
            "              \"prerequisite\": \"Rota 1\",\n" +
            "              \"offset\": \"+28d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+8m\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"14 Weeks\",\n" +
            "    \"id\": \"Fourteen_Weeks\",\n" +
            "    \"days_after_birth_due\": 98,\n" +
            "    \"vaccines\": [\n" +
            "      {\n" +
            "        \"name\": \"OPV 3\",\n" +
            "        \"type\": \"OPV\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 3\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"prerequisite\",\n" +
            "              \"prerequisite\": \"OPV 2\",\n" +
            "              \"offset\": \"+28d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+5y\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Penta 3\",\n" +
            "        \"type\": \"Penta\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 3\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"prerequisite\",\n" +
            "              \"prerequisite\": \"Penta 2\",\n" +
            "              \"offset\": \"+28d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+5y\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"PCV 3\",\n" +
            "        \"type\": \"PCV\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 3\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"prerequisite\",\n" +
            "              \"prerequisite\": \"PCV 2\",\n" +
            "              \"offset\": \"+28d\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+5y\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"9 Months\",\n" +
            "    \"id\": \"Nine_Months\",\n" +
            "    \"days_after_birth_due\": 274,\n" +
            "    \"vaccines\": [\n" +
            "      {\n" +
            "        \"name\": \"Measles 1 / MR 1\",\n" +
            "        \"type\": \"Measles / MR\",\n" +
            "        \"vaccine_separator\": \" / \",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"36AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA/162586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"36AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 1\n" +
            "        },\n" +
            "        \"schedules\": {\n" +
            "          \"Measles 1\": {\n" +
            "            \"due\": [\n" +
            "              {\n" +
            "                \"reference\": \"dob\",\n" +
            "                \"offset\": \"+9m\",\n" +
            "                \"window\": \"+10d\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"conditions\": [\n" +
            "              {\n" +
            "                \"type\": \"not_given\",\n" +
            "                \"vaccine\": \"MR 1\"\n" +
            "              }\n" +
            "            ]\n" +
            "          },\n" +
            "          \"MR 1\": {\n" +
            "            \"due\": [\n" +
            "              {\n" +
            "                \"reference\": \"dob\",\n" +
            "                \"offset\": \"+9m\",\n" +
            "                \"window\": \"+10d\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"conditions\": [\n" +
            "              {\n" +
            "                \"type\": \"not_given\",\n" +
            "                \"vaccine\": \"Measles 1\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"OPV 4\",\n" +
            "        \"type\": \"OPV\",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 4\n" +
            "        },\n" +
            "        \"schedule\": {\n" +
            "          \"due\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+9m\",\n" +
            "              \"window\": \"+10d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"expiry\": [\n" +
            "            {\n" +
            "              \"reference\": \"dob\",\n" +
            "              \"offset\": \"+5y\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"conditions\": [\n" +
            "            {\n" +
            "              \"type\": \"not_given\",\n" +
            "              \"vaccine\": \"OPV 0\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"given\",\n" +
            "              \"vaccine\": \"OPV 3\",\n" +
            "              \"comparison\": \"at_least\",\n" +
            "              \"value\": \"-28d\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"18 Months\",\n" +
            "    \"id\": \"Eighteen_Months\",\n" +
            "    \"days_after_birth_due\": 548,\n" +
            "    \"vaccines\": [\n" +
            "      {\n" +
            "        \"name\": \"Measles 2 / MR 2\",\n" +
            "        \"type\": \"Measles / MR\",\n" +
            "        \"vaccine_separator\": \" / \",\n" +
            "        \"openmrs_date\": {\n" +
            "          \"parent_entity\": \"36AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA/162586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"openmrs_calculate\": {\n" +
            "          \"parent_entity\": \"36AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"entity\": \"concept\",\n" +
            "          \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"calculation\": 2\n" +
            "        },\n" +
            "        \"schedules\": {\n" +
            "          \"Measles 2\": {\n" +
            "            \"due\": [\n" +
            "              {\n" +
            "                \"reference\": \"prerequisite\",\n" +
            "                \"prerequisite\": \"Measles 1\",\n" +
            "                \"offset\": \"+9m\",\n" +
            "                \"window\": \"+10d\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"reference\": \"prerequisite\",\n" +
            "                \"prerequisite\": \"MR 1\",\n" +
            "                \"offset\": \"+9m\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"conditions\": [\n" +
            "              {\n" +
            "                \"type\": \"not_given\",\n" +
            "                \"vaccine\": \"MR 2\"\n" +
            "              }\n" +
            "            ]\n" +
            "          },\n" +
            "          \"MR 2\": {\n" +
            "            \"due\": [\n" +
            "              {\n" +
            "                \"reference\": \"prerequisite\",\n" +
            "                \"prerequisite\": \"MR 1\",\n" +
            "                \"offset\": \"+9m\",\n" +
            "                \"window\": \"+10d\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"reference\": \"prerequisite\",\n" +
            "                \"prerequisite\": \"Measles 1\",\n" +
            "                \"offset\": \"+9m\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"conditions\": [\n" +
            "              {\n" +
            "                \"type\": \"not_given\",\n" +
            "                \"vaccine\": \"Measles 2\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "]";

}
