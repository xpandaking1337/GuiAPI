package pl.panda.gui.pages;

import java.util.concurrent.atomic.AtomicInteger;

public class Page extends AtomicInteger {

    private AtomicInteger currentPage;

    public Page() {
        this.currentPage = new AtomicInteger(0);
    }

    public AtomicInteger getCurrentPage() {
        return currentPage;
    }

    public Integer getCurrentPageCount() {
        return currentPage.get() + 1;
    }

    public Integer getNextPageCount() {
        return currentPage.get() + 2;
    }

    public Integer getPreviousPageCount() {
        return currentPage.get();
    }

}
