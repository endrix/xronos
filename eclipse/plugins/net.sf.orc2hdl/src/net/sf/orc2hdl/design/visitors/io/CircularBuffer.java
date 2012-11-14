/*
 * Copyright (c) 2012, Ecole Polytechnique Fédérale de Lausanne
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the Ecole Polytechnique Fédérale de Lausanne nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package net.sf.orc2hdl.design.visitors.io;

import net.sf.orcc.df.Port;
import net.sf.orcc.ir.IrFactory;
import net.sf.orcc.ir.Type;
import net.sf.orcc.ir.Var;

/**
 * This class contains the necessary information of a Circular Buffer
 * 
 * @author Endri Bezati
 * 
 */
public class CircularBuffer {

	/** The buffer Variable **/
	private Var buffer;

	/** The count variable of the circular Buffer Index **/
	private Var count;

	/** The head variable of the circular Buffer Index **/
	private Var head;

	/** The name of the circular buffer **/
	private String name;

	/** The circular buffer size **/
	private Port port;

	/** The circular buffer size **/
	private Integer size;

	/** The Boolean start Variable **/
	private Var start;

	public CircularBuffer(Port port, Var buffer, Integer size) {
		this.port = port;
		this.buffer = buffer;
		this.size = size;
		createVariables();
	}

	private void createVariables() {
		name = port.getName();
		Type type = IrFactory.eINSTANCE.createTypeInt();
		head = IrFactory.eINSTANCE.createVar(type, "cb_" + name + "_head",
				true, 0);
		count = IrFactory.eINSTANCE.createVar(type, "cb_" + name + "_count",
				true, 0);
		Type bool = IrFactory.eINSTANCE.createTypeBool();
		start = IrFactory.eINSTANCE.createVar(bool, "cb_" + name + "_start",
				true, 0);
	}

	public Var getBuffer() {
		return buffer;
	}

	public Var getCount() {
		return count;
	}

	public Var getHead() {
		return head;
	}

	public Port getPort() {
		return port;
	}

	public Integer getSize() {
		return size;
	}

	public Var getStart() {
		return start;
	}
}