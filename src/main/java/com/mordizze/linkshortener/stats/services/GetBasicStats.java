package com.mordizze.linkshortener.stats.services;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mordizze.linkshortener.Command;
import com.mordizze.linkshortener.link.LinkRepo;
import com.mordizze.linkshortener.link.models.ClickEvents;
import com.mordizze.linkshortener.link.models.Link;
import com.mordizze.linkshortener.stats.ClickEventsRepo;
import com.mordizze.linkshortener.stats.models.BasicStatsRequest;
import com.mordizze.linkshortener.stats.models.BasicStatsResponse;
import com.mordizze.linkshortener.stats.models.CityClicks;
import com.mordizze.linkshortener.stats.models.CountryClicks;
import com.mordizze.linkshortener.stats.models.DeviceClicks;
import com.mordizze.linkshortener.stats.models.ReferrerClicks;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class GetBasicStats implements Command<BasicStatsRequest, BasicStatsResponse>{

    private final int DAILY_INTERVAL = 7;
    private final int WEEKLY_INTERVAL = 4;

    private final ClickEventsRepo clickEventsRepo;
    private final LinkRepo linkRepo;

    @Override
    public BasicStatsResponse execute(BasicStatsRequest input) {
        // TODO Auto-generated method stub
        String shortCode = input.getShortCode();
        String interval = input.getInterval();

        Link link = linkRepo.findByShortCode(shortCode).orElseThrow(() -> new RuntimeException());
        List<CountryClicks> topCountries = getTop3Countries();
        List<CityClicks> topCities = getTop3Cities();
        List<DeviceClicks> devices = getDevices();
        List<ReferrerClicks> topReferrers = getTopReferrers();
        
        Map<String, Map<String, Long>> clicksOverTime = new HashMap<>();
    
        if (interval.equalsIgnoreCase("daily")) {
            clicksOverTime.put("daily", getClicksOverDailyInterval());
        } else if (interval.equalsIgnoreCase("weekly")) {
            clicksOverTime.put("weekly", getClicksOverWeeklyInterval());
        } else {
            throw new IllegalArgumentException("Unsupported interval: " + interval);
        }

        List<ClickEvents> events = clickEventsRepo.findByLink(link);

        log.info("**************************************************");
        log.info(events.get(0).toString());

        BasicStatsResponse response = new BasicStatsResponse(shortCode,
                                                            link.getClickCount(),
                                                            link.getCreatedAt(),
                                                            clicksOverTime,
                                                            topCountries,
                                                            topCities,
                                                            topReferrers,
                                                            devices);
        return response;
    }

    private List<CountryClicks> getTop3Countries() {
        List<Object[]> countries = clickEventsRepo.findDistinctCountriesSortedDesc();
        int len = countries.size();
        if (len > 3)
            len = 3;

        List<CountryClicks> countryClicks = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            String country = (String) countries.get(i)[0];
            long count = (long) countries.get(i)[1];
            countryClicks.add(new CountryClicks(country, count));
        }
        return countryClicks;
    }

    private List<CityClicks> getTop3Cities() {
        List<Object[]> cities = clickEventsRepo.findDistinctCitiesSortedDesc();
        int len = cities.size();
        if (len > 3)
            len = 3;

        List<CityClicks> cityClicks = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            String city = (String) cities.get(i)[0];
            long count = (long) cities.get(i)[1];
            cityClicks.add(new CityClicks(city, count));
        }
        return cityClicks;
    }

    private List<DeviceClicks> getDevices() {
        List<Object[]> devices = clickEventsRepo.findDistinctDeviceCount();

        List<DeviceClicks> deviceCount = new ArrayList<>(devices.size());
        for (Object[] arr : devices) {
            String device = (String) arr[0];
            long count = (long) arr[1];
            deviceCount.add(new DeviceClicks(device, count));    
        }
        return deviceCount;
    }

    private Map<String, Long> getClicksOverWeeklyInterval() {
        LocalDateTime now = LocalDateTime.now().minusWeeks(WEEKLY_INTERVAL);
        List<ClickEvents> events = clickEventsRepo.findByClickedAtAfter(now);

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Map<String, Long> clicksOverTime = new TreeMap<>();

        for (int i = 0; i < WEEKLY_INTERVAL; i++) {
            LocalDate date = LocalDate.now().minusWeeks(i);
            int year = date.getYear();
            int week = date.get(weekFields.weekOfWeekBasedYear());
            String yearWeek = String.format("%d-W%02d", year, week);

            clicksOverTime.put(yearWeek, 0L);
        }
        

        Map<String, Long> eventCounts = events.stream()
        .map(event -> {
            LocalDate eventDate = event.getClickedAt().toLocalDate();
            int year = eventDate.getYear();
            int week = eventDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            return String.format("%d-W%02d", year, week);
        })
        .collect(Collectors.groupingBy(
            weekKey -> weekKey,
            Collectors.counting()
        ));

        eventCounts.forEach((weekKey, count) -> {
            if (clicksOverTime.containsKey(weekKey))
                clicksOverTime.put(weekKey, count);
        });


        return clicksOverTime;
    }

    private Map<String, Long> getClicksOverDailyInterval() {
        LocalDateTime now = LocalDateTime.now().minusDays(DAILY_INTERVAL);
        List<ClickEvents> events = clickEventsRepo.findByClickedAtAfter(now);
        Map<String, Long> clicksOverTime = new TreeMap<>();

        for (int i = 0; i < DAILY_INTERVAL; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            clicksOverTime.put(date.toString(), 0L);
        }

        Map<String, Long> countEvents = events.stream().map(event -> {
            LocalDate date = event.getClickedAt().toLocalDate();
            return date.toString();
        })
        .collect(Collectors.groupingBy(
            yearDayKay -> yearDayKay,
            Collectors.counting()
        ));    

        countEvents.forEach((yearDayKay, count) -> {
            if (clicksOverTime.containsKey(yearDayKay))
                clicksOverTime.put(yearDayKay, count);
        });

        
        // for (ClickEvents event : events) {
        //     String date = event.getClickedAt().toLocalDate().toString();
        //     if (clicksOverTime.containsKey(date)) {
        //         long clicks = clicksOverTime.get(date) + 1;
        //         clicksOverTime.put(date, clicks);
        //     } else {
        //         clicksOverTime.put(date, (long) 1);
        //     }
        // }

        return clicksOverTime;
    }

    public List<ReferrerClicks> getTopReferrers() {
        List<Object[]> referrers = clickEventsRepo.findDistnctReferrersCount();

        int len = referrers.size();
        if (len > 3)
            len = 3;

        List<ReferrerClicks> topReferrers = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            String referrer = (String) referrers.get(i)[0];
            long count = (long) referrers.get(i)[1];

            topReferrers.add(new ReferrerClicks(referrer, count));
        }
        return topReferrers;
    }

}
