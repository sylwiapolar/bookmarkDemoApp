package com.semanticsquare.thrillio.bgjobs;

import com.semanticsquare.thrillio.dao.BookmarkDao;
import com.semanticsquare.thrillio.entities.WebLink;
import com.semanticsquare.thrillio.util.HttpConnect;
import com.semanticsquare.thrillio.util.IOUtil;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class WebpageDownloaderTask implements Runnable {

    private static BookmarkDao dao = new BookmarkDao();
    private static final long TIME_FRAME = 3_000_000_000L;
    private boolean downloadAll = false;
    private ExecutorService downloadExecutor;

    public WebpageDownloaderTask(boolean downloadAll) {
        this.downloadAll = downloadAll;
        downloadExecutor = Executors.newFixedThreadPool(7);
    }

    private static class Downloader<T extends WebLink> implements Callable<T> {
        private T weblink;

        public Downloader(T weblink) {
            this.weblink = weblink;
        }

        public T call() {
            try {
                if (!weblink.getUrl().endsWith(".pdf")) {
                    weblink.setDownloadStatus(WebLink.DownloadStatus.FAILED);
                    String htmlPage = HttpConnect.download(weblink.getUrl());
                    weblink.setHtmlPage(htmlPage);
                } else {
                    weblink.setDownloadStatus(WebLink.DownloadStatus.NOT_ELIGIBLE);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return weblink;
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {

            List<WebLink> webLinks = getWebLinks();

            if (webLinks.size() > 0) {
                download(webLinks);
            } else {
                System.out.println("No new Web Links to download!");
            }

            try {
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        downloadExecutor.shutdown();
    }

    private void download(List<WebLink> webLinks) {
        List<Downloader<WebLink>> tasks = getTasks(webLinks);
        List<Future<WebLink>> futures = new ArrayList<>();

        try {
            futures = downloadExecutor.invokeAll(tasks, TIME_FRAME, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Future<WebLink> future : futures) {
            try {
                if (!future.isCancelled()) {
                    WebLink webLink = future.get();
                    String webPage = webLink.getHtmlPage();
                    if (webPage != null) { 
                        IOUtil.write(webPage, webLink.getId());
                        webLink.setDownloadStatus(WebLink.DownloadStatus.SUCCESS);

                        String message = "Download Success: " + webLink.getUrl();
                        System.out.println(message);
                    } else {
                        String message = "Webpage not downloaded: " + webLink.getUrl();
                        System.out.println(message);
                    }
                } else {
                    System.out.println("\n\nTask is cancelled --> " + Thread.currentThread());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Downloader<WebLink>> getTasks(List<WebLink> webLinks) {
        List<Downloader<WebLink>> tasks = new ArrayList<>();

        for (WebLink webLink : webLinks) {
            tasks.add(new Downloader<WebLink>(webLink));
        }
        return tasks;
    }

    private List<WebLink> getWebLinks() {
        List<WebLink> webLinks = null;

        if (downloadAll) {
            webLinks = dao.getAllWebLinks();
            downloadAll = false;
        } else {
            webLinks = dao.getWebLinks(WebLink.DownloadStatus.NOT_ATTEMPTED);
        }

        return webLinks;
    }
}
