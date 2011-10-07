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


package net.sf.openforge.frontend.slim.builder;

import java.util.*;

import net.sf.openforge.lim.*;

import org.w3c.dom.*;

/**
 * PortCache maintains mappings from port {@link Node}s to the
 * {@link Port} or {@link Bus} object created for that Node.  
 *
 *
 * <p>Created: Mon Jul 11 16:01:00 2005
 *
 * @author imiller, last modified by $Author: imiller $
 * @version $Id: PortCache.java 32 2005-09-23 15:54:03Z imiller $
 */
public class PortCache extends CacheBase
{

    /** A Map of DOM Node (port) to lim Bus */
    private final Map sourceCache = new HashMap();
    /** A Map of DOM Node (port) to lim Port */
    private final Map targetCache = new HashMap();
    
    public PortCache ()
    {}

    /**
     * This method publishes mapping entries to the specified cache
     * iff the <b>value</b> of the mapping is contained in the
     * relevantValues collection. 
     *
     * @param cache a non-null PortCache
     * @param relevantValues a Collection of objects which are values
     * within this cache.
     */
    public void publish (PortCache cache, Collection relevantValues)
    {
        for (Iterator iter = this.sourceCache.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry)iter.next();
            if (relevantValues.contains(entry.getValue()))
            {
                if (_parser.db) System.out.println("Publishing " + entry.getKey() + " => " + entry.getValue() + " to " + cache);
                cache.putSource((Node)entry.getKey(), (Bus)entry.getValue());
            }
        }

        for (Iterator iter = this.targetCache.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry)iter.next();
            if (relevantValues.contains(entry.getValue()))
            {
                if (_parser.db) System.out.println("Publishing " + entry.getKey() + " => " + entry.getValue() + " to " + cache);
                cache.putTarget((Node)entry.getKey(), (Port)entry.getValue());
            }
        }
    }
    
    /**
     * Defines the specified {@link Bus} as the implementation of the
     * specified {@link Node}, overwriting any previous association
     * for the Node.
     *
     * @param node a non-null Node
     * @param bus a non-null Bus
     */
    public void putSource (Node node, Bus bus)
    {
        this.sourceCache.put(node, bus);
    }
    
    /**
     * Defines the specified {@link Port} as the implementation of the
     * specified {@link Node}, overwriting any previous association
     * for the Node.
     *
     * @param node a non-null Node
     * @param port a non-null Port
     */
    public void putTarget (Node node, Port port)
    {
        this.targetCache.put(node, port);
    }

    /**
     * Redefines the implementation for a given Node from the
     * 'original' port to the 'replacement' port.
     *
     * @param original the Port to be replaced
     * @param replacement the new Port to be associated
     */
    public void replaceTarget (Port original, Port replacement)
    {
        Node key = null;
        for (Iterator iter = this.targetCache.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry)iter.next();
            if (entry.getValue() == original)
            {
                key = (Node)entry.getKey();
                break;
            }
        }
        // We may attempt to replace a non-declared port, such as
        // clock or reset, in that case, silently fail to do the
        // replacement 
        //assert key != null : "Could not replace target port " + original + " of " + original.getOwner().show();
        if (key != null)
        {
            putTarget(key, replacement);
        }
    }

    /**
     * Redefines the implementation for a given Node from the
     * 'original' bus to the 'replacement' bus.
     *
     * @param original the Bus to be replaced
     * @param replacement the new Bus to be associated
     */
    public void replaceSource (Bus original, Bus replacement)
    {
        Node key = null;
        for (Iterator iter = this.sourceCache.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry)iter.next();
            if (entry.getValue() == original)
            {
                key = (Node)entry.getKey();
                break;
            }
        }
        assert key != null : "Could not replace source bus";
        putSource(key, replacement);
    }
    
    /**
     * Returns the input {@link Port} that was defined for the Node
     * whose key attribute ({@see CacheBase#getNodeForString}) has the
     * specified value.
     *
     * @param nodeId a non-null String
     * @return the non-null Port which was associated with the
     * specified Node.
     */
    public Port getTarget (String nodeId)
    {
        return getTarget(getNodeForString(nodeId, targetCache));
    }
    
    /**
     * Returns the input {@link Port} that was defined for the
     * specified Node.
     *
     * @param nodeId a non-null String
     * @return the non-null Port which was associated with the
     * specified Node.
     */
    public Port getTarget (Node node)
    {
        return (Port)this.targetCache.get(node);
    }

    /**
     * Returns the output {@link Bus} that was defined for the Node
     * whose key attribute ({@see CacheBase#getNodeForString}) has the
     * specified value.
     *
     * @param nodeId a non-null String
     * @return the non-null Bus which was associated with the
     * specified Node.
     */
    public Bus getSource (String nodeId)
    {
        return getSource(getNodeForString(nodeId, sourceCache));
    }
    
    /**
     * Returns the output {@link Bus} that was defined for the
     * specified Node.
     *
     * @param nodeId a non-null String
     * @return the non-null Bus which was associated with the
     * specified Node.
     */
    public Bus getSource (Node node)
    {
        return (Bus)this.sourceCache.get(node);
    }

    public void debug ()
    {
        System.out.println("PortCache " + this);
        System.out.println("SourceCache");
        for (Iterator iter = sourceCache.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry)iter.next();
            System.out.println(((Element)entry.getKey()).getAttribute("tag") + ":" + entry.getValue());
        }
        
        System.out.println("TargetCache");
        for (Iterator iter = targetCache.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry)iter.next();
            System.out.println(((Element)entry.getKey()).getAttribute("tag") + ":" + entry.getValue());
        }
    }
    
    
    
}// PortCache