package com.pw.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: P
 * @DateTime: 2022/2/10 22:50
 **/
@Getter
@AllArgsConstructor
public enum SearchModeEnum {
    MYSQL("mysql", "mySqlSearchStrategyImpl");
    /**
     * 模式
     */
    private final String mode;

    /**
     * 策略
     */
    private final String strategy;

    public static  String getStrategy(String mode){
        for (SearchModeEnum value : SearchModeEnum.values()){
            if (value.getMode().equals(mode)){
                return value.getStrategy();
            }
        }
        return null;
    }
}
