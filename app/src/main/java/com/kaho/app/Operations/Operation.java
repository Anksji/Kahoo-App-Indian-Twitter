package com.kaho.app.Operations;

import com.kaho.app.Data.Models.KahooPostModel;

public class Operation {

    public KahooPostModel kahooPostModel;
    public int kahooType;

    public Operation(KahooPostModel kahooPostModel, int kahooType) {
        this.kahooPostModel = kahooPostModel;
        this.kahooType = kahooType;
    }
}
