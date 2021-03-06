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

package org.xronos.openforge.lim;

import java.util.Collection;

import org.xronos.openforge.lim.primitive.And;


/**
 * Stallboard is a Scoreboard with additional 'stall' inputs. These inputs
 * function like a regular Scoreboard input in that the output will not go high
 * until they are recieved, except that their internal state is initialized to
 * 'set' after programming and reset. This means that during the first pass
 * through a design no input is needed on the stall ports in order to fire the
 * output. Subsequent passes through the design have Stallboards behaving
 * identically to a Scoreboard with equivalent inputs.
 * 
 * <p>
 * Created: Fri Sep 24 13:29:52 2004
 * 
 * @author imiller, last modified by $Author: imiller $
 * @version $Id: Stallboard.java 2 2005-06-09 20:00:48Z imiller $
 */
public class Stallboard extends Scoreboard {

	public Stallboard(Collection<Bus> buses) {
		super(buses);
	}

	@Override
	protected boolean isStallable() {
		return true;
	}

	/**
	 * Creates a 'stall' input to the Stallboard along with all the necessary
	 * internal logic, returning the new stall Port.
	 * 
	 * @return a non null Port
	 */
	public Port addStallPort() {
		final Port port = makeDataPort();

		final int portNum = getDataPorts().size() - 1;
		final And and = getScbdAnd();
		final Port andPort = and.makeDataPort();
		createSlice(portNum, andPort, port, true);

		return port;
	}

	/**
	 * Add a new stall port for each bus in the stallBuses Collection and
	 * connects the bus to it via the {@link Port#setBus} method.
	 * 
	 * @param stallBuses
	 *            a Collection of Bus objects.
	 */
	public void addStallSignals(Collection<Bus> stallBuses) {
		for (Bus bus : stallBuses) {
			addStallPort().setBus(bus);
		}
	}

}// Stallboard
