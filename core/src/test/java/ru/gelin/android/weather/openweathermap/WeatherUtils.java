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

package ru.gelin.android.weather.openweathermap;

import android.content.Context;

import static ru.gelin.android.weather.notification.WeatherUtils.readJSON;

public class WeatherUtils {

    public static OpenWeatherMapWeather createIncompleteOpenWeather(Context context) throws Exception {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(context);
        weather.parseCurrentWeather(readJSON("omsk_25_weather.json"));
        return weather;
    }

    public static OpenWeatherMapWeather createOpenWeather(Context context) throws Exception {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(context);
        weather.parseCurrentWeather(readJSON("omsk_25_weather.json"));
        weather.parseOneCallResult(readJSON("omsk_25_onecall_forecast.json"));
        return weather;
    }

}