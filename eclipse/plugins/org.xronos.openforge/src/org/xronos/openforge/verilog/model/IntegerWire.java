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
package org.xronos.openforge.verilog.model;

/**
 * IntegerWire is a Net which of type "integer".
 * 
 * <P>
 * 
 * Created: Fri Feb 09 2001
 * 
 * @author abk, last modified $Author: imiller $
 * @version $Id: IntegerWire.java 2 2005-06-09 20:00:48Z imiller $
 */
public class IntegerWire extends Net {

	public IntegerWire(Identifier id, int width) {
		super(Keyword.INTEGER, id, width);
	} // IntegerWire(Identifier, width)

	public IntegerWire(String id, int width) {
		this(new Identifier(id), width);
	} // IntegerWire(String, width)

	public IntegerWire(Identifier id, int msb, int lsb) {
		super(Keyword.INTEGER, id, msb, lsb);
	} // IntegerWire(Identifier, msb, lsb)

	public IntegerWire(String id, int msb, int lsb) {
		this(new Identifier(id), msb, lsb);
	} // IntegerWire(String, msb, lsb)

} // end of class IntegerWire
