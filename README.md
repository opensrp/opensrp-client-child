[![Build Status](https://travis-ci.org/OpenSRP/opensrp-client-child.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-client-child) [![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-child/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-child?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b8b5e3c6e9284bffb993d07b235a8691)](https://www.codacy.com/app/OpenSRP/opensrp-client-child?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-child&amp;utm_campaign=Badge_Grade)

# opensrp-client-child
OpenSRP client child health module library

## Configurability

By placing a file named `app.properties` in your implementation assets folder (See sample app) , one can configure certain aspects of the app

### Configurable Settings

| Configuration                       | Type    | Default | Description                                             |
| ----------------------------------- | ------- | ------- | ----------------------------------------------          |
| `notifications.bcg.enabled`         | Boolean | true    | Show BCG Notifications                                  |
| `notifications.weight.enabled`      | Boolean | true    | Show Record Weight pop notification up dialog           |
| `home.next.visit.date.enabled`      | Boolean | false   | Show Next Visit date column in home register            |
| `home.record.weight.enabled`        | Boolean | true    | Show Record Weight column in home register              |
| `feature.nfc.card.enabled`          | Boolean | false   | Enable Scan NFC Card feature                            |
| `feature.scan.qr.enabled`           | Boolean | true    | Enable Scan QR feature                                  |
| `feature.images.enabled`            | Boolean | true    | Allow profile image capture                             |
| `feature.bottom.navigation.enabled` | Boolean | false   | Show Bottom Navigation menu                             |
| `home.toolbar.scan.qr.enabled`      | Boolean | false   | Show Scan QR Code in home register toolbar              |
| `home.toolbar.scan.card.enabled`    | Boolean | false   | Show Scan Card in home register toolbar                 |
| `details.side.navigation.enabled`   | Boolean | true    | Enable side navigation drawer on details page           |
| `home.alert.upcoming.blue.disabled` | Boolean | false   | Disable showing light blue alert for upcoming in 7 days |

### Multi-language support for Immunization Group Names shown on the Register for Upcoming Statuses

You can enable multi-language support for Group Names shown on the register for upcoming statuses eg. `Upcoming 10 weeks`. :frowning: This means you need to add multiple string for the same group name [since this](https://github.com/OpenSRP/opensrp-client-immunization#multi-language-support) is also supported.

You do this by adding a **lowercase & underscored(for spaces)** string-id eg.

-   **At Birth** - It's string resource id will be `at_birth`

For group names starting with a number, you do the same as above and then add an underscore before the first character eg.

-   **6 Weeks** - It's string resource id will be `_6_weeks`
-   **10 Weeks** - It's string resource id will be `_10_weeks`
-   **1 Year after  TT 4** It's string resource id will be `_1_year_after_tt_4`