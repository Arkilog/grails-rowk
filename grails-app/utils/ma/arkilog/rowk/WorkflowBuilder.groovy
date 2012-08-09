package ma.arkilog.rowk

class WorkflowBuilder extends DelegateSupport {
	
	static workflow(String dsl) {
		def dslScript = new GroovyShell().parse(dsl)
		
		def workflow = new RowkWorkflow()
		dslScript.metaClass = createEMC(dslScript.class, {
			ExpandoMetaClass emc ->
			
			
			emc.workflow = {map,Closure cl ->
				workflow.name  = map.name
				godeeper(cl, new WorkflowDelegate(workflow))
			}
		})
		dslScript.run()
		return workflow
	}
	
	static ExpandoMetaClass createEMC(Class scriptClass, Closure cl) {
		ExpandoMetaClass emc = new ExpandoMetaClass(scriptClass, false)
		
		cl(emc)
		
		emc.initialize()
		return emc
	}

}
	class Linker{
		private nonLinked = [:]
		def srcName
		def tgtName
		def add(missing, link){
			def list = nonLinked[link] ?: []
			list << missing
			nonLinked[link] = list
		}
		def match(target){
			nonLinked[target."$tgtName"].each{
				it."$srcName" = target
				println it
			}
		}
		def matchAll(targets){targets.each{match(it)}}
	}

	class DelegateSupport{
		static godeeper(closure,delegate){
			if (closure instanceof Closure) {
				closure.delegate = delegate
				closure.resolveStrategy = Closure.DELEGATE_FIRST
				closure()
			}
		}
	}

	class WorkflowDelegate extends DelegateSupport {
		private RowkWorkflow workflow
		private nextStateLinker = new Linker(srcName:'nextState',tgtName:'name')
		private variableLinker = new Linker(srcName:'ref',tgtName:'name')
		
		WorkflowDelegate(RowkWorkflow workflow) {
			this.workflow = workflow
		}
		
		def methodMissing(String name, Object args) {
			if (name) {
				def state = new RowkState()
				state.name = name
				workflow.addToStates(state)
				workflow.start = (workflow.start ?: state)
				if (args?.length ==1){
					godeeper(args[0], new RowkStateDelegate(this, state))
				}
			} else {
				throw new MissingMethodException(name, this.class, args as Object[])
			}
		}
		void end() {
			def state = new RowkState()
			state.name = "end"
			workflow.addToStates(state)
			nextStateLinker.matchAll(workflow.states)
			variableLinker.matchAll(workflow.variables)
		}
		
		def propertyMissing(String name, value) {
			if (name=='name'){
				workflow.name = String.valueOf(value)
			} else if (name=='end'){
				end()
			}
			return null
		}
	}

	class RowkStateDelegate extends DelegateSupport {
		private RowkState state
		private WorkflowDelegate workflowDelegate
		
		RowkStateDelegate(WorkflowDelegate workflowDelegate, RowkState state) {
			this.state = state
			this.workflowDelegate = workflowDelegate
		}
		
		void methodMissing(String name, Object args) {
			if (name && args.length>=1) {
				def event = new RowkEvent()
				event.name = name
				workflowDelegate.workflow.addToEvents(event)
				def transition = new RowkTransition()
				state.addToTransitions(transition)
				if (args[0] instanceof Map) {
					if (args[0].to) {
						workflowDelegate.nextStateLinker.add(transition, args[0].to)
					}
				}
				if (args.length==2){
					godeeper(args[1], new RowkTransitionDelegate(workflowDelegate, state, event, transition))
				}
			}
		}
		
		def propertyMissing(String name) {
			return null
		}
	}

	class RowkTransitionDelegate extends DelegateSupport {
		private RowkState state
		private WorkflowDelegate workflowDelegate
		private RowkEvent event
		private RowkTransition transition

		RowkTransitionDelegate(WorkflowDelegate workflowDelegate, RowkState state, RowkEvent event, RowkTransition transition) {
			this.event = event
			this.workflowDelegate = workflowDelegate
			this.state = state
			this.transition = transition
		}
		
		void methodMissing(String name, Object args) {
			if (name=="run" && args.length>=1){
				def action = new RowkAction()
				transition.addToActions(action)
				if (args[0].indexOf('.')>=0) {
					action.service = args[0].split('\\.')[0]
					action.function = args[0].split('\\.')[1]
				}
				if (args.length==2){
					godeeper(args[1], new RowkActionDelegate(workflowDelegate, state, event, transition, action))
				}
			}
		}
		def propertyMissing(String name, value) {
			return null
		}
		
	}

	class RowkActionDelegate extends DelegateSupport {
		private RowkState state
		private WorkflowDelegate workflowDelegate
		private RowkEvent event
		private RowkTransition transition
		private RowkAction action
		
		RowkActionDelegate(WorkflowDelegate workflowDelegate, RowkState state, RowkEvent event, RowkTransition transition, RowkAction action) {
			this.event = event
			this.workflowDelegate = workflowDelegate
			this.state = state
			this.transition = transition
			this.action = action
		}
		
		def methodMissing(String name, Object args) {
			if (args.length==1) {
				if (name=="input"){
					godeeper(args[0], new RowkActionInputDelegate(workflowDelegate, state, event, transition, action))
				}
				if (name=="output"){
					godeeper(args[0], new RowkActionOutputDelegate(workflowDelegate, state, event, transition, action))
				}
			}
		}
		def propertyMissing(String name, value) {
			return null
		}
		
	}
	class RowkActionInputDelegate extends DelegateSupport {
		private RowkState state
		private WorkflowDelegate workflowDelegate
		private RowkEvent event
		private RowkTransition transition
		private RowkAction action
		
		RowkActionInputDelegate(WorkflowDelegate workflowDelegate, RowkState state, RowkEvent event, RowkTransition transition, RowkAction action) {
			this.event = event
			this.workflowDelegate = workflowDelegate
			this.state = state
			this.transition = transition
			this.action = action
		}
		
		def methodMissing(String name, Object args) {
			def result = new RowkParameter()
			result.name = name
			action.addToParameters(result)
			if (args[0] instanceof Map && args[0].ref) {
				workflowDelegate.variableLinker.add(result, args[0].ref)
			} else if (args[0]) {
				result.value = RowkValue.create(args[0])
			}
		}
		def propertyMissing(String name, value) {
			return null
		}
		
	}
	class RowkActionOutputDelegate extends DelegateSupport {
		private RowkState state
		private WorkflowDelegate workflowDelegate
		private RowkEvent event
		private RowkTransition transition
		private RowkAction action
		
		RowkActionOutputDelegate(WorkflowDelegate workflowDelegate, RowkState state, RowkEvent event, RowkTransition transition, RowkAction action) {
			this.event = event
			this.workflowDelegate = workflowDelegate
			this.state = state
			this.transition = transition
			this.action = action
		}
		
		def methodMissing(String name, Object args) {
			def result = new RowkResult()
			result.name = name
			action.addToResults(result)
			if (args[0] instanceof Map && args[0].to) {
				def var = new RowkVariable()
				var.name = args[0].to
				result.ref = var
				workflowDelegate.workflow.addToVariables(var)
			}
		}
		def propertyMissing(String name, value) {
			return action
		}
		
	}
