package com.big.company.analytics.services;

/**
 * Analytical report operations of employee data
 */
public interface AnalyticsService {

    /**
     * Runs the analytics process by reading employees from the file, generating employee hierarchy,
     * and running analytical reports on the hierarchy.
     */
    void runAnalytics();
}
