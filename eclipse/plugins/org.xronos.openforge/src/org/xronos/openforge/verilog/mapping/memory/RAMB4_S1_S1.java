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

package org.xronos.openforge.verilog.mapping.memory;

class RAMB4_S1_S1 extends DualPortBlockRam {

	protected RAMB4_S1_S1() {
	}

	@Override
	public String getName() {
		return ("RAMB4_S1_S1");
	}

	@Override
	public int getWidth() {
		return (1);
	}

	@Override
	public int getDepth() {
		return (4096);
	}

	@Override
	public int getCost() {
		return (1);
	}

	@Override
	public boolean isBlockRam16() {
		return (false);
	}
}
