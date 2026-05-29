package com.macrotrack.analytics.common;

import java.time.LocalDate;

public record DateValuePoint<T>(LocalDate date, T value) {
}