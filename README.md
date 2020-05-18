[![Build Status](https://travis-ci.org/OpenSRP/opensrp-client-child.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-client-child) [![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-child/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-child?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b8b5e3c6e9284bffb993d07b235a8691)](https://www.codacy.com/app/OpenSRP/opensrp-client-child?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-child&amp;utm_campaign=Badge_Grade)

# opensrp-client-child
OpenSRP client child health module library

## Configurability

By placing a file named `app.properties` in your implementation assets folder (See sample app) , one can configure certain aspects of the app

### Configurable Settings

| Configuration                         | Type    | Default | Description                                             |
| --------------------------------------| ------- | ------- | ----------------------------------------------          |
| `notifications.bcg.enabled`           | Boolean | true    | Show BCG Notifications                                  |
| `notifications.weight.enabled`        | Boolean | true    | Show Record Weight pop notification up dialog           |
| `home.next.visit.date.enabled`        | Boolean | false   | Show Next Visit date column in home register            |
| `home.record.weight.enabled`          | Boolean | true    | Show Record Weight column in home register              |
| `feature.nfc.card.enabled`            | Boolean | false   | Enable Scan NFC Card feature                            |
| `feature.scan.qr.enabled`             | Boolean | true    | Enable Scan QR feature                                  |
| `feature.images.enabled`              | Boolean | true    | Allow profile image capture                             |
| `feature.bottom.navigation.enabled`   | Boolean | false   | Show Bottom Navigation menu                             |
| `home.toolbar.scan.qr.enabled`        | Boolean | false   | Show Scan QR Code in home register toolbar              |
| `home.toolbar.scan.card.enabled`      | Boolean | false   | Show Scan Card in home register toolbar                 |
| `details.side.navigation.enabled`     | Boolean | true    | Enable side navigation drawer on details page           |
| `home.alert.upcoming.blue.disabled`   | Boolean | false   | Disable showing light blue alert for upcoming in 7 days |
| `mother.lookup.show.results.duration` | Integer | 30000   | Sets duration of showing mother lookup results          |
| `mother.lookup.undo.duration`         | Integer | 10000   | Sets duration of showing the undo look up view          |   
| `disable.location.picker.view`        | Boolean | false   | Disables LocationPicker View                            |

## Multi-language support for Immunization Group Names shown on the Register for Upcoming Statuses

You can enable multi-language support for Group Names shown on the register for upcoming statuses eg. `Upcoming 10 weeks`. :frowning: This means you need to add multiple string for the same group name [since this](https://github.com/OpenSRP/opensrp-client-immunization#multi-language-support) is also supported.

You do this by adding a **lowercase & underscored(for spaces)** string-id eg.

-   **At Birth** - It's string resource id will be `at_birth`

For group names starting with a number, you do the same as above and then add an underscore before the first character eg.

-   **6 Weeks** - It's string resource id will be `_6_weeks`
-   **10 Weeks** - It's string resource id will be `_10_weeks`
-   **1 Year after  TT 4** It's string resource id will be `_1_year_after_tt_4`

## Location tree configuration

The following configurations are required on your applications build.gradle file inorder to render the location tree correctly
Here's an example:
```

        buildConfigField "String[]", "LOCATION_LEVELS", '{"Country", "Province", "District", "Facility", "Village"}'
        buildConfigField "String[]", "HEALTH_FACILITY_LEVELS", '{"Country", "Province", "District", "Health Facility", "Village"}'
        buildConfigField "String[]", "ALLOWED_LEVELS", '{"Facility"}'
        buildConfigField "String", "DEFAULT_LEVEL", '"Facility"'
```

For context, the locations are synced from the server side after app login and they consist of two related data items 
  -  The location name e.g `Kenya` 
  -  The location tag e.g. `Country`

The above configurations and their use are defined below
  -   **LOCATION_LEVELS** - This this is an ordered list of the location tags as you'd want to render them
  -   **HEALTH_FACILITY_LEVELS** - This this is an ordered list of the facility location tags
  -   **DEFAULT_LEVEL** - This this is a string for the default select Location on the location picker
  -   **ALLOWED_LEVELS** - This this is a list of tags on the location tree that can be selected on your forms e.g. for one question you can select Facility and another question on the same from you can select District if the setting was _buildConfigField "String[]", "ALLOWED_LEVELS", '{"Facility"}'_

### Form Level configuration

For the location picker widget to render on the form the basic configuration is:

```
       "key": "Residential_Area",
        "openmrs_entity_parent": "usual_residence",
        "openmrs_entity": "person_address",
        "openmrs_entity_id": "address3",
        "openmrs_data_type": "text",
        "type": "tree",
        "hint": "Child's residential area *",
        "tree": [

        ],
        "v_required": {
          "value": true,
          "err": "Please enter the child's residential area"
        }
      }
```
The above can be further configured, for example if you require to have the location hierarchy for a field to be selectable at District level then you can add the field **selectable** and assign it a value _District_ which corresponds to the location tag you want selectable for that field. 

Sometimes you'd want to add an _Other_ option in case the location is not part of the tree but you may want the user to select other and possibly show an edit text via skip logic to allow manual entry of a location. For this there is the field **hierarchy** which defines a hierarchy type. 3 types of configuration values are supported _facility_only_, _facility_with_other_, _entire_tree_

Example updated config:
```
       "key": "Residential_Area",
        "openmrs_entity_parent": "usual_residence",
        "openmrs_entity": "person_address",
        "openmrs_entity_id": "address3",
        "openmrs_data_type": "text",
        "type": "tree",
        "hint": "Child's residential area *",
        "tree": [

        ],
        selectable: "District"
        hierarchy: "facility_with_other"
        ....
      }
```
More on Native form library widgets can be found [here](https://github.com/OpenSRP/opensrp-client-native-form)