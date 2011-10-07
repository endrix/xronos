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

package net.sf.openforge.verilog.pattern;

import java.util.*;

import net.sf.openforge.lim.Port;
import net.sf.openforge.lim.op.OrOpMulti;
import net.sf.openforge.verilog.model.Assign;
import net.sf.openforge.verilog.model.Net;
import net.sf.openforge.verilog.model.NetFactory;
/**
 * OrManyAssignment is used to translate the OrOpMulti which is an
 * n-bit wide logical Or.
 *
 * <p>Created: Wed Feb 19 14:35:04 2003
 *
 * @author imiller, last modified by $Author: imiller $
 * @version $Id: OrManyAssignment.java 2 2005-06-09 20:00:48Z imiller $
 */
public class OrManyAssignment extends StatementBlock implements ForgePattern
{
    private static final String _RCS_ = "$Rev: 2 $";

    private List operands = new ArrayList();
    private Net resultWire;
    
    public OrManyAssignment (OrOpMulti multi)
    {
        for (Iterator iter = multi.getDataPorts().iterator(); iter.hasNext();)
        {
            Port p = (Port)iter.next();
            if (!p.isUsed())
            {
                continue;
            }
            operands.add(new PortWire(p));
        }
        
        this.resultWire = NetFactory.makeNet(multi.getResultBus());

        add(new Assign.Continuous(resultWire, new OrMany(this.operands)));
    }
    
    public Collection getConsumedNets()
    {
        return Collections.unmodifiableList(operands);
    }
    
    public Collection getProducedNets()
    {
        return Collections.singleton(resultWire);
    }
    
}// OrManyAssignment