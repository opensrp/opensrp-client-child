[![Build Status](https://travis-ci.org/OpenSRP/opensrp-client-child.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-client-child) [![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-child/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-child?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b8b5e3c6e9284bffb993d07b235a8691)](https://www.codacy.com/app/OpenSRP/opensrp-client-child?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-child&amp;utm_campaign=Badge_Grade)

# opensrp-client-child
OpenSRP client child health module library

#Features
## Configurability

By placing a file named `app.properties` in your implementation assets folder (See sample app) , one can configure certain aspects of the app

### Configurable Settings

| Configuration                       | Type    | Default | Description                                   |
| ----------------------------------- | ------- | ------- | ----------------------------------------------|
| `notifications.bcg.enabled`         | Boolean | true    | Show BCG Notifications                        |
| `popup.weight.enabled`              | Boolean | true    | Show Record Weight pop up dialog              |
| `home.next.visit.date.enabled`      | Boolean | false   | Show Next Visit date column in home register  |
| `home.record.weight.enabled`        | Boolean | true    | Show Record Weight column in home register    |
| `feature.nfc.card.enabled`          | Boolean | false   | Enable Scan NFC Card feature                  |
| `feature.scan.qr.enabled`           | Boolean | true    | Enable Scan QR feature                        |
| `feature.images.enabled`            | Boolean | true    | Allow profile image capture                   |
| `feature.bottom.navigation.enabled` | Boolean | false   | Show Bottom Navigation menu                   |
| `home.toolbar.scan.qr.enabled`      | Boolean | false   | Show Scan QR Code in home register toolbar    |
| `home.toolbar.scan.card.enabled`    | Boolean | false   | Show Scan Card in home register toolbar       |
| `details.side.navigation.enabled`   | Boolean | true    | Enable side navigation drawer on details page |