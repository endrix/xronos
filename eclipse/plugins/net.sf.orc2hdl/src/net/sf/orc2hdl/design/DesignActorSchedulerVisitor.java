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

package net.sf.orc2hdl.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.openforge.lim.Block;
import net.sf.openforge.lim.Component;
import net.sf.openforge.lim.DataDependency;
import net.sf.openforge.lim.Decision;
import net.sf.openforge.lim.Design;
import net.sf.openforge.lim.Entry;
import net.sf.openforge.lim.Port;
import net.sf.orcc.df.Action;
import net.sf.orcc.df.Actor;
import net.sf.orcc.ir.InstAssign;
import net.sf.orcc.ir.IrFactory;
import net.sf.orcc.ir.Type;
import net.sf.orcc.ir.Var;

/**
 * DesignActorSchedulerVisitor class constructs the actors scheduler and it
 * transforms it to a task.
 * 
 * @author Endri Bezati
 * 
 */
public class DesignActorSchedulerVisitor extends DesignActorVisitor {

	/** The action scheduler components **/
	Map<Action, List<Component>> actionScheduler = new HashMap<Action, List<Component>>();

	/** The loop body components **/
	List<Component> bodyComponents = new ArrayList<Component>();

	public DesignActorSchedulerVisitor(Design design) {
		super(design);
	}

	@Override
	public Object caseActor(Actor actor) {
		currentListComponent = new ArrayList<Component>();
		// Build infinite loop decision
		Block testBlock = (Block) buildInfiniteLoopDecision();

		// Visit only the actions
		for (Action action : actor.getActions()) {
			doSwitch(action);
		}
		return null;
	}

	@Override
	public Object caseAction(Action action) {
		// Visit the scheduler
		doSwitch(action.getScheduler());
		return null;
	}

	/**
	 * Build the infinite loop decision for the actor scheduler
	 * 
	 * @return a decision component
	 */
	private Component buildInfiniteLoopDecision() {
		Decision decision = null;
		// Create an ExprInt of "1" and assign it to the varActorSched
		Type type = IrFactory.eINSTANCE.createTypeInt(1);
		Var varActorSched = IrFactory.eINSTANCE.createVar(0, type,
				"var_actor_sched", false, 0);
		InstAssign assignSched = IrFactory.eINSTANCE.createInstAssign(
				varActorSched, IrFactory.eINSTANCE.createExprInt(1));
		// Visit assignSched
		doSwitch(assignSched);
		// Create the var_actor_loop variable and assign the var_actor_sched
		// into it
		Var varActorLoop = IrFactory.eINSTANCE.createVar(1, type,
				"var_actor_loop", false, 0);

		InstAssign assignLoop = IrFactory.eINSTANCE.createInstAssign(
				varActorLoop, IrFactory.eINSTANCE.createExprVar(varActorSched));
		doSwitch(assignLoop);

		currentModule = new Block(false);
		// Make an Exit with zero data buses at the output
		currentExit = currentModule.makeExit(0);
		// Add all components to the module
		populateModule(currentModule, currentListComponent);
		// Build Dependencies
		operationDependencies();

		// Create the decision
		decision = new Decision((Block) currentModule, currentComponent);
		// Any data inputs to the decision need to be propagated from
		// the block to the decision. There should be no output ports
		// to propagate. They are inferred true/false.
		propagateInputs(decision, (Block) currentModule);

		// Build option scope
		currentModule.specifySearchScope("schedulerLoopDecision");
		return decision;

	}

	private void propagateInputs(Decision decision, Block testBlock) {
		for (Port port : testBlock.getDataPorts()) {
			Port decisionPort = decision.makeDataPort();
			Entry entry = port.getOwner().getEntries().get(0);
			entry.addDependency(port,
					new DataDependency(decisionPort.getPeer()));
			portCache.replaceTarget(port, decisionPort);
		}
	}

}