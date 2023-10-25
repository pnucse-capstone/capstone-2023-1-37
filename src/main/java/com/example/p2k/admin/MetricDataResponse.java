package com.example.p2k.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class MetricDataResponse {
   private List<Instant> timestamps;
   private List<Double> value;
}
