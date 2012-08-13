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
		def clazz
		def createMissing
		def many
		def add(missing, link){
			def list = nonLinked[link] ?: []
			list << missing
			nonLinked[link] = list
		}
		def match(target){
			nonLinked[target."$tgtName"].each{
				if (many){
					it."addTo${srcName.capitalize()}"(target)
				} else {
					it."$srcName" = target
				}
			}
		}
		def matchAll(targets){
			targets.each{
				match(it)
			}
			if (createMissing) {
				nonLinked.findAll{k,v->
					if (many){
						v.find{!((it."$srcName").find{it."$tgtName" == k})}
					} else {
						v.find{!(it."$srcName")}
					}
				}.each{k,v->
					def newObj = createMissing()
					newObj."$tgtName" = k
					if (many){
						v*."addTo${srcName.capitalize()}"(newObj)
					} else {
						v*."$srcName" = newObj
					}
				}
			}
		}
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
		private variableLinker = new Linker(srcName:'ref',tgtName:'name',createMissing:{
				def var = new RowkVariable()
				workflow.addToVariables(var)
				var
			})
		
		WorkflowDelegate(RowkWorkflow workflow) {
			this.workflow = workflow
		}
		
		def methodMissing(String name, Object args) {
			if (name) {
				if (args?.length >=1) {
					def state, closure
					if (args[0] instanceof Map) {
						switch(args[0]?.type) {
							case CASE: "andjoin"
								state = new RowkAndJoinState()
							break
							case CASE: "andfork"
								state = new RowkAndForkState()
							break
							default:
								state = new RowkState()
							break
						}
						closure = args[1]
					} else {
						state = new RowkState()
						closure = args[0]
					}
					state.name = name
					workflow.addToStates(state)
					workflow.start = (workflow.start ?: state)
					godeeper(closure, new RowkStateDelegate(this, state))
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
				if (args[0] instanceof Map) {
					def transit = {to->
						def transition = new RowkTransition()
						state.addToTransitions(transition)
						workflowDelegate.nextStateLinker.add(transition, to)
						if (args.length==2){
							godeeper(args[1], new RowkTransitionDelegate(workflowDelegate, state, event, transition))
						}
					}
					(args[0].to instanceof List ? args[0].to : [args[0].to]).each(transit)
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
			if (args[0] instanceof Map){
				if (args[0].ref) {
					def param = new RowkParameter()
					param.name = name
					action.addToParameters(param)
					workflowDelegate.variableLinker.add(param, args[0].ref)
				} else if (args[0].to) {
					addResult(name,args[0].to)
				}
			} else if (args[0]) {
				def param = new RowkParameter()
				param.name = name
				action.addToParameters(param)
				param.value = RowkValue.create(args[0])
			}
		}
		def propertyMissing(String name, value) {
			addResult(name, name)
		}
		def addResult(String name, value) {
			def result = new RowkResult()
			result.name = name
			action.addToResults(result)
			def var = new RowkVariable()
			var.name = value
			result.ref = var
			workflowDelegate.workflow.addToVariables(var)
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
		}
		def propertyMissing(String name, value) {
			return action
		}
		
	}
