package com.mordizze.linkshortener.stats.services;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.List;
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
import com.mordizze.linkshortener.stats.models.Interval;

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
        Interval interval = Interval.fromString(input.getInterval());


        Link link = linkRepo.findByShortCode(shortCode).orElseThrow(() -> new RuntimeException());
        String[] topCountries = getTop3Countries();
        String[] topCities = getTop3Cities();
        Map<String, Long> devices = getDevices();
        
        Map<String, Long> clicksOverTime = switch (interval) {
            case DAILY -> getClicksOverDailyInterval();
            case WEEKLY -> getClicksOverWeeklyInterval();
        };

        List<ClickEvents> events = clickEventsRepo.findByLink(link);

        log.info("**************************************************");
        log.info(events.get(0).toString());

        BasicStatsResponse response = new BasicStatsResponse(shortCode, link.getClickCount(), clicksOverTime, topCountries, topCities, devices);
        return response;
    }

    private String[] getTop3Countries() {
        List<Object[]> countries = clickEventsRepo.findDistinctCountriesSortedDesc();
        int len = countries.size();
        if (len > 3)
            len = 3;
        String[] topCountries = new String[len];
        for (int i = 0; i < len; i++) {
            topCountries[i] = (String) countries.get(i)[0];
        }
        return topCountries;
    }

    private String[] getTop3Cities() {
        List<Object[]> cities = clickEventsRepo.findDistinctCitiesSortedDesc();
        int len = cities.size();
        if (len > 3)
            len = 3;

        String[] topCities = new String[len];
        for (int i = 0; i < len; i++) {
            topCities[i] = (String) cities.get(i)[0];
        }
        return topCities;
    }

    private Map<String, Long> getDevices() {
        List<Object[]> devices = clickEventsRepo.findDistinctDeviceCount();

        Map<String, Long> deviceCount = new HashMap<>();
        for (Object[] arr : devices) {
            String device = (String) arr[0];
            long count = (long) arr[1];

            deviceCount.put(device, count);
        }

        return deviceCount;
    }

    private Map<String, Long> getClicksOverWeeklyInterval() {
        LocalDateTime now = LocalDateTime.now().minusWeeks(WEEKLY_INTERVAL);
        List<ClickEvents> events = clickEventsRepo.findByClickedAtAfter(now);


        Map<String, Long> clicksOverTime = new TreeMap<>();

        for (int i = 0; i < WEEKLY_INTERVAL; i++) {
            LocalDate date = LocalDate.now().minusWeeks(i);
            int year = date.getYear();
            int week = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
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

}
