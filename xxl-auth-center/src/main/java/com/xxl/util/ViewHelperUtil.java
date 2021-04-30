package com.xxl.util;

import org.springframework.data.domain.Sort;

public class ViewHelperUtil {


    public static Integer getPageByOffsetAndLimit(Integer offset, Integer limit) {
        if (offset % limit == 0) {
            return offset / limit;
        } else {
            return offset / limit + 1;
        }
    }

    public static Sort.Direction getSortDirectionByString(String sort) {
        if (sort.equals("desc")) {
            return Sort.Direction.DESC;
        } else {
            return Sort.Direction.ASC;
        }
    }

    public static int getPagingIdByOrderAndTotal(String order, int total, int totalOffset, int pageOffset) {
        if (order.equals("asc")) {
            return totalOffset + pageOffset;
        } else {
            return total - totalOffset - pageOffset - 1;
        }
    }
}