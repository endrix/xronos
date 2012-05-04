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

package net.sf.openforge.optimize;


import java.util.*;

import net.sf.openforge.lim.*;
import net.sf.openforge.lim.op.*;
import net.sf.openforge.optimize._optimize;
import net.sf.openforge.util.naming.*;

/**
 * ComponentSwapVisitor is an extention of the SafeFilteredVisitor
 * which provides some utility methods for performing constant prop.
 *
 *
 * <p>Created: Fri Jul 12 10:41:32 2002
 *
 * @author imiller
 * @version $Id: ComponentSwapVisitor.java 61 2005-11-17 18:07:21Z imiller $
 */
public class ComponentSwapVisitor extends SafeFilteredVisitor 
{
    private static final String _RCS_ = "$Rev: 61 $";

    /** Set to true if the visitor has modified the LIM */
    protected boolean modified = false;
    protected int replacedNodeCount = 0;
    protected int removedNodeCount = 0;
    
    /** Set to true if the visitor did modify the LIM */
    protected boolean did_modify = false;
    protected int replacedNodeCountTotal = 0;
    protected int removedNodeCountTotal = 0;
    
    /**
     * Implemented solely to provide debug output of which components
     * are being traversed.
     *
     * @param c a value of type 'Component'
     */
    public void preFilterAny (Component c)
    {
        if (_optimize.db) _optimize.ln("Analyzing component: " + c+ "/"+ID.glob(c)+"/"+ID.log(c));
    }

    public void swapComponents (Component orig, Component replacement)
    {
        mapExact(orig, replacement);

        // If the owner is a block use the much friendlier replace method.
        if (orig.getOwner() instanceof Block)
        {
            if (((Block)orig.getOwner()).replaceComponent(orig, replacement))
            {
                setModified(true);
            }
        }
        else
        {
            if (orig.getOwner().removeComponent(orig))
            {
                setModified(true);
            }        
            orig.getOwner().addComponent(replacement);
        }

        orig.disconnect();

        replacement.setOptionLabel(orig.getOptionLabel());
        
        this.replacedNodeCount++;
        this.replacedNodeCountTotal++;
    }

    /**
     * Used to set the 'isModified' flag.
     *
     * @param value a value of type 'boolean'
     */
    protected void setModified (boolean value)
    {
        this.modified = value;
        this.did_modify = value;
    }
    
    /**
     * Returns true if the last iteration of this Visitor modified the
     * LIM.
     *
     * @return true if this Visitor has modified the LIM since it was
     * last {@link ComponentSwapVisitor#reset}
     */
    protected boolean isModified ()
    {
        return this.modified;
    }

    /**
     * Return true if any of the iteration of this Visitor modified
     * the LIM.
     *
     * @return true if the Visitor did modify the LIM during the run.
     */
    public boolean didModify ()
    {
        return this.did_modify;
    }
    
    /**
     * Reset's the necessary flags to begin another iteration of this
     * visitor.
     *
     */
    protected void reset ()
    {
        this.modified = false;
    }

    public void clear ()
    {
        this.did_modify = false;
        clearCount();
    }
    
    protected void clearCount ()
    {
        this.replacedNodeCount = 0;
        this.removedNodeCount = 0;
    }
    
    /** 
     * used, ie in DivideOpRule, to indicate that a component was replaced
     *    without calling SwapComponents or replaceComponent
     */
    public void replacedNodeCountIncrement ()
    {
        this.replacedNodeCount++;
        this.replacedNodeCountTotal++;
    }
    /** 
     * used, ie in DivideOpRule, to indicate that a component was removed
     */
    public void removedNodeCountIncrement ()
    {
        this.removedNodeCount++;
        this.removedNodeCountTotal++;
    }
    
    public int getReplacedNodeCount ()
    {
        return this.replacedNodeCount;
    }

    public int getRemovedNodeCount ()
    {
        return this.removedNodeCount;
    }

    public int getReplacedNodeCountTotal ()
    {
        return this.replacedNodeCountTotal;
    }
    
    public int getRemovedNodeCountTotal ()
    {
        return this.removedNodeCountTotal;
    }
    
    /**
     * Returns a Number representation of the constant value applied
     * to the port via it's logical dependency, or null if the port is
     * not driven by a constant or is not analyzable.  This method
     * only analyzes ports which are driven by a single logical bus
     * whose Value is constant ({@link Value#isConstant}).
     *
     * @param port the {@link Port} to analyze.
     * @return the Number representation of the constant value or null
     * if the port could not be fully analyzed or is driven by a
     * non-constant bus.
     */
    public static Number getPortConstantValue(Port port)
    {
        return getPortConstantValue(port, new ArrayList(port.getOwner().getEntries()));
    }
    
    /** 
     * Returns a Number representation of the constant value applied
     * to the port via it's logical dependency, or null if the port is
     * not driven by a constant or is not analyzable.  This method
     * only analyzes ports which are driven by a single logical bus
     * whose Value is constant ({@link Value#isConstant}).
     *
     * @param port the {@link Port} to analyze.
     * @param the entry to use
     * @return the Number representation of the constant value or null
     * if the port could not be fully analyzed or is driven by a
     * non-constant bus.
     */
    public static Number getPortConstantValue(Port port, Entry entry)
    {
        return getPortConstantValue(port, Collections.singletonList(entry));
    }
    
    private static Number getPortConstantValue (Port port, List entries)
    {
        Component owner = port.getOwner();
        assert owner != null;
        Set logicalBuses;
        if (port.getBus() == null)
        {
            logicalBuses = new HashSet();
            for (Iterator iter = entries.iterator(); iter.hasNext();)
            {
                Entry entry = (Entry)iter.next();
                for (Iterator depIter = entry.getDependencies(port).iterator(); depIter.hasNext();)
                {
                    Dependency dep = (Dependency)depIter.next();
                    logicalBuses.add(dep.getLogicalBus());
                }
            }
        }
        else
        {
            logicalBuses = Collections.singleton(port.getBus());
        }
        

        if (logicalBuses.size() != 1)
        {
            if (_optimize.db) _optimize.ln(_optimize.FULL_CONST, "\t" + owner + " has " + logicalBuses.size() + " logical dependencies on port " + port);
            return null;
        }

        Bus logicalBus = (Bus)logicalBuses.iterator().next();

        Value busValue = logicalBus.getValue();
        
        if (busValue != null && busValue.isConstant() && !busValue.isDontCare())
        {
            // check for bus that comes from a floating point op
            if(isFPOpBus(logicalBus))
            {
                return makeFPNumber(logicalBus, busValue);
            }

            Value value=busValue;
            long lvalue = value.getValueMask();
            if (_optimize.db) _optimize.ln(_optimize.FULL_CONST, "\tConstant Valued: " + lvalue);
            return new Long(lvalue);
        }
        else
        {
            if (_optimize.db) _optimize.ln(_optimize.FULL_CONST, "\tNot constant");
            return null;
        }
    }

    /**
     * Maps the ports, buses and exits from the source component to
     * the target component _exactly_ 1 to 1.  This means that the
     * source and target must have exactly the same number of ports,
     * buses, and exits.
     *
     * @param source the source Component from which characteristics
     * are taken
     * @param target the target Component to which the characteristics
     * are copied.
     */
    public static void mapExact(Component source, Component target)
    {
        Map portCorrelation = new HashMap();
        Map busCorrelation = new HashMap();
        Map exitCorrelation = new HashMap();

        for (Iterator sourceIter = source.getPorts().iterator(),
             targetIter = target.getPorts().iterator();
             sourceIter.hasNext();)
        {
            portCorrelation.put(sourceIter.next(), targetIter.next());
        }

        for (Iterator sourceExitIter = source.getExits().iterator(),
             targetExitIter = target.getExits().iterator();
             sourceExitIter.hasNext();)
        {
            Exit sourceExit = (Exit)sourceExitIter.next();
            Exit targetExit = (Exit)targetExitIter.next();
            exitCorrelation.put(sourceExit, targetExit);

            for (Iterator sourceIter = sourceExit.getBuses().iterator(),
                 targetIter = targetExit.getBuses().iterator();
                 sourceIter.hasNext();)
            {
                busCorrelation.put(sourceIter.next(), targetIter.next());
            }
        }

        replaceConnections(portCorrelation, busCorrelation, exitCorrelation);
    }
    

    /**
     * Duplicates the connectivity (in terms of entries, dependencies,
     * and physical connections) of the ports contained in the
     * portCorrelation Map and replaces the connections of buses in
     * the busCorrelation Map.  This method assumes that the values in
     * the correlation map are ports/buses/exits on nodes which have
     * not yet been connected (ie are being inserted into the graph).
     * <P>Port connectivity is copied by creating new dependencies for
     * each port and setting logical and structural buses to be the
     * same as the original port.  The source port must have only 1
     * entry, however.
     * <P>Bus connectivity is replaced by setting the logical and/or
     * structrual buses in each dependency of the source bus to be the
     * new bus.
     * <P>Exit's are correlated so that any entry which used to be
     * 'driven' by the original exit can be set up to now be driven by
     * the new exit.
     *
     * @param portCorrelation a 'Map' of original {@link Port} to new
     * {@link Port}
     * @param busCorrelation a 'Map' of original {@link Bus} to new
     * {@link Bus}
     * @param exitCorrelation a 'Map' of original {@link Exit} to new
     * {@link Exit}
     */
    public static void replaceConnections(Map portCorrelation, Map busCorrelation, Map exitCorrelation)
    {
        // Copy port connections/dependencies
        for (Iterator portIter = portCorrelation.keySet().iterator(); portIter.hasNext();)
        {
            Port source = (Port)portIter.next();
            Port target = (Port)portCorrelation.get(source);
            mirrorPort(source, target);
        }
        
        // REPLACE bus connections/dependencies
        for (Iterator busIter = busCorrelation.keySet().iterator(); busIter.hasNext();)
        {
            Bus source = (Bus)busIter.next();
            Bus target = (Bus)busCorrelation.get(source);
            
            // Logical.  Replace the current logical dependencies with
            // a new dependency.
            List logical = new ArrayList(source.getLogicalDependents());
            for (Iterator depIter = logical.iterator(); depIter.hasNext();)
            {
                Dependency dep = (Dependency)depIter.next();
                Dependency newDep = (Dependency)dep.clone();
                newDep.setLogicalBus(target);
                dep.getEntry().addDependency(dep.getPort(), newDep);
                dep.zap();
            }

            // Physical
            for (Iterator portIter = (new ArrayList(source.getPorts())).iterator(); portIter.hasNext();)
            {
                Port port = (Port)portIter.next();
                port.setBus(target);
            }
        }

        // Replace the given exit with the new exit as the
        // 'drivingExit' for anything that it drives.
        for (Iterator exitIter = exitCorrelation.keySet().iterator(); exitIter.hasNext();)
        {
            Exit source = (Exit)exitIter.next();
            Exit target = (Exit)exitCorrelation.get(source);

//             for (Iterator entryIter = source.getDrivenEntries().iterator(); entryIter.hasNext();)
//             {
//                 ((Entry)entryIter.next()).setDrivingExit(target);
//             }
            
            // A ConcurrentModificationException will be thrown if we don't 
            // obtain the driven entries first.
            Set drivenEntries = new HashSet((Set)source.getDrivenEntries());
            for(Iterator entryIter = drivenEntries.iterator(); entryIter.hasNext();)
            {
                Entry entry = (Entry)entryIter.next();
                entry.setDrivingExit(target);
            }
            
        }
    }

    /**
     * Does a 1:1 copying of the source port's dependencies/entries to
     * the target port.  Entries are matched up by position in their
     * owners list and new entries are created on the target if none exits.
     *
     * @param source The source Port from which characteristics are copied.
     * @param target The target Port to which characteristics are copied.
     */
    private static void mirrorPort(Port source, Port target)
    {
        Component sourceOwner = source.getOwner();
        Component targetOwner = target.getOwner();
        assert sourceOwner != null : "Source port must have owner";
        assert targetOwner != null : "Target port must have owner";

        assert (targetOwner.getEntries().size() == 0) || (targetOwner.getEntries().size() == sourceOwner.getEntries().size()) : "Source and target must have same number of entries to 'mirror'";
        
        for (Iterator sourceEntryIter = sourceOwner.getEntries().iterator(),
             targetEntryIter = targetOwner.getEntries().iterator();
             sourceEntryIter.hasNext();)
        {
            Entry sourceEntry = (Entry)sourceEntryIter.next();
            Entry targetEntry;
            if (targetEntryIter.hasNext())
                targetEntry = (Entry)targetEntryIter.next();
            else
                targetEntry = targetOwner.makeEntry(sourceEntry.getDrivingExit());

            copyPort(source, sourceEntry, target, targetEntry);
        }
        
        if (source.getBus() != null)
        {
            assert target.getBus() == null : "target already has a bus connected";
            target.setBus(source.getBus());
        }
    }


    /**
     * Short-circuits a given {@link Port Port's} dependencies and connections
     * to the dependents of a {@link Bus} on the same {@link Component}.  This
     * essentially eliminates the path between the port and the bus.  Following
     * this, all connections and dependencies of the port and bus are eliminated.
     *
     * @param port the port whose connections are moved to the dependents
     *          of <code>bus</code>
     * @param bus the bus whose dependents will receive the <code>port's</code>
     *          connections
     */
    public static void shortCircuit (Port port, Bus bus)
    {
        /*
         * Make sure the Port and Bus are at opposit ends of the same
         * Component or Module.
         */
        if (port.getOwner() != bus.getOwner().getOwner())
        {
            throw new IllegalArgumentException("Port and Bus Components differ");
        }

        /*
         * Copy the Port's connections and dependencies to each of the
         * Bus's dependents.
         */
        for (Iterator iter = bus.getLogicalDependents().iterator(); iter.hasNext();)
        {
            final Dependency dependent = (Dependency)iter.next();
            final Entry dependentEntry = dependent.getEntry();
            copyPortConnections(port, dependent.getPort(), dependentEntry);
        }

        /*
         * Disconnect the Port and Bus.
         */
        port.disconnect();
        for (Iterator iter = port.getOwner().getEntries().iterator(); iter.hasNext();)
        {
            ((Entry)iter.next()).clearDependencies(port);
        }
        bus.disconnect();
        bus.clearLogicalDependents();
    }

    /**
     * Copies recreates all of the {@link Dependency dependencies} of one given
     * {@link Port} on another given {@link Port}.  The {@link Bus} connection value
     * is also copied.
     *
     * @param sourcePort the port whose connections are copied
     * @param targetPort the port on which the copied connections are created
     * @param targetEntry the entry used to create dependencies for <code>targetPort</code>
     */
    private static void copyPortConnections (Port sourcePort, Port targetPort, Entry targetEntry)
    {
        for (Iterator iter = sourcePort.getOwner().getEntries().iterator(); iter.hasNext();)
        {
            final Entry sourceEntry = (Entry)iter.next();
            for (Iterator diter = sourceEntry.getDependencies(sourcePort).iterator(); diter.hasNext();)
            {
                final Dependency sourceDependency = (Dependency)diter.next();
                final Dependency targetDependency = (Dependency)sourceDependency.clone();
                targetDependency.setLogicalBus(sourceDependency.getLogicalBus());
                targetEntry.addDependency(targetPort, targetDependency);
            }
        }

        targetPort.setBus(sourcePort.getBus());
    }

    /**
     * copy dependencies from the source port (as found in the
     * sourceEntry) to the target port (in the targetEntry).
     */
    private static void copyPort (Port source, Entry sourceEntry, Port target, Entry targetEntry)
    {
        for (Iterator iter = sourceEntry.getDependencies(source).iterator(); iter.hasNext();)
        {
            Dependency sourceDep = (Dependency)iter.next();
            Dependency targetDep = (Dependency)sourceDep.clone();
            targetDep.setLogicalBus(sourceDep.getLogicalBus());
            targetEntry.addDependency(target, targetDep);
        }
    }
    
    /**
     * Reconnects any dependency attached to the Go port to each
     * logical dependent of the done bus.
     *
     * @param c a value of type 'Component'
     */
    public static void wireControlThrough(Component c)
    {
        for (Iterator exitIter = c.getExits().iterator(); exitIter.hasNext();)
        {
            Exit exit = (Exit)exitIter.next();
            shortCircuit(c.getGoPort(), exit.getDoneBus());
        }
    }

    /**
     * Removes the component from it's owner after ensuring that the
     * component's exit(s) is not listed as the driving exit of any
     * entries.
     *
     * @param c a value of type 'Component'
     * @return true if the component was removed from the owner, false
     * if the component has no owner or if it could not be removed
     * from its owner.
     */
    public static boolean removeComp(Component c)
    {
        if (_optimize.db) _optimize.ln("\tRemoving " + c.toString());

        // Before we remove it, make sure it won't report that
        // it's still driving an entry....
        for (Iterator iter = c.getExits().iterator(); iter.hasNext();)
        {
            Exit exit = (Exit)iter.next();
            // assert exit.getDrivenEntries().size()==0;
            if (_optimize.db && exit.getDrivenEntries().size() > 0)
            {
                _optimize.ln(_optimize.DEAD_CODE, "WARNING.  Exit has " + exit.getDrivenEntries().size() + " entries that still report it as their driving exit");
            }
            
            for (Iterator entryIter = new ArrayList(exit.getDrivenEntries()).iterator();
                 entryIter.hasNext();)
            {
                ((Entry)entryIter.next()).setDrivingExit(null);
            }
        }
        
        return c.getOwner() != null && c.getOwner().removeComponent(c);
    }

    /**
     * Replaced the given component with the specified constant, but
     * does NOT do any reverse traversal to delete 'dangling' nodes.
     * We will rely on the code cleanup pass to do this for us.
     *
     * @param comp a value of type 'Component'
     * @param constant a value of type 'Constant'
     */
    public void replaceComponent(Component comp, Constant constant)
    {
        if (_optimize.db) _optimize.ln(_optimize.FULL_CONST, "FULL CONST.  " + comp + " replaced with " + constant);
        
        replacedNodeCount++;
        replacedNodeCountTotal++;
        
        Module owner = comp.getOwner();
        assert owner != null : "Cannot replace a component which is not contained in a module";

        // map the dependencies/connections.
        Map portCorrelation = new HashMap();
        portCorrelation.put(comp.getClockPort(), constant.getClockPort());
        portCorrelation.put(comp.getResetPort(), constant.getResetPort());
        portCorrelation.put(comp.getGoPort(), constant.getGoPort());
        
        assert comp.getExits().size() == 1 : "Only expecting one exit on node to be replaced";
        Exit exit = (Exit)comp.getExits().iterator().next();
        Map busCorrelation = new HashMap();
        Map exitCorrelation = new HashMap();
        if (!exit.getDataBuses().isEmpty())
        {
            assert exit.getDataBuses().size() == 1 : "Only expecting one data bus on component to be replaced: " + comp
            + " data bus count=" + exit.getDataBuses().size();

            busCorrelation.put(exit.getDataBuses().get(0), constant.getValueBus());
        }

        busCorrelation.put(exit.getDoneBus(), ((Exit)constant.getOnlyExit()).getDoneBus());

        exitCorrelation.put(exit, constant.getOnlyExit());

        replaceConnections(portCorrelation, busCorrelation, exitCorrelation);

        moduleReplace(comp, constant);
        
        comp.disconnect();
        this.replacedNodeCount++;
        this.replacedNodeCountTotal++;
        
    }

    protected void moduleReplace (Component orig, Component replacement)
    {
        // If the owner is a block use the much friendlier replace method.
        if (orig.getOwner() instanceof Block)
        {
            if (((Block)orig.getOwner()).replaceComponent(orig, replacement))
            {
                setModified(true);
            }
        }
        else
        {
            Module owner = orig.getOwner();
            if (orig.getOwner().removeComponent(orig))
            {
                setModified(true);
            }
            owner.addComponent(replacement);
        }
        replacement.setOptionLabel(orig.getOptionLabel());
    }
    
    protected void moduleInsert (Component location, Component toInsert)
    {
        // If the owner is a block use the much friendlier insert method.
        if (location.getOwner() instanceof Block)
        {
            Block block = (Block)location.getOwner();
            int position = block.getSequence().indexOf(location);
            block.insertComponent(toInsert, position);
            setModified(true);
        }
        else
        {
            location.getOwner().addComponent(toInsert);
        }
    }
    
    /**
     * Checks if the Operation whichs owns the bus operates on
     * floating point constant(s).
     *
     * @param bus an operation bus
     * 
     * @return true if the bus belongs to a floating point op
     */
    private static boolean isFPOpBus (Bus bus)
    {
        if(bus.isFloat())
        {
            return true;
        }
        if(bus.getOwner().getOwner() instanceof Operation &&
           ((Operation)bus.getOwner().getOwner()).isFloat())
        {
            return true;
        }
        
        return false;
    }

    /**
     * Creates a floating point number from a bus value.
     *
     * @param bus an operation bus
     * @param value bus value
     *
     * @return A floating point number
     */
    private static Number makeFPNumber (Bus bus, Value value)
    {
        if(value.getSize() <= 32)
        {
            return new Float(Float.intBitsToFloat((int)value.getValueMask()));
        }

        return new Double(Double.longBitsToDouble(value.getValueMask()));
    }

    /**
     * Converts a Number to the representation of that number in bits,
     * stored in a long by calling longValue on any integer types and
     * using the 'toRawBits' methods for floats and doubles.
     *
     * @param number a value of type 'Number'
     * @return a value of type 'long'
     */
    protected static long numberToLongBits (Number number)
    {
        long longValue;
        if (number instanceof Double) longValue = Double.doubleToRawLongBits(number.doubleValue());
        else if (number instanceof Float) longValue = ((long)Float.floatToRawIntBits(number.floatValue())) & 0x00000000FFFFFFFFL;
        else longValue = number.longValue();
        return longValue;
    }
    
}// ComponentSwapVisitor