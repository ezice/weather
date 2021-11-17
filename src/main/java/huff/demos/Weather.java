package huff.demos;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is an example application showing how to build a greenfield project.
 * 
 * We're using a location in the Arizona desert to make the API call documented here:
 * https://www.weather.gov/documentation/services-web-api
 *
 */
public class Weather {
	public static final String REQUEST_URL = "https://api.weather.gov/gridpoints/PSR/91,76/forecast";
	public static void main(String[] args) {
		/*
		 * Make the API call
		 */

		HttpClient client = HttpClient.newBuilder().version(Version.HTTP_1_1)
				.followRedirects(Redirect.NORMAL)
				.connectTimeout(Duration.ofSeconds(20))
				.build();
		HttpRequest request = HttpRequest.newBuilder()
		         .uri(URI.create(REQUEST_URL))
		         .build();
		
		/*
		 * I'm skipping non-zero response handling here, but feel free to add it yourself.
		 */
		HttpResponse<String> response = null;
		
		try {
			response = client.send(request, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			System.err.println("Error calling weather API: " + e.getMessage());
			System.exit(1);
		}

		/*
		 * Parse the JSON
		 * 
		 * Here is an example JSON output:
		 * 
		// {
		//    "@context": [
		//        "https://geojson.org/geojson-ld/geojson-context.jsonld",
		//        {
		//            "@version": "1.1",
		//            "wx": "https://api.weather.gov/ontology#",
		//            "geo": "http://www.opengis.net/ont/geosparql#",
		//            "unit": "http://codes.wmo.int/common/unit/",
		//            "@vocab": "https://api.weather.gov/ontology#"
		//        }
		//    ],
		//    "type": "Feature",
		//    "geometry": {
		//        "type": "Polygon",
		//        "coordinates": [
		//            [
		//                [
		//                    -113.9551762,
		//                    33.687590899999996
		//                ],
		//                [
		//                    -113.95139619999999,
		//                    33.665236799999995
		//                ],
		//                [
		//                    -113.92460179999999,
		//                    33.668372299999994
		//                ],
		//                [
		//                    -113.92837659999999,
		//                    33.690726799999993
		//                ],
		//                [
		//                    -113.9551762,
		//                    33.687590899999996
		//                ]
		//            ]
		//        ]
		//    },
		//    "properties": {
		//        "updated": "2021-11-16T11:29:43+00:00",
		//        "units": "us",
		//        "forecastGenerator": "BaselineForecastGenerator",
		//        "generatedAt": "2021-11-16T19:07:10+00:00",
		//        "updateTime": "2021-11-16T11:29:43+00:00",
		//        "validTimes": "2021-11-16T05:00:00+00:00/P7DT20H",
		//        "elevation": {
		//            "unitCode": "wmoUnit:m",
		//            "value": 426.11040000000003
		//        },
		//        "periods": [
		//            {
		//                "number": 1,
		//                "name": "This Afternoon",
		//                "startTime": "2021-11-16T12:00:00-07:00",
		//                "endTime": "2021-11-16T18:00:00-07:00",
		//                "isDaytime": true,
		//                "temperature": 84,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": "falling",
		//                "windSpeed": "5 to 10 mph",
		//                "windDirection": "SSW",
		//                "icon": "https://api.weather.gov/icons/land/day/bkn?size=medium",
		//                "shortForecast": "Partly Sunny",
		//                "detailedForecast": "Partly sunny. High near 84, with temperatures falling to around 78 in the afternoon. South southwest wind 5 to 10 mph."
		//            },
		//            {
		//                "number": 2,
		//                "name": "Tonight",
		//                "startTime": "2021-11-16T18:00:00-07:00",
		//                "endTime": "2021-11-17T06:00:00-07:00",
		//                "isDaytime": false,
		//                "temperature": 51,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": "rising",
		//                "windSpeed": "5 mph",
		//                "windDirection": "S",
		//                "icon": "https://api.weather.gov/icons/land/night/sct?size=medium",
		//                "shortForecast": "Partly Cloudy",
		//                "detailedForecast": "Partly cloudy. Low around 51, with temperatures rising to around 55 overnight. South wind around 5 mph."
		//            },
		//            {
		//                "number": 3,
		//                "name": "Wednesday",
		//                "startTime": "2021-11-17T06:00:00-07:00",
		//                "endTime": "2021-11-17T18:00:00-07:00",
		//                "isDaytime": true,
		//                "temperature": 80,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": "falling",
		//                "windSpeed": "5 to 10 mph",
		//                "windDirection": "ENE",
		//                "icon": "https://api.weather.gov/icons/land/day/sct?size=medium",
		//                "shortForecast": "Mostly Sunny",
		//                "detailedForecast": "Mostly sunny. High near 80, with temperatures falling to around 75 in the afternoon. East northeast wind 5 to 10 mph."
		//            },
		//            {
		//                "number": 4,
		//                "name": "Wednesday Night",
		//                "startTime": "2021-11-17T18:00:00-07:00",
		//                "endTime": "2021-11-18T06:00:00-07:00",
		//                "isDaytime": false,
		//                "temperature": 49,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 mph",
		//                "windDirection": "NE",
		//                "icon": "https://api.weather.gov/icons/land/night/sct?size=medium",
		//                "shortForecast": "Partly Cloudy",
		//                "detailedForecast": "Partly cloudy, with a low around 49. Northeast wind around 5 mph."
		//            },
		//            {
		//                "number": 5,
		//                "name": "Thursday",
		//                "startTime": "2021-11-18T06:00:00-07:00",
		//                "endTime": "2021-11-18T18:00:00-07:00",
		//                "isDaytime": true,
		//                "temperature": 81,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 to 10 mph",
		//                "windDirection": "ESE",
		//                "icon": "https://api.weather.gov/icons/land/day/few?size=medium",
		//                "shortForecast": "Sunny",
		//                "detailedForecast": "Sunny, with a high near 81. East southeast wind 5 to 10 mph."
		//            },
		//            {
		//                "number": 6,
		//                "name": "Thursday Night",
		//                "startTime": "2021-11-18T18:00:00-07:00",
		//                "endTime": "2021-11-19T06:00:00-07:00",
		//                "isDaytime": false,
		//                "temperature": 51,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 to 10 mph",
		//                "windDirection": "SE",
		//                "icon": "https://api.weather.gov/icons/land/night/sct?size=medium",
		//                "shortForecast": "Partly Cloudy",
		//                "detailedForecast": "Partly cloudy, with a low around 51. Southeast wind 5 to 10 mph."
		//            },
		//            {
		//                "number": 7,
		//                "name": "Friday",
		//                "startTime": "2021-11-19T06:00:00-07:00",
		//                "endTime": "2021-11-19T18:00:00-07:00",
		//                "isDaytime": true,
		//                "temperature": 80,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 to 10 mph",
		//                "windDirection": "SSE",
		//                "icon": "https://api.weather.gov/icons/land/day/sct?size=medium",
		//                "shortForecast": "Mostly Sunny",
		//                "detailedForecast": "Mostly sunny, with a high near 80. South southeast wind 5 to 10 mph."
		//            },
		//            {
		//                "number": 8,
		//                "name": "Friday Night",
		//                "startTime": "2021-11-19T18:00:00-07:00",
		//                "endTime": "2021-11-20T06:00:00-07:00",
		//                "isDaytime": false,
		//                "temperature": 51,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 mph",
		//                "windDirection": "SSE",
		//                "icon": "https://api.weather.gov/icons/land/night/bkn?size=medium",
		//                "shortForecast": "Mostly Cloudy",
		//                "detailedForecast": "Mostly cloudy, with a low around 51. South southeast wind around 5 mph."
		//            },
		//            {
		//                "number": 9,
		//                "name": "Saturday",
		//                "startTime": "2021-11-20T06:00:00-07:00",
		//                "endTime": "2021-11-20T18:00:00-07:00",
		//                "isDaytime": true,
		//                "temperature": 78,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 to 10 mph",
		//                "windDirection": "NE",
		//                "icon": "https://api.weather.gov/icons/land/day/bkn?size=medium",
		//                "shortForecast": "Partly Sunny",
		//                "detailedForecast": "Partly sunny, with a high near 78. Northeast wind 5 to 10 mph."
		//            },
		//            {
		//                "number": 10,
		//                "name": "Saturday Night",
		//                "startTime": "2021-11-20T18:00:00-07:00",
		//                "endTime": "2021-11-21T06:00:00-07:00",
		//                "isDaytime": false,
		//                "temperature": 52,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 mph",
		//                "windDirection": "NNW",
		//                "icon": "https://api.weather.gov/icons/land/night/bkn?size=medium",
		//                "shortForecast": "Mostly Cloudy",
		//                "detailedForecast": "Mostly cloudy, with a low around 52. North northwest wind around 5 mph."
		//            },
		//            {
		//                "number": 11,
		//                "name": "Sunday",
		//                "startTime": "2021-11-21T06:00:00-07:00",
		//                "endTime": "2021-11-21T18:00:00-07:00",
		//                "isDaytime": true,
		//                "temperature": 76,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 to 10 mph",
		//                "windDirection": "NNW",
		//                "icon": "https://api.weather.gov/icons/land/day/bkn?size=medium",
		//                "shortForecast": "Partly Sunny",
		//                "detailedForecast": "Partly sunny, with a high near 76. North northwest wind 5 to 10 mph."
		//            },
		//            {
		//                "number": 12,
		//                "name": "Sunday Night",
		//                "startTime": "2021-11-21T18:00:00-07:00",
		//                "endTime": "2021-11-22T06:00:00-07:00",
		//                "isDaytime": false,
		//                "temperature": 51,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 mph",
		//                "windDirection": "N",
		//                "icon": "https://api.weather.gov/icons/land/night/sct?size=medium",
		//                "shortForecast": "Partly Cloudy",
		//                "detailedForecast": "Partly cloudy, with a low around 51. North wind around 5 mph."
		//            },
		//            {
		//                "number": 13,
		//                "name": "Monday",
		//                "startTime": "2021-11-22T06:00:00-07:00",
		//                "endTime": "2021-11-22T18:00:00-07:00",
		//                "isDaytime": true,
		//                "temperature": 78,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 to 10 mph",
		//                "windDirection": "N",
		//                "icon": "https://api.weather.gov/icons/land/day/sct?size=medium",
		//                "shortForecast": "Mostly Sunny",
		//                "detailedForecast": "Mostly sunny, with a high near 78. North wind 5 to 10 mph."
		//            },
		//            {
		//                "number": 14,
		//                "name": "Monday Night",
		//                "startTime": "2021-11-22T18:00:00-07:00",
		//                "endTime": "2021-11-23T06:00:00-07:00",
		//                "isDaytime": false,
		//                "temperature": 50,
		//                "temperatureUnit": "F",
		//                "temperatureTrend": null,
		//                "windSpeed": "5 mph",
		//                "windDirection": "SSW",
		//                "icon": "https://api.weather.gov/icons/land/night/sct?size=medium",
		//                "shortForecast": "Partly Cloudy",
		//                "detailedForecast": "Partly cloudy, with a low around 50. South southwest wind around 5 mph."
		//            }
		//        ]
		//    }
		//}
         *
         * If this were a Java object, we'd be looking for map.properties.periods[2] (tomorrow) and periods[3] (tomorrow night).
		 */
		
		System.out.println(response.body());
		
		// create object mapper instance
	    ObjectMapper mapper = new ObjectMapper();

	    // convert JSON file to map
	    Map<String, String> map = null;
		try {
			map = mapper.readValue(response.body(), Map.class);
		} catch (JsonProcessingException e) {
			System.err.println("Error parsing weather response: " + e.getMessage());
			System.exit(2);
		}

	    /*
	     * get properties.periods 2 tomorrow morning and 3 tomorrow night.
	     */
	    
	    /*
	     * properties contains another JSON object.
	     */
	    
	    String propertiesString = map.get("properties");
	    
	    // convert propertiesString to a map
	    Map<String, String> propertiesMap = null;
		try {
			propertiesMap = mapper.readValue(propertiesString, Map.class);
		} catch (JsonProcessingException e) {
			System.err.println("Error parsing properties response: " + e.getMessage());
			System.exit(3);
		}
	    
	    // print map entries
	    for (Map.Entry<?, ?> entry : propertiesMap.entrySet()) {
	        System.out.println(entry.getKey() + "=" + entry.getValue());
	    }
	    
		/*
		 * Display the output
		 */
	}
}
