/*
 * Copyright 2010—2016 Denis Nelubin and others.
 *
 * This file is part of Weather Notification.
 *
 * Weather Notification is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Weather Notification is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Weather Notification.  If not, see http://www.gnu.org/licenses/.
 */

package ru.gelin.android.weather.notification.skin.blacktextplus;

import android.content.Context;
import ru.gelin.android.weather.Weather;

/**
 *  A special class to format weather.
 */
public class WeatherFormatter extends ru.gelin.android.weather.notification.skin.impl.WeatherFormatter {

    public WeatherFormatter(Context context, Weather weather) {
        super(context, weather);
    }

    @Override
    protected TemperatureFormat getTemperatureFormat() {
        return new TemperatureFormat();
    }
}
