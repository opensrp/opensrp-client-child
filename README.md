![Build status](https://github.com/OpenSRP/opensrp-client-reveal/workflows/Android%20CI%20with%20Gradle/badge.svg) [![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-child/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-child?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b8b5e3c6e9284bffb993d07b235a8691)](https://www.codacy.com/app/OpenSRP/opensrp-client-child?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-child&amp;utm_campaign=Badge_Grade)

# opensrp-client-child
OpenSRP client child health module library

## Configurability

By placing a file named `app.properties` in your implementation assets folder (See sample app) , one can configure certain aspects of the app

### Configurable Settings

| Configuration                                 | Type    | Default | Description                                                            |
| ------------------------------------------    | ------- | ------- | -----------------------------------------------------------------------|
| `notifications.bcg.enabled`                   | Boolean | true    | Show BCG Notifications                                                 |
| `notifications.weight.enabled`                | Boolean | true    | Show Record Weight pop notification up dialog                          |
| `home.next.visit.date.enabled`                | Boolean | false   | Show Next Visit date column in home register                           |
| `home.record.weight.enabled`                  | Boolean | true    | Show Record Weight column in home register                             |
| `feature.nfc.card.enabled`                    | Boolean | false   | Enable Scan NFC Card feature                                           |
| `feature.scan.qr.enabled`                     | Boolean | true    | Enable Scan QR feature                                                 |
| `feature.images.enabled`                      | Boolean | true    | Allow profile image capture                                            |
| `feature.bottom.navigation.enabled`           | Boolean | false   | Show Bottom Navigation menu                                            |
| `home.toolbar.scan.qr.enabled`                | Boolean | false   | Show Scan QR Code in home register toolbar                             |
| `home.toolbar.scan.card.enabled`              | Boolean | false   | Show Scan Card in home register toolbar                                |
| `details.side.navigation.enabled`             | Boolean | true    | Enable side navigation drawer on details page                          |
| `home.alert.upcoming.blue.disabled`           | Boolean | false   | Disable showing light blue alert for upcoming in 7 days                |
| `mother.lookup.show.results.duration`         | Integer | 30000   | Sets duration of showing mother lookup results                         |
| `mother.lookup.undo.duration`                 | Integer | 10000   | Sets duration of showing the undo look up view                         |
| `disable.location.picker.view`                | Boolean | false   | Disables LocationPicker View                                           |
| `use.new.advance.search.approach`             | Boolean | false   | Use new advance search feature that is based on client search endpoint |
| `multi.language.support`                      | Boolean | false   | Use new Multi Language Support for JSON forms                          |
| `recurring.services.enabled`                  | Boolean | true    | Show recurring services                                                |
| `hide.overdue.vaccine.status`                 | Boolean | false   | Hide overdue and due vaccine states indication color                   |
| `show.out.of.catchment.recurring.services`    | Boolean | false   | SHow recurring services in out pf catchment form                       |
| `home.split.fully.immunized.status`           | Boolean | false   | Show Fully Immunized U1 status if first year vaccines are completed    |
|                                               |         |         | and Fully Immunized U2 if all vaccines are completed                   |

## Multi-language Support for Immunization Group Names Shown on the Register for Upcoming Statuses

>NOTE: If you set the `multi.language.support` app property to true, you are required to use JMAG (Json Multi Language Asset Generator) tool to generate string properties 
file used for translating the forms. The tool will also add placeholders in places of form strings in the respective form while generating the properties file. Refer to 
[Native Form Documentation](https://github.com/OpenSRP/opensrp-client-native-form#multi-language-support-mls) for more info.

You can enable multi-language support for Group Names shown on the register for upcoming statuses eg. `Upcoming 10 weeks`. :frowning: This means you need to add multiple string for the same group name [since this](https://github.com/OpenSRP/opensrp-client-immunization#multi-language-support) is also supported.

You do this by adding a **lowercase & underscored(for spaces)** string-id eg.

-   **At Birth** - It's string resource id will be `at_birth`

For group names starting with a number, you do the same as above and then add an underscore before the first character eg.

-   **6 Weeks** - It's string resource id will be `_6_weeks`
-   **10 Weeks** - It's string resource id will be `_10_weeks`
-   **1 Year after  TT 4** It's string resource id will be `_1_year_after_tt_4`

## Location Tree Configuration

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

### Location Spinner Configuration

Location selection can optionally be done using cascaded drop-downs.

For location name reverse look-up to be done correctly in the registration view, form fields of type `spinner` meant to select a location must have tag **sub_type** set to **location**.

For cascaded location spinners to pre-populate the default location or saved location on the form, they need to be grouped.
Tag **value_field** was added to provide the link between related spinner fields. The tag takes a value that is the **key** of the lowest field in the locations hierarchy selection. For example, if the hierarchy is Province > District > Commune, the `value_field` value for Province and District will be the JSON form field key for Commune field.

```
      {
        "key": "Residential_Area_District",
        "openmrs_entity_parent": "",
        "openmrs_entity": "",
        "openmrs_entity_id": "",
        "type": "spinner",
        "sub_type": "location",
        "value_field": "Residential_Area_Commune",
        "hint": "Child's Residential Area District",
        "options": [
        ],
        ....
      },
      {
        "key": "Residential_Area_Commune",
        "openmrs_entity_parent": "usual_residence",
        "openmrs_entity": "person_address",
        "openmrs_entity_id": "address3",
        "type": "spinner",
        "sub_type": "location",
        "hint": "Child's Residential Area Commune",
        "options": [
        ],
        ....
      }
```

### Form Level Configuration

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

### Selectable Levels

The above can be further configured, for example if you require to have the location hierarchy for a field to be selectable at District level then you can add the field **selectable** and assign it a value _District_ which corresponds to the location tag you want selectable for that field. 

### Selectable Other Option

Sometimes you'd want to add an _Other_ option in case the location is not part of the tree but you may want the user to select other and possibly show an edit text via skip logic to allow manual entry of a location. For this there is the field **hierarchy** which defines a hierarchy type. 3 types of configuration values are supported _facility_only_, _facility_with_other_, _entire_tree_

### Auto-populate Location Fields

All form fields of type `tree` with **selectable** tag set will be auto-populated with the logged in provider's details. E.g., the field defined below will be auto-populated with the district of the logged in provider.

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

### Supporting Mother Lookup

Mother lookup functionality is a feature that allows you to search for a list of available mothers when doing child registration. This comes in handy
when you want to register a sibling to an existing child. This functionality pre-populates the mother form fields, when you select a mother from the search results.

To include this functionality in your app first apply the following attributes to the child enrollment form json.

```json 
 "look_up": "true",
 "entity_id": "mother"
```

> Note: Mother lookup dialog will only  be shown on  fields of type `EditText` since the dialog is only triggered by the `TextWatcher` listener. However other fields that are not of the type
>`EditText` are also filled with the returned values.

Next override 2 classes `org.smartregister.child.activity.BaseChildFormActivity` and`org.smartregister.child.fragment.ChildFormFragment` class. The subclass of `ChildFormFragment` is used in `BaseChildFormActivity`. Also remember to register the subclass of the `BaseChildFormActivity` to your `AndroidManifest.xml` file.
 
Inside the overridden `ChildFormFragment` class. Override the method `org.smartregister.child.fragment.ChildFormFragment.getKeyAliasMap` and return a map of the **key** field name against the column name of the client object returned by mother lookup. If you do not want a field value to be formatted before being set on the view, add them to the method `org.smartregister.child.sample.fragment.SampleChildFormFragment.getNonHumanizedFields`.