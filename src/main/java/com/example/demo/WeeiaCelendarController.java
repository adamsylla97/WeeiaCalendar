package com.example.demo;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@RestController
@RequestMapping("/")
public class WeeiaCelendarController {

    @GetMapping("weeiaCalendar/november")
    public ResponseEntity<Resource> generateNovemberCalendar() throws IOException {

        ICalendar novemberCalendar = new ICalendar();

        String november = "http://www.weeia.p.lodz.pl/pliki_strony_kontroler/kalendarz.php?rok=2019&miesiac=11&lang=1";
        String websiteContentNovember = getWebsiteHTML(november);

        List<WeeiaEvent> novemberEvents = getWeeiaEvents(websiteContentNovember);

        LocalDate actualDate = LocalDate.now();
        Month month = actualDate.getMonth();



        for(WeeiaEvent wEvent: novemberEvents){
            VEvent event = new VEvent();
            event.setSummary(wEvent.getEventName());
            Date eventDate = new GregorianCalendar(actualDate.getYear(),month.getValue()-1, Integer.valueOf(wEvent.getEventDay())).getTime();
            event.setDateStart(eventDate);
            event.setDateEnd(eventDate);
            novemberCalendar.addEvent(event);
        }

        File calendarFile = new File("november.ics");
        Biweekly.write(novemberCalendar).go(calendarFile);
        Resource fileSystemResource = new FileSystemResource(calendarFile);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/calendar"))
                .body(fileSystemResource);
    }

    @GetMapping("weeiaCalendar/december")
    public ResponseEntity<Resource> generateDecemberCalendar() throws IOException {

        ICalendar decemberCalendar = new ICalendar();

        String december = "http://www.weeia.p.lodz.pl/pliki_strony_kontroler/kalendarz.php?rok=2019&miesiac=12&lang=1";
        String websiteContentDecember = getWebsiteHTML(december);

        LocalDate actualDate = LocalDate.now();
        Month month = actualDate.getMonth();

        List<WeeiaEvent> decemberEvents = getWeeiaEvents(websiteContentDecember);

        for(WeeiaEvent wEvent: decemberEvents){
            VEvent event = new VEvent();
            event.setSummary(wEvent.getEventName());
            Date eventDate = new GregorianCalendar(actualDate.getYear(),month.getValue()-1, Integer.valueOf(wEvent.getEventDay())).getTime();
            event.setDateStart(eventDate);
            event.setDateEnd(eventDate);
            decemberCalendar.addEvent(event);
        }

        File calendarFile = new File("december.ics");
        Biweekly.write(decemberCalendar).go(calendarFile);

        Resource fileSystemResource = new FileSystemResource(calendarFile);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/calendar"))
                .body(fileSystemResource);

    }

    private static List<WeeiaEvent> getWeeiaEvents(String websiteContent){
        String[] splitted = websiteContent.split("href=\"javascript:void\\(\\);\">");
        List<String> eventsCalendar = new ArrayList<String>();

        for (String s : splitted) {
            if (s.contains("<div class=\"calendar-text\"><div class=\"InnerBox\"><p>")) {
                eventsCalendar.add(s);
            }
        }

        List<WeeiaEvent> readyEvents = new ArrayList<WeeiaEvent>();
        for(String event: eventsCalendar){
            String[] splittedEvent = event.split("</a>|<p>|</p>");
            readyEvents.add(new WeeiaEvent(splittedEvent[2],splittedEvent[0]));
        }

        return readyEvents;
    }

    private static String getWebsiteHTML(String website){
        try {

            URL url = new URL(website);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;
            String returnString = "";

            while ((inputLine = in.readLine()) != null)
                returnString += inputLine;
            in.close();

            return returnString;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}