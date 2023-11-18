package com.ayitinya.englishdictionary.data.settings.source.local

enum class SettingsKeys(val value: String) {
    NOTIFY_WORD_OF_THE_DAY(value = "notify_word_of_the_day"),
    WORK_REQUEST_ID(value = "work_request_id"),
    UPDATE_WORD_OF_THE_DAY_REQUEST_ID(value = "update_word_of_the_day_request_id"),
    IS_HISTORY_DEACTIVATED(value = "is_history_deactivated"),
    FIRST_OPEN(value = "first_open"),
    APP_VERSION(value = "app_version"),
    WOTD_MODAL_DISPLAY(value = "wotd_modal_display"),
}

enum class WorkManagerKeys {
    NOTIFICATION_REQUEST_FOR_WORD_OF_THE_DAY, UPDATE_WORD_OF_THE_DAY
}