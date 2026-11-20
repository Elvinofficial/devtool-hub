package com.devtoolshub.dto;

public record DashboardStats (
    long projects,
    long developers,
    long tools,
    long languages,
    long operatingSystems,
    long categories
){ }
