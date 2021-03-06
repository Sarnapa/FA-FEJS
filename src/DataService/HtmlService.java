package DataService;

import Layout.LayoutInit;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Base class that download source code of page that will be parsed.
 * Youth leagues pages are downloaded as JSON due to structure of these pages.
 */

class HtmlService {

    static Document getHtmlSource(String address, boolean isJSON, LayoutInit controller) {
        int timeout = 30 * 1000;
        int delay1 = 5 * 1000; // for "normal" league
        int delay2 = 10 * 1000; // for "youth" league
        if (isJSON)
            return loadJSON(address, timeout, delay2, controller);
        else
            return loadHTML(address, timeout, delay1, controller);
    }


    private static Document loadJSON(String address, int timeout, int delay, LayoutInit controller) {
        int i = 0;
        Connection conn;
        Connection.Response resp = null;
        while (i < 5) {
            try {
                conn = Jsoup.connect(address)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/53.0.2785.143 Chrome/53.0.2785.143 Safari/537.36")
                        .maxBodySize(0).referrer("http://www.google.com")
                        .timeout(timeout).ignoreHttpErrors(true).ignoreContentType(true);
                resp = conn.execute();
                if (resp.statusCode() == 200) {
                    String json = resp.body();
                    return Jsoup.parse(json);
                } else if (resp.statusCode() == 420) // when the client is being rate limited
                {
                    try {
                        controller.log("JSON File - HTTP Status 420 for address: " + address, 1);
                        Thread.currentThread().sleep(delay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                } else
                    ++i;
            } catch (SocketTimeoutException ste) {
                ++i;
                controller.log("Socket timeout reached for address: " + address + " . Attempt: " + i, 2);
            } catch (IOException e) {
                controller.log("Cannot download HTML file from address: " + address, 2);
                return null;
            }
        }
        if (resp != null)
            controller.log("Cannot download HTML file from address: " + address, 2);
        return null;
    }

    private static Document loadHTML(String address, int timeout, int delay, LayoutInit controller) {
        int i = 0;
        Connection conn;
        Connection.Response resp = null;
        while (i < 5) {
            try {
                conn = Jsoup.connect(address)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/53.0.2785.143 Chrome/53.0.2785.143 Safari/537.36")
                        .maxBodySize(0).referrer("http://www.google.com")
                        .timeout(timeout).ignoreHttpErrors(true);
                resp = conn.execute();
                if (resp.statusCode() == 200) {
                    return conn.get();
                }
                else if (resp.statusCode() == 420) // when the client is being rate limited
                {
                    try {
                        controller.log("HTML File - HTTP Status 420 for address: " + address, 1);
                        Thread.currentThread().sleep(delay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                } else
                    ++i;
            } catch (SocketTimeoutException ste) {
                ++i;
                controller.log("Socket timeout reached for address: " + address + " . Attempt: " + i, 2);
            } catch (IOException e) {
                controller.log("Cannot download HTML file from address: " + address, 2);
                return null;
            }
        }
        if (resp != null)
            controller.log("Cannot download HTML file from address: " + address, 2);
        return null;
    }
}
