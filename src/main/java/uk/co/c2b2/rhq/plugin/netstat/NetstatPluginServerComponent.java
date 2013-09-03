/*
 * RHQ Management Platform
 * Copyright (C) 2005-2008 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package uk.co.c2b2.rhq.plugin.netstat;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.ConfigurationUpdateStatus;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.configuration.ConfigurationFacet;
import org.rhq.core.pluginapi.configuration.ConfigurationUpdateReport;
import org.rhq.core.pluginapi.inventory.DeleteResourceFacet;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.core.system.NetworkStats;
import org.rhq.core.system.SystemInfo;

/**
 * @author Jaromir Hamala
 *
 */
public class NetstatPluginServerComponent implements ResourceComponent, MeasurementFacet  {
    private final Log log = LogFactory.getLog(NetstatPluginServerComponent.class);

    private ResourceContext resourceContext;

    @Override
    public void start(ResourceContext context) {
        resourceContext = context;
    }

    @Override
    public void stop() {
    }

    @Override
    public AvailabilityType getAvailability() {
        return resourceContext.getSystemInformation().isNative() ? AvailabilityType.UP : AvailabilityType.DOWN;
    }

    @Override
    public void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> requests) {
        SystemInfo systemInfo = resourceContext.getSystemInformation();
        NetworkStats networkStats = systemInfo.getNetworkStats(null, -1);

        for (MeasurementScheduleRequest request : requests) {
            String name = request.getName();
            try {
                Number value = networkStats.getByName(name);
                report.addData(new MeasurementDataNumeric(request, value.doubleValue()));
            } catch (Exception e) {
                log.error("Failed to obtain measurement [" + name + "]. Cause: " + e);
            }
        }
    }
}
