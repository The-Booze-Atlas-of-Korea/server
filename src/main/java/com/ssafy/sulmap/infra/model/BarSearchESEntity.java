package com.ssafy.sulmap.infra.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarSearchESEntity {
    //  {
    //    "Adress": "대전광역시 대덕구 신탄진동 139-17 (1층) ",
    //    "Name": "영화반점",
    //    "CategoryName": "중국식",
    //    "X": 238620.16,
    //    "Y": 327739.16,
    //    "OpenInfo": "매일 11:00~20:00 브레이크 타임 오후3시~오후5시",
    //    "Menu": "['짜장면', '간짜장', '해물짬뽕', '탕수육', '볶음밥', '쟁반짜장', '짜장밥', '해물짬뽕밥']",
    //    "RestID": 20930
    //  },
    private Long id;
    private String address;
    private String name;
    private String baseCategoryName;
    private Double latitude;
    private Double longitude;
    private String openInfo;
    private String menuJsonString;
    private Date CreatedAt;
    private Date updatedAt;
    private Date deletedAt;

}
