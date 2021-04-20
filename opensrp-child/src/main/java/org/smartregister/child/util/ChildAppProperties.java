package org.smartregister.child.util;

/**
 * Created by ndegwamartin on 2019-06-07.
 */
public final class ChildAppProperties extends org.smartregister.util.AppProperties {

    public final static class KEY {
        //Notifications
        public static final String NOTIFICATIONS_BCG_ENABLED = "notifications.bcg.enabled";
        public static final String NOTIFICATIONS_WEIGHT_ENABLED = "notifications.weight.enabled";

        //Full features
        public static final String FEATURE_IMAGES_ENABLED = "feature.images.enabled";
        public static final String FEATURE_NFC_CARD_ENABLED = "feature.nfc.card.enabled";
        public static final String FEATURE_BOTTOM_NAVIGATION_ENABLED = "feature.bottom.navigation.enabled";
        public static final String FEATURE_SCAN_QR_ENABLED = "feature.scan.qr.enabled";

        //Home controls/widgets
        public static final String HOME_NEXT_VISIT_DATE_ENABLED = "home.next.visit.date.enabled";
        public static final String HOME_RECORD_WEIGHT_ENABLED = "home.record.weight.enabled";
        public static final String HOME_TOOLBAR_SCAN_CARD_ENABLED = "home.toolbar.scan.card.enabled";
        public static final String HOME_TOOLBAR_SCAN_QR_ENABLED = "home.toolbar.scan.qr.enabled";
        public static final String HOME_COMPLIANCE_ENABLED = "home.compliance.enabled";
        public static final String HOME_ZEIR_ID_COL_ENABLED = "home.zeir.id.column.enabled";

        //Home styling
        public static final String HOME_ALERT_STYLE_LEGACY = "home.alert.style.legacy";
        public static final String HOME_ALERT_UPCOMING_BLUE_DISABLED = "home.alert.upcoming.blue.disabled";
        public static final String HOME_SPLIT_FULLY_IMMUNIZED_STATUS = "home.split.fully.immunized.status";

        //Details page widgets
        public static final String DETAILS_SIDE_NAVIGATION_ENABLED = "details.side.navigation.enabled";

        //Service
        public static final String MONITOR_HEIGHT = "monitor.height";

        //Search configuration
        public static final String USE_NEW_ADVANCE_SEARCH_APPROACH = "use.new.advance.search.approach";

        //Use new multi language support. Requires your forms generated by latest XLSON and converted with JMAG tool
        public static final String MULTI_LANGUAGE_SUPPORT = "multi.language.support";

        public static final String FEATURE_RECURRING_SERVICE_ENABLED = "recurring.services.enabled";

        // vaccine status color
        public static final String HIDE_OVERDUE_VACCINE_STATUS = "hide.overdue.vaccine.status";

        //Show extra vaccines
        public static final String SHOW_EXTRA_VACCINES = "show.extra.vaccines";

        //Show recurring service on out of catchment form
        public static final String SHOW_OUT_OF_CATCHMENT_RECURRING_SERVICES = "show.out.of.catchment.recurring.services";

        //Show booster vaccines
        public static final String SHOW_BOOSTER_IMMUNIZATIONS = "show.booster.immunizations";

        //Count of extra vaccines to select at a particular time
        public static final String EXTRA_VACCINES_COUNT = "extra.vaccines.count";

        //Generate an event with the date of the next due vaccine
        public static final String NEXT_APPOINTMENT_EVENT_ENABLED = "next.appointment.event.enabled";

        //novel features
        public final static class NOVEL {
            public static final String OUT_OF_CATCHMENT = "novel.out.of.catchment";
        }
    }
}
