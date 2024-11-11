package com.voinearadu.utils.file_manager.utils;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    public static @NotNull String getDate(String format) {
        return new SimpleDateFormat(format).format(Calendar.getInstance().getTime());
    }

}
