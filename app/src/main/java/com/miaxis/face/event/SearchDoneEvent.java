package com.miaxis.face.event;

import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.bean.Record;

import java.util.List;

/**
 * Created by xu.nan on 2017/6/8.
 */

public class SearchDoneEvent {

    private long totalCount;
    private long totalPageNum;
    private List<IDCardRecord> recordList;

    public SearchDoneEvent(long totalCount, long totalPageNum, List<IDCardRecord> recordList) {
        this.totalCount = totalCount;
        this.totalPageNum = totalPageNum;
        this.recordList = recordList;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(long totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    public List<IDCardRecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<IDCardRecord> recordList) {
        this.recordList = recordList;
    }
}
