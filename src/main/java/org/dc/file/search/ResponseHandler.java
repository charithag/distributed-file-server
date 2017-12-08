package org.dc.file.search;

public interface ResponseHandler {
    void onSuccess();
    void onError(Exception e);
}
