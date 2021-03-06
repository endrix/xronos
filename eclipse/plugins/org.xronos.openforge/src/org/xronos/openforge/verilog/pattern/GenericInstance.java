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

package org.xronos.openforge.verilog.pattern;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.xronos.openforge.verilog.model.Expression;
import org.xronos.openforge.verilog.model.Input;
import org.xronos.openforge.verilog.model.Module;
import org.xronos.openforge.verilog.model.ModuleInstance;
import org.xronos.openforge.verilog.model.Net;
import org.xronos.openforge.verilog.model.Output;


/**
 * GenericInstance.java
 * 
 * 
 * <p>
 * Created: Thu Feb 20 13:08:56 2003
 * 
 * @author imiller, last modified by $Author: imiller $
 * @version $Id: GenericInstance.java 2 2005-06-09 20:00:48Z imiller $
 */
public class GenericInstance extends ModuleInstance implements ForgePattern {

	private Set<Net> produced = new HashSet<Net>();
	private Set<Net> consumed = new HashSet<Net>();

	public GenericInstance(Module module, String id) {
		super(module, id);
	}

	@Override
	public void connect(Net port, Expression e) {
		super.connect(port, e);
		if (port instanceof Input && e instanceof Net)
			consumed.add((Net) e);
		if (port instanceof Output && e instanceof Net)
			produced.add((Net) e);
	}

	/**
	 * Provides the collection of Nets which this statement of verilog uses as
	 * input signals.
	 */
	@Override
	public Collection<Net> getConsumedNets() {
		return Collections.unmodifiableSet(consumed);
	}

	@Override
	public Collection<Net> getProducedNets() {
		return Collections.unmodifiableSet(produced);
	}

}// GenericInstance
