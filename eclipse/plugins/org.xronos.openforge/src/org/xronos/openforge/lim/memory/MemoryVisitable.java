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

package org.xronos.openforge.lim.memory;

/**
 * MemoryVisitable is a lightweight interface implemented by any node which can
 * be visited by the {@link MemoryVisitor}
 * 
 * <p>
 * Created: Tue Sep 16 15:26:08 2003
 * 
 * @author imiller, last modified by $Author: imiller $
 * @version $Id: MemoryVisitable.java 2 2005-06-09 20:00:48Z imiller $
 */
public interface MemoryVisitable {

	void accept(MemoryVisitor vis);

}// MemoryVisitable
