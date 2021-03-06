/*******************************************************************************
 * Copyright 2002-2009  Xilinx Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/*
 * 
 *
 * 
 */
package org.xronos.openforge.lim.util;

import org.xronos.openforge.lim.Component;
import org.xronos.openforge.schedule.LatencyTracker;

public interface PostScheduleCallback {
	/**
	 * Method provided by entities requiring post scheduling information by a
	 * component. Called after the component is completely scheduled and all
	 * attributes are updated.
	 * 
	 * @param lt
	 *            an instance of the LatencyTracker used to schedule the
	 *            specified component.
	 * @param comp
	 *            the component for which scheduling was just completed.
	 */
	void postSchedule(LatencyTracker lt, Component comp);
}
