package com.mrap.jurnalapp.data;

public abstract class JnlData {
    public abstract void openChildrenDbs(DbFactory dbFactory);
    public abstract void closeChildrenDbs();
}
