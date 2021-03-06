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

import org.xronos.openforge.verilog.model.Identifier;
import org.xronos.openforge.verilog.model.Keyword;
import org.xronos.openforge.verilog.model.Lexicality;
import org.xronos.openforge.verilog.model.Net;

/**
 * A special Net which defers to another Net. This can be used as a placeholder
 * when the real Net has yet to be defined. Until the real Net is provided, the
 * ProxyNet presents itself as an unnamed wire of maximum width.
 * <P>
 * 
 * Created: May 7, 2002
 * 
 * @author <a href="mailto:andreas.kollegger@xilinx.com">Andreas Kollegger</a>
 * @version $Id: ProxyNet.java 2 2005-06-09 20:00:48Z imiller $
 */
public class ProxyNet extends Net {

	Net client = null;

	/**
	 * Constructs a 64-bit ProxyNet named "unnamed".
	 * 
	 */
	public ProxyNet() {
		this(64);
	}

	/**
	 * Constructs a ProxyNet named "unnamed".
	 * 
	 * @param width
	 *            The default width
	 */
	public ProxyNet(int width) {
		super(Keyword.WIRE, new Identifier("unnamed"), width);
	}

	/**
	 * Specifies the actual Net for which this is a proxy.
	 * 
	 * @param client
	 *            the actual Net definition
	 */
	public void setNet(Net client) {
		this.client = client;
		setIdentifier(client.getIdentifier());
	}

	@Override
	public Keyword getType() {
		return (client == null) ? super.getType() : client.getType();
	}

	@Override
	public Identifier getIdentifier() {
		return (client == null) ? super.getIdentifier() : client
				.getIdentifier();
	}

	@Override
	public int getWidth() {
		return (client == null) ? super.getWidth() : client.getWidth();
	}

	@Override
	public int getMSB() {
		return (client == null) ? super.getMSB() : client.getMSB();
	}

	@Override
	public int getLSB() {
		return (client == null) ? super.getLSB() : client.getLSB();
	}

	@Override
	public Lexicality lexicalify() {
		return (client == null) ? super.lexicalify() : client.lexicalify();
	}

} // ProxyNet

