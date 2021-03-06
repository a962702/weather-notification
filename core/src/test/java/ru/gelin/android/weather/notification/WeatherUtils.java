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

package ru.gelin.android.weather.notification;

import android.content.Context;
import android.os.Parcel;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.gelin.android.weather.Cloudiness;
import ru.gelin.android.weather.CloudinessUnit;
import ru.gelin.android.weather.Humidity;
import ru.gelin.android.weather.PrecipitationPeriod;
import ru.gelin.android.weather.Temperature;
import ru.gelin.android.weather.TemperatureUnit;
import ru.gelin.android.weather.Weather;
import ru.gelin.android.weather.WeatherCondition;
import ru.gelin.android.weather.WeatherConditionType;
import ru.gelin.android.weather.Wind;
import ru.gelin.android.weather.WindDirection;
import ru.gelin.android.weather.WindSpeedUnit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WeatherUtils {

    public enum Version {
        V_0_2, V_0_3
    }

    private WeatherUtils() {
        //avoid instantiation
    }

    public static Weather createOpenWeather(Context context) throws Exception {
        return ru.gelin.android.weather.openweathermap.WeatherUtils.createOpenWeather(context);
    }

    public static Weather createIncompleteOpenWeather(Context context) throws Exception {
        return ru.gelin.android.weather.openweathermap.WeatherUtils.createIncompleteOpenWeather(context);
    }

    public static ru.gelin.android.weather.v_0_2.Weather createWeather_v_0_2(Context context) throws Exception {
        return new ru.gelin.android.weather.v_0_2.google.GoogleWeather(
                new InputStreamReader(context.getAssets().open("google_weather_api_en.xml")));
    }

    @SuppressWarnings("deprecated")
    public static ru.gelin.android.weather.v_0_2.Weather convert(Weather weather) {
        Parcel parcel = Parcel.obtain();
        ParcelableWeather parcelable = new ParcelableWeather(weather);
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return ru.gelin.android.weather.v_0_2.notification.ParcelableWeather.CREATOR.createFromParcel(parcel);
    }

    @SuppressWarnings("deprecated")
    public static Weather convert(ru.gelin.android.weather.v_0_2.Weather weather) {
        Parcel parcel = Parcel.obtain();
        ru.gelin.android.weather.v_0_2.notification.ParcelableWeather parcelable =
                new ru.gelin.android.weather.v_0_2.notification.ParcelableWeather(weather);
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return ParcelableWeather.CREATOR.createFromParcel(parcel);
    }

    public static void checkWeather(Weather weather, Version version) throws MalformedURLException {
        switch (version) {
        case V_0_2:
            checkWeather_v_0_2(weather);
            break;
        default:
            checkOpenWeather(weather);
        }
    }

    public static void checkOpenWeather(Weather weather) throws MalformedURLException {
        assertEquals("Omsk", weather.getLocation().getText());

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(2020, Calendar.JUNE, 5, 17, 30, 5);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(calendar.getTime(), weather.getTime());

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.MINUTE, -1);
        assertTrue(weather.getQueryTime().after(calendar.getTime()));

        assertEquals(new URL("https://openweathermap.org/city/1496153"), weather.getForecastURL());

        assertEquals(8, weather.getConditions().size());

        WeatherCondition condition0 = weather.getConditions().get(0);
        assertEquals("Scattered clouds", condition0.getConditionText());
        Temperature temp0 = condition0.getTemperature(TemperatureUnit.K);
        assertEquals(289, temp0.getCurrent());
        assertEquals(287, temp0.getLow());
        assertEquals(289, temp0.getHigh());
        Humidity humidity = condition0.getHumidity();
        assertEquals("Humidity: 44%", humidity.getText());
        assertEquals(44, humidity.getValue());
        Wind wind = condition0.getWind(WindSpeedUnit.MPS);
        assertEquals("Wind: NNW, 3 m/s", wind.getText());
        assertEquals(WindDirection.NNW, wind.getDirection());
        assertEquals(3, wind.getSpeed());
        Cloudiness cloudiness = condition0.getCloudiness(CloudinessUnit.PERCENT);
        assertEquals(46, cloudiness.getValue());
        assertEquals(0f, condition0.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(1, condition0.getConditionTypes().size());
        assertTrue(condition0.getConditionTypes().contains(WeatherConditionType.CLOUDS_SCATTERED));

        WeatherCondition condition1 = weather.getConditions().get(1);
        assertEquals("Light rain", condition1.getConditionText());
        Temperature temp1 = condition1.getTemperature(TemperatureUnit.K);
        assertEquals(289, temp1.getCurrent());
        assertEquals(283, temp1.getLow());
        assertEquals(295, temp1.getHigh());
        assertEquals(46, condition1.getHumidity().getValue());
        assertEquals(WindDirection.N, condition1.getWind().getDirection());
        assertEquals(6, condition1.getWind().getSpeed());
        assertEquals(46, condition1.getCloudiness().getValue());
        assertEquals(0.67f, condition1.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_3H), 0.01f);
        assertEquals(1, condition1.getConditionTypes().size());
        assertTrue(condition1.getConditionTypes().contains(WeatherConditionType.RAIN_LIGHT));

        WeatherCondition condition2 = weather.getConditions().get(2);
        assertEquals("Scattered clouds", condition2.getConditionText());
        Temperature temp2 = condition2.getTemperature(TemperatureUnit.K);
        assertEquals(292, temp2.getCurrent());
        assertEquals(287, temp2.getLow());
        assertEquals(296, temp2.getHigh());
        assertEquals(38, condition2.getHumidity().getValue());
        assertEquals(WindDirection.NNW, condition2.getWind().getDirection());
        assertEquals(4, condition2.getWind().getSpeed());
        assertEquals(25, condition2.getCloudiness().getValue());
        assertEquals(0f, condition2.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(1, condition2.getConditionTypes().size());
        assertTrue(condition2.getConditionTypes().contains(WeatherConditionType.CLOUDS_SCATTERED));

        WeatherCondition condition3 = weather.getConditions().get(3);
        assertEquals("Broken clouds", condition3.getConditionText());
        Temperature temp3 = condition3.getTemperature(TemperatureUnit.K);
        assertEquals(293, temp3.getCurrent());
        assertEquals(288, temp3.getLow());
        assertEquals(298, temp3.getHigh());
        assertEquals(42, condition3.getHumidity().getValue());
        assertEquals(WindDirection.WNW, condition3.getWind().getDirection());
        assertEquals(4, condition3.getWind().getSpeed());
        assertEquals(65, condition3.getCloudiness().getValue());
        assertEquals(0f, condition3.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(1, condition3.getConditionTypes().size());
        assertTrue(condition3.getConditionTypes().contains(WeatherConditionType.CLOUDS_BROKEN));

        WeatherCondition condition4 = weather.getConditions().get(4);
        assertEquals("Light rain", condition4.getConditionText());
        Temperature temp4 = condition4.getTemperature(TemperatureUnit.K);
        assertEquals(294, temp4.getCurrent());
        assertEquals(289, temp4.getLow());
        assertEquals(299, temp4.getHigh());
        assertEquals(51, condition4.getHumidity().getValue());
        assertEquals(WindDirection.NNW, condition4.getWind().getDirection());
        assertEquals(5, condition4.getWind().getSpeed());
        assertEquals(24, condition4.getCloudiness().getValue());
        assertEquals(1.24f, condition4.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_3H), 0.01f);
        assertEquals(1, condition4.getConditionTypes().size());
        assertTrue(condition4.getConditionTypes().contains(WeatherConditionType.RAIN_LIGHT));

        WeatherCondition condition5 = weather.getConditions().get(5);
        assertEquals("Light rain", condition5.getConditionText());
        Temperature temp5 = condition5.getTemperature(TemperatureUnit.K);
        assertEquals(293, temp5.getCurrent());
        assertEquals(286, temp5.getLow());
        assertEquals(299, temp5.getHigh());
        assertEquals(41, condition5.getHumidity().getValue());
        assertEquals(WindDirection.N, condition5.getWind().getDirection());
        assertEquals(3, condition5.getWind().getSpeed());
        assertEquals(3, condition5.getCloudiness().getValue());
        assertEquals(0.21f, condition5.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_3H), 0.01f);
        assertEquals(1, condition5.getConditionTypes().size());
        assertTrue(condition5.getConditionTypes().contains(WeatherConditionType.RAIN_LIGHT));

        WeatherCondition condition6 = weather.getConditions().get(6);
        assertEquals("Few clouds", condition6.getConditionText());
        Temperature temp6 = condition6.getTemperature(TemperatureUnit.K);
        assertEquals(292, temp6.getCurrent());
        assertEquals(288, temp6.getLow());
        assertEquals(296, temp6.getHigh());
        assertEquals(42, condition6.getHumidity().getValue());
        assertEquals(WindDirection.N, condition6.getWind().getDirection());
        assertEquals(6, condition6.getWind().getSpeed());
        assertEquals(17, condition6.getCloudiness().getValue());
        assertEquals(0f, condition6.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(1, condition6.getConditionTypes().size());
        assertTrue(condition6.getConditionTypes().contains(WeatherConditionType.CLOUDS_FEW));

        WeatherCondition condition7 = weather.getConditions().get(7);
        assertEquals("Few clouds", condition7.getConditionText());
        Temperature temp7 = condition7.getTemperature(TemperatureUnit.K);
        assertEquals(294, temp7.getCurrent());
        assertEquals(288, temp7.getLow());
        assertEquals(300, temp7.getHigh());
        assertEquals(48, condition7.getHumidity().getValue());
        assertEquals(WindDirection.WSW, condition7.getWind().getDirection());
        assertEquals(4, condition7.getWind().getSpeed());
        assertEquals(22, condition7.getCloudiness().getValue());
        assertEquals(0f, condition7.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(1, condition7.getConditionTypes().size());
        assertTrue(condition7.getConditionTypes().contains(WeatherConditionType.CLOUDS_FEW));
    }

    public static void checkWeather(ru.gelin.android.weather.v_0_2.Weather weather) {
        assertEquals("Omsk, Omsk Oblast", weather.getLocation().getText());
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(2010, Calendar.DECEMBER, 28, 6, 0, 0);
        assertEquals(calendar.getTime(), weather.getTime());
        assertEquals(ru.gelin.android.weather.v_0_2.UnitSystem.US, weather.getUnitSystem());
        assertEquals(4, weather.getConditions().size());

        ru.gelin.android.weather.v_0_2.WeatherCondition condition0 = weather.getConditions().get(0);
        assertEquals("Clear", condition0.getConditionText());
        ru.gelin.android.weather.v_0_2.Temperature temp0 =
            condition0.getTemperature(ru.gelin.android.weather.v_0_2.UnitSystem.US);
        assertEquals(-11, temp0.getCurrent());
        assertEquals(-10, temp0.getLow());
        assertEquals(-4, temp0.getHigh());
        assertEquals("Humidity: 66%", condition0.getHumidityText());
        assertEquals("Wind: SW at 2 mph", condition0.getWindText());

        ru.gelin.android.weather.v_0_2.WeatherCondition condition1 = weather.getConditions().get(1);
        assertEquals("Snow Showers", condition1.getConditionText());
        ru.gelin.android.weather.v_0_2.Temperature temp1 =
            condition1.getTemperature(ru.gelin.android.weather.v_0_2.UnitSystem.US);
        assertEquals(7, temp1.getCurrent());
        assertEquals(-7, temp1.getLow());
        assertEquals(20, temp1.getHigh());

        ru.gelin.android.weather.v_0_2.WeatherCondition condition2 = weather.getConditions().get(2);
        assertEquals("Partly Sunny", condition2.getConditionText());
        ru.gelin.android.weather.v_0_2.Temperature temp2 =
            condition2.getTemperature(ru.gelin.android.weather.v_0_2.UnitSystem.US);
        assertEquals(-10, temp2.getCurrent());
        assertEquals(-14, temp2.getLow());
        assertEquals(-6, temp2.getHigh());

        ru.gelin.android.weather.v_0_2.WeatherCondition condition3 = weather.getConditions().get(3);
        assertEquals("Partly Sunny", condition3.getConditionText());
        ru.gelin.android.weather.v_0_2.Temperature temp3 =
            condition3.getTemperature(ru.gelin.android.weather.v_0_2.UnitSystem.US);
        assertEquals(-22, temp3.getCurrent());
        assertEquals(-29, temp3.getLow());
        assertEquals(-15, temp3.getHigh());
    }

    @SuppressWarnings("deprecation")
    public static void checkWeather_v_0_2(Weather weather) {
        assertEquals("Omsk, Omsk Oblast", weather.getLocation().getText());
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(2010, Calendar.DECEMBER, 28, 6, 0, 0);
        assertEquals(calendar.getTime(), weather.getTime());
        assertEquals(4, weather.getConditions().size());

        WeatherCondition condition0 = weather.getConditions().get(0);
        assertEquals("Clear", condition0.getConditionText());
        Temperature temp0 = condition0.getTemperature();
        assertEquals(-11, temp0.getCurrent());
        assertEquals(-10, temp0.getLow());
        assertEquals(-4, temp0.getHigh());
        assertEquals("Humidity: 66%", condition0.getHumidityText());
        assertEquals("Wind: SW at 2 mph", condition0.getWindText());

        WeatherCondition condition1 = weather.getConditions().get(1);
        assertEquals("Snow Showers", condition1.getConditionText());
        Temperature temp1 = condition1.getTemperature();
        assertEquals(7, temp1.getCurrent());
        assertEquals(-7, temp1.getLow());
        assertEquals(20, temp1.getHigh());

        WeatherCondition condition2 = weather.getConditions().get(2);
        assertEquals("Partly Sunny", condition2.getConditionText());
        Temperature temp2 = condition2.getTemperature();
        assertEquals(-10, temp2.getCurrent());
        assertEquals(-14, temp2.getLow());
        assertEquals(-6, temp2.getHigh());

        WeatherCondition condition3 = weather.getConditions().get(3);
        assertEquals("Partly Sunny", condition3.getConditionText());
        Temperature temp3 = condition3.getTemperature();
        assertEquals(-22, temp3.getCurrent());
        assertEquals(-29, temp3.getLow());
        assertEquals(-15, temp3.getHigh());
    }

    public static JSONObject readJSON(String resourceName) throws IOException, JSONException {
        Reader reader = new InputStreamReader(WeatherUtils.class.getClassLoader().getResourceAsStream(resourceName));
        StringBuilder buffer = new StringBuilder();
        int c = reader.read();
        while (c >= 0) {
            buffer.append((char)c);
            c = reader.read();
        }
        JSONTokener parser = new JSONTokener(buffer.toString());
        return (JSONObject)parser.nextValue();
    }

}
