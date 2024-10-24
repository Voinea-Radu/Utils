package com.voinearadu.generic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Range {

    private int min;
    private int max;

    @SuppressWarnings("unused")
    public static Range exact(int n) {
        return new Range(n, n);
    }

}